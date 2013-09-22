package pl.ciruk.film.utils.web;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pl.ciruk.film.utils.core.PreconditionsHelper;
import pl.ciruk.film.utils.core.StringHelper;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class FilmwebParser {
	private static final String SEARCH_BY_TITLE_URL_FORMAT = "http://www.filmweb.pl/search/film?q=%s";
	
	private static final String FILM_DESC_CSS_CLASS = ".hitDescWrapper";
	
	private static final Log LOG = LogFactory.getLog(FilmwebParser.class);
	
	/**
	 * Finds films' descriptions for given title.
	 * @param title
	 * @return
	 */
	public List<FilmwebDescription> find(String title) {
		LOG.info("find(title)");
		LOG.info(String.format("find - Title: %s", title));
		
		List<FilmwebDescription> descriptions = Lists.newArrayList();
		
		if (!Strings.isNullOrEmpty(title)) {
			try {
				// Pobieranie strony z wynikami wyszukiwania
				String url = String.format(SEARCH_BY_TITLE_URL_FORMAT, URLEncoder.encode(title, "UTF-8"));
				Document doc = Jsoup.connect(url).get();
				
				// Kontener opisu filmu 
				Elements foundDescriptions = doc.select(FILM_DESC_CSS_CLASS);
				for (Element description : foundDescriptions) {
					if (StringHelper.fuzzyEquals(getShortTitle(description), title)) {
						FilmwebDescription filmwebDescription = mapDescription(description);
						descriptions.add(filmwebDescription);
					}
				}
			} catch (IOException e) {
				LOG.error("find - Error while retrieving Filmweb page", e);
			}
		} else {
			LOG.warn("find - Title is empty");
		}
		
		return descriptions;
	}
	
	/**
	 * Finds films' descriptions for given title and actors. <br/>
	 * Actors is a list of names in format: {@code firstname lastname}. <br/>
	 * If actors list is empty, search is performed by title only.
	 * @param title
	 * @param actors
	 * @return
	 */
	public List<FilmwebDescription> find(String title, List<String> actors) {
		LOG.info("find");
		LOG.info(String.format("find - Title: %s; Actors: %s", title, actors));
		
		List<FilmwebDescription> descriptions = Lists.newArrayList();
		
		if (actors == null || actors.isEmpty()) {
			descriptions = find(title);
		} else if (!Strings.isNullOrEmpty(title)) {
			try {
				// Pobieranie strony z wynikami wyszukiwania
				String url = String.format(SEARCH_BY_TITLE_URL_FORMAT, URLEncoder.encode(title, "UTF-8"));
				Document searchResultPage = Jsoup.connect(url).get();
				
				// Kontener opisu filmu 
				Elements foundDescriptions = searchResultPage.select(FILM_DESC_CSS_CLASS);
				for (Element description : foundDescriptions) {
					if (StringHelper.fuzzyContains(getTitleChunks(description), title)) {
						Document filmDetailsPage = Jsoup.connect(getURL(description)).get();
						
						List<String> filmwebActors = getActors(filmDetailsPage);
						if (StringHelper.fuzzyContainsAll(filmwebActors, actors)) {
							descriptions.add(mapDescription(description));
						}
					}
				}
			} catch (IOException e) {
				LOG.error("find - Error while retrieving Filmweb page", e);
			}
		} else {
			LOG.warn("parse - Title is empty");
		}
		
		return descriptions;
	}
	
	private FilmwebDescription mapDescription(Element description) {
		Preconditions.checkArgument(description != null, PreconditionsHelper.CANT_BE_NULL, "FilmDescription element");
		
		FilmwebDescription filmwebDescription = new FilmwebDescription();
		filmwebDescription.setTitle(getFullTitle(description));
		filmwebDescription.setScore(getScore(description));
		filmwebDescription.setUrl(getURL(description));
		return filmwebDescription;
	}
	
	private List<String> getActors(Element description) {
		Preconditions.checkArgument(description != null, PreconditionsHelper.CANT_BE_NULL, "FilmDescription element");
		
		List<String> actors = Lists.newArrayListWithExpectedSize(description.childNodes().size());
		
		// Aktorzy pobierani sa ze strony ze szczegolowym opisem filmu, nie zas z wynikow wyszukiwania
		for (Element filmwebActor : description.select(".filmCastBox .personName")) {
			actors.add(filmwebActor.text());
		}
		return actors;
	}
	
	private String getShortTitle(Element description) {
		Preconditions.checkArgument(description != null, PreconditionsHelper.CANT_BE_NULL, "FilmDescription element");
		
		List<String> titleChunks = getTitleChunks(description);
		
		return titleChunks.isEmpty() ? null : titleChunks.get(0);
	}
	
	/**
	 * Retrieves all parts of filmweb title. <br/>
	 * Film's title is presented in format: {@code POLISH TITLE / ORIGINAL TITLE (YEAR)}.
	 * @param description
	 * @return
	 */
	private List<String> getTitleChunks(Element description) {
		Preconditions.checkArgument(description != null, PreconditionsHelper.CANT_BE_NULL, "FilmDescription element");
		
		List<String> result = Collections.emptyList();
		
		String title = getFullTitle(description);
		if (!Strings.isNullOrEmpty(title)) {
			String[] parts = title.split("[\\)\\(/\\\\]");
			result = Lists.newArrayList(parts);
		}
		
		return result;
	}
	
	private String getFullTitle(Element description) {
		Preconditions.checkArgument(description != null, PreconditionsHelper.CANT_BE_NULL, "FilmDescription element");
		
		String title = null;
		for (Element filmwebTitle : description.select(".hdr-medium")) {
			title = filmwebTitle.text();
			break;
		}
		return title;
	}
	
	private Double getScore(Element description) {
		Preconditions.checkArgument(description != null, PreconditionsHelper.CANT_BE_NULL, "FilmDescription element");
		
		Double score = null;
		for (Element filmwebRatio : description.select(".rateInfo strong")) {
			String textRatio = filmwebRatio.text();
			String[] ratioParsts = textRatio.split("/");
			if (ratioParsts.length == 2) {
				score = StringHelper.parseNumber(ratioParsts[0]) / StringHelper.parseNumber(ratioParsts[1]);
				break;
			} else {
				LOG.warn("getScore - Invalid ratio: " + textRatio);
			}
		}
		return score;
	}
	
	private String getURL(Element description) {
		Preconditions.checkArgument(description != null, PreconditionsHelper.CANT_BE_NULL, "FilmDescription element");
		
		String result = null;
		try {
			String domain = "http://www.filmweb.pl";
			for (Element filmwebTitle : description.select("a.hdr-medium")) {
				String href = "href";
				if (filmwebTitle.hasAttr(href)) {
					String location = filmwebTitle.attributes().get(href);
					URI uri = new URI(location);
					if (uri.isAbsolute()) {
						result = uri.toURL().toString();
					} else {
						result = new URL(domain + uri.toString()).toString();
					}
					break;
				}
			}
		} catch (MalformedURLException e) {
			LOG.warn("getURL - Invalid url address", e);
		} catch (URISyntaxException e) {
			LOG.warn("getURL - Invalid url address", e);
		}
		return result;
	}
}

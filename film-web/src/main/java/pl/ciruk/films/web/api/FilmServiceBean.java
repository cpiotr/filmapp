package pl.ciruk.films.web.api;

import java.io.File;
import java.util.Collections;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pl.ciruk.film.utils.core.PreconditionsHelper;
import pl.ciruk.film.utils.domain.FilmDTO;
import pl.ciruk.film.utils.file.FilmListParser;
import pl.ciruk.film.utils.web.FilmwebDescription;
import pl.ciruk.film.utils.web.FilmwebParser;
import pl.ciruk.films.web.adapter.FilmAdapter;
import pl.ciruk.films.web.entity.Film;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

@LocalBean
@Stateless
public class FilmServiceBean {
	private static final Log LOG = LogFactory.getLog(FilmServiceBean.class);
	
	@PersistenceContext
	EntityManager em;
	
	private FilmwebParser filmwebParser = new FilmwebParser();
	
	@SuppressWarnings("unchecked")
	public List<Film> find() {
		LOG.info("find()");
		
		return em.createNamedQuery(Film.Query.GET_ALL).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Film> find(FilmSearchCriteria criteria) {
		LOG.info("find");
		LOG.debug("find - Criteria: " + criteria);
		
		StringBuilder queryBuilder = new StringBuilder();
		List<Object> queryParams = Lists.newArrayList();
		queryBuilder.append("select f from ").append(Film.class.getSimpleName()).append(" f ");
		if (criteria != null && !criteria.isEmpty()) {
			queryBuilder.append("where 1=1");
			
			if (!Strings.isNullOrEmpty(criteria.getTitle())) {
				queryBuilder.append("and f.title like ?" + (queryParams.size() + 1) + " ");
				queryParams.add(criteria.getTitle() + "%");
			}
			
			if (!criteria.getTypes().isEmpty()) {
				queryBuilder.append("and f.type in ?" + (queryParams.size() + 1) + " ");
				queryParams.add(criteria.getTypes());
			}
			
			if (criteria.getAdditionDate() != null) {
				queryBuilder.append("and f.insertionDate >= ?" + (queryParams.size() + 1) + " ");
				queryParams.add(criteria.getAdditionDate());
			}
		}
		queryBuilder.append("order by f.title ");
		Query query = em.createQuery(queryBuilder.toString());
		for (int i = 0; i < queryParams.size(); i++) {
			query.setParameter(i+1, queryParams.get(i));
		}
		
		return query.getResultList();
	}

	public boolean save(Film film) {
		Preconditions.checkArgument(film != null, "Film cannot be null");
		
		LOG.info("save");
		LOG.info("save - Film: " + film);
		
		if (film.getId() != null) {
			em.merge(film);
		} else if (!exists(film)) {
			em.persist(film);
		} else {
			LOG.info("Film already exists");
		}
		
		return film.getId() != null;
	}
	
	private boolean exists(Film film) {
		Preconditions.checkArgument(film != null, PreconditionsHelper.CANT_BE_NULL, "Film");
		
		boolean result = false;
		
		Query query = em.createNamedQuery(Film.Query.BY_TITLE_AND_LABEL).setParameter("title", film.getTitle()).setParameter("label", film.getLabel());
		@SuppressWarnings("unchecked")
		List<Film> films = query.getResultList();
		result = !films.isEmpty();
		
		return result;
	}

	public boolean remove(Film film) {
		Preconditions.checkArgument(film != null, PreconditionsHelper.CANT_BE_NULL, "Film");
		
		LOG.info("remove");
		LOG.debug("remove - Film: " + film);
		
		boolean removed = false;
		if (film.getId() != null) {
			em.remove(em.find(Film.class, film.getId()));
			removed = em.find(Film.class, film.getId()) == null;
		}
		
		return removed;
	}
	
	public void removeDuplicates() {
		LOG.info("removeDuplicates");
		
		List<?> ids = em.createQuery("select min(f.id) from Film f group by f.title, f.label").getResultList();
		
		@SuppressWarnings("unchecked")
		List<Film> unique = em.createQuery("select f from Film f where f.id IN :ids").setParameter("ids", ids).getResultList();
		LOG.info("removeDuplicates - Unique films number: " + unique.size());
		
		int removedNumber = 0;
		
		for (Film film : unique) {
			Query query = em.createQuery("select f from Film f where f.title = :title and f.label = :label and f.id != :id")
					.setParameter("title", film.getTitle())
					.setParameter("label", film.getLabel())
					.setParameter("id", film.getId());
			
			@SuppressWarnings("unchecked")
			List<Film> duplicates = query.getResultList();
			
			for (Film duplicate : duplicates) {
				boolean removed = remove(duplicate);
				if (removed) {
					removedNumber++;
				}
			}
		}
		
		LOG.info("removeDuplicates - Number of removed films: " + removedNumber);
	}

	public void updateWithListFile(File filmListFile) {
		if (filmListFile != null && filmListFile.isFile()) {
			FilmListParser parser = new FilmListParser();
			List<FilmDTO> filmList = parser.parseToList(filmListFile);
				
			for (FilmDTO dto : filmList) {
				try {
					save(FilmAdapter.fromDTO(dto));
				} catch (Exception e) {
					LOG.error("updateWithListFile - An error occurred while saving film: " + dto.getTitle());
				}
			}
		} else {
			LOG.error("updateWithListFile - FilmListFile does not exist");
		}
	}

	public FilmwebDescription getDescrption(Film film) {
		Preconditions.checkArgument(film != null, PreconditionsHelper.CANT_BE_NULL, "Film");

		FilmwebDescription description = null;
		
		if (!Strings.isNullOrEmpty(film.getLabel())) {
			List<FilmwebDescription> filmwebDescriptions = null;
			
			List<String> actors = retrieveActors(film);
			if (actors.isEmpty()) {
				filmwebDescriptions = filmwebParser.find(film.getTitle());
			} else {
				filmwebDescriptions = filmwebParser.find(film.getTitle(), actors);
			}
			
			LOG.debug("getDescription - Result: " + filmwebDescriptions);
			if (!filmwebDescriptions.isEmpty()) {
				description = filmwebDescriptions.get(0);
			}
		}
		
		return description;
	}
	
	private List<String> retrieveActors(Film film) {
		Preconditions.checkArgument(film != null, PreconditionsHelper.CANT_BE_NULL, "Film");
		
		List<String> actors = null;
		
		int from = film.getLabel().lastIndexOf("(") + 1;
		if (from > 0 && from < film.getLabel().length()-1) {
			int to = film.getLabel().lastIndexOf(")");
			if (to < from) {
				to = film.getLabel().length();
			}
			actors = Lists.newArrayList(film.getLabel().substring(from, to).split(","));
		} else {
			actors = Collections.emptyList();
		}
		
		return actors;
	}
}

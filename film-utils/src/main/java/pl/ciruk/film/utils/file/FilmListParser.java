package pl.ciruk.film.utils.file;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import pl.ciruk.film.utils.core.StringHelper;
import pl.ciruk.film.utils.domain.FilmCategory;
import pl.ciruk.film.utils.domain.FilmDTO;

import com.google.common.collect.Lists;

public class FilmListParser {
	private static final Logger LOG = Logger.getLogger(FilmListParser.class);
	
	/**
	 * Domyslny format pojedynczej linii zwracanego tekstu. <br/>
	 * Jako parametry podawane sa: TYTUL, PELNY_OPIS, KATEGORIA, DATA. Wszystkie dane w postaci tekstu.
	 */
	public static final String DEFAULT_OUTPUT = "%s~%s~%s~%s";
	
	/** Domyslny separator danych. */
	public static final String DEFAULT_DELIMITER = "~";
	
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
	
	private String outputFormat;
	
	private ContentExtractor extractor = new ContentExtractor();
	
	public FilmListParser() {
		this(null);
	}
	
	public FilmListParser(String outputFormat) {
		this.outputFormat = outputFormat == null ? DEFAULT_OUTPUT : outputFormat;
	}
	
	public String parseToString(File file) {
		String content = extractor.getContent(file);
		
		StringBuffer buffer = new StringBuffer();
		
		for (FilmDTO dto : getFilms(content, new Date(file.lastModified()))) {
			buffer.append(String.format(outputFormat, dto.getTitle(), dto.getLabel(), dto.getCategory().name(), DATE_FORMAT.format(dto.getInsertionDate())));
			buffer.append('\n');
		}
		
		return buffer.toString();
	}
	
	public List<FilmDTO> parseToList(File file) {
		return getFilms(extractor.getContent(file), new Date(file.lastModified()));
	}
	
	private List<FilmDTO> getFilms(String content, Date fileModificationDate) {
		String[] lines = content.split("\r?\n");
		
		List<FilmDTO> films = Lists.newArrayListWithExpectedSize(lines.length);
		
		for (String line : lines) {
			String trimmed = line.trim();
			if (!trimmed.isEmpty()) {
				if (trimmed.contains("-") || trimmed.contains("–")) {
					
					String[] parts = trimmed.split("[-–]");
					String title = parts[0].trim();
					FilmCategory category = (isMusic(trimmed)) ? FilmCategory.MUSIC : (isCartoon(trimmed) ? FilmCategory.CARTOON : FilmCategory.FILM);
					
					films.add(new FilmDTO(title, trimmed, category, fileModificationDate));
					
				} else {
					LOG.debug("processContent - Current line does not contain standard delimiter: " + trimmed);
				}
			}
		}
		
		return films;
	}	
	
	private boolean isMusic(String line) {
		return StringHelper.containsAny(FilmCategory.MUSIC.getSynonyms(), line);
	}
	
	private boolean isCartoon(String line) {
		return StringHelper.containsAny(FilmCategory.CARTOON.getSynonyms(), line);
	}
}

package pl.ciruk.film.utils.domain;

import java.util.Arrays;

/**
 * Kategoria pozycji filmowej.
 * @author piotrc
 *
 */
public enum FilmCategory {
	MUSIC("music", new String[] { "koncert", "teledysk", "film muzyczny" }),
	CARTOON("cartoon", new String[] { "animacja", "kreskówka" }),
	FILM("film", new String[] {});
	
	private String label;
	
	private String[] synonyms;
	
	private FilmCategory(String label, String[] synonyms) {
		this.label = label;
		this.synonyms = Arrays.copyOf(synonyms, synonyms.length);
	}
	
	public String getLabel() {
		return label;
	}
	
	public String[] getSynonyms() {
		return synonyms;
	}
}

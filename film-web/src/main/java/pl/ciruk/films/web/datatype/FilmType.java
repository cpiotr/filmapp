package pl.ciruk.films.web.datatype;

import com.google.common.base.Objects;

public enum FilmType {
	/** Film. */
	F("film"),
	
	/** Bajka. */
	C("cartoon"),
	
	/** Muzyka. */
	M("music");
	
	private String label;
	
	private FilmType(String label) {
		this.label = label;
	}
	
	public static FilmType getByLabel(String label) {
		for (FilmType type : values()) {
			if (Objects.equal(type.label, label)) {
				return type;
			}
		}
		
		return null;
	}
	
	public String getLabel() {
		return label;
	}
}

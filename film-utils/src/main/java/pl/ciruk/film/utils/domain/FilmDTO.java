package pl.ciruk.film.utils.domain;

import java.util.Date;

public class FilmDTO {
	private String title;
	
	private String label;
	
	private FilmCategory category;
	
	private Date insertionDate;

	public FilmDTO(String title, String label, FilmCategory category, Date insertionDate) {
		this.title = title;
		this.label = label;
		this.category = category;
		this.insertionDate = insertionDate;
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %s)", title, label);
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public FilmCategory getCategory() {
		return category;
	}

	public void setCategory(FilmCategory category) {
		this.category = category;
	}

	public Date getInsertionDate() {
		return insertionDate;
	}

	public void setInsertionDate(Date insertionDate) {
		this.insertionDate = insertionDate;
	}
}

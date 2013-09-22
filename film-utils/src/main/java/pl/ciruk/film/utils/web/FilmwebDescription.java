package pl.ciruk.film.utils.web;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class FilmwebDescription {
	private String title;
	
	private String url;
	
	private Double score;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}
}

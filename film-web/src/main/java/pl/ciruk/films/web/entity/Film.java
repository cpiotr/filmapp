package pl.ciruk.films.web.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import pl.ciruk.films.web.datatype.FilmType;

@Entity
@Table(name="fb_film")
@NamedQueries({
	@NamedQuery(name=Film.Query.BY_TITLE_AND_LABEL, query="select f from Film f where f.title = :title and f.label = :label"),
	@NamedQuery(name=Film.Query.GET_ALL, query="select f from Film f"),
	@NamedQuery(name=Film.Query.GET_UNIQUE, query="select min(f.id) as id, f.title, f.label, f.insertionDate, f.type from Film f group by f.title, f.label")
})
public class Film extends BaseEntity {

	/** */
	private static final long serialVersionUID = 5625979730267056945L;
	
	public static class Query {
		/** Pobiera filmy o podanym tekscie i tytule. <br/> Paramtery: {@code title} oraz {@code label}. */
		public static final String BY_TITLE_AND_LABEL = "Film.Get";
		
		/** Pobiera wszystkie filmy. */
		public static final String GET_ALL = "Film.GetAll";
		
		/** Pobiera liste niepowtarzajacych sie rekordow. */
		public static final String GET_UNIQUE = "Film.GetUnique";
	}
	
	@Column(name="film_title")
	private String title;
	
	@Column(name="film_text")
	private String label;
	
	@Column(name="film_insert_date")
	@Temporal(TemporalType.DATE)
	private Date insertionDate;
	
	@Column(name="film_genre")
	@Enumerated(EnumType.STRING)
	private FilmType type;
	
	@ManyToMany(mappedBy="films", cascade=CascadeType.ALL)
	private List<User> users;
	
	@Override
	public String toString() {
		return String.format("Film(%s; %s)", title, label);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof Film)) {
			return false;
		}
		Film other = (Film) obj;
		if (label == null) {
			if (other.label != null) {
				return false;
			}
		} else if (!label.equals(other.label)) {
			return false;
		}
		if (title == null) {
			if (other.title != null) {
				return false;
			}
		} else if (!title.equals(other.title)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
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

	public Date getInsertionDate() {
		return insertionDate;
	}

	public void setInsertionDate(Date insertionDate) {
		this.insertionDate = insertionDate;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public FilmType getType() {
		return type;
	}

	public void setType(FilmType type) {
		this.type = type;
	}
	
	
}

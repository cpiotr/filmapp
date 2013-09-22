package pl.ciruk.films.web.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name="fb_user")
public class User extends BaseEntity {

	/** */
	private static final long serialVersionUID = -664268177259737249L;

	@Column(name="usr_login")
	private String login;
	
	@ManyToMany(fetch=FetchType.LAZY, cascade={CascadeType.ALL})
	@JoinTable(name="fb_user_film", 
		joinColumns={@JoinColumn(name="usr_fk", referencedColumnName="id")},
		inverseJoinColumns={@JoinColumn(name="film_fk", referencedColumnName="id")})
	private
	List<Film> films;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((login == null) ? 0 : login.hashCode());
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
		if (!(obj instanceof User)) {
			return false;
		}
		User other = (User) obj;
		if (login == null) {
			if (other.login != null) {
				return false;
			}
		} else if (!login.equals(other.login)) {
			return false;
		}
		return true;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public List<Film> getFilms() {
		return films;
	}

	public void setFilms(List<Film> films) {
		this.films = films;
	}
}

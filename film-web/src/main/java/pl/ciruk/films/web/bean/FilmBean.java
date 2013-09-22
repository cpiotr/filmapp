package pl.ciruk.films.web.bean;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import pl.ciruk.film.utils.web.FilmwebDescription;
import pl.ciruk.films.web.api.FilmServiceBean;
import pl.ciruk.films.web.entity.Film;

@Named(value="filmBean")
//@ManagedBean(name="filmBean")
@SessionScoped
public class FilmBean implements Serializable {
	/** */
	private static final long serialVersionUID = 3343180246659294215L;

	private static final Logger LOG = Logger.getLogger(FilmBean.class);
	
	@Inject
	private FilmServiceBean service;
	
	@Inject
	private FilmListBean filmList;
	
	private Film film;
	
	private FilmwebDescription description;
	
	public void remove() {
		LOG.info("remove");
		LOG.debug("remove - Film: " + film);
		
		if (film != null) {
			service.remove(getFilm());
			filmList.refresh();
		} else {
			LOG.warn("remove - Film is null");
		}
	}
	
	public void save() {
		LOG.info("save");
		LOG.debug("save - Film: " + film);
		
		if (film != null) {
			service.save(getFilm());
			filmList.refresh();
		} else {
			LOG.warn("save - Film is null");
		}
	}
	
	public void view() {
		LOG.info("view");
		LOG.info("view - Film: " + film);
		
		if (film != null) {
			setDescription(service.getDescrption(film));
		} else {
			LOG.warn("view - No film was selected");
		}
	}

	public Film getFilm() {
		return film;
	}

	public void setFilm(Film film) {
		this.film = film;
	}

	public FilmwebDescription getDescription() {
		return description;
	}

	public void setDescription(FilmwebDescription description) {
		this.description = description;
	}

}

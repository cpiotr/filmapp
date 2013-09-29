package pl.ciruk.films.web.api;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pl.ciruk.films.web.entity.Film;

@Singleton
@Startup
public class StartupBean {
	private static final Log LOG = LogFactory.getLog(StartupBean.class);
	
	@EJB
	private FilmServiceBean filmService;
	
	@PostConstruct
	void init() {
		filmService.removeDuplicates();
	}
}

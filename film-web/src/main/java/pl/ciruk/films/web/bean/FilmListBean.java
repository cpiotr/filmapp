package pl.ciruk.films.web.bean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import pl.ciruk.films.web.api.FilmSearchCriteria;
import pl.ciruk.films.web.api.FilmServiceBean;
import pl.ciruk.films.web.entity.Film;

@Named(value="filmListBean")
//@ManagedBean(name="filmListBean")
@SessionScoped
public class FilmListBean implements Serializable {
	/** */
	private static final long serialVersionUID = 371728849250989646L;

	private static final Logger LOG = Logger.getLogger(FilmListBean.class);
	
	private static final String UPLOAD_DIR = "D:\\tmp";
	
	@Inject
	private FilmServiceBean service;
	
	private List<Film> films;
	
	private String title;
	
	private Date insertionDate;
	
	public void refresh() {
		LOG.info("refresh");
		
		search();
	}
	
	public void handleFileUpload(FileUploadEvent event) {  
        LOG.info("handleFileUpload");
		
		File file = null;
		try {
			file = createFile(event.getFile());
			
			service.updateWithListFile(file);
			
			FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");  
	        FacesContext.getCurrentInstance().addMessage(null, msg);
		} catch (Exception e) {
			LOG.error("handleFileUpload", e);
		}
    }
	
	private File createFile(UploadedFile uploadedFile) throws IOException {
		LOG.info("createFile");
		
		File file = null;
		
		if (uploadedFile != null) {
			File dir = new File(UPLOAD_DIR);
			if (!dir.isDirectory()) {
				dir.mkdirs();
			}
			file = new File(UPLOAD_DIR, uploadedFile.getFileName());
			
			BufferedOutputStream outputStream = null;
			BufferedInputStream inputStream = null;
			try {
				inputStream = new BufferedInputStream(uploadedFile.getInputstream());
				outputStream = new BufferedOutputStream(new FileOutputStream(file));
				
				byte[] buffer = new byte[1024];
				int length = -1;
				while ((length = inputStream.read(buffer)) > -1) {
					outputStream.write(buffer, 0, length);
				}
				
				outputStream.flush();
			} finally {
				if (inputStream != null) {
					inputStream.close();
				}
				if (outputStream != null) {
					outputStream.close();
				}
			}
		}
		
		return file;
	}
	
	public String search() {
		LOG.info("search");
		
		FilmSearchCriteria criteria = new FilmSearchCriteria();
		criteria.setTitle(title);
		criteria.setAdditionDate(insertionDate);
		films = service.find(criteria); 
		return null;
	}
	
	public List<Film> getFilms() {
		return films;
	}

	public void setFilms(List<Film> films) {
		this.films = films;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getInsertionDate() {
		return insertionDate;
	}

	public void setInsertionDate(Date insertionDate) {
		this.insertionDate = insertionDate;
	}
}

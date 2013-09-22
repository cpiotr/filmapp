package pl.ciruk.films.web.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import pl.ciruk.films.web.datatype.FilmSortColumn;
import pl.ciruk.films.web.datatype.FilmType;

import com.google.common.base.Strings;

public class FilmSearchCriteria implements Serializable {

	/**  */
	private static final long serialVersionUID = -3586200660901487305L;

	private String title;
	
	private List<FilmType> types;
	
	private Date additionDate;
	
	private FilmSortColumn sortColumn = FilmSortColumn.TITLE;
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public boolean isEmpty() {
		return Strings.isNullOrEmpty(title) && getTypes().isEmpty() && additionDate == null;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<FilmType> getTypes() {
		if (types == null) {
			types = new ArrayList<FilmType>();
		}
		return types;
	}

	public void setTypes(List<FilmType> types) {
		this.types = types;
	}

	public Date getAdditionDate() {
		return additionDate;
	}

	public void setAdditionDate(Date additionDate) {
		this.additionDate = additionDate;
	}

	public FilmSortColumn getSortColumn() {
		return sortColumn;
	}

	public void setSortColumn(FilmSortColumn sortColumn) {
		this.sortColumn = sortColumn;
	}
}

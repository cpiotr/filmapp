package pl.ciruk.films.web.adapter;

import pl.ciruk.film.utils.domain.FilmDTO;
import pl.ciruk.films.web.datatype.FilmType;
import pl.ciruk.films.web.entity.Film;

public final class FilmAdapter {
	private FilmAdapter() {
		
	}
	
	public static Film fromDTO(FilmDTO dto) {
		Film film = new Film();
		film.setInsertionDate(dto.getInsertionDate());
		film.setLabel(dto.getLabel());
		film.setTitle(dto.getTitle());
		film.setType(FilmType.getByLabel(dto.getCategory().getLabel()));
		return film;
	}
}

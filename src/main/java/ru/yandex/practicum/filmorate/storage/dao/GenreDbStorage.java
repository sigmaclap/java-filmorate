package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
public class GenreDbStorage implements GenreStorage {

    private static final String GENRE_ID_COLUMN = "GENRE_ID";
    private static final String NAME_COLUMN = "NAME";

    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Genre getGenreById(Integer genreId) {
        SqlRowSet genreRow = jdbcTemplate.queryForRowSet("SELECT * FROM GENRE g WHERE GENRE_ID = ?", genreId);
        if (genreRow.next()) {
            return new Genre(
                    genreRow.getInt(GENRE_ID_COLUMN),
                    genreRow.getString(NAME_COLUMN));
        } else {
            log.info("Жанр с идентификатором {} не найден.", genreId);
            throw new FilmNotFoundException("Пустое значение, Жанр не найден");
        }
    }

    public List<Genre> getGenresList() {
        String sqlQuery = "SELECT * FROM GENRE g ORDER BY GENRE_ID ASC";
        return jdbcTemplate.query(sqlQuery, this::makeGenres);
    }

    public Genre makeGenre(ResultSet rs) throws SQLException {
        return new Genre(
                rs.getInt(GENRE_ID_COLUMN),
                rs.getString(NAME_COLUMN));
    }

    public Genre makeGenres(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(
                rs.getInt(GENRE_ID_COLUMN),
                rs.getString(NAME_COLUMN));
    }
}

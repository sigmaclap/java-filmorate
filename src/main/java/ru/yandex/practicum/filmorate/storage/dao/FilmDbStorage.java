package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.constants.SQLScripts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;


    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getFilms() {
        String sqlQuery = SQLScripts.GET_ALL_FILMS;
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);

    }

    @Override
    public Film findFilmById(Integer filmId) {
        String sqlQuery = SQLScripts.GET_FILM_WITH_ID;
        List<Film> listFilms = getFilms();
        if (listFilms.stream().noneMatch(film -> film.getId().equals(filmId))) {
            log.info("Фильм с идентификатором {} не найден.", filmId);
            throw new FilmNotFoundException("Пустое значение, Фильм не создан");
        }
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, filmId);
    }

    @Override
    public Film create(Film film) {
        String sqlQuery = SQLScripts.ADD_NEW_FILM;
        jdbcTemplate.update(sqlQuery, film.getMpa().getId(), film.getName(),
                film.getDescription(), film.getReleaseDate(), film.getDuration());

        SqlRowSet filmRow = jdbcTemplate.queryForRowSet(SQLScripts.GET_FILM_WITH_RATING_NAME, film.getName());
        if (filmRow.next()) {
            int idFilm = filmRow.getInt("FILM_ID");
            updateFilmGenres(film, idFilm);
            return Film.builder()
                    .id(filmRow.getInt("FILM_ID"))
                    .name(filmRow.getString("NAME"))
                    .description(filmRow.getString("DESCRIPTION"))
                    .releaseDate(Objects.requireNonNull(filmRow.getDate("RELEASE_DATE").toLocalDate()))
                    .duration(filmRow.getInt("DURATION"))
                    .mpa(Mpa.builder().id(filmRow.getInt("RATING_ID"))
                            .name(filmRow.getString("R_NAME"))
                            .build())
                    .genres(film.getGenres())
                    .build();
        } else {
            log.info("Фильм с именем {} не найден.", film.getName());
            throw new FilmNotFoundException("Пустое значение, Фильм не создан");
        }
    }

    private void updateFilmGenres(Film film, Integer filmId) {
        jdbcTemplate.update(SQLScripts.DELETE_FILMS_CATEGORY, film.getId());
        if (film.getGenres() != null) {
            List<Integer> genresId = film.getGenres().stream().map(Genre::getId).collect(Collectors.toList());
            if (!genresId.isEmpty()) {
                String genres = genresId.stream()
                        .map(id -> String.format("(%d,%d)", filmId, id)).collect(Collectors.joining(","));
                jdbcTemplate.update(SQLScripts.INSERT_GENRE_ID + genres);
                String sql2 = SQLScripts.GET_GENRE_ID_WITH_SORT;
                film.setGenres(new HashSet<>(jdbcTemplate.query(sql2, (rs, rowNum) -> makeGenre(rs),
                        filmId)));
            }
        } else {
            film.setGenres(Collections.emptySet());
        }
    }

    @Override
    public Film update(Film film) {
        Integer mpaId;
        if (film.getMpa() != null) {
            mpaId = film.getMpa().getId();
        } else {
            mpaId = null;
        }
        String sqlUpdateFilm = SQLScripts.UPDATE_FILM_SET;
        jdbcTemplate.update(sqlUpdateFilm, mpaId, film.getName(),
                film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getId());
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SQLScripts.GET_FILM_WITH_FILM_ID, film.getId());
        if (rs.next()) {
            int idFilm = rs.getInt("FILM_ID");
            updateFilmGenres(film, idFilm);
            return Film.builder()
                    .id(rs.getInt("FILM_ID"))
                    .mpa(Mpa.builder().id(rs.getInt("RATING_ID"))
                            .name(rs.getString("R_NAME"))
                            .build())
                    .name(rs.getString("NAME"))
                    .description(rs.getString("DESCRIPTION"))
                    .releaseDate(Objects.requireNonNull(rs.getDate("RELEASE_DATE").toLocalDate()))
                    .duration(rs.getInt("DURATION"))
                    .genres(film.getGenres())
                    .build();
        } else {
            log.info("Фильм с идентификатором {} не найден.", film.getId());
            throw new FilmNotFoundException("Пустое значение, Фильм не создан");
        }
    }

    @Override
    public List<Film> getFilmsWithCountLikes(Integer count) {
        String sqlQuery = SQLScripts.GET_FILMS_WITH_COUNT_LIKES;
        if (count == null) {
            count = 10;
            return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
        } else {
            return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
        }
    }

    @Override
    public boolean likeFilm(Integer filmId, Integer userId) {
        String sqlQuery = SQLScripts.INSERT_USER_LIKE_ON_FILM;
        if (jdbcTemplate.update(sqlQuery, filmId, userId) > 0) {
            log.info("Лайк от пользователя {} на фильм {} успешно добавлен", userId, filmId);
            return true;
        } else {
            log.info("Пользователь с идентификатором {} не найден.", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    @Override
    public boolean deleteLike(Integer filmId, Integer userId) {
        String sqlQuery = SQLScripts.DELETE_USER_LIKE_ON_FILM;
        if (jdbcTemplate.update(sqlQuery, filmId, userId) > 0) {
            log.info("Лайк от пользователя {} на фильм {} успешно удален", userId, filmId);
            return true;
        } else {
            log.info("Пользователь с идентификатором {} не найден.", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    public Genre getGenreById(Integer genreId) {
        SqlRowSet genreRow = jdbcTemplate.queryForRowSet("SELECT * FROM GENRE g WHERE GENRE_ID = ?", genreId);
        if (genreRow.next()) {
            return new Genre(
                    genreRow.getInt("GENRE_ID"),
                    genreRow.getString("NAME"));
        } else {
            log.info("Жанр с идентификатором {} не найден.", genreId);
            throw new FilmNotFoundException("Пустое значение, Жанр не найден");
        }
    }

    public List<Genre> getGenresList() {
        String sqlQuery = "SELECT * FROM GENRE g ORDER BY GENRE_ID ASC";
        return jdbcTemplate.query(sqlQuery, this::makeGenres);
    }

    public List<Mpa> getMpaList() {
        return jdbcTemplate.query("SELECT * FROM FILMS_RATINGS fr ORDER BY RATING_ID ASC", this::makeMpas);
    }

    public Mpa getMpaById(Integer ratingId) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM PUBLIC.FILMS_RATINGS WHERE RATING_ID = ?"
                , ratingId);
        if (mpaRows.next()) {
            return Mpa.builder()
                    .id(mpaRows.getInt("RATING_ID"))
                    .name(mpaRows.getString("NAME"))
                    .build();
        } else {
            log.info("Рейтинг МРА с идентификатором {} не найден.", ratingId);
            throw new FilmNotFoundException("Рейтинг МРА не найден");
        }
    }

    public Genre makeGenre(ResultSet rs) throws SQLException {
        return new Genre(
                rs.getInt("GENRE_ID"),
                rs.getString("NAME"));
    }

    public Genre makeGenres(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(
                rs.getInt("GENRE_ID"),
                rs.getString("NAME"));
    }

    public Mpa makeMpa(ResultSet rs) throws SQLException {
        return new Mpa(
                rs.getInt("RATING_ID"),
                rs.getString("NAME"));
    }

    public Mpa makeMpas(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(
                rs.getInt("RATING_ID"),
                rs.getString("NAME"));
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(rs.getInt("FILM_ID"))
                .mpa(Mpa.builder().id(rs.getInt("RATING_ID"))
                        .name(rs.getString("R_NAME"))
                        .build())
                .name(rs.getString("NAME"))
                .description(rs.getString("DESCRIPTION"))
                .releaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
                .duration(rs.getInt("DURATION"))
                .build();

        SqlRowSet rset = jdbcTemplate.queryForRowSet(SQLScripts.GET_FILM_ID_WITH_GENRE
                , rs.getInt("FILM_ID"));
        film.setGenres(new HashSet<>());
        while (rset.next()) {
            int id = rset.getInt("GENRE_ID");
            if (id != 0) {
                Genre genre = Genre.builder()
                        .id(id)
                        .name(rset.getString("name"))
                        .build();
                film.getGenres().add(genre);
            } else {
                film.setGenres(new HashSet<>());
            }
        }
        return film;
    }
}

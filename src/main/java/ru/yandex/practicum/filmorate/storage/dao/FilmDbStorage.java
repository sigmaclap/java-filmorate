package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.InvalidDataException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.constants.SQLScripts;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private static final String FILM_ID_COLUMN = "FILM_ID";
    private static final String NAME_COLUMN = "NAME";
    private static final String DESCRIPTION_COLUMN = "DESCRIPTION";
    private static final String RELEASE_DATE_COLUMN = "RELEASE_DATE";
    private static final String DURATION_COLUMN = "DURATION";
    private static final String RATING_ID_COLUMN = "RATING_ID";
    private static final String RATING_NAME_COLUMN = "R_NAME";
    private static final String GENRE_ID_COLUMN = "GENRE_ID";
    private static final String GENRE_NAME_COLUMN = "NAME";
    private static final String ERROR_EMPTY_FILM_VALUE = "Пустое значение, Фильм не создан";
    private static final String ERROR_EMPTY_USER_VALUE = "Пользователь не найден";
    private static final String TITLE_PROPERTY = "title";
    private static final String DIRECTOR_PROPERTY = "director";
    private static final String ERROR_USER_ID_NOT_FOUND = "Пользователь с идентификатором {} не найден.";

    private final JdbcTemplate jdbcTemplate;

    private final GenreDbStorage genreDbStorage;
    private final DirectorDbStorage directorStorage;


    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreDbStorage genreDbStorage, DirectorDbStorage directorStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDbStorage = genreDbStorage;
        this.directorStorage = directorStorage;
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
        boolean isFilmExists = listFilms.stream().noneMatch(film -> film.getId().equals(filmId));
        if (isFilmExists) {
            log.info("Фильм с идентификатором {} не найден.", filmId);
            throw new FilmNotFoundException(ERROR_EMPTY_FILM_VALUE);
        }
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, filmId);
    }

    @Override
    public Film create(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(SQLScripts.ADD_NEW_FILM, new String[]{FILM_ID_COLUMN});
            ps.setInt(1, film.getMpa().getId());
            ps.setString(2, film.getName());
            ps.setString(3, film.getDescription());
            ps.setDate(4, Date.valueOf(film.getReleaseDate()));
            ps.setLong(5, film.getDuration());
            return ps;
        }, keyHolder);
        int idFilm = Objects.requireNonNull(keyHolder.getKey()).intValue();
        updateFilmGenres(film, idFilm);
        updateDirectors(film, idFilm);
        SqlRowSet filmRow = jdbcTemplate.queryForRowSet(SQLScripts.GET_FILM_WITH_RATING_ID, idFilm);
        if (filmRow.next()) {
            film.setMpa(Mpa.builder().id(filmRow.getInt(RATING_ID_COLUMN))
                    .name(filmRow.getString(RATING_NAME_COLUMN))
                    .build());
        }
        film.setId(idFilm);
        return film;
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
                film.setGenres(new HashSet<>(jdbcTemplate.query(sql2, (rs, rowNum) -> genreDbStorage.makeGenre(rs),
                        filmId)));
            }
        } else {
            film.setGenres(Collections.emptySet());
        }
    }

    private void updateDirectors(Film film, Integer filmId) {
        if (film.getDirectors() != null) {
            List<Integer> directorsId = film.getDirectors().stream().map(Director::getId).collect(Collectors.toList());
            if (!directorsId.isEmpty()) {
                String directors = directorsId.stream()
                        .map(id -> String.format("(%d,%d)", filmId, id)).collect(Collectors.joining(","));
                for (Integer directorIds : directorsId) {
                    if (directorStorage.getDirectorById(directorIds) == null) {
                        log.info("Режиссер c идентификатором не существует {}.", directorIds);
                        throw new InvalidDataException("Режиссер с данным ID не существует");
                    }
                }
                jdbcTemplate.update("INSERT INTO PUBLIC.DIRECTOR_FILMS(FILM_ID, DIRECTOR_ID) VALUES" + directors);
                film.setDirectors(new ArrayList<>(jdbcTemplate.query(SQLScripts.GET_DIRECTOR_ID_FOR_UPDATE,
                        (rs, rowNum) -> directorStorage.makeDir(rs),
                        filmId)));
            }
        } else {
            jdbcTemplate.update("DELETE FROM DIRECTOR_FILMS WHERE FILM_ID = ?", filmId);
            film.setDirectors(Collections.emptyList());
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
            int idFilm = rs.getInt(FILM_ID_COLUMN);
            updateFilmGenres(film, idFilm);
            updateDirectors(film, idFilm);
            return Film.builder()
                    .id(rs.getInt(FILM_ID_COLUMN))
                    .mpa(Mpa.builder().id(rs.getInt(RATING_ID_COLUMN))
                            .name(rs.getString(RATING_NAME_COLUMN))
                            .build())
                    .name(rs.getString(NAME_COLUMN))
                    .description(rs.getString(DESCRIPTION_COLUMN))
                    .releaseDate(Objects.requireNonNull(rs.getDate(RELEASE_DATE_COLUMN).toLocalDate()))
                    .duration(rs.getInt(DURATION_COLUMN))
                    .genres(film.getGenres())
                    .directors(film.getDirectors())
                    .build();
        } else {
            log.info("Фильм с идентификатором {} не найден.", film.getId());
            throw new FilmNotFoundException(ERROR_EMPTY_FILM_VALUE);
        }
    }

    @Override
    public List<Film> getMostPopularFilms(Integer count, Integer genreId, Integer year) {
        if (genreId == 0 && year == 0) {
            String sqlQuery = SQLScripts.GET_MOST_POPULAR_FILMS_WITHOUT_GENRES_AND_YEAR;
            return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count).stream()
                    .distinct().collect(Collectors.toList());
        } else if (genreId == 0) {
            String sqlQuery = SQLScripts.GET_MOST_POPULAR_FILMS_WITHOUT_GENRES_AND_WITH_YEAR;
            return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, year, count).stream()
                    .distinct().collect(Collectors.toList());
        } else if (year == 0) {
            String sqlQuery = SQLScripts.GET_MOST_POPULAR_FILMS_WITH_GENRES_AND_WITHOUT_YEAR;
            return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, genreId, count).stream()
                    .distinct().collect(Collectors.toList());
        } else {
            String sqlQuery = SQLScripts.GET_MOST_POPULAR_FILMS_WITH_GENRES_AND_YEAR;
            return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, genreId, year, count).stream()
                    .distinct().collect(Collectors.toList());
        }
    }

    @Override
    public boolean likeFilm(Integer filmId, Integer userId) {
        String sqlQuery = SQLScripts.INSERT_USER_LIKE_ON_FILM;
        if (jdbcTemplate.update(sqlQuery, filmId, userId) > 0) {
            log.info("Лайк от пользователя {} на фильм {} успешно добавлен", userId, filmId);
            return true;
        } else {
            log.info(ERROR_USER_ID_NOT_FOUND, userId);
            throw new UserNotFoundException(ERROR_EMPTY_USER_VALUE);
        }
    }

    @Override
    public boolean deleteLike(Integer filmId, Integer userId) {
        String sqlQuery = SQLScripts.DELETE_USER_LIKE_ON_FILM;
        if (jdbcTemplate.update(sqlQuery, filmId, userId) > 0) {
            log.info("Лайк от пользователя {} на фильм {} успешно удален", userId, filmId);
            return true;
        } else {
            log.info(ERROR_USER_ID_NOT_FOUND, userId);
            throw new UserNotFoundException(ERROR_EMPTY_USER_VALUE);
        }
    }

    @Override
    public boolean deleteFilm(Integer filmId) {
        return jdbcTemplate.update("DELETE FROM PUBLIC.FILMS WHERE FILM_ID=?", filmId) > 0;
    }

    @Override
    public List<Film> getFilmByTitleOrDirector(String queryName, String options) {
        String opt = options.replaceAll("\\s+", "");
        String[] optionsParam = opt.split(",");
        if (TITLE_PROPERTY.equals(options)) {
            String sqlQuery = SQLScripts.GET_LIKE_TITLE_PROPERTY;
            return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, queryName.toLowerCase());
        } else if (DIRECTOR_PROPERTY.equals(options)) {
            String sqlQuery = SQLScripts.GET_LIKE_DIRECTOR_PROPERTY;
            return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, queryName.toLowerCase());
        } else if (optionsParam.length == 2 &&
                ((optionsParam[0].equals(DIRECTOR_PROPERTY) && optionsParam[1].equals(TITLE_PROPERTY)) ||
                        (optionsParam[0].equals(TITLE_PROPERTY) && optionsParam[1].equals(DIRECTOR_PROPERTY)))) {
            String sqlQuery = SQLScripts.GET_LIKE_TITLE_AND_DIRECTOR_PROPERTY;
            return jdbcTemplate.query(con -> {
                PreparedStatement ps = con.prepareStatement(sqlQuery);
                ps.setString(1, queryName.toLowerCase());
                ps.setString(2, queryName.toLowerCase());
                ps.executeQuery();
                return ps;
            }, this::mapRowToFilm);
        } else {
            log.info("Фильм по подстроке {} не найден.", queryName);
            throw new FilmNotFoundException("Фильм не найден");
        }
    }

    @Override
    public List<Film> getFilmByDirector(Integer directorId, String sortBy) {
        if (directorStorage.getDirectorById(directorId) == null) {
            log.info("Режиссер по идентификатору {} не найден.", directorId);
            throw new FilmNotFoundException("Режиссер не найден");
        }
        if ("year".equals(sortBy)) {
            String sqlQuery = SQLScripts.GET_LIST_DIRECTOR_BY_YEAR;
            return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, directorId);
        } else if ("likes".equals(sortBy)) {
            String sqlQuery = SQLScripts.GET_LIST_DIRECTOR_BY_LIKE;
            return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, directorId);
        } else {
            log.info("Фильм по идентификатору {} не найден.", directorId);
            throw new FilmNotFoundException("Фильм не найден");
        }
    }

    @Override
    public List<Film> commonFilms(Integer userId, Integer friendId) {
        SqlRowSet userRow = jdbcTemplate.queryForRowSet(SQLScripts.GET_USER, userId);
        SqlRowSet friendRow = jdbcTemplate.queryForRowSet(SQLScripts.GET_USER, friendId);
        if (userRow.next() && friendRow.next()) {
            String sqlQuery = SQLScripts.GET_COMMON_FILMS_TWO_USERS;
            return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, userId, friendId);
        } else if (!friendRow.next()) {
            log.info("Друг пользователя с идентификатором {} не найден.", friendId);
            throw new UserNotFoundException("Друг не найден");
        } else {
            log.info(ERROR_USER_ID_NOT_FOUND, userId);
            throw new UserNotFoundException(ERROR_EMPTY_USER_VALUE);
        }
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(rs.getInt(FILM_ID_COLUMN))
                .mpa(Mpa.builder().id(rs.getInt(RATING_ID_COLUMN))
                        .name(rs.getString(RATING_NAME_COLUMN))
                        .build())
                .name(rs.getString(NAME_COLUMN))
                .description(rs.getString(DESCRIPTION_COLUMN))
                .releaseDate(rs.getDate(RELEASE_DATE_COLUMN).toLocalDate())
                .duration(rs.getInt(DURATION_COLUMN))
                .build();
        film.setDirectors(directorStorage.getDirectorsByFilmId(rs.getInt(FILM_ID_COLUMN)));
        SqlRowSet rset = jdbcTemplate.queryForRowSet(SQLScripts.GET_FILM_ID_WITH_GENRE, rs.getInt(FILM_ID_COLUMN));
        film.setGenres(new HashSet<>());
        while (rset.next()) {
            int id = rset.getInt(GENRE_ID_COLUMN);
            if (id != 0) {
                Genre genre = Genre.builder()
                        .id(id)
                        .name(rset.getString(GENRE_NAME_COLUMN))
                        .build();
                film.getGenres().add(genre);
            } else {
                film.setGenres(new HashSet<>());
            }
        }
        return film;
    }
}

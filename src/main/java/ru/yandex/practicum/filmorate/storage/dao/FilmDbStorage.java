package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.yandex.practicum.filmorate.service.enums.EventType;
import ru.yandex.practicum.filmorate.service.enums.OperationType;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
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
@RequiredArgsConstructor
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
    private final FeedStorage feedStorage;
    private final GenreDbStorage genreDbStorage;
    private final DirectorDbStorage directorStorage;


    @Override
    public List<Film> getFilms() {
        String sqlQuery = SQLScripts.GET_ALL_FILMS;
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);

    }

    @Override
    public Film findFilmById(Integer filmId) {
        String sqlQuery = SQLScripts.GET_FILM_WITH_ID;
        List<Film> listFilms = getFilms();
        boolean isFilmExists = listFilms.stream()
                .noneMatch(film -> film.getId().equals(filmId));
        if (isFilmExists) {
            log.error("Фильм с идентификатором {} не найден.", filmId);
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
        if (film.getGenres() == null) {
            film.setGenres(Collections.emptySet());
            return;
        }
        List<Integer> genresId = film.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toList());
        if (!genresId.isEmpty()) {
            String genres = genresId.stream()
                    .map(id -> String.format("(%d,%d)", filmId, id))
                    .collect(Collectors.joining(","));
            jdbcTemplate.update(SQLScripts.INSERT_GENRE_ID + genres);
            String sql2 = SQLScripts.GET_GENRE_ID_WITH_SORT;
            film.setGenres(new HashSet<>(jdbcTemplate.query(sql2, (rs, rowNum) -> genreDbStorage.makeGenre(rs),
                    filmId)));
        }
    }

    private void updateDirectors(Film film, Integer filmId) {
        if (film.getDirectors() == null) {
            jdbcTemplate.update("DELETE FROM DIRECTOR_FILMS WHERE FILM_ID = ?", filmId);
            film.setDirectors(Collections.emptyList());
            return;
        }
        List<Integer> directorsId = film.getDirectors().stream()
                .map(Director::getId)
                .collect(Collectors.toList());
        if (!directorsId.isEmpty()) {
            String directors = directorsId.stream()
                    .map(id -> String.format("(%d,%d)", filmId, id))
                    .collect(Collectors.joining(","));
            for (Integer directorIds : directorsId) {
                if (directorStorage.getDirectorById(directorIds) == null) {
                    log.error("Режиссер c идентификатором не существует {}.", directorIds);
                    throw new InvalidDataException("Режиссер с данным ID не существует");
                }
            }
            jdbcTemplate.update("INSERT INTO PUBLIC.DIRECTOR_FILMS(FILM_ID, DIRECTOR_ID) VALUES" + directors);
            film.setDirectors(new ArrayList<>(jdbcTemplate.query(SQLScripts.GET_DIRECTOR_ID_FOR_UPDATE,
                    (rs, rowNum) -> directorStorage.makeDir(rs),
                    filmId)));
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
            log.error("Фильм с идентификатором {} не найден.", film.getId());
            throw new FilmNotFoundException(ERROR_EMPTY_FILM_VALUE);
        }
    }

    @Override
    public List<Film> getMostPopularFilms(Integer count, Integer genreId, Integer year) {
        if (genreId == 0 && year == 0) {
            return getMostPopularFilms(count);
        } else if (genreId == 0) {
            return getMostPopularFilmsWithYear(count, year);
        } else if (year == 0) {
            return getMostPopularFilmsWithGenres(count, genreId);
        } else {
            return getMostPopularFilmsWithGenresAndYear(count, genreId, year);
        }
    }

    private List<Film> getMostPopularFilmsWithGenresAndYear(Integer count, Integer genreId, Integer year) {
        String sqlQuery = SQLScripts.GET_MOST_POPULAR_FILMS_WITH_GENRES_AND_YEAR;
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, genreId, year, count).stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Film> getMostPopularFilmsWithGenres(Integer count, Integer genreId) {
        String sqlQuery = SQLScripts.GET_MOST_POPULAR_FILMS_WITH_GENRES_AND_WITHOUT_YEAR;
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, genreId, count).stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Film> getMostPopularFilmsWithYear(Integer count, Integer year) {
        String sqlQuery = SQLScripts.GET_MOST_POPULAR_FILMS_WITHOUT_GENRES_AND_WITH_YEAR;
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, year, count).stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Film> getMostPopularFilms(Integer count) {
        String sqlQuery = SQLScripts.GET_MOST_POPULAR_FILMS_WITHOUT_GENRES_AND_YEAR;
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count).stream()
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public boolean likeFilm(Integer filmId, Integer userId) {
        String sqlQuery = SQLScripts.INSERT_USER_LIKE_ON_FILM;
        if (jdbcTemplate.update(sqlQuery, filmId, userId) > 0) {
            feedStorage.addFeed(userId, filmId, EventType.LIKE, OperationType.ADD);
            log.info("Лайк от пользователя {} на фильм {} успешно добавлен", userId, filmId);
            return true;
        } else {
            log.error(ERROR_USER_ID_NOT_FOUND, userId);
            throw new UserNotFoundException(ERROR_EMPTY_USER_VALUE);
        }
    }

    @Override
    public boolean deleteLike(Integer filmId, Integer userId) {
        String sqlQuery = SQLScripts.DELETE_USER_LIKE_ON_FILM;
        if (jdbcTemplate.update(sqlQuery, filmId, userId) > 0) {
            feedStorage.addFeed(userId, filmId, EventType.LIKE, OperationType.REMOVE);
            log.info("Лайк от пользователя {} на фильм {} успешно удален", userId, filmId);
            return true;
        } else {
            log.error(ERROR_USER_ID_NOT_FOUND, userId);
            throw new UserNotFoundException(ERROR_EMPTY_USER_VALUE);
        }
    }

    @Override
    public boolean deleteFilm(Integer filmId) {
        return jdbcTemplate.update("DELETE FROM PUBLIC.FILMS WHERE FILM_ID=?", filmId) > 0;
    }

    @Override
    public List<Film> getFilmByTitleOrDirector(String queryName, String options) {
        if (TITLE_PROPERTY.equals(options)) {
            String sqlQuery = SQLScripts.GET_LIKE_TITLE_PROPERTY;
            return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, queryName.toLowerCase());
        } else if (DIRECTOR_PROPERTY.equals(options)) {
            String sqlQuery = SQLScripts.GET_LIKE_DIRECTOR_PROPERTY;
            return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, queryName.toLowerCase());
        } else if (isExistDirectorAndTitle(options)) {
            String sqlQuery = SQLScripts.GET_LIKE_TITLE_AND_DIRECTOR_PROPERTY;
            return jdbcTemplate.query(con -> {
                PreparedStatement ps = con.prepareStatement(sqlQuery);
                ps.setString(1, queryName.toLowerCase());
                ps.setString(2, queryName.toLowerCase());
                ps.executeQuery();
                return ps;
            }, this::mapRowToFilm);
        } else {
            log.error("Фильм по подстроке {} не найден.", queryName);
            throw new FilmNotFoundException("Фильм не найден");
        }
    }

    private boolean isExistDirectorAndTitle(String options) {
        String opt = options.replaceAll("\\s+", "");
        String[] optionsParam = opt.split(",");
        String optionFirst = optionsParam[0];
        String optionLast = optionsParam[1];
        return (optionsParam.length == 2 &&
                ((optionFirst.equals(DIRECTOR_PROPERTY) && optionLast.equals(TITLE_PROPERTY)) ||
                        (optionFirst.equals(TITLE_PROPERTY) && optionLast.equals(DIRECTOR_PROPERTY))));
    }

    @Override
    public List<Film> getFilmByDirector(Integer directorId, String sortBy) {
        if (directorStorage.getDirectorById(directorId) == null) {
            log.error("Режиссер по идентификатору {} не найден.", directorId);
            throw new FilmNotFoundException("Режиссер не найден");
        }
        if ("year".equals(sortBy)) {
            String sqlQuery = SQLScripts.GET_LIST_DIRECTOR_BY_YEAR;
            return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, directorId);
        } else if ("likes".equals(sortBy)) {
            String sqlQuery = SQLScripts.GET_LIST_DIRECTOR_BY_LIKE;
            return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, directorId);
        } else {
            log.error("Фильм по идентификатору {} не найден.", directorId);
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
            log.error("Друг пользователя с идентификатором {} не найден.", friendId);
            throw new UserNotFoundException("Друг не найден");
        } else {
            log.error(ERROR_USER_ID_NOT_FOUND, userId);
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
        SqlRowSet rSet = jdbcTemplate.queryForRowSet(SQLScripts.GET_FILM_ID_WITH_GENRE, rs.getInt(FILM_ID_COLUMN));
        film.setGenres(new HashSet<>());
        while (rSet.next()) {
            int id = rSet.getInt(GENRE_ID_COLUMN);
            if (id != 0) {
                Genre genre = Genre.builder()
                        .id(id)
                        .name(rSet.getString(GENRE_NAME_COLUMN))
                        .build();
                film.getGenres().add(genre);
            } else {
                film.setGenres(new HashSet<>());
            }
        }
        return film;
    }
}

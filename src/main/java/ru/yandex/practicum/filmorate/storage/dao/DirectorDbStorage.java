package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private static final String DIRECTOR_ID_COLUMN = "DIRECTOR_ID";
    private static final String NAME_COLUMN = "NAME";
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Director> getDirectorsList() {
        String sqlQuery = "SELECT * FROM DIRECTOR";
        return jdbcTemplate.query(sqlQuery, this::makeDirector);
    }

    @Override
    public Director getDirectorById(Integer directorId) {
        SqlRowSet directorRow = jdbcTemplate.queryForRowSet("SELECT * FROM DIRECTOR WHERE DIRECTOR_ID = ?",
                directorId);
        if (directorRow.next()) {
            return new Director(
                    directorRow.getInt(DIRECTOR_ID_COLUMN),
                    directorRow.getString(NAME_COLUMN));
        } else {
            log.error("Режиссер с идентификатором {} не найден.", directorId);
            throw new FilmNotFoundException("Пустое значение, Режиссер не найден");
        }
    }

    @Override
    public Director createDirector(Director director) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement("INSERT INTO PUBLIC.DIRECTOR(NAME) VALUES(?)",
                    new String[]{DIRECTOR_ID_COLUMN});
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);
        director.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        jdbcTemplate.update("UPDATE PUBLIC.DIRECTOR SET NAME=? WHERE DIRECTOR_ID=?",
                director.getName(), director.getId());
        return getDirectorById(director.getId());
    }

    @Override
    public boolean deleteDirector(Integer directorId) {
        return jdbcTemplate.update("DELETE FROM PUBLIC.DIRECTOR WHERE DIRECTOR_ID=?", directorId) > 0;
    }

    @Override
    public List<Director> getDirectorsByFilmId(Integer filmId) {
        String sqlQuery = "SELECT d.DIRECTOR_ID AS DIRECTOR_ID , d.NAME AS NAME  FROM DIRECTOR d " +
                "JOIN DIRECTOR_FILMS df ON d.DIRECTOR_ID = df.DIRECTOR_ID WHERE df.FILM_ID = ?";
        return jdbcTemplate.query(sqlQuery, this::makeDirector, filmId);
    }

    protected Director makeDir(ResultSet rs) throws SQLException {
        return new Director(
                rs.getInt(DIRECTOR_ID_COLUMN),
                rs.getString(NAME_COLUMN));
    }

    private Director makeDirector(ResultSet rs, int rowNum) throws SQLException {
        return new Director(
                rs.getInt(DIRECTOR_ID_COLUMN),
                rs.getString(NAME_COLUMN));
    }
}

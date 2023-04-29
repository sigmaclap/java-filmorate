package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
public class MpaDbStorage implements MpaStorage {

    private static final String RATING_ID_COLUMN = "RATING_ID";
    private static final String NAME_COLUMN = "NAME";

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Mpa> getMpaList() {
        return jdbcTemplate.query("SELECT * FROM FILMS_RATINGS fr ORDER BY RATING_ID ASC", this::makeMpas);
    }

    public Mpa getMpaById(Integer ratingId) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM PUBLIC.FILMS_RATINGS WHERE RATING_ID = ?",
                ratingId);
        if (mpaRows.next()) {
            return Mpa.builder()
                    .id(mpaRows.getInt(RATING_ID_COLUMN))
                    .name(mpaRows.getString(NAME_COLUMN))
                    .build();
        } else {
            log.info("Рейтинг МРА с идентификатором {} не найден.", ratingId);
            throw new FilmNotFoundException("Рейтинг МРА не найден");
        }
    }

    public Mpa makeMpas(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(
                rs.getInt(RATING_ID_COLUMN),
                rs.getString(NAME_COLUMN));
    }
}

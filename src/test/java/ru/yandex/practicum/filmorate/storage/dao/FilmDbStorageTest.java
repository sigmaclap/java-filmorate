package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmDbStorageTest {
    private final FilmStorage filmDbStorage;
    private final UserStorage userDbStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private Film filmTest1;
    private Film filmTest2;
    private User userTest1;
    private User userTest2;

    @BeforeEach
    void setUp() {
        filmTest1 = Film.builder()
                .mpa(Mpa.builder()
                        .id(1)
                        .build())
                .name("Новый фильм 1")
                .description("Описание фильма 1")
                .releaseDate(LocalDate.of(1999, Month.DECEMBER, 12))
                .duration(130)
                .build();

        filmTest2 = Film.builder()
                .mpa(Mpa.builder()
                        .id(2)
                        .build())
                .name("Новый фильм 2")
                .description("Описание фильма 2")
                .releaseDate(LocalDate.of(2000, Month.DECEMBER, 12))
                .duration(210)
                .build();

        userTest1 = User.builder()
                .email("box@gmail.com")
                .login("boxbox")
                .name("Саня")
                .birthday(LocalDate.of(2000, Month.APRIL, 12))
                .build();

        userTest2 = User.builder()
                .email("box2@gmail.com")
                .login("boxbox2")
                .name("Саня2")
                .birthday(LocalDate.of(2002, Month.APRIL, 12))
                .build();
    }

    @Test
    void testLikeFilmWithUsersExists() {
        Film film = filmDbStorage.create(filmTest1);
        User user = userDbStorage.create(userTest1);
        boolean likeFilm = filmDbStorage.likeFilm(film.getId(), user.getId());

        assertThat(likeFilm).isTrue();
    }

    @Test
    void testDeleteLikeWithUsersExists() {
        Film film = filmDbStorage.create(filmTest1);
        User user = userDbStorage.create(userTest1);
        filmDbStorage.likeFilm(film.getId(), user.getId());
        boolean deleteLikeFilm = filmDbStorage.deleteLike(film.getId(), user.getId());

        assertThat(deleteLikeFilm).isTrue();
    }

    @Test
    void testGetFilmsWithUsersExists() {
        Film film1 = filmDbStorage.create(filmTest1);
        Film film2 = filmDbStorage.create(filmTest2);
        List<Film> filmsList = filmDbStorage.getFilms();

        assertThat(filmsList).contains(film1, film2);

    }

    @Test
    void testFindFilmByIdIsValidData() {
        filmDbStorage.create(filmTest1);
        Optional<Film> filmOptional = Optional.ofNullable(filmDbStorage.findFilmById(1));
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film -> assertThat(film).hasFieldOrPropertyWithValue("id", 1));
    }

    @Test
    void testCreateFilmIsValidData() {
        Film film = filmDbStorage.create(filmTest1);
        Film filmEx = filmDbStorage.findFilmById(film.getId());

        assertThat(filmEx).hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    void testUpdateFilmIsValidData() {
        Film film = filmDbStorage.create(filmTest1);
        film.setName("Другое название 1");
        film.setDescription("Другое описание 1");
        Set<Genre> genresList = new HashSet<>();
        genresList.add(Genre.builder().id(1).build());
        genresList.add(Genre.builder().id(2).build());
        genresList.add(Genre.builder().id(3).build());
        film.setGenres(genresList);
        Film filmEx = filmDbStorage.update(film);

        assertThat(filmEx)
                .hasFieldOrPropertyWithValue("name", "Другое название 1")
                .hasFieldOrPropertyWithValue("description", "Другое описание 1");

        assertThat(filmEx.getGenres()).hasSize(3);
    }


    @Test
    void testGetGenreByIdIsValidData() {
        Genre genre = genreStorage.getGenreById(1);
        Genre genre2 = genreStorage.getGenreById(2);

        assertThat(genre).hasFieldOrPropertyWithValue("name", "Комедия");
        assertThat(genre2).hasFieldOrPropertyWithValue("name", "Драма");
    }

    @Test
    void testGetGenresListIsValidData() {
        List<Genre> genreList = genreStorage.getGenresList();

        assertThat(genreList).hasSize(6);
    }

    @Test
    void testGetMpaListIsValidData() {
        List<Mpa> mpaList = mpaStorage.getMpaList();

        assertThat(mpaList).hasSize(5);
    }

    @Test
    void testGetMpaByIdIsValidData() {
        Mpa mpa1 = mpaStorage.getMpaById(1);

        assertThat(mpa1).hasFieldOrPropertyWithValue("name", "G");
    }
}
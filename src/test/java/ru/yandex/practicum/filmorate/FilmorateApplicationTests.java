package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.Month;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmorateApplicationTests {
    private final LocalDate VALID_DATA_TIME = LocalDate.of(1895, Month.DECEMBER, 28);
    private final LocalDate INVALID_DATA_TIME = LocalDate.of(1895, Month.DECEMBER, 27);
    private final LocalDate VALID_DATA_TIME_BIRTHDAY = LocalDate.of(2020, Month.DECEMBER, 28);
    private final LocalDate INVALID_DATA_TIME_BIRTHDAY = LocalDate.of(4000, Month.DECEMBER, 27);
    private static Validator validator;
    private FilmController filmController;
    private UserController userController;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.usingContext().getValidator();
        filmController = new FilmController();
        userController = new UserController();

    }

    @Test
    @DisplayName("Create Films with VALID_DATA_TIME")
    void createFilmWithValidDateTime() {
        Film film = Film.builder()
                .id(null).name("TEST").description("description").releaseDate(VALID_DATA_TIME).duration(100).build();
        filmController.createFilm(film);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("дата релиза — не раньше 28 декабря 1895 года")
    void createFilmWithInvalidDateTime() {
        Film film = Film.builder()
                .id(null).name("TEST").description("description").releaseDate(INVALID_DATA_TIME).duration(100).build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertEquals("{LocalDateRestrictions.invalid}", violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining()));
    }

    @Test
    @DisplayName("Название не может быть пустым")
    void notNullNameFilm() {
        Film film = Film.builder()
                .id(null).name(null).description("description").releaseDate(VALID_DATA_TIME).duration(100).build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.size() > 0);
        assertEquals("Name cannot be empty", violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining()));
    }

    @Test
    @DisplayName("максимальная длина описания — 201 символов")
    void maxCharactersDescription201() {
        Film film = Film.builder()
                .id(null).name("TEST").description("Lorem ipsum dolor sit amet, consectetuer adipiscing elit," +
                        " sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. " +
                        "Ut wisi enim ad minim veniam, quis nostrud exerci tatioa").releaseDate(VALID_DATA_TIME)
                .duration(100).build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.size() > 0);
        assertEquals("Maximum length description 200 characters", violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining()));
    }

    @Test
    @DisplayName("максимальная длина описания — 200 символов")
    void maxCharactersDescription200() {
        Film film = Film.builder()
                .id(null).name("TEST").description("Lorem ipsum dolor sit amet, consectetuer adipiscing elit," +
                        " sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. " +
                        "Ut wisi enim ad minim veniam, quis nostrud exerci tatio").releaseDate(VALID_DATA_TIME)
                .duration(100).build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("продолжительность фильма должна быть положительной")
    void DurationShouldBePositive() {
        Film film = Film.builder()
                .id(null).name("TEST").description("description").releaseDate(VALID_DATA_TIME).duration(-100).build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.size() > 0);
        assertEquals("должно быть больше 0", violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining()));
    }

    @Test
    @DisplayName("продолжительность фильма должна быть положительной = 0")
    void DurationShouldBePositiveZero() {
        Film film = Film.builder()
                .id(null).name("TEST").description("description").releaseDate(VALID_DATA_TIME).duration(0).build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.size() > 0);
        assertEquals("должно быть больше 0", violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining()));
    }

    @Test
    @DisplayName("электронная почта не может быть пустой и должна содержать символ @ - null")
    void emailCannotBeNull() {
        User user = User.builder().id(null).email(null).login("test_login")
                .name("test_name").birthday(VALID_DATA_TIME_BIRTHDAY).build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.size() > 0);
        assertEquals("не должно равняться null", violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining()));
    }

    @Test
    @DisplayName("электронная почта не может быть пустой и должна содержать символ @ - без собаки")
    void emailCannotBeWithoutDog() {
        User user = User.builder().id(null).email("testasd").login("test_login")
                .name("test_name").birthday(VALID_DATA_TIME_BIRTHDAY).build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.size() > 0);
        assertEquals("должно иметь формат адреса электронной почты", violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining()));
    }

    @Test
    @DisplayName("логин не может быть пустым и содержать пробелы")
    void loginShouldBeNotNull() {
        User user = User.builder().id(null).email("test@yandex.com").login(null)
                .name("test_name").birthday(VALID_DATA_TIME_BIRTHDAY).build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.size() > 0);
        assertEquals("не должно быть пустым", violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining()));
    }

    @Test
    @DisplayName("имя для отображения может быть пустым — в таком случае будет использован логин")
    void NameCanBeNull() {
        User user = User.builder().id(null).email("test@yandex.com").login("test_login")
                .name(null).birthday(VALID_DATA_TIME_BIRTHDAY).build();
        userController.createUser(user);
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty());
        assertEquals("test_login", user.getName());
    }

    @Test
    @DisplayName("дата рождения не может быть в будущем - valid data")
    void birthdayCannotBeFuture() {
        User user = User.builder().id(null).email("test@yandex.com").login("test_login")
                .name("test_name").birthday(VALID_DATA_TIME_BIRTHDAY).build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("дата рождения не может быть в будущем - invalid data")
    void birthdayCannotBeFutureInvalid() {
        User user = User.builder().id(null).email("test@yandex.com").login("test_login")
                .name("test_name").birthday(INVALID_DATA_TIME_BIRTHDAY).build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.size() > 0);
        assertEquals("должно содержать прошедшую дату", violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining()));
    }
}

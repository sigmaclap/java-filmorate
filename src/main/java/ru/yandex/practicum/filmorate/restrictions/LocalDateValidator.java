package ru.yandex.practicum.filmorate.restrictions;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Month;

public class LocalDateValidator implements
        ConstraintValidator<LocalDateRestrictions, LocalDate> {
    private static final LocalDate CREATED_MOVIE_DATE = LocalDate.of(1895, Month.DECEMBER, 27);


    @Override
    public void initialize(LocalDateRestrictions constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        return date != null && date.isAfter(CREATED_MOVIE_DATE);
    }
}

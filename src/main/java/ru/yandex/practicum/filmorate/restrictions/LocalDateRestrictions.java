package ru.yandex.practicum.filmorate.restrictions;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LocalDateValidator.class)
@Documented
public @interface LocalDateRestrictions {
    String message() default "{LocalDateRestrictions.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

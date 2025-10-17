package org.example.sleeptracker.validations.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.example.sleeptracker.validations.validators.UsernameValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {UsernameValidator.class})
public @interface Username {
    String message() default "{validation.username}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

package com.example.backend.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SocialInfoValidator.class)
public @interface ValidSocialInfo {
    String message() default "Invalid social link";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

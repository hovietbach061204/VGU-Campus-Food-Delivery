package com.identityApplication.IdentityApplication.validator;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({FIELD}) // means where this custom annotation will be applied
@Retention(RUNTIME) // when this annotation will be processed. In this case, we choose at RUNTIME
@Constraint(validatedBy = {DobValidator.class})
public @interface DobConstraint { // @interface announces java that this a custom annotation
    String message() default "Invalid date of birth";

    int min();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
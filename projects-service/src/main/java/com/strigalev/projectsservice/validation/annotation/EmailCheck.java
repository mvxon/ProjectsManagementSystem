package com.strigalev.projectsservice.validation.annotation;

import com.strigalev.projectsservice.validation.validator.EmailCheckValidator;
import com.strigalev.projectsservice.validation.validator.ProjectNameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EmailCheckValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailCheck {

    String message() default "Invalid email";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

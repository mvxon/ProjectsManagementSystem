package com.strigalev.projectsservice.validation.annotation;

import com.strigalev.projectsservice.validation.validator.ProjectNameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ProjectNameValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProjectName {
    String message() default "Invalid project name";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

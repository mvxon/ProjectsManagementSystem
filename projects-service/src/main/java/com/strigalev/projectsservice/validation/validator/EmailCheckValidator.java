package com.strigalev.projectsservice.validation.validator;

import com.strigalev.projectsservice.service.UserService;
import com.strigalev.projectsservice.validation.annotation.EmailCheck;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class EmailCheckValidator implements ConstraintValidator<EmailCheck, String> {
    private final UserService userService;


    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        if (userService.existsByEmail(email)) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate(String.format("User with email %s already exists",
                            email))
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}

package com.strigalev.projectsservice.validation.validator;

import com.strigalev.projectsservice.validation.annotation.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DateValidator implements ConstraintValidator<Date, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        LocalDate deadLine;
        try {
            deadLine = LocalDate.parse(s);
        } catch (Exception e) {
            return false;
        }
        if (deadLine.isBefore(LocalDate.now())) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Deadline date should be " +
                            "later than creation date")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}

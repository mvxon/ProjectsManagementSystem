package com.strigalev.projectsservice.util;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.validation.constraints.NotNull;


public final class MethodsUtil {
    public static String getBindingResultErrors(@NotNull BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            return "";
        }
        StringBuilder errors = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            if (bindingResult.getFieldErrors().indexOf(fieldError) == bindingResult.getFieldErrors().size() - 1) {
                errors.append(fieldError.getField()).append(" : ").append(fieldError.getDefaultMessage());
                return errors.toString();
            }
            errors.append(fieldError.getField()).append(" : ").append(fieldError.getDefaultMessage()).append(", ");
        }
        return errors.toString();
    }

    public static String getProjectNotExistsMessage(Long projectId) {
        return String.format("Project with %oid does not exists", projectId);
    }

    public static String getTaskNotExistsMessage(Long taskId) {
        return String.format("Task with %oid does not exists", taskId);
    }

    public static String getUserNotExistsMessage(Long userId) {
        return String.format("User with %oid does not exists", userId);
    }

    public static String getUserWithEmailNotExistsMessage(String email) {
        return String.format("User with email: %s does not exists", email);
    }

}

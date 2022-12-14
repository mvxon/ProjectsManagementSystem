package com.strigalev.starter.util;

import com.strigalev.starter.model.Role;
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
                errors
                        .append(fieldError.getField()).append(" : ")
                        .append(fieldError.getDefaultMessage());
                return errors.toString();
            }
            errors.append(fieldError.getField()).append(" : ").append(fieldError.getDefaultMessage()).append(", ");
        }
        return errors.toString();
    }

    public static String getProjectNotExistsMessage(Long projectId) {
        return String.format("Project with %oid does not exists", projectId);
    }

    public static String getProjectWithNameNotExistsMessage(String name) {
        return String.format("Project with name: %s does not exists", name);
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

    public static String getTaskIsNotInStatusMessage(Long taskId, String status) {
        return String.format("Task with %oid is not in %s status", taskId, status);
    }

    public static String getTaskIsAlreadyInStatusMessage(Long taskId, String status) {
        return String.format("Task with %oid is already in %s status", taskId, status);
    }

    public static String getTaskIsAlreadyHasUserWithRoleMessage(Long taskId, Role role) {
        return String.format("Task with %oid is already have user with role %s", taskId, role);
    }

    public static String getUserNotAssignedWithTaskMessage(Long userId, Long taskId) {
        return String.format("User %oid don't have assigned task with %oid", userId, taskId);
    }

    public static String getUserIsAlreadyAssignedWithTaskMessage(Long userId, Long taskId) {
        return String.format("User %oid is already have assigned task with %oid", userId, taskId);
    }

}
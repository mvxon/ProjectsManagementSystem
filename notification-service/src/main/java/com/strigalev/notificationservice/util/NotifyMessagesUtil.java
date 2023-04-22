package com.strigalev.notificationservice.util;

import com.strigalev.starter.dto.UserActionMailMessageDTO;
import lombok.experimental.UtilityClass;

@UtilityClass
public class NotifyMessagesUtil {
    private final static String QUOTE = "\"";

    public static String getAddedToProjectMessage(UserActionMailMessageDTO businessActionMessage) {
        return String.format("What's up, %s,\nYou are added to %s project by %s.",
                businessActionMessage.getActionedUserFirstName(),
                businessActionMessage.getProjectName(),
                businessActionMessage.getActionUserFnAndEmail()
        );
    }

    public static String getProjectUpdatedMessage(UserActionMailMessageDTO businessActionMessage) {
        return String.format("Hi, %s!,\nProject \"%s\" was updated by %s.\nYou can check changes at PMsystem.",
                businessActionMessage.getActionedUserFirstName(),
                businessActionMessage.getProjectName(),
                businessActionMessage.getActionUserFnAndEmail()
        );
    }

    public static String getProjectDeletedMessage(UserActionMailMessageDTO businessActionMessage) {
        return String.format("Hi, %s!,\nProject \"%s\" was deleted by %s.",
                businessActionMessage.getActionedUserFirstName(),
                businessActionMessage.getProjectName(),
                businessActionMessage.getActionUserFnAndEmail()
        );
    }

    public static String getTaskUpdatedMessage(UserActionMailMessageDTO businessActionMessage) {
        return String.format("Hi, %s!,\nTask \"%s\" was updated by %s.\nYou can check changes at PMsystem.\nProject: %s.",
                businessActionMessage.getActionedUserFirstName(),
                businessActionMessage.getTaskTittle(),
                businessActionMessage.getActionUserFnAndEmail(),
                businessActionMessage.getProjectName()
        );
    }

    public static String getAddedTaskMessage(UserActionMailMessageDTO businessActionMessage) {
        return String.format("%s added new task \"%s\" to project you are working on\nProject: %s.",
                businessActionMessage.getActionUserFnAndEmail(),
                businessActionMessage.getTaskTittle(),
                businessActionMessage.getProjectName()
        );
    }

    public static String getTaskAssignedTaskMessage(UserActionMailMessageDTO businessActionMessage) {
        return String.format("%s,\nTask \"%s\" was assigned to you by %s\nProject: %s.",
                businessActionMessage.getActionedUserFirstName(),
                businessActionMessage.getTaskTittle(),
                businessActionMessage.getActionUserFnAndEmail(),
                businessActionMessage.getProjectName()
        );
    }

    public static String getTaskUnAssignedTaskMessage(UserActionMailMessageDTO businessActionMessage) {
        return String.format("%s,\nTask \"%s\" was unassigned from you by %s\nProject: %s.",
                businessActionMessage.getActionedUserFirstName(),
                businessActionMessage.getTaskTittle(),
                businessActionMessage.getActionUserFnAndEmail(),
                businessActionMessage.getProjectName()
        );
    }

    public static String getTaskOpenedMessage(UserActionMailMessageDTO businessActionMessage) {
        return String.format("%s,\nTask \"%s\" was opened by %s\nProject: %s.",
                businessActionMessage.getActionedUserFirstName(),
                businessActionMessage.getTaskTittle(),
                businessActionMessage.getActionUserFnAndEmail(),
                businessActionMessage.getProjectName()
        );
    }

    public static String getTaskDeletedMessage(UserActionMailMessageDTO businessActionMessage) {
        return String.format("%s,\nTask \"%s\" was deleted by %s\nProject: %s.",
                businessActionMessage.getActionedUserFirstName(),
                businessActionMessage.getTaskTittle(),
                businessActionMessage.getActionUserFnAndEmail(),
                businessActionMessage.getProjectName()
        );
    }

    public static String getTaskStartedDevelopingMessage(UserActionMailMessageDTO businessActionMessage) {
        return String.format("%s,\nTask \"%s\" started developing by %s\nProject: %s.",
                businessActionMessage.getActionedUserFirstName(),
                businessActionMessage.getTaskTittle(),
                businessActionMessage.getActionUserFnAndEmail(),
                businessActionMessage.getProjectName()
        );
    }

    public static String getTaskCompletedMessage(UserActionMailMessageDTO businessActionMessage) {
        return String.format("%s, \nTask \"%s\"was completed by %s\nProject: %s.",
                businessActionMessage.getActionedUserFirstName(),
                businessActionMessage.getTaskTittle(),
                businessActionMessage.getActionUserFnAndEmail(),
                businessActionMessage.getProjectName()
        );

    }

    public static String getTaskStartedTestingMessage(UserActionMailMessageDTO businessActionMessage) {
        return String.format("%s,\nTask \"%s\" was started testing by %s\nProject: %s.",
                businessActionMessage.getActionedUserFirstName(),
                businessActionMessage.getTaskTittle(),
                businessActionMessage.getActionUserFnAndEmail(),
                businessActionMessage.getProjectName()
        );

    }

    public static String getTaskTestingCompletedMessage(UserActionMailMessageDTO businessActionMessage) {
        return String.format("%s,\nTask \"%s\" was finished testing by %s\nProject: %s.",
                businessActionMessage.getActionedUserFirstName(),
                businessActionMessage.getTaskTittle(),
                businessActionMessage.getActionUserFnAndEmail(),
                businessActionMessage.getProjectName()
        );

    }

    public static String getTaskDocumentedMessage(UserActionMailMessageDTO businessActionMessage) {
        return String.format("%s,\nTask \"%s\" was documented by %s\nProject: %s.",
                businessActionMessage.getActionedUserFirstName(),
                businessActionMessage.getTaskTittle(),
                businessActionMessage.getActionUserFnAndEmail(),
                businessActionMessage.getProjectName()
        );

    }

    public static String getTaskAssignedSubject(String taskTittle) {
        return "Task " + QUOTE + taskTittle + QUOTE + " assigned to you!";
    }

    public static String getTaskUnAssignedSubject(String taskTittle) {
        return "Task " + QUOTE + taskTittle + QUOTE + " unassigned to you!";
    }

    public static String getTaskOpenedSubject(String taskTittle) {
        return "Task " + QUOTE + taskTittle + QUOTE + " opened!";
    }

    public static String getTaskDeletedSubject(String taskTittle) {
        return "Task " + QUOTE + taskTittle + QUOTE + " deleted!";
    }

    public static String getTaskStartedDevelopingSubject(String taskTittle) {
        return "Task " + QUOTE + taskTittle + QUOTE + " started developing!";
    }

    public static String getTaskCompletedSubject(String taskTittle) {
        return "Task " + QUOTE + taskTittle + QUOTE + " completed!";
    }

    public static String getTaskStartedTestingSubject(String taskTittle) {
        return "Task " + QUOTE + taskTittle + QUOTE + " started testing!";
    }

    public static String getTaskTestingCompletedSubject(String taskTittle) {
        return "Task " + QUOTE + taskTittle + QUOTE + " was tested!";
    }

    public static String getTaskDocumentedSubject(String taskTittle) {
        return "Task " + QUOTE + taskTittle + QUOTE + " was documented!";
    }

    public static String getAddedTaskSubject(UserActionMailMessageDTO businessActionMessage) {
        return "New task " + QUOTE + businessActionMessage.getTaskTittle() + QUOTE + " was created by " +
                businessActionMessage.getActionUserFnAndEmail();
    }

    public static String getProjectUpdatedSubject(UserActionMailMessageDTO businessActionMessage) {
        return String.format("\nProject \"%s\" was updated by %s!",
                businessActionMessage.getProjectName(),
                businessActionMessage.getActionUserFnAndEmail()
        );
    }

    public static String getProjectDeletedSubject(UserActionMailMessageDTO businessActionMessage) {
        return String.format("\nProject \"%s\" was deleted by %s!",
                businessActionMessage.getProjectName(),
                businessActionMessage.getActionUserFnAndEmail()
        );
    }

    public static String getTaskUpdatedSubject(UserActionMailMessageDTO businessActionMessage) {
        return String.format("\nTask \"%s\" was updated by %s!",
                businessActionMessage.getTaskTittle(),
                businessActionMessage.getActionUserFnAndEmail()
        );
    }

}

package com.strigalev.notificationservice.service;

import com.strigalev.starter.dto.MailMessageDTO;
import com.strigalev.starter.dto.UserActionMailMessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import static com.strigalev.notificationservice.util.NotifyMessagesUtil.*;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final JavaMailSender mailSender;
    @RabbitListener(queues = "${spring.rabbitmq.notification-queue}")
    public void receiveAndSend(UserActionMailMessageDTO businessMessage) {
        SimpleMailMessage message = createAndFeelMessageBody(businessMessage);
        mailSender.send(message);
    }

    private SimpleMailMessage createAndFeelMessageBody(UserActionMailMessageDTO businessMessage) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        MailMessageDTO mailMessageDTO = businessMessage.getMailMessageDTO();
        String message = "";
        String subject = "";


        switch (businessMessage.getAction()) {

            case ADD_TASK_TO_PROJECT -> {
                message = getAddedTaskMessage(businessMessage);
                subject = getAddedTaskSubject(businessMessage);
            }
            case UPDATE_PROJECT -> {
                message = getProjectUpdatedMessage(businessMessage);
                subject = getProjectUpdatedSubject(businessMessage);
            }
            case DELETE_PROJECT -> {
                message = getProjectDeletedMessage(businessMessage);
                subject = getProjectDeletedSubject(businessMessage);
            }
            case UPDATE_TASK -> {
                message = getTaskUpdatedMessage(businessMessage);
                subject = getTaskUpdatedSubject(businessMessage);
            }
            case ASSIGN_TASK_TO_USER -> {
                message = getTaskAssignedTaskMessage(businessMessage);
                subject = getTaskAssignedSubject(businessMessage.getTaskTittle());
            }

            case UNASSIGN_TASK_TO_USER -> {
                message = getTaskUnAssignedTaskMessage(businessMessage);
                subject = getTaskUnAssignedSubject(businessMessage.getTaskTittle());
            }

            case OPEN_TASK -> {
                message = getTaskOpenedMessage(businessMessage);
                subject = getTaskOpenedSubject(businessMessage.getTaskTittle());
            }

            case DELETE_TASK -> {
                message = getTaskDeletedMessage(businessMessage);
                subject = getTaskDeletedSubject(businessMessage.getTaskTittle());
            }

            case TAKE_TASK_FOR_DEVELOPING -> {
                message = getTaskStartedDevelopingMessage(businessMessage);
                subject = getTaskStartedDevelopingSubject(businessMessage.getTaskTittle());
            }

            case COMPLETED_TASK -> {
                message = getTaskCompletedMessage(businessMessage);
                subject = getTaskCompletedSubject(businessMessage.getTaskTittle());
            }

            case TAKE_TASK_FOR_TESTING -> {
                message = getTaskStartedTestingMessage(businessMessage);
                subject = getTaskStartedTestingSubject(businessMessage.getTaskTittle());
            }

            case COMPLETED_TASK_TESTING -> {
                message = getTaskTestingCompletedMessage(businessMessage);
                subject = getTaskTestingCompletedSubject(businessMessage.getTaskTittle());
            }

            case SET_TASK_DOCUMENTED -> {
                message = getTaskDocumentedMessage(businessMessage);
                subject = getTaskDocumentedSubject(businessMessage.getTaskTittle());
            }

            case ADD_USER_TO_PROJECT -> {
                message = getAddedToProjectMessage(businessMessage);
                subject = "New project";
            }

            case REQUEST_RESET_PASSWORD -> {
                message = mailMessageDTO.getBody();
                subject = "Password resetting";
            }

            case RESET_PASSWORD -> {
                message = mailMessageDTO.getBody();
                subject = "Password successfully changed";
            }
        }

        message += "\n\n\nBest regards, Projects Management System (c)";
        mailMessage.setText(message);
        mailMessage.setSubject(subject);
        mailMessage.setTo(mailMessageDTO.getToEmail());

        return mailMessage;
    }
}

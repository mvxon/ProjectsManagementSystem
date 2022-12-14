package com.strigalev.starter.rabbit;

import com.strigalev.starter.dto.AuditDTO;
import com.strigalev.starter.dto.MailMessageDTO;
import com.strigalev.starter.dto.UserActionMailMessageDTO;
import com.strigalev.starter.dto.UserDTO;
import com.strigalev.starter.model.UserAction;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RabbitMQService {

    @Value("${spring.rabbitmq.exchange}")
    private String exchange;

    @Value("${spring.rabbitmq.audit-routing-key:#{null}}")
    private String routingKey;

    @Value("${spring.rabbitmq.notification-routing-key:#{null}}")
    private String mailRoutingKey;


    private final RabbitTemplate rabbitTemplate;

    public void sendAuthAuditMessage(UserAction action, LocalDateTime date, UserDTO user) {
        rabbitTemplate.convertAndSend(
                exchange,
                routingKey,
                AuditDTO.builder()
                        .action(action)
                        .date(date)
                        .actionUser(user)
                        .build()
        );
    }

    public void sendAuditMessage(
            UserAction action,
            LocalDateTime date,
            UserDTO user,
            Long projectId,
            Long taskId,
            Long actionedUserId
    ) {
        rabbitTemplate.convertAndSend(
                exchange,
                routingKey,
                AuditDTO.builder()
                        .action(action)
                        .projectId(projectId)
                        .taskId(taskId)
                        .date(date)
                        .actionedUserId(actionedUserId)
                        .actionUser(user)
                        .build()
        );
    }

    public void sendMailMessage(String toEmail, String body, String subject, UserAction action) {
        rabbitTemplate.convertAndSend(exchange, mailRoutingKey,
                UserActionMailMessageDTO.builder()
                        .mailMessageDTO(
                                MailMessageDTO.builder()
                                        .body(body)
                                        .subject(subject)
                                        .toEmail(toEmail)
                                        .build()
                        )
                        .action(action)
                        .build()
        );
    }

    public void sendActionMailMessage(
            UserAction action,
            String taskTitle,
            String projectName,
            String managerFnAndEmail,
            String firstName,
            MailMessageDTO mailMessageDTO
    ) {
        rabbitTemplate.convertAndSend(exchange,
                mailRoutingKey,
                UserActionMailMessageDTO.builder()
                        .actionUserFnAndEmail(managerFnAndEmail)
                        .action(action)
                        .taskTittle(taskTitle)
                        .actionedUserFirstName(firstName)
                        .mailMessageDTO(mailMessageDTO)
                        .projectName(projectName)
                        .build()
        );
    }
}
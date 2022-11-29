package com.strigalev.starter.rabbit;

import com.strigalev.starter.dto.AuditDTO;
import com.strigalev.starter.dto.UserActionMailMessageDTO;
import com.strigalev.starter.dto.MailMessageDTO;
import com.strigalev.starter.model.Role;
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

    @Value("${spring.rabbitmq.routingkey}")
    private String routingKey;

    @Value("${spring.rabbitmq.mail-routing-key}")
    private String mailRoutingKey;


    private final RabbitTemplate rabbitTemplate;

    public void sendAuthAuditMessage(UserAction action, LocalDateTime date, String userEmail) {
        rabbitTemplate.convertAndSend(
                exchange,
                routingKey,
                AuditDTO.builder()
                        .action(action)
                        .date(date)
                        .userEmail(userEmail)
                        .build()
        );
    }

    public void sendAuditMessage(
            UserAction action,
            LocalDateTime date,
            String userEmail,
            Role role,
            Long projectId,
            Long taskId,
            Long actionedUserId
    ) {
        rabbitTemplate.convertAndSend(
                exchange,
                routingKey,
                AuditDTO.builder()
                        .action(action)
                        .userRole(role)
                        .projectId(projectId)
                        .taskId(taskId)
                        .date(date)
                        .userEmail(userEmail)
                        .actionedUserId(actionedUserId)
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
                        .userAction(action)
                        .build()
        );
    }

    public void sendActionMailMessage(UserAction action,
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
                        .userAction(action)
                        .taskTittle(taskTitle)
                        .actionedUserFirstName(firstName)
                        .mailMessageDTO(mailMessageDTO)
                        .projectName(projectName)
                        .build()
        );
    }
}

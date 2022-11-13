package com.strigalev.starter.rabbit;

import com.strigalev.starter.dto.AuditDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class RabbitMQService {
    @Value("${spring.rabbitmq.exchange}")
    private String exchange;
    @Value("${spring.rabbitmq.routingkey}")
    private String routingKey;
    private final RabbitTemplate rabbitTemplate;

    public void sendAuditMessage(String action, Date date, String userEmail) {
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
}

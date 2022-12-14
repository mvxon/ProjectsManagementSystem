package com.strigalev.starter.rabbit.config.audit;

import com.strigalev.starter.rabbit.config.general.RabbitMQConfig;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(RabbitMQConfig.class)
public class AuditQueueConfig {
    @Value("${spring.rabbitmq.audit-queue}")
    private String auditQueue;

    @Value("${spring.rabbitmq.audit-routing-key}")
    private String auditRoutingKey;

    @Bean
    Queue auditQueue() {
        return new Queue(auditQueue, true);
    }

    @Bean
    Binding auditBinding(Exchange exchange) {
        return BindingBuilder
                .bind(auditQueue())
                .to(exchange)
                .with(auditRoutingKey)
                .noargs();
    }
}

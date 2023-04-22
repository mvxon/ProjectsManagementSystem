package com.strigalev.starter.rabbit.config.notification;

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
public class NotificationQueueConfig {

    @Value("${spring.rabbitmq.notification-queue}")
    private String notificationQueue;

    @Value("${spring.rabbitmq.notification-routing-key}")
    private String notificationRoutingKey;

    @Bean
    Queue mailQueue() {
        return new Queue(notificationQueue, true);
    }
    @Bean
    Binding notificationBinding(Exchange exchange) {
        return BindingBuilder
                .bind(mailQueue())
                .to(exchange)
                .with(notificationRoutingKey)
                .noargs();
    }
}

package com.strigalev.mailservice.service;

import com.strigalev.starter.dto.MailMessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;


    @RabbitListener(queues = "${spring.rabbitmq.mail-queue}")
    public void receiveAndSend(MailMessageDTO mailMessageDTO) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("pmsystem666@gmail.com");
        message.setTo(mailMessageDTO.getToEmail());
        message.setText(mailMessageDTO.getBody());
        message.setSubject(mailMessageDTO.getSubject());

        mailSender.send(message);
    }
}

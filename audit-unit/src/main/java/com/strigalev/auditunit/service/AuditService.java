package com.strigalev.auditunit.service;

import com.strigalev.auditunit.domain.Audit;
import com.strigalev.auditunit.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditRepository auditRepository;

    @RabbitListener(queues = "${spring.rabbitmq.queue}")
    public void receivedMessage(Audit audit) {
        auditRepository.save(audit);
    }
}

package com.strigalev.auditunit.service;

import com.strigalev.auditunit.domain.Audit;
import com.strigalev.auditunit.repository.AuditRepository;
import com.strigalev.starter.dto.DateIntervalDTO;
import com.strigalev.starter.model.UserAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.strigalev.starter.model.UserAction.TAKE_TASK_FOR_DEVELOPING;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditRepository auditRepository;

    @RabbitListener(queues = "${spring.rabbitmq.audit-queue}")
    public void saveReceivedMessage(Audit audit) {
        auditRepository.save(audit);
    }

    public List<Audit> getUserCompletedTasksStatisticBetween(
            Long userId,
            UserAction action,
            DateIntervalDTO dateInterval
    ) {
        return auditRepository.findByActionUserIdAndActionAndDateBetween(userId,
                        action,
                        dateInterval.getFrom(),
                        dateInterval.getTo()
                ).stream()
                .peek(audit -> audit.setDateOfDevStart(
                        auditRepository.findByActionAndTaskId(TAKE_TASK_FOR_DEVELOPING, audit.getTaskId())
                                .orElseThrow(() -> new RuntimeException("INVALID BUSINESS PROCESS"))
                                .getDate()
                )).toList();

    }
}

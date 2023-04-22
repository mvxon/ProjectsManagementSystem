package com.strigalev.auditunit.repository;

import com.strigalev.auditunit.domain.Audit;
import com.strigalev.starter.model.UserAction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AuditRepository extends MongoRepository<Audit, String> {

    List<Audit> findByActionUserIdAndActionAndDateBetween(Long actionUserId,
                                                       UserAction action,
                                                       LocalDateTime startDate,
                                                       LocalDateTime endDate);

    Optional<Audit> findByActionAndTaskId(UserAction action, Long taskId);
}

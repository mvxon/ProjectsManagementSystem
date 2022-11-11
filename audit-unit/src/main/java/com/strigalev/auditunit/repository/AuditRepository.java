package com.strigalev.auditunit.repository;

import com.strigalev.auditunit.domain.Audit;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuditRepository extends MongoRepository<Audit, String> {
}

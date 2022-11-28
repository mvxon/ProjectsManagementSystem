package com.strigalev.authenticationservice.repository;

import com.strigalev.authenticationservice.domain.AccessCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessCodeRepository extends JpaRepository<AccessCode, Long> {

    AccessCode findByUserEmail(String userEmail);

    void deleteAllByUserEmail(String email);
}

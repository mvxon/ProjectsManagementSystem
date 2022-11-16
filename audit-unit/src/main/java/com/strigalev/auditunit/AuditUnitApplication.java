package com.strigalev.auditunit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.strigalev.starter", "com.strigalev.auditunit"})
public class AuditUnitApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuditUnitApplication.class, args);
    }

}

package com.strigalev.reportservice.feign;

import com.strigalev.starter.dto.AuditDTO;
import com.strigalev.starter.dto.DateIntervalDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "audit-unit", path = "/api/v1/audit")
public interface FeignClientService {

    @GetMapping("/userCompletedTasksStatistics/{userId}")
    ResponseEntity<List<AuditDTO>> getUserCompletedTasks(
            @PathVariable Long userId,
            @RequestBody DateIntervalDTO dateInterval
    );
}

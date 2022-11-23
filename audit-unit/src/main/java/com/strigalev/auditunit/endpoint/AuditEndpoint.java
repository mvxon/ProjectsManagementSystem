package com.strigalev.auditunit.endpoint;

import com.strigalev.auditunit.domain.Audit;
import com.strigalev.auditunit.service.AuditService;
import com.strigalev.starter.dto.DateIntervalDTO;
import com.strigalev.starter.model.UserAction;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/audit")
public class AuditEndpoint {

    private final AuditService auditService;

    @PostMapping("/userCompletedTasksStatistics/{userEmail}")
    public ResponseEntity<List<Audit>> getUserCompletedTasks(
            @PathVariable(name = "userEmail") String userEmail, @RequestBody DateIntervalDTO dateInterval
            ) {
        return ResponseEntity.ok(auditService.getUserCompletedTasksStatisticBetween(userEmail, UserAction.COMPLETED_TASK, dateInterval));
    }
}

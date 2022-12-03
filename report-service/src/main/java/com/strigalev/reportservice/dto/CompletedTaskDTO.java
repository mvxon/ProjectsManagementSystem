package com.strigalev.reportservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompletedTaskDTO {
    private Long taskId;
    private Long projectId;
    private LocalDateTime dateOfDevStarted;
    private LocalDateTime completionDate;
    private Long daysTook;
    private Integer hoursTook;
    private Integer minutesTook;
}

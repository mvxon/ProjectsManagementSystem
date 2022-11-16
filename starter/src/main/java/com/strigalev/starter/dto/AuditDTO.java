package com.strigalev.starter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuditDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;

    private String action;
    private LocalDateTime date;
    private String userEmail;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long projectId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long taskId;
}

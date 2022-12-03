package com.strigalev.starter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.strigalev.starter.model.UserAction;
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

    private UserAction action;
    private LocalDateTime date;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long actionedUserId;

    private UserDTO actionUser;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long projectId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long taskId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime dateOfDevStart;
}

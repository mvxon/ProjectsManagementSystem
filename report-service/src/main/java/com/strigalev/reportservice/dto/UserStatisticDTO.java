package com.strigalev.reportservice.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.strigalev.starter.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserStatisticDTO {
    private UserDTO user;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer completedTasksCount;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double completionRate; // tasks/day

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<CompletedTaskDTO> completedTasksStatistic;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
}

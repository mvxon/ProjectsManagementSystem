package com.strigalev.projectsservice.dto;

import com.strigalev.starter.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private List<Long> workingProjectsIds;
    private List<Long> workingTasksIds;
    private Role role;
}

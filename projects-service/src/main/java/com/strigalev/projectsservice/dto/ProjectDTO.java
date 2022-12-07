package com.strigalev.projectsservice.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.strigalev.projectsservice.domain.ProjectStatus;
import com.strigalev.projectsservice.validation.annotation.ProjectName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDTO {

    private Long id;

    private ProjectStatus status;

    @ProjectName
    @NotEmpty(message = "Name should not be empty")
    @Size(max = 30, min = 3, message = "Name length should be between {min} and {max} chars")
    private String name;

    @NotEmpty(message = "Title should not be empty")
    @Size(min = 7, max = 40, message = "Title length should be between {min} and {max} chars")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String title;

    @NotEmpty(message = "Customer should not be empty")
    @Size(min = 3, max = 30, message = "Customer length should be between {min} and {max} chars")
    private String customer;

    @NotEmpty(message = "Description should not be empty")
    @Size(min = 10, max = 1000, message = "Description length should be between {min} and {max} chars")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime creationDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NotNull(message = "Deadline date should not be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Future(message = "Deadline date should be in future")
    private LocalDateTime deadLineDate;
}

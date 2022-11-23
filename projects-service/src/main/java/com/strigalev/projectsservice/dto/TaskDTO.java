package com.strigalev.projectsservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.strigalev.projectsservice.domain.TaskStatus;
import com.strigalev.projectsservice.validation.annotation.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDTO {

    private Long id;

    private Set<TaskStatus> statuses;

    @NotEmpty(message = "Title should not be empty")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Size(min = 7, max = 40, message = "Title length should be between {min} and {max} chars")
    private String title;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Size(min = 10, max = 1000, message = "Description length should be between {min} and {max} chars")
    private String description;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String creationDate;

    @NotEmpty(message = "Deadline date should not be empty")
    @Date
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String deadLineDate;
}

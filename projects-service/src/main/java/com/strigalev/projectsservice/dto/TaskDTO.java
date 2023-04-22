package com.strigalev.projectsservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.strigalev.projectsservice.domain.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDTO {

    private Long id;

    private TaskStatus status;

    @NotEmpty(message = "Title should not be empty")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Size(min = 7, max = 40, message = "Title length should be between {min} and {max} chars")
    private String title;

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

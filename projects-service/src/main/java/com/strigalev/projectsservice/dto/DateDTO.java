package com.strigalev.projectsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateDTO {

    @NotNull(message = "Year should not be empty")
    private Integer year;
    private Integer month;
    private Integer day;
}

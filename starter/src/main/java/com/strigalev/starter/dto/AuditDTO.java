package com.strigalev.starter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuditDTO {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;
    private String action;
    private Date date;
    private String userEmail;
}

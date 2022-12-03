package com.strigalev.starter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.strigalev.starter.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class TokenDTO {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long userId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Role userRole;
    private String accessToken;
    private String refreshToken;
}

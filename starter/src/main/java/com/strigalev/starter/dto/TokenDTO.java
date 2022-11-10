package com.strigalev.starter.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class TokenDTO {
    private Long userId;
    private String accessToken;
    @NotEmpty(message = "Refresh token should not be empty")
    private String refreshToken;
}

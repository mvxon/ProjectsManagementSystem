package com.strigalev.starter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class TokenDTO {
    private Long userId;
    private String accessToken;
    private String refreshToken;
}

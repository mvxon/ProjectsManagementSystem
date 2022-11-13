package com.strigalev.starter.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class TokenDTO {
    private Long userId;
    private String accessToken;
    private String refreshToken;
}

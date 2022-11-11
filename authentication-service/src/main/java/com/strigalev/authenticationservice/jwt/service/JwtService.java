package com.strigalev.authenticationservice.jwt.service;

import com.strigalev.authenticationservice.jwt.RefreshToken;
import com.strigalev.authenticationservice.security.model.CustomUserDetails;
import com.strigalev.starter.dto.TokenDTO;

public interface JwtService {

    String generateAccessToken(CustomUserDetails userDetails);

    String generateRefreshToken(CustomUserDetails user, RefreshToken refreshToken);

    void validateAndDeleteRefreshToken(String refreshToken);

    TokenDTO generateTokensPair(CustomUserDetails userDetails);

    void validateAccessToken(String token);

    void validateRefreshToken(String token);

    String getUserEmailFromAccessToken(String token);

    String getUserEmailFromRefreshToken(String token);

    Long getTokenIdFromRefreshToken(String token);
}

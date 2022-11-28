package com.strigalev.authenticationservice.jwt.service;

import com.strigalev.authenticationservice.domain.User;
import com.strigalev.authenticationservice.jwt.RefreshToken;
import com.strigalev.starter.dto.TokenDTO;

public interface JwtService {

    String generateAccessToken(User userDetails);

    String generateRefreshToken(User user, RefreshToken refreshToken);

    void validateAndDeleteRefreshToken(String refreshToken);

    TokenDTO generateTokensPair(User user);

    void validateAccessToken(String token);

    void validateRefreshToken(String token);

    String getUserEmailFromAccessToken(String token);

    String getUserEmailFromRefreshToken(String token);

    Long getTokenIdFromRefreshToken(String token);
}
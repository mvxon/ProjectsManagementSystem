package com.strigalev.authenticationservice.service;

import com.strigalev.authenticationservice.dto.SignInDTO;
import com.strigalev.starter.dto.TokenDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    TokenDTO login(SignInDTO dto);

    void logout(String refreshToken);

    TokenDTO updateRefreshToken(String refreshToken);

    TokenDTO validateAccessToken(String token);

}

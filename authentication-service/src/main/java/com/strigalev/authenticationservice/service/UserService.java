package com.strigalev.authenticationservice.service;

import com.strigalev.authenticationservice.dto.SignInDTO;
import com.strigalev.starter.dto.TokenDTO;
import com.strigalev.authenticationservice.security.model.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    TokenDTO login(SignInDTO dto);

    void logout(TokenDTO tokenDTO);

    TokenDTO updateAccessToken(TokenDTO tokenDTO);

    TokenDTO updateRefreshToken(TokenDTO tokenDTO);

    TokenDTO validateToken(String token);

}

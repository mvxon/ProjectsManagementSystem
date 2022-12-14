package com.strigalev.authenticationservice.service;

import com.strigalev.authenticationservice.dto.ResetPasswordDTO;
import com.strigalev.authenticationservice.dto.SignInDTO;
import com.strigalev.authenticationservice.dto.SignUpRequest;
import com.strigalev.starter.dto.TokenDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    TokenDTO signUp(SignUpRequest signUpRequest);

    TokenDTO signIn(SignInDTO dto);

    void logout(String refreshToken);

    TokenDTO updateRefreshToken(String refreshToken);

    boolean existsByEmail(String email);

    void createAndSendAccessCode(String email);

    void resetPassword(ResetPasswordDTO resetPasswordDTO);
}


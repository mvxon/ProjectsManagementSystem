package com.strigalev.authenticationservice.service;

import com.strigalev.authenticationservice.dto.SignInDTO;
import com.strigalev.authenticationservice.feign.FeignClientService;
import com.strigalev.authenticationservice.jwt.service.JwtService;
import com.strigalev.authenticationservice.jwt.service.JwtServiceImpl;
import com.strigalev.authenticationservice.security.model.CustomUserDetails;
import com.strigalev.starter.dto.TokenDTO;
import com.strigalev.starter.dto.UserDTO;
import com.strigalev.starter.rabbit.RabbitMQService;
import feign.FeignException;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {
    private final FeignClientService feignClientService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    private final RabbitMQService rabbitMQService;

    public UserServiceImpl(FeignClientService feignClientService,
                           @Lazy AuthenticationManager authenticationManager,
                           JwtServiceImpl jwtService,
                           RabbitMQService rabbitMQService) {
        this.feignClientService = feignClientService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.rabbitMQService = rabbitMQService;
    }

    @Override
    public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            return feignClientService.getUserDetailsByEmail(email).getBody();
        } catch (FeignException e) {
            throw new BadCredentialsException("User with email: " + email + " not found");
        }
    }

    @Override
    public TokenDTO login(SignInDTO dto) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    dto.getEmail(),
                    dto.getPassword()
            ));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
            rabbitMQService.sendAuditMessage("LOGIN", new Date(), user.getEmail());
            return jwtService.generateTokensPair(user);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Wrong password");
        }
    }

    @Override
    public void logout(String refreshToken) {
        jwtService.validateAndDeleteRefreshToken(refreshToken);
        rabbitMQService.sendAuditMessage("LOGOUT", new Date(), jwtService.getUserEmailFromRefreshToken(refreshToken));
    }

    @Override
    public TokenDTO updateRefreshToken(String refreshToken) {
        jwtService.validateAndDeleteRefreshToken(refreshToken);

        CustomUserDetails user = loadUserByUsername(jwtService.getUserEmailFromRefreshToken(refreshToken));

        return jwtService.generateTokensPair(user);
    }

    @Override
    public UserDTO validateAccessToken(String token) {
        jwtService.validateAccessToken(token);
        CustomUserDetails user = loadUserByUsername(jwtService.getUserEmailFromAccessToken(token));
        return UserDTO.builder()
                .newAccessToken(jwtService.generateAccessToken(user))
                .id(user.getId())
                .build();
    }

}


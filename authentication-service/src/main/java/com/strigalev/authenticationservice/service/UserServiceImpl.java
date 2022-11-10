package com.strigalev.authenticationservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strigalev.authenticationservice.dto.SignInDTO;
import com.strigalev.starter.dto.TokenDTO;
import com.strigalev.authenticationservice.feign.FeignClientService;
import com.strigalev.authenticationservice.jwt.JwtService;
import com.strigalev.authenticationservice.jwt.RefreshToken;
import com.strigalev.authenticationservice.repository.TokenRepository;
import com.strigalev.authenticationservice.security.model.CustomUserDetails;
import com.strigalev.starter.dto.ApiResponseEntity;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final FeignClientService feignClientService;
    private final ObjectMapper objectMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;

    public UserServiceImpl(FeignClientService feignClientService,
                           @Lazy ObjectMapper objectMapper,
                           @Lazy AuthenticationManager authenticationManager,
                           JwtService jwtService,
                           TokenRepository tokenRepository) {
        this.feignClientService = feignClientService;
        this.objectMapper = objectMapper;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return checkAndParseReceivedMessage(feignClientService.getUserDetailsByEmail(email));
    }

    @Override
    public TokenDTO login(SignInDTO dto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                dto.getEmail(),
                dto.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        return generateTokensAndSaveRefreshToken(user);
    }

    @Override
    public void logout(TokenDTO tokenDTO) {
        String refreshToken = tokenDTO.getRefreshToken();
        checkToken(refreshToken);
        tokenRepository.deleteById(jwtService.getTokenIdFromRefreshToken(refreshToken));
    }

    @Override
    public TokenDTO updateAccessToken(TokenDTO tokenDTO) {
        String refreshToken = tokenDTO.getRefreshToken();
        checkToken(refreshToken);

        CustomUserDetails user = loadUserByUsername(jwtService.getUserEmailFromAccessToken(refreshToken));
        String accessToken = jwtService.generateAccessToken(user);

        return TokenDTO.builder()
                .userId(user.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public TokenDTO updateRefreshToken(TokenDTO tokenDTO) {
        String refreshToken = tokenDTO.getRefreshToken();
        checkToken(refreshToken);

        tokenRepository.deleteById(jwtService.getTokenIdFromRefreshToken(refreshToken));

        CustomUserDetails user = loadUserByUsername(jwtService.getUserEmailFromRefreshToken(refreshToken));

        return generateTokensAndSaveRefreshToken(user);
    }

    @Override
    public TokenDTO validateToken(String token) {
        if (jwtService.validateAccessToken(token)) {
            CustomUserDetails user = loadUserByUsername(jwtService.getUserEmailFromAccessToken(token));
            return TokenDTO.builder()
                    .userId(user.getId())
                    .accessToken(jwtService.generateAccessToken(user))
                    .build();
        } else {
            throw new RuntimeException("INVALID TOKEN");
        }
    }


    private CustomUserDetails checkAndParseReceivedMessage(ApiResponseEntity apiResponseEntity) {
        if (apiResponseEntity.getObject() == null) {
            throw new RuntimeException("HTTP-STATUS: " + apiResponseEntity.getStatus() + "\n"
                    + apiResponseEntity.getMessage());
        }
        return objectMapper.convertValue(apiResponseEntity.getObject(), CustomUserDetails.class);
    }

    private TokenDTO generateTokensAndSaveRefreshToken(CustomUserDetails user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setOwnerId(user.getId());
        tokenRepository.save(refreshToken);

        String accessToken = jwtService.generateAccessToken(user);
        String newRefreshTokenString = jwtService.generateRefreshToken(user, refreshToken);
        refreshToken.setToken(newRefreshTokenString);
        tokenRepository.save(refreshToken);

        return TokenDTO.builder()
                .userId(user.getId())
                .accessToken(accessToken)
                .refreshToken(newRefreshTokenString)
                .build();
    }

    private boolean checkToken(String refreshToken) { // valid and exists in db
        if (jwtService.validateRefreshToken(refreshToken) &&
                tokenRepository.existsById(jwtService.getTokenIdFromRefreshToken(refreshToken))) {
            return true;
        }
        throw new BadCredentialsException("invalid token");
    }
}


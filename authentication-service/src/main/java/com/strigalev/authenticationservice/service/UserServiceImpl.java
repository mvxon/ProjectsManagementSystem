package com.strigalev.authenticationservice.service;

import com.strigalev.authenticationservice.domain.AccessCode;
import com.strigalev.authenticationservice.domain.User;
import com.strigalev.authenticationservice.dto.ResetPasswordDTO;
import com.strigalev.authenticationservice.dto.SignInDTO;
import com.strigalev.authenticationservice.dto.SignUpRequest;
import com.strigalev.authenticationservice.jwt.service.JwtService;
import com.strigalev.authenticationservice.jwt.service.JwtServiceImpl;
import com.strigalev.authenticationservice.mapper.UserMapper;
import com.strigalev.authenticationservice.repository.AccessCodeRepository;
import com.strigalev.authenticationservice.repository.UserRepository;
import com.strigalev.starter.dto.TokenDTO;
import com.strigalev.starter.exception.ResourceNotFoundException;
import com.strigalev.starter.model.Role;
import com.strigalev.starter.rabbit.RabbitMQService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.strigalev.starter.model.UserAction.*;
import static com.strigalev.starter.util.MethodsUtil.getUserWithEmailNotExistsMessage;

@Service
public class UserServiceImpl implements UserService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RabbitMQService rabbitMQService;
    private final PasswordEncoder passwordEncoder;
    private final AccessCodeRepository accessCodeRepository;
    private final UserMapper userMapper;
    @Value("${recovery.accessCodePartsCount}")
    private int accessCodePartsCount;
    @Value("${recovery.accessCodeExpirationHours}")
    private int accessCodeExpirationHours;

    public UserServiceImpl(
            @Lazy AuthenticationManager authenticationManager,
            JwtServiceImpl jwtService,
            UserRepository userRepository,
            RabbitMQService rabbitMQService,
            @Lazy PasswordEncoder passwordEncoder,
            AccessCodeRepository accessCodeRepository,
            UserMapper userMapper
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.rabbitMQService = rabbitMQService;
        this.passwordEncoder = passwordEncoder;
        this.accessCodeRepository = accessCodeRepository;
        this.userMapper = userMapper;
    }

    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {

        return userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new ResourceNotFoundException(getUserWithEmailNotExistsMessage(email))
                );
    }

    @Override
    @Transactional
    public TokenDTO signUp(SignUpRequest signUpRequest) {
        User user = userMapper.map(signUpRequest);
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setActive(true);
        user.setRole(Role.valueOf(signUpRequest.getRole()));
        userRepository.save(user);

        rabbitMQService.sendAuthAuditMessage(
                SIGN_UP,
                LocalDateTime.now(),
                userMapper.map(user)
        );

        return jwtService.generateTokensPair(user);
    }

    @Override
    public TokenDTO signIn(SignInDTO signInDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    signInDTO.getEmail(),
                    signInDTO.getPassword()
            ));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = (User) authentication.getPrincipal();

            rabbitMQService.sendAuthAuditMessage(
                    SIGN_IN,
                    LocalDateTime.now(),
                    userMapper.map(user)
            );

            return jwtService.generateTokensPair(user);
        } catch (BadCredentialsException e) {
            SecurityContextHolder.getContext().setAuthentication(null);
            throw new BadCredentialsException("Wrong password/not active user");
        }
    }

    @Override
    public void logout(String refreshToken) {
        jwtService.validateAndDeleteRefreshToken(refreshToken);

        User user = loadUserByUsername(jwtService.getUserEmailFromRefreshToken(refreshToken));
        rabbitMQService.sendAuthAuditMessage(
                LOGOUT,
                LocalDateTime.now(),
                userMapper.map(user)
        );
    }

    @Override
    public TokenDTO updateRefreshToken(String refreshToken) {
        jwtService.validateAndDeleteRefreshToken(refreshToken);

        User user = loadUserByUsername(jwtService.getUserEmailFromRefreshToken(refreshToken));

        return jwtService.generateTokensPair(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public void createAndSendAccessCode(String email) {
        User user = loadUserByUsername(email);

        accessCodeRepository.deleteAllByUserEmail(email);

        StringBuilder accessCode = new StringBuilder();
        for (int i = 0; i < accessCodePartsCount; i++) {
            accessCode.append((long) (Math.random() * (999 - 100 + 1) + 100));
            accessCode.append(" ");
        }
        String accessCodeValue = accessCode.toString().replaceAll("\\s", "");

        AccessCode accessCodeEntity = AccessCode.builder()
                .userEmail(email)
                .accessCodeValue(accessCodeValue)
                .creationDateTime(LocalDateTime.now())
                .build();

        accessCodeRepository.save(accessCodeEntity);


        rabbitMQService.sendAuthAuditMessage(
                REQUEST_RESET_PASSWORD,
                LocalDateTime.now(),
                userMapper.map(user)
        );

        rabbitMQService.sendMailMessage(
                email,
                String.format("Hello, %s!\nYour access code: " + accessCode, user.getFirstName()),
                "Password recovering",
                REQUEST_RESET_PASSWORD
        );
    }


    @Override
    @Transactional
    public void resetPassword(ResetPasswordDTO resetPasswordDTO) {
        AccessCode accessCode = accessCodeRepository.findByUserEmail(resetPasswordDTO.getEmail());
        if (accessCode == null || !Objects.equals(accessCode.getAccessCodeValue(), resetPasswordDTO.getAccessCode())) {
            throw new ResourceNotFoundException("Invalid access code");
        }

        LocalDateTime creationDateTime = accessCode.getCreationDateTime();

        if (creationDateTime.isAfter(creationDateTime.plusHours(accessCodeExpirationHours))) {
            throw new BadCredentialsException("Access code expired");
        }

        User user = loadUserByUsername(resetPasswordDTO.getEmail());
        user.setPassword(passwordEncoder.encode(resetPasswordDTO.getPassword()));

        accessCodeRepository.deleteAllByUserEmail(resetPasswordDTO.getEmail());

        userRepository.save(user);

        rabbitMQService.sendMailMessage(
                user.getEmail(),
                String.format("Dear %s, your password is successfully changed!", user.getFirstName()),
                "Password is changed",
                RESET_PASSWORD
        );

        rabbitMQService.sendAuthAuditMessage(
                RESET_PASSWORD,
                LocalDateTime.now(),
                userMapper.map(user)
        );
    }
}
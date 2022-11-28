package com.strigalev.authenticationservice.service;

import com.strigalev.authenticationservice.domain.AccessCode;
import com.strigalev.authenticationservice.domain.User;
import com.strigalev.authenticationservice.dto.ResetPasswordDTO;
import com.strigalev.authenticationservice.dto.SignInDTO;
import com.strigalev.authenticationservice.dto.SignUpRequest;
import com.strigalev.authenticationservice.jwt.service.JwtService;
import com.strigalev.authenticationservice.jwt.service.JwtServiceImpl;
import com.strigalev.authenticationservice.repository.AccessCodeRepository;
import com.strigalev.authenticationservice.repository.UserRepository;
import com.strigalev.starter.dto.MailMessageDTO;
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
            AccessCodeRepository accessCodeRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.rabbitMQService = rabbitMQService;
        this.passwordEncoder = passwordEncoder;
        this.accessCodeRepository = accessCodeRepository;
    }

    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {

        return userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new ResourceNotFoundException(getUserWithEmailNotExistsMessage(email))
                );
    }

    @Transactional
    public void setUserUnActive(String email) {
        User user = loadUserByUsername(email);
        user.setActive(false);

        userRepository.save(user);
    }


    @Override
    @Transactional
    public TokenDTO signUp(SignUpRequest signUpRequest) {
        User user = userRepository.save(
                User.builder()
                        .email(signUpRequest.getEmail())
                        .firstName(signUpRequest.getFirstName())
                        .lastName(signUpRequest.getLastName())
                        .role(Role.valueOf(signUpRequest.getRole()))
                        .password(passwordEncoder.encode(signUpRequest.getPassword()))
                        .active(true)
                        .build());

        rabbitMQService.sendAuthAuditMessage(SIGN_UP, LocalDateTime.now(), user.getEmail());

        return jwtService.generateTokensPair(user);
    }

    @Override
    public TokenDTO login(SignInDTO dto) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    dto.getEmail(),
                    dto.getPassword()
            ));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = (User) authentication.getPrincipal();

            rabbitMQService.sendAuthAuditMessage(SIGN_IN, LocalDateTime.now(), user.getEmail());

            return jwtService.generateTokensPair(user);
        } catch (BadCredentialsException e) {
            SecurityContextHolder.getContext().setAuthentication(null);
            throw new BadCredentialsException("Wrong password/not active user");
        }
    }

    @Override
    public void signIn(String refreshToken) {
        jwtService.validateAndDeleteRefreshToken(refreshToken);

        rabbitMQService.sendAuthAuditMessage(LOGOUT, LocalDateTime.now(),
                jwtService.getUserEmailFromRefreshToken(refreshToken)
        );
    }

    @Override
    public TokenDTO updateRefreshToken(String refreshToken) {
        jwtService.validateAndDeleteRefreshToken(refreshToken);

        User user = loadUserByUsername(jwtService.getUserEmailFromRefreshToken(refreshToken));

        return jwtService.generateTokensPair(user);
    }

    @Override
    public Long validateAccessToken(String token) {
        jwtService.validateAccessToken(token);
        User user = loadUserByUsername(jwtService.getUserEmailFromAccessToken(token));
        return user.getId();
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public void createAndSendAccessCode(String email) {

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


        rabbitMQService.sendAuthAuditMessage(REQUEST_RESET_PASSWORD, LocalDateTime.now(), email);

        rabbitMQService.sendMailMessage(MailMessageDTO.builder()
                .toEmail(email)
                .subject("Password recovering")
                .body("Your access code: " + accessCode)
                .build());
    }


    @Override
    @Transactional
    public void resetPassword(ResetPasswordDTO resetPasswordDTO) {
        AccessCode accessCode = accessCodeRepository.findByUserEmail(resetPasswordDTO.getEmail());
        if (accessCode == null) {
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

        rabbitMQService.sendMailMessage(MailMessageDTO.builder()
                .toEmail(user.getEmail())
                .subject("Password is changed")
                .body("Your password is successfully changed.")
                .build());

        rabbitMQService.sendAuthAuditMessage(RESET_PASSWORD, LocalDateTime.now(), user.getEmail());
    }
}
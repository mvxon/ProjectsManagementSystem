package com.strigalev.authenticationservice.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.strigalev.authenticationservice.domain.User;
import com.strigalev.authenticationservice.jwt.RefreshToken;
import com.strigalev.authenticationservice.repository.TokenRepository;
import com.strigalev.starter.dto.TokenDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class JwtServiceImpl implements JwtService {
    private static final String issuer = "Projects-Management-System";
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;
    private final Algorithm accessTokenAlgorithm;
    private final Algorithm refreshTokenAlgorithm;
    private final JWTVerifier refreshTokenVerifier;
    private final TokenRepository tokenRepository;

    @Autowired
    public JwtServiceImpl(@Value("${jwt.accessTokenSecret}") String accessTokenSecret,
                          @Value("${jwt.refreshTokenSecret}") String refreshTokenSecret,
                          @Value("${jwt.refreshTokenExpirationDays}") int refreshTokenExpirationDays,
                          @Value("${jwt.accessTokenExpirationMinutes}") int accessTokenExpirationMinutes,
                          TokenRepository tokenRepository
    ) {
        accessTokenExpirationMs = (long) accessTokenExpirationMinutes * 60 * 1000;
        refreshTokenExpirationMs = (long) refreshTokenExpirationDays * 24 * 60 * 60 * 1000;
        accessTokenAlgorithm = Algorithm.HMAC512(accessTokenSecret);
        refreshTokenAlgorithm = Algorithm.HMAC512(refreshTokenSecret);
        this.tokenRepository = tokenRepository;
        refreshTokenVerifier = JWT.require(refreshTokenAlgorithm)
                .withIssuer(issuer)
                .build();
    }

    public String generateAccessToken(User user) {
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(user.getEmail())
                .withClaim("role", user.getRole().toString())
                .withClaim("userId", user.getId())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(new Date().getTime() + accessTokenExpirationMs))
                .sign(accessTokenAlgorithm);
    }

    public String generateRefreshToken(User user, RefreshToken refreshToken) {
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(user.getEmail())
                .withClaim("tokenId", refreshToken.getId())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date((new Date()).getTime() + refreshTokenExpirationMs))
                .sign(refreshTokenAlgorithm);
    }

    private Optional<DecodedJWT> decodeRefreshToken(String token) {
        try {
            return Optional.of(refreshTokenVerifier.verify(token));
        } catch (JWTVerificationException e) {
            throw new BadCredentialsException("Invalid refresh token");
        }
    }

    public void validateAndDeleteRefreshToken(String refreshToken) {
        validateRefreshToken(refreshToken);
        tokenRepository.deleteById(getTokenIdFromRefreshToken(refreshToken));
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public TokenDTO generateTokensPair(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setOwnerId(user.getId());

        tokenRepository.save(refreshToken);
        refreshToken.setToken(generateRefreshToken(user, refreshToken));
        tokenRepository.save(refreshToken);

        return TokenDTO.builder()
                .accessToken(generateAccessToken(user))
                .refreshToken(refreshToken.getToken())
                .build();
    }

    public void validateRefreshToken(String token) {
        decodeRefreshToken(token);
        if (!tokenRepository.existsById(getTokenIdFromRefreshToken(token))) {
            throw new BadCredentialsException("Invalid refresh token");
        }
    }

    public String getUserEmailFromRefreshToken(String token) {
        return decodeRefreshToken(token).get().getSubject();
    }

    public Long getTokenIdFromRefreshToken(String token) {
        return decodeRefreshToken(token).get().getClaim("tokenId").asLong();
    }
}
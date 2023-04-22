package com.strigalev.starter.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JWTUtil {
    private static final String issuer = "Projects-Management-System";

    private final JWTVerifier accessTokenVerifier;

    @Autowired
    public JWTUtil(@Value("${jwt.accessTokenSecret}") String accessTokenSecret) {
        Algorithm accessTokenAlgorithm = Algorithm.HMAC512(accessTokenSecret);
        accessTokenVerifier = JWT.require(accessTokenAlgorithm)
                .withIssuer(issuer)
                .build();
    }


    public Optional<DecodedJWT> decodeAccessToken(String token) {
        try {
            return Optional.of(accessTokenVerifier.verify(token));
        } catch (JWTVerificationException e) {
            throw new BadCredentialsException("Invalid access token");
        }
    }

    public JwtClaims getJwtClaims(String token) {
        DecodedJWT decodedJWT = decodeAccessToken(token).get();
        return JwtClaims.builder()
                .id(decodedJWT.getClaim("userId").asLong())
                .role(decodedJWT.getClaim("role").asString())
                .build();
    }

}
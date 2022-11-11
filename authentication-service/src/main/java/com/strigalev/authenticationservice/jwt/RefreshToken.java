package com.strigalev.authenticationservice.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;


@Data
@Builder
@RedisHash(value = "refresh_token", timeToLive = 2592000)
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {
    @Id
    private Long id;
    private Long ownerId;
    private String token;
}

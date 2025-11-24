package com.memoring.memoring_server.global.config.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret;
    private long accessTokenExpiration = Duration.ofMinutes(10).toMillis();
    private long refreshTokenExpiration = Duration.ofDays(7).toMillis();
}

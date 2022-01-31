package com.chubock.userservice.component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.chubock.userservice.config.security.AppSecurityProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JWTTokenUtil {

    public static final String SCOPES_CLAIM_NAME = "scopes";
    public static final String AUTHORITIES_CLAIM_NAME = "authorities";
    public static final String AUTH_TOKEN_SCOPE = "auth_scope";
    public static final String REFRESH_TOKEN_SCOPE = "refresh_token";

    private final AppSecurityProperties appSecurityProperties;

    public JWTTokenUtil(AppSecurityProperties appSecurityProperties) {
        this.appSecurityProperties = appSecurityProperties;
    }

    public String createAccessToken(UserDetails user) {

        LocalDateTime expireAt = LocalDateTime.now()
                .plus(appSecurityProperties.getAccessTokenExpirationMilliseconds(), ChronoUnit.MILLIS);

        List<String> authorities = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return JWT.create()
                .withSubject(user.getUsername())
                .withIssuedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
                .withExpiresAt(Date.from(expireAt.atZone(ZoneId.systemDefault()).toInstant()))
                .withClaim(SCOPES_CLAIM_NAME, Collections.singletonList(AUTH_TOKEN_SCOPE))
                .withClaim(AUTHORITIES_CLAIM_NAME, authorities)
                .sign(Algorithm.HMAC512(appSecurityProperties.getTokenSecret().getBytes()));
    }

    public String createRefreshToken(UserDetails user) {

        LocalDateTime expireAt = LocalDateTime.now()
                .plus(appSecurityProperties.getRefreshTokenExpirationMilliseconds(), ChronoUnit.MILLIS);

        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withSubject(user.getUsername())
                .withIssuedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
                .withExpiresAt(Date.from(expireAt.atZone(ZoneId.systemDefault()).toInstant()))
                .withClaim(SCOPES_CLAIM_NAME, Collections.singletonList(REFRESH_TOKEN_SCOPE))
                .sign(Algorithm.HMAC512(appSecurityProperties.getTokenSecret().getBytes()));

    }

    public DecodedJWT decode(String token) {

        return JWT.require(Algorithm.HMAC512(appSecurityProperties.getTokenSecret().getBytes()))
                .build()
                .verify(token);

    }

}

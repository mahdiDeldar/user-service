package com.chubock.userservice.rest;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.chubock.userservice.component.JWTTokenUtil;
import com.chubock.userservice.model.TokenModel;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@RestController
public class RefreshTokenRestController {

    private static final String REFRESH_TOKEN_ENDPOINT = "/refresh";
    private static final String HEADER_STRING = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    private final UserDetailsService userService;
    private final JWTTokenUtil jwtTokenUtil;

    public RefreshTokenRestController(UserDetailsService userService, JWTTokenUtil jwtTokenUtil) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping(REFRESH_TOKEN_ENDPOINT)
    @Operation(summary = "Refresh auth token")
    public TokenModel refreshToken(@RequestHeader(HEADER_STRING) String authorization) {

        String token = authorization.replace(TOKEN_PREFIX, "");

        DecodedJWT decodedJWT = jwtTokenUtil.decode(token);

        if (decodedJWT.getExpiresAt().before(new Date()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        UserDetails user = userService.loadUserByUsername(decodedJWT.getSubject());

        if (!user.isAccountNonLocked() || !user.isAccountNonExpired() || !user.isEnabled())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        String accessToken = jwtTokenUtil.createAccessToken(user);
        String refreshToken = jwtTokenUtil.createRefreshToken(user);

        return new TokenModel(accessToken, refreshToken);

    }

}

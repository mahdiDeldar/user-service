package com.chubock.userservice.config.security.jwt;

import com.chubock.userservice.component.JWTTokenUtil;
import com.chubock.userservice.config.security.token.EmailPasswordAuthenticationToken;
import com.chubock.userservice.config.security.token.OAuthOtpAuthenticationToken;
import com.chubock.userservice.config.security.token.PhoneOtpAuthenticationToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final String EMAIL_PROPERTY = "email";
    private static final String PASSWORD_PROPERTY = "password";
    private static final String PHONE_PROPERTY = "phone";
    private static final String CODE_PROPERTY = "code";
    private static final String SUB_PROPERTY = "sub";
    private static final String TOKEN_PROPERTY = "token";

    private AuthenticationManager authenticationManager;
    private JWTTokenUtil jwtTokenUtil;
    private ObjectMapper objectMapper;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager,
                                   JWTTokenUtil jwtTokenUtil, ObjectMapper objectMapper) {

        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.objectMapper = objectMapper;

//        setFilterProcessesUrl("/api/services/controller/user/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {

            Map<?, ?> body = new ObjectMapper().readValue(req.getInputStream(), Map.class);

            Authentication authentication;

            if (body.containsKey(EMAIL_PROPERTY) && body.containsKey(PASSWORD_PROPERTY))
                authentication = new EmailPasswordAuthenticationToken(body.get(EMAIL_PROPERTY), body.get(PASSWORD_PROPERTY));
            else if (body.containsKey(PHONE_PROPERTY) && body.containsKey(CODE_PROPERTY))
                authentication = new PhoneOtpAuthenticationToken(body.get(PHONE_PROPERTY), body.get(CODE_PROPERTY));
            else if (body.containsKey(SUB_PROPERTY) && body.containsKey(TOKEN_PROPERTY))
                authentication = new OAuthOtpAuthenticationToken(body.get(SUB_PROPERTY), body.get(TOKEN_PROPERTY));
            else
                throw new AuthenticationCredentialsNotFoundException("");

            return authenticationManager.authenticate(authentication);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException {

        Map<String, String> body = new HashMap<>();
        body.put("access_token", jwtTokenUtil.createAccessToken((UserDetails) auth.getPrincipal()));
        body.put("refresh_token", jwtTokenUtil.createRefreshToken((UserDetails) auth.getPrincipal()));

        res.setStatus(HttpStatus.OK.value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(res.getWriter(), body);

    }
}

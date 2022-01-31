package com.chubock.userservice.config.security.token;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class OAuthOtpAuthenticationToken extends UsernamePasswordAuthenticationToken {
    public OAuthOtpAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

}

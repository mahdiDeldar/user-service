package com.chubock.userservice.config.security.token;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class PhoneOtpAuthenticationToken extends UsernamePasswordAuthenticationToken {

    public PhoneOtpAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

}

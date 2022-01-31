package com.chubock.userservice.config.security.provider;

import com.chubock.userservice.config.security.token.EmailPasswordAuthenticationToken;
import com.chubock.userservice.repository.LocalUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class EmailPasswordAuthenticationProvider implements AuthenticationProvider {

    private final LocalUserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    public EmailPasswordAuthenticationProvider(LocalUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        return userRepository.findByEmail(authentication.getName())
                .map(user -> {

                    if (!passwordEncoder.matches(authentication.getCredentials().toString(), user.getPassword()))
                        throw new BadCredentialsException("user not found");

                    return new UsernamePasswordAuthenticationToken(user,
                            authentication.getCredentials(), Collections.emptyList());

                }).orElse(null);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(EmailPasswordAuthenticationToken.class);
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}

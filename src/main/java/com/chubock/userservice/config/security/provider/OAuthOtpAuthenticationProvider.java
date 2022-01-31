package com.chubock.userservice.config.security.provider;

import com.chubock.userservice.config.security.token.OAuthOtpAuthenticationToken;
import com.chubock.userservice.entity.OAuthUser;
import com.chubock.userservice.repository.OAuthUserRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class OAuthOtpAuthenticationProvider implements AuthenticationProvider {

    private static final int MAX_ATTEMPT_COUNT = 5;

    private final OAuthUserRepository userRepository;

    public OAuthOtpAuthenticationProvider(OAuthUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        return userRepository.findById(authentication.getName())
                .map(user -> {

                    user.increaseAttempt();

                    if (!authentication.getCredentials().equals(user.getOtpToken())) {
                        failAuthentication(user);
                        throw new BadCredentialsException("user not found");
                    }

                    user.setOtpToken(null);
                    userRepository.save(user);

                    return new UsernamePasswordAuthenticationToken(user,
                            authentication.getCredentials(), Collections.emptyList());

                }).orElse(null);
    }

    private void failAuthentication(OAuthUser user) {
        user.increaseAttempt();
        if (user.getAttempt() >= MAX_ATTEMPT_COUNT)
            user.setOtpToken(null);
        userRepository.save(user);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(OAuthOtpAuthenticationToken.class);
    }

}

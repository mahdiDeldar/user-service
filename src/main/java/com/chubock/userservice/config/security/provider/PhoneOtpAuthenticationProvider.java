package com.chubock.userservice.config.security.provider;

import com.chubock.userservice.config.security.token.PhoneOtpAuthenticationToken;
import com.chubock.userservice.repository.LocalUserRepository;
import com.chubock.userservice.service.VerificationCodeService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class PhoneOtpAuthenticationProvider implements AuthenticationProvider {

    private final LocalUserRepository userRepository;

    private final VerificationCodeService verificationCodeService;

    public PhoneOtpAuthenticationProvider(LocalUserRepository userRepository, VerificationCodeService verificationCodeService) {
        this.userRepository = userRepository;
        this.verificationCodeService = verificationCodeService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        return userRepository.findByPhone(authentication.getName())
                .map(user -> {

                    if (!verificationCodeService.verify(authentication.getName(), authentication.getCredentials().toString()))
                        throw new BadCredentialsException("user not found");


                    return new UsernamePasswordAuthenticationToken(user,
                            authentication.getCredentials(), Collections.emptyList());

                }).orElse(null);

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(PhoneOtpAuthenticationToken.class);
    }
}

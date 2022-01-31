package com.chubock.userservice.service;

import com.chubock.userservice.entity.LocalUser;
import com.chubock.userservice.repository.LocalUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocalUserService {

    private final PasswordEncoder passwordEncoder;

    private final LocalUserRepository localUserRepository;

    public LocalUserService(PasswordEncoder passwordEncoder, LocalUserRepository localUserRepository) {
        this.passwordEncoder = passwordEncoder;
        this.localUserRepository = localUserRepository;
    }

    @Transactional
    public void updatePassword(String userId, String password) {
        LocalUser user = localUserRepository.findById(userId)
                .orElseThrow();
        user.setPassword(passwordEncoder.encode(password));
    }
}

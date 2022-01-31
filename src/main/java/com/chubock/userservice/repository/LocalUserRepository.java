package com.chubock.userservice.repository;

import com.chubock.userservice.entity.LocalUser;
import com.chubock.userservice.entity.User;

import java.util.Optional;

public interface LocalUserRepository extends AbstractUserRepository<LocalUser> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}

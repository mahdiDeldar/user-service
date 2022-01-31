package com.chubock.userservice.repository;

import com.chubock.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbstractUserRepository<T extends User> extends JpaRepository<T, String> {
    boolean existsByNickname(String nickname);
}

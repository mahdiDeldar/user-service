package com.chubock.userservice.repository;

import com.chubock.userservice.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, String> {

    Optional<VerificationCode> findByPhoneAndExpiredIsFalse(String phone);
    Optional<VerificationCode> findFirstByPhoneAndExpiredIsFalseOrderByCreatedDateDesc(String phone);

}

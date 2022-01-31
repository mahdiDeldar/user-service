package com.chubock.userservice.service;

import com.chubock.userservice.component.EmailSender;
import com.chubock.userservice.component.JWTTokenUtil;
import com.chubock.userservice.entity.LocalUser;
import com.chubock.userservice.model.RegistrationModel;
import com.chubock.userservice.model.TokenModel;
import com.chubock.userservice.repository.LocalUserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
public class RegistrationService {

    private static final String ACTIVATE_EMAIL_SUBJECT = "Activate Your Email at Keys";
    private static final String ACTIVATE_EMAIL_BODY_PREFIX = "To activate you email address at Keys click here: ";

    private final EmailSender emailSender;
    private final PasswordEncoder passwordEncoder;

    private final LocalUserRepository userRepository;

    private final VerificationCodeService verificationCodeService;

    private final JWTTokenUtil jwtTokenUtil;

    private final String baseUrl;

    public RegistrationService(EmailSender emailSender,
                               PasswordEncoder passwordEncoder,
                               LocalUserRepository userRepository,
                               VerificationCodeService verificationCodeService,
                               JWTTokenUtil jwtTokenUtil,
                               @Value("${app.host}") String baseUrl) {
        this.emailSender = emailSender;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.verificationCodeService = verificationCodeService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.baseUrl = baseUrl;
    }

    @Transactional
    public TokenModel register(RegistrationModel model) {

        if (userRepository.existsByEmail(model.getEmail()))
            throw new IllegalArgumentException("email has already been taken");

        if (userRepository.existsByPhone(model.getPhone()))
            throw new IllegalArgumentException("phone has already been registered");

        if (!verificationCodeService.verify(model.getPhone(), model.getVerificationCode()))
            throw new IllegalArgumentException("phone number is not verified");

        LocalUser user = LocalUser.builder()
                .email(model.getEmail())
                .phone(model.getPhone())
                .password(passwordEncoder.encode(model.getPassword()))
                .emailVerificationCode(RandomStringUtils.randomAlphanumeric(10))
                .build();

        userRepository.save(user);

        String emailActivationLink = ACTIVATE_EMAIL_BODY_PREFIX +
                baseUrl + "/register/users/" + user.getId() + "/email-validation-code/" +
                user.getEmailVerificationCode();

        CompletableFuture.runAsync(() ->
                emailSender.send(user.getEmail(), ACTIVATE_EMAIL_SUBJECT, emailActivationLink));

        String accessToken = jwtTokenUtil.createAccessToken(user);
        String refreshToken = jwtTokenUtil.createRefreshToken(user);

        return new TokenModel(accessToken, refreshToken);

    }

    @Transactional
    public void validateEmail(String userId, String code) {

        LocalUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        if (!user.getEmailVerificationCode().equals(code))
            throw new IllegalArgumentException("invalid code");

        user.setEmailVerified(true);

    }

    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean phoneExists(String phone) {
        return userRepository.existsByPhone(phone);
    }

    @Transactional(readOnly = true)
    public boolean nicknameExists(String username) {
        return userRepository.existsByNickname(username);
    }

}

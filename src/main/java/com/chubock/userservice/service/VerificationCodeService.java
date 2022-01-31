package com.chubock.userservice.service;

import com.chubock.userservice.component.Messages;
import com.chubock.userservice.endpoint.SmsEndpoint;
import com.chubock.userservice.entity.VerificationCode;
import com.chubock.userservice.repository.VerificationCodeRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class VerificationCodeService {

    private static final int VERIFICATION_CODE_LENGTH = 4;
    private static final int VERIFICATION_CODE_LIFE_TIME = 180;
    private static final int VERIFICATION_CODE_MAX_ATTEMPT = 3;

    private final VerificationCodeRepository verificationCodeRepository;

    private final SmsEndpoint smsEndpoint;

    private final Messages messages;

    public VerificationCodeService(VerificationCodeRepository verificationCodeRepository,
                                   SmsEndpoint smsEndpoint,
                                   Messages messages) {
        this.verificationCodeRepository = verificationCodeRepository;
        this.smsEndpoint = smsEndpoint;
        this.messages = messages;
    }

    @Transactional
    public String generate(String phone) {

        VerificationCode verificationCode = verificationCodeRepository.findByPhoneAndExpiredIsFalse(phone)
                .filter(vcode -> {

                    vcode.setExpired(vcode.getCreatedDate()
                            .isBefore(LocalDateTime.now().minusSeconds(VERIFICATION_CODE_LIFE_TIME)));

                    return !vcode.isExpired();

                }).orElseGet(VerificationCode.builder()
                        .phone(phone).code(RandomStringUtils.randomNumeric(VERIFICATION_CODE_LENGTH))::build);


        verificationCodeRepository.save(verificationCode);

        String code = verificationCode.getCode();

        smsEndpoint.sendMessage(phone, messages.getMessage("sms.verification", code));

        return code;

    }

    @Transactional
    public boolean verify(String phone, String code) {

        return verificationCodeRepository.findByPhoneAndExpiredIsFalse(phone)
                .filter(verificationCode -> {

                    verificationCode.setExpired(verificationCode.getCreatedDate()
                            .isBefore(LocalDateTime.now().minusSeconds(VERIFICATION_CODE_LIFE_TIME)));
                    return !verificationCode.isExpired();

                }).filter(verificationCode -> {

                    verificationCode.setExpired(verificationCode.getAttempt() >= VERIFICATION_CODE_MAX_ATTEMPT);
                    return !verificationCode.isExpired();

                }).map(verificationCode -> {

                    verificationCode.increaseAttempt();

                    verificationCode.setExpired(verificationCode.getCode().equals(code));

                    return verificationCode.isExpired();

                }).orElse(false);

    }

}

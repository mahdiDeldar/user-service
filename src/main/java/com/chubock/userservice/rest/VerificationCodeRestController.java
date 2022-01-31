package com.chubock.userservice.rest;

import com.chubock.userservice.service.VerificationCodeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@RestController
public class VerificationCodeRestController {

    private final VerificationCodeService verificationCodeService;

    public VerificationCodeRestController(VerificationCodeService verificationCodeService) {
        this.verificationCodeService = verificationCodeService;
    }

    @PostMapping("/verification")
    @Operation(summary = "Send OTP verification code")
    public String sendVerificationCode(@Validated @RequestBody PhoneVerificationRequest request) {
        //TODO chubock: return void later.
        return verificationCodeService.generate(request.getPhone());
    }

    @Getter
    @Setter
    public static class PhoneVerificationRequest {
        @NotBlank
        @Pattern(regexp = "(\\+|00)\\d{6,}", message = "phone number is not valid")
        private String phone;
    }

}

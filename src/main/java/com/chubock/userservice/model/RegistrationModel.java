package com.chubock.userservice.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class RegistrationModel {

    @NotBlank(message = "email can't be empty")
    @Pattern(regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", message = "email address is not valid")
    private String email;
    @NotBlank(message = "password can't be empty")
    private String password;
    @NotBlank(message = "phone number can't be empty")
    @Pattern(regexp = "(\\+|00)\\d{6,}", message = "phone number is not valid")
    private String phone;
    @NotBlank(message = "verification code can't be empty")
    @Pattern(regexp = "\\d{4,}", message = "verification code is not valid")
    private String verificationCode;

}

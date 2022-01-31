package com.chubock.userservice.rest;

import com.chubock.userservice.service.LocalUserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;

@RestController
public class UpdatePasswordRestController {

    private final LocalUserService localUserService;

    public UpdatePasswordRestController(LocalUserService localUserService) {
        this.localUserService = localUserService;
    }

    @PostMapping("/update-password")
    @Operation(summary = "Update password")
    public void updatePassword(Authentication authentication, @Validated @RequestBody Request request) {
        localUserService.updatePassword(authentication.getName(), request.getPassword());
    }

    @Getter
    @Setter
    public static class Request {
        @NotEmpty
        private String password;
    }
}

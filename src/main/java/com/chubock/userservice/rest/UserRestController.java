package com.chubock.userservice.rest;

import com.chubock.userservice.model.UserModel;
import com.chubock.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("me")
    @Operation(summary = "Get current user details")
    public UserModel getAuthentication(Authentication authentication) {
        return userService.getProfile(authentication.getName());
    }

    @GetMapping("{username}")
    @Operation(summary = "Get user details")
    public UserModel getAuthentication(@PathVariable("username") String username) {
        return userService.getUser(username);
    }

    @PutMapping("me")
    @Operation(summary = "Update user details")
    public UserModel updateProfile(Authentication authentication, @RequestBody UserModel model) {

        model.setId(authentication.getName());
        return userService.update(model);

    }

}

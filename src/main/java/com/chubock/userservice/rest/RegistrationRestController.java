package com.chubock.userservice.rest;

import com.chubock.userservice.model.RegistrationModel;
import com.chubock.userservice.model.TokenModel;
import com.chubock.userservice.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Pattern;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/register")
public class RegistrationRestController {

    private final String host;
    private final RegistrationService registrationService;

    public RegistrationRestController(@Value("${app.host}") String host,
                                      RegistrationService registrationService) {
        this.host = host;
        this.registrationService = registrationService;
    }

    @GetMapping("/oauth2/redirect")
    public String oauth2Redirect(HttpServletRequest request) {
        String sub = request.getParameter("sub");
        String token = request.getParameter("token");
        return "curl -XPOST -H \"Content-Type: application/json\" " + host +
                "/login -d '{\"sub\": \"" + sub + "\", \"token\": \"" + token + "\"}'";
    }

    @PostMapping
    @Operation(summary = "Register a new user")
    public TokenModel register(@Validated @RequestBody RegistrationModel model) {
        return registrationService.register(model);
    }

    @GetMapping("/check")
    @Operation(summary = "Checking users properties validity")
    public Map<String, Boolean> check(
            @RequestParam(value = "email", required = false)
                @Pattern(regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", message = "email address is not valid")
                    String email,
            @RequestParam(value = "phone", required = false)
                @Pattern(regexp = "(\\+|00)\\d{6,}", message = "phone number is not valid")
                    String phone,
            @RequestParam(value = "username", required = false) String nickname) {
        Map<String, Boolean> response = new HashMap<>();

        if (email != null)
            response.put("email", registrationService.emailExists(email));

        if (phone != null)
            response.put("phone", registrationService.phoneExists(phone));

        if (nickname != null)
            response.put("username", registrationService.nicknameExists(nickname));

        return response;
    }

    @GetMapping("/users/{id}/email-validation-code/{code}")
    @Operation(summary = "Validates user email address")
    public void validateEmail(@PathVariable String id, @PathVariable String code) {
        registrationService.validateEmail(id, code);
        //TODO chubock: here we should redirect user to a page maybe.
    }

}


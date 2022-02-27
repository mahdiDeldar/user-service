package com.chubock.userservice.rest;

import com.chubock.userservice.Exception.UserNotFoundException;
import com.chubock.userservice.model.UserModel;
import com.chubock.userservice.service.TerminationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/termination")
public class TerminationRestController {
    private TerminationService terminationService;

    @GetMapping()
    @Operation(summary = "Try to delete user info after deleting user activities")
    public void UserTermination(Authentication authentication) {
        terminationService.userTermination(new UserModel(authentication.getName()).getId());
    }
}

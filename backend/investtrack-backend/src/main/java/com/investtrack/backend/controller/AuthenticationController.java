package com.investtrack.backend.controller;

import com.investtrack.backend.dto.AuthResponse;
import com.investtrack.backend.dto.LoginRequest;
import com.investtrack.backend.dto.RegisterRequest;
import com.investtrack.backend.entity.AppUser;
import com.investtrack.backend.service.AuthenticationService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(
            AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> register(
            @Valid @RequestBody RegisterRequest request) {

        AppUser user = authenticationService.register(request);

        return Map.of(
                "message", "Registration successful",
                "email", user.getEmail()
        );
    }

    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody LoginRequest request) {

        return authenticationService.login(
                request.email(),
                request.password()
        );
    }
}

package com.investtrack.backend.service;

import com.investtrack.backend.dto.RegisterRequest;
import com.investtrack.backend.entity.AppUser;
import com.investtrack.backend.repository.AppUserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.investtrack.backend.dto.AuthResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtEncoder jwtEncoder;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void register_shouldSaveUserWithEncodedPassword() {

        RegisterRequest request = new RegisterRequest(
                "TEST@example.com",
                "StrongPassword@123"
        );

        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("StrongPassword@123"))
                .thenReturn("bcrypt-hashed-password");

        when(userRepository.save(any(AppUser.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AppUser result = authenticationService.register(request);

        assertEquals("test@example.com", result.getEmail());
        assertEquals("bcrypt-hashed-password", result.getPassword());
        assertEquals("USER", result.getRole());

        verify(userRepository).save(any(AppUser.class));
        verify(passwordEncoder).encode("StrongPassword@123");
    }

    @Test
    void register_whenEmailAlreadyExists_shouldReturnConflict() {

        RegisterRequest request = new RegisterRequest(
                "test@example.com",
                "StrongPassword@123"
        );

        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authenticationService.register(request)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());

        verify(userRepository, never()).save(any(AppUser.class));
    }

    @Test
    void login_withValidCredentials_shouldGenerateJwtToken() {

        AppUser user = new AppUser();
        user.setEmail("test@example.com");
        user.setPassword("encoded-password");
        user.setRole("USER");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "StrongPassword@123", "encoded-password"))
                .thenReturn(true);

        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("mock-access-token");

        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(jwt);

        AuthResponse response = authenticationService.login(
                "TEST@example.com", "StrongPassword@123");

        assertEquals("mock-access-token", response.accessToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(900, response.expiresIn());
        assertEquals("test@example.com", response.email());
        assertEquals("USER", response.role());

        verify(jwtEncoder).encode(any(JwtEncoderParameters.class));
    }

    @Test
    void login_withInvalidPassword_shouldReturnUnauthorized() {

        AppUser user = new AppUser();
        user.setEmail("test@example.com");
        user.setPassword("encoded-password");
        user.setRole("USER");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "WrongPassword@123", "encoded-password"))
                .thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authenticationService.login(
                        "test@example.com", "WrongPassword@123")
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());

        verify(jwtEncoder, never())
                .encode(any(JwtEncoderParameters.class));
    }
}

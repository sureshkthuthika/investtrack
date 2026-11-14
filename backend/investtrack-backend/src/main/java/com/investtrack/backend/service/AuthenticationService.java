package com.investtrack.backend.service;

import com.investtrack.backend.dto.AuthResponse;
import com.investtrack.backend.dto.RegisterRequest;
import com.investtrack.backend.entity.AppUser;
import com.investtrack.backend.repository.AppUserRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Locale;

@Service
public class AuthenticationService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    public AuthenticationService(
            AppUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtEncoder jwtEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
    }

    public AppUser register(RegisterRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already registered"
            );
        }

        AppUser user = new AppUser();

        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole("USER");

        try {
            return userRepository.save(user);
        } catch (DuplicateKeyException ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already registered"
            );
        }
    }

    public AuthResponse login(String email, String password) {

        AppUser user = userRepository.findByEmail(
                email.trim().toLowerCase(Locale.ROOT)
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password"
        ));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }

        Instant now = Instant.now();
        long expirationSeconds = 900;

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("investtrack")
                .subject(user.getEmail())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expirationSeconds))
                .claim("role", user.getRole())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        String token = jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();

        return new AuthResponse(
                token,
                "Bearer",
                expirationSeconds,
                user.getEmail(),
                user.getRole()
        );
    }
}
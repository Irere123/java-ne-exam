package com.example.javaexam.service;

import com.example.javaexam.dto.AuthResponse;
import com.example.javaexam.dto.LoginRequest;
import com.example.javaexam.dto.MessageResponse;
import com.example.javaexam.dto.RegisterRequest;
import com.example.javaexam.exception.EmailAlreadyUsedException;
import com.example.javaexam.exception.VerificationException;
import com.example.javaexam.model.Role;
import com.example.javaexam.model.User;
import com.example.javaexam.model.VerificationToken;
import com.example.javaexam.repository.UserRepository;
import com.example.javaexam.repository.VerificationTokenRepository;
import com.example.javaexam.security.JwtService;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Core authentication logic: registration, email verification, resending
 * verification, and login (JWT issuance).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailService emailService;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.verification.expiration-minutes}")
    private long verificationExpirationMinutes;

    /**
     * Registers a new (disabled) user and emails them a verification link.
     */
    @Transactional
    public MessageResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyUsedException(email);
        }

        User user = User.builder()
                .firstName(request.firstName().trim())
                .lastName(request.lastName().trim())
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .enabled(false)
                .build();
        userRepository.save(user);

        sendVerificationToken(user);

        log.info("Registered new user {} (pending verification)", email);
        return new MessageResponse(
                "Registration successful. Check your email to verify your account.");
    }

    /**
     * Confirms a verification token and enables the associated account.
     */
    @Transactional
    public MessageResponse verify(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new VerificationException("Invalid verification token"));

        if (verificationToken.isConfirmed()) {
            throw new VerificationException("This verification link has already been used");
        }
        if (verificationToken.isExpired()) {
            throw new VerificationException("This verification link has expired. Please request a new one.");
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        verificationToken.setConfirmedAt(LocalDateTime.now());
        tokenRepository.save(verificationToken);

        log.info("Verified email for user {}", user.getEmail());
        return new MessageResponse("Email verified successfully. You can now log in.");
    }

    /**
     * Issues a fresh verification token for an existing, not-yet-verified user.
     */
    @Transactional
    public MessageResponse resendVerification(String rawEmail) {
        String email = rawEmail.trim().toLowerCase();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new VerificationException("No account found for that email"));

        if (user.isEnabled()) {
            throw new VerificationException("This account is already verified");
        }

        sendVerificationToken(user);
        return new MessageResponse("A new verification email has been sent.");
    }

    /**
     * Authenticates credentials and returns a signed JWT. Spring Security
     * rejects unverified accounts (the {@code enabled} flag) automatically.
     */
    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password()));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + email));

        String token = jwtService.generateToken(Map.of("role", user.getRole().name()), user);

        return new AuthResponse(
                token,
                "Bearer",
                jwtService.getExpirationMs(),
                user.getEmail(),
                user.getRole());
    }

    private void sendVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(verificationExpirationMinutes))
                .build();
        tokenRepository.save(verificationToken);

        String verificationUrl = baseUrl + "/api/auth/verify?token=" + token;
        emailService.sendVerificationEmail(user.getEmail(), user.getFirstName(), verificationUrl);
    }
}

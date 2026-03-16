package coifure.coif.authservice.service;

import coifure.coif.authservice.api.dto.AuthResponse;
import coifure.coif.authservice.api.dto.LoginRequest;
import coifure.coif.authservice.api.dto.RegisterRequest;
import coifure.coif.authservice.api.dto.TokenValidationResponse;
import coifure.coif.authservice.client.UserProfileClient;
import coifure.coif.authservice.client.dto.CreateUserProfileRequest;
import coifure.coif.authservice.domain.UserAccount;
import coifure.coif.authservice.domain.UserAccountRepository;
import coifure.coif.authservice.exception.AuthenticationFailedException;
import coifure.coif.authservice.exception.EmailAlreadyUsedException;
import coifure.coif.authservice.exception.UserProfileProvisioningException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserProfileClient userProfileClient;

    public AuthService(
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            UserProfileClient userProfileClient
    ) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userProfileClient = userProfileClient;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (userAccountRepository.existsByEmail(email)) {
            throw new EmailAlreadyUsedException("Email already used");
        }

        UserAccount user = new UserAccount();
        user.setEmail(email);
        user.setNom(request.nom().trim());
        user.setPrenom(request.prenom().trim());
        user.setTelephone(request.telephone().trim());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(normalizeRole(request.role()));
        user.setEnabled(true);

        UserAccount saved = userAccountRepository.save(user);
        try {
            userProfileClient.createUserProfile(
                    new CreateUserProfileRequest(
                            saved.getId(),
                            saved.getEmail(),
                            saved.getRole()
                    )
            );
        } catch (RuntimeException ex) {
            userAccountRepository.deleteById(saved.getId());
            throw new UserProfileProvisioningException("Failed to provision user profile", ex);
        }
        String token = jwtService.generateAccessToken(saved);
        Instant expiresAt = jwtService.getExpiration(token);

        return new AuthResponse(
                token,
                "Bearer",
                expiresAt,
                saved.getId(),
                saved.getNom(),
                saved.getPrenom(),
                saved.getEmail(),
                saved.getTelephone(),
                saved.getRole()
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());

        UserAccount user = userAccountRepository.findByEmail(email)
                .filter(UserAccount::isEnabled)
                .orElseThrow(() -> new AuthenticationFailedException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new AuthenticationFailedException("Invalid credentials");
        }

        String token = jwtService.generateAccessToken(user);
        Instant expiresAt = jwtService.getExpiration(token);

        return new AuthResponse(
                token,
                "Bearer",
                expiresAt,
                user.getId(),
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getTelephone(),
                user.getRole()
        );
    }

    public TokenValidationResponse validateToken(String token) {
        try {
            Claims claims = jwtService.parseClaims(token);
            return new TokenValidationResponse(
                    true,
                    claims.getSubject(),
                    claims.get("email", String.class),
                    claims.get("role", String.class),
                    claims.get("nom", String.class),
                    claims.get("prenom", String.class),
                    claims.get("telephone", String.class)
            );
        } catch (JwtException | IllegalArgumentException ex) {
            return new TokenValidationResponse(false, null, null, null, null, null, null);
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeRole(String role) {
        String normalized = role.trim().toUpperCase(Locale.ROOT);
        if (!normalized.equals("CLIENT") && !normalized.equals("HAIRDRESSER") && !normalized.equals("ADMIN")) {
            throw new IllegalArgumentException("Role must be CLIENT, HAIRDRESSER, or ADMIN");
        }
        return normalized;
    }
}

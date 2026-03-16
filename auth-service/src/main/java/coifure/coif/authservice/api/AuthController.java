package coifure.coif.authservice.api;

import coifure.coif.authservice.api.dto.AuthResponse;
import coifure.coif.authservice.api.dto.LoginRequest;
import coifure.coif.authservice.api.dto.RegisterRequest;
import coifure.coif.authservice.api.dto.TokenValidationRequest;
import coifure.coif.authservice.api.dto.TokenValidationResponse;
import coifure.coif.authservice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/validate")
    public TokenValidationResponse validate(@Valid @RequestBody TokenValidationRequest request) {
        return authService.validateToken(request.token());
    }
}

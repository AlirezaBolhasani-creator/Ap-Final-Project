package divar.aut.backend.controller;

import divar.aut.backend.dto.AuthResponse;
import divar.aut.backend.dto.LoginRequest;
import divar.aut.backend.dto.RegisterRequest;
import divar.aut.backend.service.AuthService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        String token = authService.register(request);
        return ResponseEntity.ok(new AuthResponse("Registration successful", token, "USER"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginData) {
        AuthService.LoginResult result = authService.login(loginData);
        return ResponseEntity.ok(new AuthResponse("Login successful", result.token(), result.role()));
    }
}

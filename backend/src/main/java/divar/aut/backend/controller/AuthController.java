package divar.aut.backend.controller;

import divar.aut.backend.entity.User;
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
    public ResponseEntity<?> register(@Valid @RequestBody User user) {
        String token = authService.register(user);
        String response = String.format("{\"message\": \"Registration successful\", \"token\": \"%s\"}", token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody User loginData) {
        AuthService.LoginResult result = authService.login(loginData);
        String response = String.format("{\"message\": \"Login successful\", \"token\": \"%s\", \"role\": \"%s\"}",
                result.token(), result.role());
        return ResponseEntity.ok(response);
    }
}

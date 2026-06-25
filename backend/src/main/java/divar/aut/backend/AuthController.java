package divar.aut.backend;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        if (request.username() == null || request.password() == null ||
                request.username().isEmpty() || request.password().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Please fill all fields"));
        }

        boolean isSaved = userRepository.saveUser(request.username(), request.password());

        if (isSaved) {
            return ResponseEntity.ok(Map.of("message", "Registration successful! "));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Username already exists!"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        boolean isValid = userRepository.verifyUser(request.username(), request.password());

        if (isValid) {
            return ResponseEntity.ok(Map.of("message", "Login successful! "));
        } else {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid username or password!"));
        }
    }
}
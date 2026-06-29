package divar.aut.backend;

import divar.aut.backend.model.User;
import divar.aut.backend.repository.UserRepository;
import divar.aut.backend.util.JwtUtils;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body("{\"message\": \"this username is already taken!\"}");
        }
        userRepository.save(user);
        String token = jwtUtils.generateToken(user.getUsername());
        String response = String.format("{\"message\": \"Registration successful\", \"token\": \"%s\"}", token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginData) {
        User user = userRepository.findByUsername(loginData.getUsername());

        if (user != null && user.getPassword().equals(loginData.getPassword())) {
            String token = jwtUtils.generateToken(loginData.getUsername());
            String response = String.format("{\"message\": \"Login successful\", \"token\": \"%s\"}", token);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("{\"message\": \"username or password is incorrect!\"}");
        }
    }
}
package divar.aut.backend;

import divar.aut.backend.model.User;
import divar.aut.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body("{\"message\": \"این نام کاربری قبلاً گرفته شده!\"}");
        }
        userRepository.save(user);
        return ResponseEntity.ok("{\"message\": \"ثبت‌نام با موفقیت انجام شد\"}");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginData) {
        User user = userRepository.findByUsername(loginData.getUsername());

        if (user != null && user.getPassword().equals(loginData.getPassword())) {
            return ResponseEntity.ok("{\"message\": \"ورود موفقیت‌آمیز بود!\"}");
        } else {
            return ResponseEntity.status(401).body("{\"message\": \"نام کاربری یا رمز عبور اشتباه است\"}");
        }
    }
}
package divar.aut.backend.service;

import divar.aut.backend.dto.LoginRequest;
import divar.aut.backend.dto.RegisterRequest;
import divar.aut.backend.entity.User;
import divar.aut.backend.exception.ApiException;
import divar.aut.backend.repository.UserRepository;
import divar.aut.backend.security.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Registration/login business rules. Kept out of AuthController so the
 * controller only handles HTTP concerns and delegates everything else here,
 * matching the layering used by every other feature (Controller -> Service
 * -> Repository).
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public String register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw ApiException.badRequest("این نام کاربری قبلاً استفاده شده است");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw ApiException.badRequest("این ایمیل قبلاً ثبت شده است");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw ApiException.badRequest("این شماره موبایل قبلاً ثبت شده است");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullname(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        userRepository.save(user);
        return jwtUtils.generateToken(user.getUsername());
    }

    public LoginResult login(LoginRequest loginData) {
        User user = userRepository.findByUsername(loginData.getUsername());
        if (user == null || !passwordEncoder.matches(loginData.getPassword(), user.getPassword())) {
            throw ApiException.unauthorized("نام کاربری یا رمز عبور اشتباه است");
        }
        if (user.isBlocked()) {
            throw ApiException.forbidden("حساب کاربری شما توسط مدیر مسدود شده است");
        }
        String token = jwtUtils.generateToken(user.getUsername());
        return new LoginResult(token, user.getRole().name());
    }

    /** Small holder for what AuthController needs after a successful login. */
    public record LoginResult(String token, String role) {
    }
}

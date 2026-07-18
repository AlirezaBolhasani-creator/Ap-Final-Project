package divar.aut.backend.service;

import divar.aut.backend.dto.LoginRequest;
import divar.aut.backend.dto.RegisterRequest;
import divar.aut.backend.entity.Role;
import divar.aut.backend.entity.User;
import divar.aut.backend.exception.ApiException;
import divar.aut.backend.repository.UserRepository;
import divar.aut.backend.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link AuthService} class.
 * Tests the business logic for user registration and authentication,
 * including validation of duplicate usernames, incorrect credentials,
 * and blocked account restrictions.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    private User user;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);
        user.setBlocked(false);
        ReflectionTestUtils.setField(user, "id", 1L);

        registerRequest = mock(RegisterRequest.class);
        lenient().when(registerRequest.getUsername()).thenReturn("testuser");
        lenient().when(registerRequest.getPassword()).thenReturn("password");
        lenient().when(registerRequest.getFullName()).thenReturn("Test User");
        lenient().when(registerRequest.getEmail()).thenReturn("test@test.com");
        lenient().when(registerRequest.getPhone()).thenReturn("09123456789");

        loginRequest = mock(LoginRequest.class);
        lenient().when(loginRequest.getUsername()).thenReturn("testuser");
        lenient().when(loginRequest.getPassword()).thenReturn("password");
    }

    /**
     * Tests that registration fails when the provided username already exists in the database.
     */
    @Test
    void register_ThrowsException_WhenUsernameAlreadyExists() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThrows(ApiException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Tests that a valid registration request successfully saves a new user and generates a token.
     */
    @Test
    void register_SavesUserAndReturnsToken_WhenValid() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(jwtUtils.generateToken("testuser")).thenReturn("mockedToken");

        String token = authService.register(registerRequest);

        assertEquals("mockedToken", token);
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * Tests that login fails when the user does not exist or the password does not match.
     */
    @Test
    void login_ThrowsException_WhenCredentialsAreIncorrect() {
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(false);

        assertThrows(ApiException.class, () -> authService.login(loginRequest));
    }

    /**
     * Tests that login fails when the user's account is marked as blocked.
     */
    @Test
    void login_ThrowsException_WhenUserIsBlocked() {
        user.setBlocked(true);
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

        assertThrows(ApiException.class, () -> authService.login(loginRequest));
    }

    /**
     * Tests that valid credentials for an active user return a correct token and role mapping.
     */
    @Test
    void login_ReturnsLoginResult_WhenValid() {
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtUtils.generateToken("testuser")).thenReturn("mockedToken");

        AuthService.LoginResult result = authService.login(loginRequest);

        assertNotNull(result);
        assertEquals("mockedToken", result.token());
        assertEquals("USER", result.role());
    }
}
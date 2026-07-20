package divar.aut.backend.config;

import divar.aut.backend.repository.UserRepository;
import divar.aut.backend.security.JsonAccessDeniedHandler;
import divar.aut.backend.security.JsonAuthenticationEntryPoint;
import divar.aut.backend.security.JwtAuthenticationFilter;
import divar.aut.backend.security.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Central place that decides, for every incoming request, whether it needs
 * a valid JWT and/or a specific role. This is the real enforcement point -
 * the JavaFX frontend hiding a button is only a convenience, this config is
 * what actually protects the data.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JsonAuthenticationEntryPoint authenticationEntryPoint;
    private final JsonAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(JsonAuthenticationEntryPoint authenticationEntryPoint,
                           JsonAccessDeniedHandler accessDeniedHandler) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtUtils jwtUtils,
                                                     UserRepository userRepository) throws Exception {
        http
            // REST API with JWTs does not need CSRF protection or HTTP sessions.
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptions -> exceptions
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler))
            .authorizeHttpRequests(authorize -> authorize
                    // Public endpoints
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/cities/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/ads/pending").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/ads/*/approve", "/ads/*/reject", "/ads/*/status").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/ads", "/ads/*").permitAll()
                    .requestMatchers(HttpMethod.DELETE, "/ratings/*").hasRole("ADMIN")
                    .requestMatchers("/uploads/**").permitAll()

                    // Everything else requires a logged-in user
                    .anyRequest().authenticated())
            .addFilterBefore(new JwtAuthenticationFilter(jwtUtils, userRepository),
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

package divar.aut.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("/health")
    public String checkHealth() {
        return "Server is UP and Database connection is successful! 🚀";
    }
}
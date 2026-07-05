package divar.aut.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import divar.aut.backend.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Runs when a request is authenticated but the user's role does not allow
 * the action (e.g. a regular user calling an admin-only endpoint). Returns
 * a structured JSON 403.
 */
@Component
public class JsonAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                        AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse body = new ErrorResponse("You do not have permission to do that", HttpStatus.FORBIDDEN.value());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}

package divar.aut.backend.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import divar.aut.backend.model.User;
import divar.aut.backend.repository.SqlUserRepository;
import divar.aut.backend.repository.UserRepository;
import divar.aut.backend.util.HttpUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class AuthHandler implements HttpHandler
{
    private final UserRepository userRepository = SqlUserRepository.getInstance();
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String path = httpExchange.getRequestURI().getPath();
        if(!"POST".equalsIgnoreCase(method))
        {
            sendResponse(httpExchange, 405, "Method Not Allowed");
            return;
        }
        String body = HttpUtils.readBody(httpExchange.getRequestBody());
        Map<String, String> map = HttpUtils.parseFormData(body);
        String username = map.get("username");
        String password = map.get("password");

        if(username == null || password == null)
        {
            sendResponse(httpExchange, 405, "Username or Password Required");
            return;
        }
        if (path.endsWith("/register")) {
            handleRegister(httpExchange, username, password);
        } else if (path.endsWith("/login")) {
            handleLogin(httpExchange, username, password);
        } else {
            sendResponse(httpExchange, 404, "Not Found");
        }
    }
    private void handleRegister(HttpExchange httpExchange, String username, String password) throws IOException
    {
        if(userRepository.findByUsername(username) != null)
        {
            sendResponse(httpExchange, 400, "Username Already Exists");
            return;
        }
        User user = new User(username, password);
        userRepository.save(user);
        sendResponse(httpExchange, 200, "Registration successful!");
    }
    private void handleLogin(HttpExchange httpExchange, String username, String password)throws IOException
    {
        User user = userRepository.findByUsername(username);
        if(user == null || !user.getPassword().equals(password))
        {
            sendResponse(httpExchange, 401, "Invalid username or password!");
            return;
        }
        sendResponse(httpExchange, 200, "Login successful!");
    }
    private void sendResponse(HttpExchange exchange, int statusCode, String responseText) throws IOException {
        byte[] bytes = responseText.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}

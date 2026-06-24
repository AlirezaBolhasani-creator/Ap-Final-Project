package divar.aut.backend.handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import divar.aut.backend.model.User;
import divar.aut.backend.repository.SqlUserRepository;
import divar.aut.backend.repository.UserRepository;
import divar.aut.backend.util.HttpUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class AuthHandler implements HttpHandler
{
    private final UserRepository userRepository = SqlUserRepository.getInstance();
    private final Gson gson = new Gson();
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String path = httpExchange.getRequestURI().getPath();
        if(!"POST".equalsIgnoreCase(method))
        {
            sendJsonResponse(httpExchange, 405, "Method Not Allowed");
            return;
        }
        String body = HttpUtils.readBody(httpExchange.getRequestBody());
        Type mapType = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> params = gson.fromJson(body, mapType);
        if (params == null) {
            sendJsonResponse(httpExchange, 400, "Invalid JSON body");
            return;
        }
        String username = params.get("username");
        String password = params.get("password");
        if(username == null || password == null|| username.trim().isEmpty() || password.trim().isEmpty())
        {
            sendJsonResponse(httpExchange, 405, "Username or Password Required");
            return;
        }
        if (path.endsWith("/register")) {
            handleRegister(httpExchange, username, password);
        } else if (path.endsWith("/login")) {
            handleLogin(httpExchange, username, password);
        } else {
            sendJsonResponse(httpExchange, 404, "Not Found");
        }
    }
    private void handleRegister(HttpExchange httpExchange, String username, String password) throws IOException
    {
        if(userRepository.findByUsername(username) != null)
        {
            sendJsonResponse(httpExchange, 400, "Username Already Exists");
            return;
        }
        User user = new User(username, password);
        userRepository.save(user);
        sendJsonResponse(httpExchange, 200, "Registration successful!");
    }
    private void handleLogin(HttpExchange httpExchange, String username, String password)throws IOException
    {
        User user = userRepository.findByUsername(username);
        if(user == null || !user.getPassword().equals(password))
        {
            sendJsonResponse(httpExchange, 401, "Invalid username or password!");
            return;
        }
        sendJsonResponse(httpExchange, 200, "Login successful!");
    }
    private void sendJsonResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        String jsonResponse = String.format("{\"message\": \"%s\"}", message);
        byte[] bytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}

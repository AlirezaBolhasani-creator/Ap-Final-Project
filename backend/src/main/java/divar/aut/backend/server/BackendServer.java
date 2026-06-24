package divar.aut.backend.server;
import com.sun.net.httpserver.HttpServer;
import divar.aut.backend.handler.AuthHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
public class BackendServer
{
    private final int port;
    private HttpServer server;
    public BackendServer(int port)
    {
        this.port = port;
    }
    public void start() throws IOException
    {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newCachedThreadPool());
        server.createContext("/api/auth", new AuthHandler());
        server.start();
        System.out.println("BackendServer started on port " + port);
    }
    public void stop() throws IOException
    {
        server.stop(0);
    }
}

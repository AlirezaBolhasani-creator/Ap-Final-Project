package divar.aut.backend;

import divar.aut.backend.repository.SqlUserRepository;
import divar.aut.backend.server.BackendServer;

import java.io.IOException;

public class Main
{
    public static void main(String[] args)
    {
        int port = 8080;
        try
        {
            BackendServer server = new BackendServer(port);
            server.start();
        }
        catch (IOException e)
        {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

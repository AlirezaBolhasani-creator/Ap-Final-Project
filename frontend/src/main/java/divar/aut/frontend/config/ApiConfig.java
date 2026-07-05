package divar.aut.frontend.config;

public class ApiConfig
{
   /** Host+port only, no path - individual services append their own path (e.g. "/api/auth/login" or "/ads"). */
   public static final String SERVER_URL = "http://localhost:8080";

   public static final String BASE_URL = SERVER_URL + "/api";

}

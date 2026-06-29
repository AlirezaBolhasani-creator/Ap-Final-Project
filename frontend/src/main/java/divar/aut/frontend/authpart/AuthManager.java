package divar.aut.frontend.authpart;

public class AuthManager
{
    private static String token;
    public static void setToken(String set)
    {
        token = set;
    }
    public static String getToken()
    {
        return token;
    }
    public static boolean isLoggedIn()
    {
        return token != null && !token.isEmpty();
    }
    public static void logout()
    {
        token = null;
    }
}

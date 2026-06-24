package divar.aut.backend.util;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpUtils
{
    public static String readBody(InputStream inputStream)
    {
        try{
            byte[] buffer = inputStream.readAllBytes();
            return new String(buffer, StandardCharsets.UTF_8);
        }
        catch(Exception e)
        {
            System.err.println("Failed to read body: " + e.getMessage());
            return "";
        }
    }
}

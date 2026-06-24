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
    public static Map<String, String> parseFormData(String body) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        if(body == null|| body.trim().isEmpty())
        {
            return map;
        }
        String[] parts = body.split("&");
        for(String part : parts)
        {
            String[] keyValue = part.split("=", 2);
            if(keyValue.length == 2)
            {
                try {
                    String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                    String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                    map.put(key, value);
                } catch (IllegalArgumentException e) {
                    System.err.println("Warning: Malformed form data parameter ignored: " + part);
                }
            }
        }
        return map;
    }
}

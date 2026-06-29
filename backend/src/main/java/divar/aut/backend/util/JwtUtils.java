package divar.aut.backend.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
@Component
public class JwtUtils
{
    @Value("${jwt.secret}")
    private String secretKey;


    public String generateToken(String username)
    {
        return Jwts.builder().setSubject(username).setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+86400000))//one day period
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }
}

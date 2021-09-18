package top.aengus.panther.tool;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TokenUtil {

    private static final long MILLIS_IN_DAY = 1000 * 60 * 60 * 24;

    private static final String SECRET_KEY = "-(6ve^u&2cfnhycf1zk=8rk@-i5rr1ezeek1tk5kkri5*ru7n9";

    public static String sign(String username, int days) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        long expired = System.currentTimeMillis() + days * MILLIS_IN_DAY;
        return Jwts.builder()
                .addClaims(claims)
                .setExpiration(new Date(expired))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public static boolean verify(String token, String username) {
        try {
            Map<String, Object> body = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
            return body.containsKey("username") && body.get("username").toString().equals(username);
        } catch (Exception e) {
            if (!(e instanceof ExpiredJwtException)) {
                log.error("校验token失败", e);
            }
            return false;
        }
    }
}

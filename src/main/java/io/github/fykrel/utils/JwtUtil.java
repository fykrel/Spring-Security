package io.github.fykrel.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fangyk
 * @date 2023年08月09日 14:27
 */
public class JwtUtil {
    private static final String SECRET = "*4!d3.4c5";

    public static String createToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities());

        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuer("spring security")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60000))
                .compact();
    }

    /**
     * 解析token, 获取用户名
     *
     * @param token jwt token
     * @return username
     */
    public static String getUsername(String token) {
        return parse(token).getSubject();
    }

    /**
     * 解析jwt token
     *
     * @param token jwt token
     * @return {@link Claims}
     */
    public static Claims parse(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }
}
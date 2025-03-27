package com.app.authjwt.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;


@Service
public class JwtUtils {

    @Value("${auth.app.jwtSecret}")
    private String jwtSecret;

    @Value("${auth.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${auth.app.jwtCookieName}")
    private String jwtCookie;
    @Value("${auth.app.jwtRefresh}")
    private String jwtRefreshMs;

    public String getUsernameFromToken(String token) {
        return getClaims(token, Claims::getSubject);
    }

    public String getToken(UserDetails user) {
        return generateTokenFromUsername(new HashMap<>(), user);
    }

    public Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String generateTokenFromUsername(HashMap<String, Object> extraClaims, UserDetails user) {
        Date issuedAt = new Date();
        Date expiration = new Date(System.currentTimeMillis() + jwtExpirationMs);

        extraClaims.put("issuedAt", issuedAt);
        extraClaims.put("expiration", expiration);

        String token = Jwts.builder()
                .claims(extraClaims)
                .subject(user.getUsername())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
        return token;
    }

    private Claims getAllClaims(String token)
    {
        return Jwts
                .parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T getClaims(String token, Function<Claims,T> claimsReslver){
        final Claims claims=getAllClaims(token);
        return claimsReslver.apply(claims);
    }
    private Date getExpiration(String token){
        return getClaims(token,Claims::getExpiration);
    }
    public boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username=getUsernameFromToken(token);
        return ((username.equals(userDetails.getUsername())&& !isTokenExpired(token)));
    }
}

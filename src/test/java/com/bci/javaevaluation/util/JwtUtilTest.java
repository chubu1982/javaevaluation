package com.bci.javaevaluation.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String SECRET_KEY = "secret";

    @BeforeEach
    public void setUp() {
        jwtUtil = new JwtUtil();
    }

    @Test
    public void testGetJWTToken() {
        String username = "testUser";

        String token = jwtUtil.getJWTToken(username);

        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes())
                .parseClaimsJws(token)
                .getBody();

        assertEquals(username, claims.getSubject(), "The subject of the token should be the username.");
        assertTrue(claims.getExpiration().after(new Date()), "The expiration date should be in the future.");
        assertTrue(claims.getExpiration().before(new Date(System.currentTimeMillis() + 600000)),
                "The expiration date should be within 10 minutes from now.");

        Object authoritiesClaim = claims.get("authorities");
        assertTrue(authoritiesClaim instanceof List, "The 'authorities' claim should be a List.");
        @SuppressWarnings("unchecked")
        List<String> authorities = (List<String>) authoritiesClaim;
        assertTrue(authorities.contains("ROLE_USER"), "The 'authorities' claim should contain 'ROLE_USER'.");
    }
}

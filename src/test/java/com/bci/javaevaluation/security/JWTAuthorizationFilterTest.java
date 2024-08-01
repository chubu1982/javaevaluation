package com.bci.javaevaluation.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JWTAuthorizationFilterTest {

    private JWTAuthorizationFilter jwtAuthorizationFilter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain chain;

    @BeforeEach
    public void setUp() {
        jwtAuthorizationFilter = new JWTAuthorizationFilter();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
    }

    @Test
    public void testDoFilterInternalWithValidToken() throws Exception {
        String token = generateToken("testUser");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        jwtAuthorizationFilter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testDoFilterInternalWithInvalidToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidToken");

        jwtAuthorizationFilter.doFilterInternal(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "JWT strings must contain exactly 2 period characters. Found: 0");
    }

    @Test
    public void testDoFilterInternalWithNoToken() throws Exception {
        jwtAuthorizationFilter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testDoFilterInternalWithExpiredToken() throws Exception {
        String token = generateToken("testUser", -1000); // Expired token
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        jwtAuthorizationFilter.doFilterInternal(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);

        ArgumentCaptor<String> errorCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), errorCaptor.capture());

        String errorMessage = errorCaptor.getValue();
        assertTrue(errorMessage.contains("JWT expired"), "Error message should contain 'JWT expired'");
    }

    private String generateToken(String username) {
        return generateToken(username, 600000); // Token valid for 10 minutes
    }

    private String generateToken(String username, long expirationTime) {
        return Jwts.builder()
                .setSubject(username)
                .claim("authorities", List.of("ROLE_USER"))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS512, "secret".getBytes())
                .compact();
    }
}

package com.dbtraining.reconx.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JwtAuthenticationFilter
 *
 * Reads Authorization: Bearer <token>, parses it, sets SecurityContext.
 * Errors are not rendered here — Spring's exception handler converts
 * missing/expired tokens into a 401 once the request reaches a protected
 * endpoint.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider provider;

    public JwtAuthenticationFilter(JwtTokenProvider provider) { 
        this.provider = provider; 
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
            
        String header = req.getHeader("Authorization");
        
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                // Parse the token using the provider we built in ADV072
                Claims claims = provider.parse(token);
                String email = claims.getSubject();
                String role = (String) claims.get("role");
                
                // Spring Security expects roles to be prefixed with "ROLE_"
                var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                var auth = new UsernamePasswordAuthenticationToken(email, null, authorities);
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                
                // Lock the user into the security context for this request
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (JwtException ex) {
                // If token is expired or tampered with, fail closed (clear context)
                SecurityContextHolder.clearContext();
            }
        }
        
        // Always continue down the chain
        chain.doFilter(req, res);
    }
}
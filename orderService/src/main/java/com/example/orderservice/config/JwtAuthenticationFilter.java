package com.example.orderservice.config;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization Header = " + authHeader);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("JWT NOT FOUND");
            filterChain.doFilter(request, response);
            return;
        }
        String token = authHeader.substring(7);
        System.out.println("JWT FOUND");
        try {

            String username = jwtService.extractUsername(token);
            String role = jwtService.extractRole(token);

            System.out.println("Username = " + username);
            System.out.println("Role = " + role);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            Collections.singletonList(
                                    new SimpleGrantedAuthority(role)
                            )
                    );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authenticationToken);

            System.out.println("AUTHENTICATION SUCCESS");

        } catch (Exception e) {

            System.out.println("JWT VALIDATION FAILED");
            System.out.println("Reason = " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}

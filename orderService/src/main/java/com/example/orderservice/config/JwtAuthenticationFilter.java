
package com.example.orderservice.config;

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

import java.io.IOException;
import java.util.List;

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

        System.out.println("Request: " + request.getMethod()
                + " " + request.getRequestURI());

        System.out.println("Authorization header present: "
                + (authHeader != null
                && authHeader.startsWith("Bearer ")));

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);

            if (!jwtService.isValid(token)) {
                System.err.println("JWT is invalid or expired");
                filterChain.doFilter(request, response);

                System.out.println("Final HTTP response status: "
                        + response.getStatus());                return;
            }

            String username = jwtService.extractUsername(token);
            String role = jwtService.extractRole(token);

            System.out.println("JWT username: " + username);
            System.out.println("JWT role: " + role);

            if (username == null || username.isBlank()
                    || role == null || role.isBlank()) {
                throw new IllegalArgumentException(
                        "Username or role missing from JWT");
            }

            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role;
            }

            var authentication =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            List.of(new SimpleGrantedAuthority(role))
                    );

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

            System.out.println("JWT authentication successful");

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            System.err.println("JWT authentication failed: "
                    + e.getClass().getSimpleName()
                    + " - " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}

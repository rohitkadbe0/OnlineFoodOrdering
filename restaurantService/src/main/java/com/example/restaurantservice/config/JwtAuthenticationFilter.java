package com.example.restaurantservice.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.io.IOException;

import io.jsonwebtoken.security.Keys;
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
    )
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = authHeader.substring(7);
        try {
          if(!jwtService.isTokenValid(token)){
              System.out.println("JWT is INVALID");
              filterChain.doFilter(request,response);
              return;
          }
          String username= jwtService.extractUsername(token);
          String role =jwtService.extractRole(token);

            System.out.println("JWT Username: "+username);
            System.out.println("JWT Role: "+ role);
          SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
          UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                  username,
                  null,
                  List.of(authority)
          );
          SecurityContextHolder
                  .getContext()
                  .setAuthentication(authentication);
            System.out.println("Authentication: "+
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication()
            );

        } catch (Exception e) {
            System.out.println("JWT ERROR: "+e.getMessage());
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }
}

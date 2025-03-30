package com.example.demo.configuration;

import com.example.demo.common.Enums;
import com.example.demo.exception.ErrorMessage;
import com.example.demo.model.Account;
import com.example.demo.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final AccountService accountService;
    private final List<String> acceptedNoAuth = List.of("/api/v1/accounts/login", "/api/v1/accounts/register");

    public JwtFilter(JwtService jwtService, AccountService accountService) {
        this.jwtService = jwtService;
        this.accountService = accountService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");
            String token = null;
            String email = null;


            if (acceptedNoAuth.contains(request.getRequestURI())) {
                filterChain.doFilter(request, response);
                return;
            }

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                ErrorMessage errResponse = ErrorMessage.builder()
                        .success(false)
                        .statusCode(Enums.ErrorKey.ErrorInternal)
                        .message("Unauthorized: Missing or invalid Authorization header.")
                        .data(null)
                        .build();

                this.writeJsonResponse(response, 401, errResponse);
                return;
            }

            token = authHeader.substring(7);
            email = jwtService.extractEmail(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Account account = accountService.getAccount(email);

                UserDetails userDetails = User.builder()
                        .username(account.getEmail())
                        .roles(String.valueOf(account.getRole()))
                        .password(account.getPassword())
                        .authorities(Collections.singletonList(new SimpleGrantedAuthority(account.getRole().toString())))
                        .build();

                if (jwtService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities() //Get authorities from UserDetails
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorMessage errResponse = ErrorMessage.builder()
                    .success(false)
                    .statusCode(Enums.ErrorKey.ErrorInternal)
                    .message("Unauthorized: The token is invalid or expired.")
                    .data(null)
                    .build();

            this.writeJsonResponse(response, 401, errResponse);
        }
    }

    private void writeJsonResponse(HttpServletResponse response, int status, Object content) {
        response.setStatus(status);
        response.setContentType("application/json");
        try {
            response.getWriter().write(new ObjectMapper().writeValueAsString(content));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.example.demo.configuration;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.demo.common.Enums;
import com.example.demo.common.ErrorMessage;
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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class JwtFilter extends OncePerRequestFilter {
    private JwtService jwtService;
    private AccountService accountService;

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


            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                email = jwtService.extractEmail(token);
            }

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Account account = accountService.getAccount(email);

                UserDetails userDetails = User.builder()
                        .username(account.getName())
                        .roles(String.valueOf(account.getRole()))
                        .authorities(Collections
                                .singletonList(new SimpleGrantedAuthority(account.getRole().toString()))
                        )
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
        }

        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            ErrorMessage errorResponse = ErrorMessage.builder()
                    .success(false)
                    .statusCode(Enums.ErrorKey.ErrorInternal)
                    .message(e.getLocalizedMessage())
                    .data(null)
                    .build();

            ObjectMapper mapper = new ObjectMapper();

            response.getWriter()
                    .write(mapper.writeValueAsString(errorResponse));
        }
    }
}

package com.example.demo.configuration;

import com.example.demo.common.Enums;
import com.example.demo.exception.ErrorMessage;
import com.example.demo.service.AccountService;
import com.example.demo.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final JwtFilter jwtFilter;
    private final AuthenticationProvider authenticationProvider;

    public SecurityConfiguration(JwtFilter jwtFilter, AuthenticationProvider authenticationProvider) {
        this.jwtFilter = jwtFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String[] whitelist = {
                "/api/v1/accounts/login",
                "/api/v1/accounts/register",
                "/api/v1/accounts/forgot-password",
                "/api/v1/accounts/reset-password",
                "/api/v1/accounts/verify-email",
                "/api/v1/accounts/verify-otp",
//                "/api/v1/accounts/refresh-token",
//                "/api/v1/accounts/reset-password",
//                "/api/v1/customers/**"
        };

        String[] adminWhiteList = {
                "/api/v1/admin/**",
        };

        http
                .csrf(csrf -> csrf.disable()) // Vô hiệu hóa CSRF nếu không cần thiết
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(whitelist).permitAll()
                        .requestMatchers(adminWhiteList).hasAnyAuthority(Enums.Role.Admin.value())
                        .anyRequest().authenticated()
                )
                .exceptionHandling(
                        exception -> exception
                                .authenticationEntryPoint((request, response, authException) -> {
                                    writeJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, authException);
                                })
                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                    writeJsonResponse(response, HttpServletResponse.SC_FORBIDDEN, accessDeniedException);
                                })
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOriginPattern("*"); // Cho phép tất cả các origin
        corsConfiguration.addAllowedHeader("*"); // Cho phép tất cả các header
        corsConfiguration.addAllowedMethod("*"); // Cho phép tất cả các phương thức (GET, POST, PUT, DELETE, ...)
        corsConfiguration.setAllowCredentials(true); // Cho phép gửi cookie và thông tin xác thực

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(source);
    }


    private void writeJsonResponse(HttpServletResponse response, int status, Exception exception) {
        ErrorMessage errResponse = ErrorMessage.builder()
                .success(false)
                .statusCode(Enums.ErrorKey.ErrorInternal)
                .message("Unauthorized: " + exception.toString())
                .data(null)
                .build();
        response.setStatus(status);
        response.setContentType("application/json");
        try {
            response.getWriter().write(new ObjectMapper().writeValueAsString(errResponse));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

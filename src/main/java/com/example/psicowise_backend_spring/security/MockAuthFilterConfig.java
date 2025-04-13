package com.example.psicowise_backend_spring.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Configuração para mock do AuthenticationFilter durante testes
 */
@Configuration
@Profile("test")
public class MockAuthFilterConfig {

    @Bean
    public AuthenticationFilter mockAuthenticationFilter() {
        return new AuthenticationFilter(null, null) {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                // Bypass authentication in tests
                filterChain.doFilter(request, response);
            }
        };
    }
}
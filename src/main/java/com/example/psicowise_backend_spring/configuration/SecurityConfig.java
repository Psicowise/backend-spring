package com.example.psicowise_backend_spring.configuration;

import com.example.psicowise_backend_spring.security.AuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuração de segurança da aplicação.
 * Define regras de acesso, configuração de CORS e JWT.
 */
@Profile("!test")
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final AuthenticationFilter authenticationFilter;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permitir todas as origens para desenvolvimento
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type", "Accept", "Origin"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(false); // Importante quando allowedOrigins tem "*"
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        // Configuração específica para /api/auth
            CorsConfiguration authConfig = new CorsConfiguration(configuration);
            source.registerCorsConfiguration("/api/auth/**", authConfig);

        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      http
      .cors(cors -> cors.configurationSource(corsConfigurationSource()))
      .csrf(AbstractHttpConfigurer::disable)
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(authorize -> authorize
        // Endpoints públicos (mais específicos primeiro)
        .requestMatchers("/ping", "/health", "/actuator/health").permitAll()
        .requestMatchers("/api/autenticacao/login", "/api/autenticacao/esqueci",
          "/api/autenticacao/redefinir", "/api/autenticacao/validar-token").permitAll()
        .requestMatchers("/api/usuarios/criar/**").permitAll()
        .requestMatchers("/api/roles/**").permitAll()

                        // Endpoint problemático - seja mais específico com ele
                        .requestMatchers("/api/auth/atual").authenticated()

                        // Outros endpoints autenticados
                        .requestMatchers("/api/whatsapp/**").authenticated()
                        .requestMatchers("/api/usuarios/buscar/atual").authenticated()

                        // Regra geral para todos outros endpoints /api
                        .requestMatchers("/api/**").authenticated()

                        // Regra para qualquer outro endpoint
                        .anyRequest().authenticated()
      )
      .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

package com.example.psicowise_backend_spring.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Desabilita CSRF (exemplo; ajuste se precisar manter habilitado)
        http.csrf(csrf -> csrf.disable());

        // Define regras de autorização
        http.authorizeHttpRequests(auth -> auth
                // Libera POST para criar usuário sem autenticação
                .requestMatchers("/api/usuarios/**").permitAll()
                .requestMatchers("/api/roles/**").permitAll()
                // Libera se houver mais algum endpoint que precise de acesso anônimo
                // .requestMatchers("/outro/endpoint").permitAll()
                // Qualquer outra requisição precisa estar autenticada
                .anyRequest().authenticated()
        );

        // Se quiser usar HTTP Basic para os demais endpoints:
        http.httpBasic(Customizer.withDefaults());

        // Retorna o objeto de configuração
        return http.build();
    }
}

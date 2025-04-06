package com.example.psicowise_backend_spring.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuração de persistência
 * Habilita a auditoria JPA para campos como createdAt e updatedAt
 */
@Configuration
@EnableJpaAuditing
public class PersistenceConfig {
}
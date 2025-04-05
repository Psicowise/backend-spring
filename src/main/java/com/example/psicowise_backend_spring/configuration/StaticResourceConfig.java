package com.example.psicowise_backend_spring.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração dedicada para recursos estáticos.
 * A anotação @Order garante que esta configuração seja aplicada antes de outras
 */
@Configuration
@Order(1)
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Desabilitar explicitamente o mapeamento de recursos para caminhos da API
        registry.addResourceHandler("/api/**")
                .addResourceLocations("classpath:/META-INF/resources/")
                .resourceChain(false);

        // Configurar explicitamente os recursos estáticos para os diretórios específicos
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true);

        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/")
                .resourceChain(true);
    }
}
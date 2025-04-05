package com.example.psicowise_backend_spring.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {

        configurer.setUseTrailingSlashMatch(false);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configure to explicitly avoid treating API paths as static resources
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");

        // Importante: desabilitar o mapeamento padrão para evitar conflitos com endpoints API
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/",
                        "classpath:/resources/",
                        "classpath:/static/",
                        "classpath:/public/")
                .resourceChain(true)
                .addResolver(new NoApiRequestResourceResolver());
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        // Configurar para favorecer cabeçalhos de conteúdo sobre extensões de arquivo
        configurer.favorParameter(false)
                .ignoreAcceptHeader(false)
                .useRegisteredExtensionsOnly(true);
    }
}
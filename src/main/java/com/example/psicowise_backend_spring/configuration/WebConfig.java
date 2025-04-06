package com.example.psicowise_backend_spring.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.resource.PathResourceResolver;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseTrailingSlashMatch(false);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Clear any existing resource handlers to prevent conflicts
        registry.setOrder(-1);

        // Add a handler for specific static resources
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600)
                .resourceChain(true);

        // Add a catch-all handler with custom resolver that ignores API paths
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new ApiPathSkippingResourceResolver());
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .favorParameter(false)
                .ignoreAcceptHeader(false)
                .defaultContentType(org.springframework.http.MediaType.APPLICATION_JSON);
    }

    // Inner class for our custom resource resolver
    private static class ApiPathSkippingResourceResolver extends PathResourceResolver {
        @Override
        public Resource resolveResource(HttpServletRequest request, String requestPath,
                                        List<? extends Resource> locations,
                                        org.springframework.web.servlet.resource.ResourceResolverChain chain) {
            // Skip API paths
            if (requestPath.startsWith("api/") || requestPath.startsWith("/api/")) {
                return null;
            }
            return super.resolveResource(request, requestPath, locations, chain);
        }

        @Override
        protected Resource resolveResourceInternal(HttpServletRequest request, String requestPath,
                                                   List<? extends Resource> locations,
                                                   org.springframework.web.servlet.resource.ResourceResolverChain chain) {
            // Also check in the internal method
            if (requestPath.startsWith("api/") || requestPath.startsWith("/api/")) {
                return null;
            }
            return super.resolveResourceInternal(request, requestPath, locations, chain);
        }
    }
}
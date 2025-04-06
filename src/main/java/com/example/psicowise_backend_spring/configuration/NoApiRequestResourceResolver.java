package com.example.psicowise_backend_spring.configuration;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import java.util.List;

/**
 * Resolvedor personalizado de recursos que ignora requisições que começam com /api/
 * Isso evita que o Spring tente servir endpoints de API como recursos estáticos
 */
public class NoApiRequestResourceResolver extends PathResourceResolver {
    @Override
    public Resource resolveResource(HttpServletRequest request, String requestPath,
                                    List<? extends Resource> locations, ResourceResolverChain chain) {
        // Se o caminho começar com api/, não tente resolvê-lo como recurso estático
        if (requestPath.startsWith("api/") || requestPath.startsWith("/api/")) {
            return null;
        }
        return super.resolveResource(request, requestPath, locations, chain);
    }

    @Override
    protected Resource resolveResourceInternal(HttpServletRequest request, String requestPath,
                                               List<? extends Resource> locations, ResourceResolverChain chain) {
        // Também verifica no método interno
        if (requestPath.startsWith("api/") || requestPath.startsWith("/api/")) {
            return null;
        }
        return super.resolveResourceInternal(request, requestPath, locations, chain);
    }
}
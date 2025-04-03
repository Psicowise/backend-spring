package com.example.psicowise_backend_spring.security;

import com.example.psicowise_backend_spring.enums.authenticacao.ERole;
import com.example.psicowise_backend_spring.service.autenticacao.TokenBlackListService;
import com.example.psicowise_backend_spring.util.JwtUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class AuthenticationFilter implements Filter {

    private final JwtUtil jwtUtil;
    private final TokenBlackListService tokenBlacklistService;
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();

        // Verificar se é uma URL que está isenta de autenticação
        boolean isExemptUrl = isExemptUrl(requestURI);
        log.debug("Processing request: {} - Exempt: {}", requestURI, isExemptUrl);

        final String authorizationHeader = httpRequest.getHeader(AUTHORIZATION_HEADER);
        log.info("Auth header: {}", authorizationHeader != null ? "Present" : "Absent");

        // Se a URL está isenta ou não há header de autorização, apenas passe adiante
        if (isExemptUrl || authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        // Processar a autenticação
        try {
            String token = authorizationHeader.substring(7);

            if (tokenBlacklistService.isTokenRevogado(token)) {
                sendErrorResponse(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, "Token revogado");
                return;
            }

            String userId = jwtUtil.extractUserId(token);

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtUtil.validateToken(token, userId)) {
                    // Load user details and authorities here
                    List<GrantedAuthority> authorities = List.of(
                            new SimpleGrantedAuthority(ERole.ADMIN.name()),
                            new SimpleGrantedAuthority(ERole.USER.name()),
                            new SimpleGrantedAuthority(ERole.PSICOLOGO.name())
                    );

                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            authorities
                    );

                    // Set the authentication in the SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // Continue com o processamento da requisição
                    chain.doFilter(request, response);
                } else {
                    sendErrorResponse(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
                }
            } else {
                // Token inválido ou autenticação já definida
                chain.doFilter(request, response);
            }
        } catch (Exception ex) {
            log.error("Erro durante autenticação: {}", ex.getMessage(), ex);
            sendErrorResponse(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, "Erro de autenticação: " + ex.getMessage());
        }
    }

    /**
     * Verifica se a URL está isenta de autenticação
     */
    private boolean isExemptUrl(String requestURI) {
        return requestURI.startsWith("/api/roles/") ||
                requestURI.startsWith("/api/autenticacao/") ||
                requestURI.equals("/ping") ||
                requestURI.equals("/actuator/health");
    }

    /**
     * Envia uma resposta de erro
     */
    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.write("{\"error\": \"Unauthorized\", \"message\": \"" + message + "\"}");
        writer.flush();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("Inicializando AuthenticationFilter");
    }

    @Override
    public void destroy() {
        log.info("Destruindo AuthenticationFilter");
    }
}

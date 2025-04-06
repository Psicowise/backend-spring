package com.example.psicowise_backend_spring.security;

import com.example.psicowise_backend_spring.enums.authenticacao.ERole;
import com.example.psicowise_backend_spring.service.autenticacao.TokenBlackListService;
import com.example.psicowise_backend_spring.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Filtro responsável pela autenticação baseada em JWT.
 * Este filtro é executado uma vez por requisição e verifica se o token JWT é válido.
 */
@RequiredArgsConstructor
@Component
@Slf4j
@Profile("!test")
public class AuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final TokenBlackListService tokenBlacklistService;
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // Verificar se é uma URL que está isenta de autenticação
        if (isExemptUrl(requestURI)) {
            log.debug("URL isenta de autenticação: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        final String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        log.debug("Auth header: {}", authorizationHeader != null ? "Present" : "Absent");

        // Se não há header de autorização, apenas passe adiante
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.debug("Sem token de autenticação ou formato inválido");
            filterChain.doFilter(request, response);
            return;
        }

        // Processar a autenticação
        try {
            String token = authorizationHeader.substring(7);

            if (tokenBlacklistService.isTokenRevogado(token)) {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token revogado");
                return;
            }

            String userId = jwtUtil.extractUserId(token);

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtUtil.validateToken(token, userId)) {
                    // Load user details and authorities here
                    List<GrantedAuthority> authorities = List.of(
                            new SimpleGrantedAuthority("ROLE_" + ERole.ADMIN.name()),
                            new SimpleGrantedAuthority("ROLE_" + ERole.USER.name()),
                            new SimpleGrantedAuthority("ROLE_" + ERole.PSICOLOGO.name())
                    );

                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            authorities
                    );

                    // Set the authentication in the SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Autenticação configurada para o usuário: {}", userId);
                } else {
                    log.warn("Token inválido para o usuário: {}", userId);
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
                    return;
                }
            }
            // Continue with the filter chain
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            log.error("Erro durante autenticação: {}", ex.getMessage(), ex);
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Erro de autenticação: " + ex.getMessage());
        }
    }

    /**
     * Verifica se a URL está isenta de autenticação
     */
    private boolean isExemptUrl(String requestURI) {
        return requestURI.equals("/ping") ||
                requestURI.equals("/actuator/health") ||
                requestURI.equals("/health") ||
                requestURI.startsWith("/api/autenticacao/login") ||
                requestURI.startsWith("/api/autenticacao/esqueci") ||
                requestURI.startsWith("/api/autenticacao/redefinir") ||
                requestURI.startsWith("/api/autenticacao/validar-token") ||
                requestURI.startsWith("/api/roles") ||
                requestURI.startsWith("/api/usuarios/criar");
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
}
package com.example.psicowise_backend_spring.security;

import com.example.psicowise_backend_spring.enums.authenticacao.ERole;
import com.example.psicowise_backend_spring.util.JwtUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
@EnableWebSecurity
@EnableMethodSecurity
public class AuthenticationFilter implements Filter {

    private final JwtUtil jwtUtil;
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        final String authorizationHeader = httpRequest.getHeader(AUTHORIZATION_HEADER);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            try {
                String userId = jwtUtil.extractUserId(token);

                if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    if (jwtUtil.validateToken(token, userId)) {
                        // Load user details and authorities here
                        List<GrantedAuthority> authorities = List.of(
                                new SimpleGrantedAuthority(ERole.ADMIN.name()),
                                new SimpleGrantedAuthority(ERole.USER.name()),
                                new SimpleGrantedAuthority(ERole.PSICOLOGO.name())
                        ); // Adjust based on your needs

                        Authentication authentication = new UsernamePasswordAuthenticationToken(
                                userId,
                                null,
                                authorities
                        );

                        // Set the authentication in the SecurityContext
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        throw new ServletException("Invalid token");
                    }
                }
            } catch (Exception ex) {
                // Set the response status to 401 and write the error message
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.setContentType("application/json");

                PrintWriter writer = httpResponse.getWriter();
                writer.write("{\"error\": \"Unauthorized\", \"message\": \"" + ex.getMessage() + "\"}");
                writer.flush();
                return;
            }
        }

        chain.doFilter(request, response);
    }
}

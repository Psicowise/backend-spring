package com.example.psicowise_backend_spring.service.autenticacao;

import com.example.psicowise_backend_spring.entity.TokenRevogado;
import com.example.psicowise_backend_spring.repository.autenticacao.TokenRevogadoRepository;
import com.example.psicowise_backend_spring.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenBlackListService {

    private final TokenRevogadoRepository tokenRevogadoRepository;
    private final JwtUtil jwtUtil;

    /**
     * Adiciona um token à lista negra
     * @param token O token JWT
     * @param usuarioId ID do usuário associado ao token
     */
    public void revogarToken(String token, UUID usuarioId) {
        // Verificar se o token ainda não expirou
        if (jwtUtil.isTokenExpired(token)) {
            // Token já expirado, não precisa adicionar à lista negra
            return;
        }

        // Extrair a data de expiração do token
        Date expiracaoDate = jwtUtil.extractExpiration(token);
        LocalDateTime expiracao = LocalDateTime.ofInstant(
                expiracaoDate.toInstant(), ZoneId.systemDefault());

        // Criar e salvar a entidade TokenRevogado
        TokenRevogado tokenRevogado = new TokenRevogado();
        tokenRevogado.setToken(token);
        tokenRevogado.setUsuarioId(usuarioId);
        tokenRevogado.setExpiracao(expiracao);

        tokenRevogadoRepository.save(tokenRevogado);
    }

    /**
     * Verifica se um token está na lista negra
     * @param token O token JWT
     * @return true se o token estiver na lista negra, false caso contrário
     */
    public boolean isTokenRevogado(String token) {
        return tokenRevogadoRepository.existsByToken(token);
    }

    /**
     * Tarefa agendada para remover tokens expirados da lista negra
     * Executa uma vez por dia às 1:00
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void limparTokensExpirados() {
        tokenRevogadoRepository.limparTokensExpirados(LocalDateTime.now());
    }
}

package com.example.psicowise_backend_spring.repository.autenticacao;

import com.example.psicowise_backend_spring.entity.autenticacao.TokenRevogado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TokenRevogadoRepository extends JpaRepository<TokenRevogado, UUID> {

    boolean existsByToken(String token);

    // Consulta para limpar tokens expirados
    @Query("DELETE FROM TokenRevogado t WHERE t.expiracao < :now")
    void limparTokensExpirados(LocalDateTime now);
}

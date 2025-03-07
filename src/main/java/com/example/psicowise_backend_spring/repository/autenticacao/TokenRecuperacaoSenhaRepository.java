package com.example.psicowise_backend_spring.repository.autenticacao;

import com.example.psicowise_backend_spring.entity.autenticacao.TokenRecuperacaoSenha;
import com.example.psicowise_backend_spring.entity.autenticacao.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRecuperacaoSenhaRepository extends JpaRepository<TokenRecuperacaoSenha, UUID> {

    Optional<TokenRecuperacaoSenha> findByToken(String token);

    @Query("SELECT t FROM TokenRecuperacaoSenha t WHERE t.usuario.id = :usuarioId AND t.dataExpiracao > :agora")
    List<TokenRecuperacaoSenha> findTokensByUsuarioAndNotExpired(@Param("usuarioId") UUID usuarioId, @Param("agora") LocalDateTime agora);

    @Query("SELECT t FROM TokenRecuperacaoSenha t WHERE t.usuario.id = :usuarioId AND t.expired = false AND t.dataExpiracao > :agora")
    Optional<TokenRecuperacaoSenha> findValidTokenByUsuario(@Param("usuarioId") UUID usuarioId, @Param("agora") LocalDateTime agora);

    @Modifying
    @Query("UPDATE TokenRecuperacaoSenha t SET t.expired = true WHERE t.usuario.id = :usuarioId")
    void invalidarTokensPorUsuarioId(@Param("usuarioId") UUID usuarioId);

    @Modifying
    @Query("UPDATE TokenRecuperacaoSenha t SET t.expired = true WHERE t.usuario = :usuario")
    void invalidarTokensAnteriores(@Param("usuario") Usuario usuario);

    @Modifying
    @Query("DELETE FROM TokenRecuperacaoSenha t WHERE t.dataExpiracao < :agora")
    void limparTokensExpirados(@Param("agora") LocalDateTime agora);
}

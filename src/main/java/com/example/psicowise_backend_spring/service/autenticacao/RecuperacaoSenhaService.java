package com.example.psicowise_backend_spring.service.autenticacao;

import com.example.psicowise_backend_spring.entity.autenticacao.TokenRecuperacaoSenha;
import com.example.psicowise_backend_spring.entity.autenticacao.Usuario;
import com.example.psicowise_backend_spring.exception.autenticacao.TokenInvalidoException;
import com.example.psicowise_backend_spring.exception.usuario.UsuarioNaoEncontradoException;
import com.example.psicowise_backend_spring.repository.autenticacao.TokenRecuperacaoSenhaRepository;
import com.example.psicowise_backend_spring.repository.autenticacao.UsuarioRepository;
import com.example.psicowise_backend_spring.util.EmailUtil;
import com.example.psicowise_backend_spring.util.HashUtil;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecuperacaoSenhaService {

    private final EmailUtil emailUtil;
    private final UsuarioRepository usuarioRepository;
    private final TokenRecuperacaoSenhaRepository tokenRepository;
    private final HashUtil hashUtil;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.token-expiracao-horas:24}")
    private long tokenExpiracaoHoras;

    /**
     * Envia um email de recuperação de senha para o usuário
     * @param email Email do usuário
     * @throws MessagingException Se houver erro no envio do email
     */
    @Transactional
    public void enviarEmailRecuperacaoSenha(String email) throws MessagingException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Email não cadastrado"));

        // Invalidar tokens anteriores
        tokenRepository.invalidarTokensAnteriores(usuario);

        // Gerar token de recuperação de senha
        String token = UUID.randomUUID().toString();

        // Criar e salvar o token
        TokenRecuperacaoSenha tokenRecuperacao = new TokenRecuperacaoSenha();
        tokenRecuperacao.setToken(token);
        tokenRecuperacao.setUsuario(usuario);
        tokenRecuperacao.setDataExpiracao(LocalDateTime.now().plusHours(tokenExpiracaoHoras));
        tokenRepository.save(tokenRecuperacao);

        // Preparar parâmetros para o template
        String resetLink = frontendUrl + "/reset-password?token=" + token;
        Map<String, Object> params = new HashMap<>();
        params.put("nome", usuario.getNome());
        params.put("resetLink", resetLink);

        // Enviar email usando o template
        emailUtil.enviarEmailTemplate(
                email,
                "Recuperação de Senha - PsicoWise",
                "reset-password",
                params
        );
    }

    /**
     * Redefine a senha do usuário usando um token válido
     * @param token Token de recuperação de senha
     * @param novaSenha Nova senha do usuário
     * @throws TokenInvalidoException Se o token for inválido ou estiver expirado
     */
    @Transactional
    public void redefinirSenha(String token, String novaSenha) {
        TokenRecuperacaoSenha tokenRecuperacao = tokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenInvalidoException("Token inválido ou expirado"));

        if (tokenRecuperacao.isExpirado()) {
            throw new TokenInvalidoException("Token expirado");
        }

        Usuario usuario = tokenRecuperacao.getUsuario();
        usuario.setSenha(hashUtil.hashPassword(novaSenha));
        usuarioRepository.save(usuario);

        // Invalidar o token após uso
        tokenRecuperacao.setExpired(true);
        tokenRepository.save(tokenRecuperacao);
    }

    /**
     * Valida se um token é válido
     * @param token Token a ser validado
     * @return true se o token for válido, false caso contrário
     */
    public boolean validarToken(String token) {
        return tokenRepository.findByToken(token)
                .map(t -> !t.isExpirado())
                .orElse(false);
    }

    /**
     * Limpa tokens expirados do banco de dados
     * Executa diariamente à 1:00
     */
//    @Scheduled(cron = "0 0 1 * * ?")
//    @Transactional
//    public void limparTokensExpirados() {
//        tokenRepository.limparTokensExpirados(LocalDateTime.now());
//    }
}
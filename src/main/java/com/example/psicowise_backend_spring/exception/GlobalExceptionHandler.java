package com.example.psicowise_backend_spring.exception;

import com.example.psicowise_backend_spring.enums.authenticacao.ERole;
import com.example.psicowise_backend_spring.exception.autenticacao.CredenciaisInvalidasException;
import com.example.psicowise_backend_spring.exception.autenticacao.TokenInvalidoException;
import com.example.psicowise_backend_spring.exception.role.RoleException;
import com.example.psicowise_backend_spring.exception.role.RoleJaExisteException;
import com.example.psicowise_backend_spring.exception.role.RoleNaoEncontradaException;
import com.example.psicowise_backend_spring.exception.usuario.EmailJaCadastradoException;
import com.example.psicowise_backend_spring.exception.usuario.UsuarioException;
import com.example.psicowise_backend_spring.exception.usuario.UsuarioNaoEncontradoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manipulador global de exceções para toda a aplicação.
 * Captura e trata exceções de forma centralizada, garantindo
 * respostas consistentes para o cliente.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Manipula exceções relacionadas a email já cadastrado
     */
    @ExceptionHandler(EmailJaCadastradoException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleEmailJaCadastradoException(EmailJaCadastradoException ex) {
        log.error("Email já cadastrado: {}", ex.getMessage());
        return criarRespostaDeErro(ex.getMessage(), HttpStatus.CONFLICT);
    }

    /**
     * Manipula exceções relacionadas a role não encontrada
     */
    @ExceptionHandler({com.example.psicowise_backend_spring.exception.usuario.RoleNaoEncontradaException.class,
            RoleNaoEncontradaException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleRoleNaoEncontradaException(Exception ex) {
        log.error("Role não encontrada: {}", ex.getMessage());
        return criarRespostaDeErro(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Manipula exceções relacionadas a usuário não encontrado
     */
    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleUsuarioNaoEncontradoException(UsuarioNaoEncontradoException ex) {
        log.error("Usuário não encontrado: {}", ex.getMessage());
        return criarRespostaDeErro(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Manipula exceções relacionadas a credenciais inválidas
     */
    @ExceptionHandler(CredenciaisInvalidasException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Object> handleCredenciaisInvalidasException(CredenciaisInvalidasException ex) {
        log.error("Credenciais inválidas: {}", ex.getMessage());
        return criarRespostaDeErro(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    /**
     * Manipula exceções relacionadas a token inválido
     */
    @ExceptionHandler(TokenInvalidoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleTokenInvalidoException(TokenInvalidoException ex) {
        log.error("Token inválido: {}", ex.getMessage());
        return criarRespostaDeErro(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Manipula exceções relacionadas a role já existente
     */
    @ExceptionHandler(RoleJaExisteException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleRoleJaExisteException(RoleJaExisteException ex) {
        log.error("Role já existe: {}", ex.getMessage());
        return criarRespostaDeErro(ex.getMessage(), HttpStatus.CONFLICT);
    }

    /**
     * Manipula exceções relacionadas a acesso negado
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("Acesso negado: {}", ex.getMessage());
        return criarRespostaDeErro("Você não tem permissão para acessar este recurso", HttpStatus.FORBIDDEN);
    }

    /**
     * Manipula exceções relacionadas a endpoints não encontrados
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        log.error("Endpoint não encontrado: {}", ex.getRequestURL());
        return criarRespostaDeErro("Endpoint não encontrado: " + ex.getRequestURL(), HttpStatus.NOT_FOUND);
    }

    /**
     * Manipula exceções gerais de usuário
     */
    @ExceptionHandler(UsuarioException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleUsuarioException(UsuarioException ex) {
        log.error("Erro de usuário: {}", ex.getMessage());
        return criarRespostaDeErro(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Manipula exceções gerais de role
     */
    @ExceptionHandler(RoleException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleRoleException(RoleException ex) {
        log.error("Erro de role: {}", ex.getMessage());
        return criarRespostaDeErro(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Manipula exceções gerais não tratadas
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
        log.error("Erro não tratado: {}", ex.getMessage(), ex);

        // Mensagem amigável para o usuário
        String mensagem = "Ocorreu um erro interno no servidor. Por favor, tente novamente mais tarde.";

        // Em ambiente de desenvolvimento/teste, pode incluir mais detalhes
        if (isDevelopmentEnvironment()) {
            mensagem += " Detalhes: " + ex.getMessage();
        }

        return criarRespostaDeErro(mensagem, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Verifica se está em ambiente de desenvolvimento
     */
    private boolean isDevelopmentEnvironment() {
        // Implementar lógica para verificar o ambiente atual
        // Por exemplo, verificar propriedades do Spring ou variáveis de ambiente
        return true; // Temporariamente true para debug
    }

    /**
     * Cria uma resposta de erro formatada
     */
    private ResponseEntity<Object> criarRespostaDeErro(String mensagem, HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", mensagem);

        return new ResponseEntity<>(body, status);
    }
}
package com.example.psicowise_backend_spring.exception;

import com.example.psicowise_backend_spring.enums.authenticacao.ERole;
import com.example.psicowise_backend_spring.exception.role.RoleExceptionHandler;
import com.example.psicowise_backend_spring.exception.role.RoleJaExisteException;
import com.example.psicowise_backend_spring.exception.usuario.EmailJaCadastradoException;
import com.example.psicowise_backend_spring.exception.usuario.UsuarioExceptionHandler;
import com.example.psicowise_backend_spring.exception.usuario.UsuarioNaoEncontradoException;
import com.example.psicowise_backend_spring.util.ErroResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private UsuarioExceptionHandler usuarioExceptionHandler;

    @Autowired
    private RoleExceptionHandler roleExceptionHandler;

    @Autowired
    private ErroResponseUtil erroResponseUtil;

    // Exceções de Usuário
    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<Object> handleEmailJaCadastradoException(EmailJaCadastradoException ex) {
        return usuarioExceptionHandler.handleEmailJaCadastradoException(ex);
    }

    @ExceptionHandler(com.example.psicowise_backend_spring.exception.usuario.RoleNaoEncontradaException.class)
    public ResponseEntity<Object> handleUsuarioRoleNaoEncontradaException(
            com.example.psicowise_backend_spring.exception.usuario.RoleNaoEncontradaException ex) {
        return usuarioExceptionHandler.handleRoleNaoEncontradaException(ex);
    }

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<Object> handleUsuarioNaoEncontradoException(UsuarioNaoEncontradoException ex) {
        return usuarioExceptionHandler.handleUsuarioNaoEncontradoException(ex);
    }

    // Exceções de Role
    @ExceptionHandler(RoleJaExisteException.class)
    public ResponseEntity<Object> handleRoleJaExisteException(RoleJaExisteException ex) {
        return roleExceptionHandler.handleRoleJaExisteException(ex);
    }

    @ExceptionHandler(com.example.psicowise_backend_spring.exception.role.RoleNaoEncontradaException.class)
    public ResponseEntity<Object> handleRoleNaoEncontradaException(
            com.example.psicowise_backend_spring.exception.role.RoleNaoEncontradaException ex) {
        return roleExceptionHandler.handleRoleNaoEncontradaException(ex);
    }

    // Exceção genérica
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        // Check if this is a wrapped exception from our domain
        if (ex.getCause() instanceof EmailJaCadastradoException) {
            return usuarioExceptionHandler.handleEmailJaCadastradoException((EmailJaCadastradoException) ex.getCause());
        } else if (ex.getCause() instanceof RoleJaExisteException) {
            return roleExceptionHandler.handleRoleJaExisteException((RoleJaExisteException) ex.getCause());
        } else if (ex.getMessage() != null && ex.getMessage().contains("Email já cadastrado")) {
            return usuarioExceptionHandler.handleEmailJaCadastradoException(new EmailJaCadastradoException());
        } else if (ex.getMessage() != null && ex.getMessage().contains("Role") && ex.getMessage().contains("já existe")) {
            // Extract the role name from the error message
            String roleName = ex.getMessage().split("'")[1]; // Extracts the role name from "Role 'USER' já existe"
            return roleExceptionHandler.handleRoleJaExisteException(new RoleJaExisteException(ERole.valueOf(roleName)));
        }

        return erroResponseUtil.criarRespostaDeErro("Erro interno do servidor: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
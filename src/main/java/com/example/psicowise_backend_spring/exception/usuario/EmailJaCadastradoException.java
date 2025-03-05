package com.example.psicowise_backend_spring.exception.usuario;

public class EmailJaCadastradoException extends UsuarioException {
    public EmailJaCadastradoException() {
        super("Email já cadastrado");
    }
}
package com.example.psicowise_backend_spring.exception.usuario;

public class UsuarioNaoEncontradoException extends UsuarioException {
    public UsuarioNaoEncontradoException(String identificador) {
        super("Usuário não encontrado com o identificador: " + identificador);
    }
}
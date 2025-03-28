package com.example.psicowise_backend_spring.enums.common;

public enum TipoTelefone {
    CELULAR("Celular"),
    RESIDENCIAL("Residencial"),
    COMERCIAL("Comercial"),
    RECADO("Recado"),
    OUTRO("Outro");

    private final String descricao;

    TipoTelefone(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
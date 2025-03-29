package com.example.psicowise_backend_spring.enums.common;

public enum TipoProprietario {
    PACIENTE("Paciente"),
    PSICOLOGO("Psicólogo"),
    FUNCIONARIO("Funcionário"),
    OUTRO("Outro");

    private final String descricao;

    TipoProprietario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
package com.example.psicowise_backend_spring.enums.consulta;

public enum TipoPagamento {
    CONSULTA_AVULSA("Pagamento por consulta"),
    MENSAL("Pagamento mensal"),
    PLANO_SAUDE("Plano de saúde"),
    DOACAO("Doação"),
    ISENTO("Isento");

    private final String descricao;

    TipoPagamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}

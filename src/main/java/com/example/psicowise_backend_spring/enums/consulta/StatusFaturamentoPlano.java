package com.example.psicowise_backend_spring.enums.consulta;

public enum StatusFaturamentoPlano {
    AGUARDANDO_ENVIO("Aguardando envio ao plano"),
    ENVIADO("Enviado ao plano"),
    EM_ANALISE("Em análise pelo plano"),
    APROVADO("Aprovado, aguardando pagamento"),
    PAGO("Pagamento recebido"),
    GLOSADO("Faturamento glosado"),
    CANCELADO("Faturamento cancelado");

    private final String descricao;

    StatusFaturamentoPlano(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
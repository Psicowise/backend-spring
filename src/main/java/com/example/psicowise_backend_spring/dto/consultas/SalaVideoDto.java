
package com.example.psicowise_backend_spring.dto.consultas;

import java.time.LocalDateTime;
import java.util.UUID;

public class SalaVideoDto {
    private UUID id;
    private UUID consultaId;
    private String salaId;
    private String linkAcesso;
    private String linkHost;
    private boolean ativa;
    private LocalDateTime dataAtivacao;
    private LocalDateTime dataDesativacao;

    public SalaVideoDto(UUID id, UUID consultaId, String salaId, String linkAcesso, String linkHost, boolean ativa, LocalDateTime dataAtivacao, LocalDateTime dataDesativacao) {
        this.id = id;
        this.consultaId = consultaId;
        this.salaId = salaId;
        this.linkAcesso = linkAcesso;
        this.linkHost = linkHost;
        this.ativa = ativa;
        this.dataAtivacao = dataAtivacao;
        this.dataDesativacao = dataDesativacao;
    }

    // Métodos padrão para compatibilidade com Java records
    public UUID id() {
        return id;
    }

    public UUID consultaId() {
        return consultaId;
    }

    public String salaId() {
        return salaId;
    }

    public String linkAcesso() {
        return linkAcesso;
    }

    public String linkHost() {
        return linkHost;
    }

    public boolean ativa() {
        return ativa;
    }

    public LocalDateTime dataAtivacao() {
        return dataAtivacao;
    }

    public LocalDateTime dataDesativacao() {
        return dataDesativacao;
    }

    // Getters e Setters tradicionais
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getConsultaId() {
        return consultaId;
    }

    public void setConsultaId(UUID consultaId) {
        this.consultaId = consultaId;
    }

    public String getSalaId() {
        return salaId;
    }

    public void setSalaId(String salaId) {
        this.salaId = salaId;
    }

    public String getLinkAcesso() {
        return linkAcesso;
    }

    public void setLinkAcesso(String linkAcesso) {
        this.linkAcesso = linkAcesso;
    }

    public String getLinkHost() {
        return linkHost;
    }

    public void setLinkHost(String linkHost) {
        this.linkHost = linkHost;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    public LocalDateTime getDataAtivacao() {
        return dataAtivacao;
    }

    public void setDataAtivacao(LocalDateTime dataAtivacao) {
        this.dataAtivacao = dataAtivacao;
    }

    public LocalDateTime getDataDesativacao() {
        return dataDesativacao;
    }

    public void setDataDesativacao(LocalDateTime dataDesativacao) {
        this.dataDesativacao = dataDesativacao;
    }
}

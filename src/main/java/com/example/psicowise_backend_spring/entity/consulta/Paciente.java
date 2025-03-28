package com.example.psicowise_backend_spring.entity.consulta;

import com.example.psicowise_backend_spring.entity.common.Telefone;
import com.example.psicowise_backend_spring.entity.consulta.configuracao_financeira.ConfiguracaoPagamento;
import com.example.psicowise_backend_spring.util.TelefoneUtil;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "TB_PACIENTES")
public class Paciente {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String nome;

    private String sobrenome;

    private String email;

    @ManyToOne
    @JoinColumn(name = "psicologo_id")
    private Psicologo psicologo;

    @OneToOne(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private ConfiguracaoPagamento configuracaoPagamento;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Telefone> telefones = new ArrayList<>();

    /**
     * Método de conveniência para obter o telefone principal
     * @return O número de telefone principal formatado ou null
     */
    @Transient
    public String getTelefone() {
        return TelefoneUtil.obterNumeroTelefonePrincipal(telefones);
    }

    /**
     * Método de conveniência para obter o telefone WhatsApp
     * @return O número WhatsApp formatado para API ou null
     */
    @Transient
    public String getTelefoneWhatsapp() {
        return TelefoneUtil.obterNumeroWhatsapp(telefones);
    }

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

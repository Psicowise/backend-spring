    package com.example.psicowise_backend_spring.entity.consulta;

    import com.example.psicowise_backend_spring.entity.autenticacao.Usuario;
    import com.example.psicowise_backend_spring.entity.common.Telefone;
    import com.example.psicowise_backend_spring.util.TelefoneUtil;
    import jakarta.persistence.*;
    import lombok.*;
    import org.springframework.data.annotation.CreatedDate;
    import org.springframework.data.annotation.LastModifiedDate;
    import org.springframework.data.jpa.domain.support.AuditingEntityListener;

    import java.time.LocalDateTime;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.UUID;

    @Entity
    @Data
    @Table(name = "TB_PSICOLOGOS")
    @EntityListeners(AuditingEntityListener.class)
    public class Psicologo {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private UUID id;

        @Column(name = "crp", unique = true)
        private String crp;

        @OneToOne
        @JoinColumn(name = "usuario_id")
        private Usuario usuario;

        @ManyToMany
        @JoinTable(
                name = "TB_PSICOLOGOS_ESPECIALIDADES",
                joinColumns = @JoinColumn(name = "psicologo_id"),
                inverseJoinColumns = @JoinColumn(name = "especialidade_id")
        )
        private List<Especialidade> especialidades;

        @OneToMany(mappedBy = "psicologo")
        private List<Paciente> pacientes;

        @OneToMany(mappedBy = "psicologo", cascade = CascadeType.ALL, orphanRemoval = true)
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
package com.example.psicowise_backend_spring.util;

import com.example.psicowise_backend_spring.entity.common.Telefone;
import com.example.psicowise_backend_spring.entity.consulta.Paciente;
import com.example.psicowise_backend_spring.entity.consulta.Psicologo;
import com.example.psicowise_backend_spring.enums.common.TipoProprietario;
import com.example.psicowise_backend_spring.repository.common.TelefoneRepository;
import com.example.psicowise_backend_spring.repository.consulta.PacienteRepository;
import com.example.psicowise_backend_spring.repository.consulta.PsicologoRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Classe utilitária para ajudar na migração de telefones do modelo antigo para o novo
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MigracaoTelefoneHelper {

    private final TelefoneRepository telefoneRepository;
    private final PacienteRepository pacienteRepository;
    private final PsicologoRepository psicologoRepository;

    /**
     * Este método é executado quando a aplicação inicia, após a criação de todos os beans
     * Verifica se existem telefones que ainda não possuem proprietarioId ou tipoProprietario e os corrige
     */
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void verificarECorrigirTelefones() {
        log.info("Verificando telefones que precisam de migração...");

        List<Telefone> telefonesParaCorrigir = telefoneRepository.findByProprietarioIdIsNullOrTipoProprietarioIsNull();

        if (telefonesParaCorrigir.isEmpty()) {
            log.info("Não foram encontrados telefones para migrar.");
            return;
        }

        log.info("Foram encontrados {} telefones para migrar.", telefonesParaCorrigir.size());
        int corrigidos = 0;

        for (Telefone telefone : telefonesParaCorrigir) {
            boolean corrigido = false;

            // Tentar encontrar o proprietário paciente
            if (telefone.getProprietarioId() == null && telefone.getTipoProprietario() == null) {
                // Implementação de migração para o novo modelo
                // Esta parte seria customizada de acordo com o modelo de dados existente
                // e como você estava relacionando telefones anteriormente

                log.warn("Telefone id={} não possui dados de proprietário. Verifique manualmente.", telefone.getId());
            }

            if (corrigido) {
                corrigidos++;
                telefoneRepository.save(telefone);
            }
        }

        log.info("Migração de telefones concluída. {}/{} telefones corrigidos.",
                corrigidos, telefonesParaCorrigir.size());
    }
}

package com.example.psicowise_backend_spring.service.mensagens;

import com.example.psicowise_backend_spring.entity.consulta.Consulta;
import com.example.psicowise_backend_spring.repository.consulta.ConsultaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgendadorLembretesService {

    private final ConsultaRepository consultaRepository;
    private final NotificacaoService notificacaoService;

    /**
     * Verifica e envia lembretes de consultas a cada 15 minutos
     */
    @Scheduled(fixedRate = 900000) // 15 minutos em milissegundos
    public void verificarEEnviarLembretes() {
        log.info("Iniciando verificação de lembretes de consultas");
        
        LocalDateTime agora = LocalDateTime.now();
        
        // 1 hora antes
        enviarLembretesParaIntervalo(agora.plusHours(1).minusMinutes(7), agora.plusHours(1).plusMinutes(7));
        
        // 24 horas antes
        enviarLembretesParaIntervalo(agora.plusDays(1).minusMinutes(15), agora.plusDays(1).plusMinutes(15));
        
        // 3 dias antes
        enviarLembretesParaIntervalo(agora.plusDays(3).minusMinutes(30), agora.plusDays(3).plusMinutes(30));
        
        log.info("Verificação de lembretes concluída");
    }

    /**
     * Envia lembretes de consulta ao criar ou atualizar uma consulta
     * @param consulta Consulta a ser lembrada
     */
    public void agendarLembretesParaNovaConsulta(Consulta consulta) {
        if (consulta == null || consulta.getDataHora() == null) {
            log.error("Impossível agendar lembretes: consulta inválida");
            return;
        }

        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime dataConsulta = consulta.getDataHora();
        
        // Se a consulta ainda estiver a mais de 3 dias, agenda para 3 dias antes
        if (ChronoUnit.HOURS.between(agora, dataConsulta) > 72) {
            log.info("Consulta {} agendada para mais de 3 dias - lembrete de 3 dias será enviado posteriormente", consulta.getId());
        } 
        // Se a consulta estiver a mais de 24 horas, mas menos de 3 dias
        else if (ChronoUnit.HOURS.between(agora, dataConsulta) > 24) {
            log.info("Consulta {} agendada para menos de 3 dias - enviando lembrete de 3 dias imediatamente", consulta.getId());
            notificacaoService.enviarLembreteConsulta(consulta);
        }
        // Se a consulta estiver a mais de 1 hora, mas menos de 24 horas
        else if (ChronoUnit.HOURS.between(agora, dataConsulta) > 1) {
            log.info("Consulta {} agendada para menos de 24 horas - enviando lembrete de 24 horas imediatamente", consulta.getId());
            notificacaoService.enviarLembreteConsulta(consulta);
        }
        // Se a consulta estiver a menos de 1 hora
        else {
            log.info("Consulta {} agendada para menos de 1 hora - enviando lembrete de 1 hora imediatamente", consulta.getId());
            notificacaoService.enviarLembreteConsulta(consulta);
        }
    }

    /**
     * Método auxiliar para buscar e enviar lembretes dentro de um intervalo específico
     * @param inicio Início do intervalo de tempo
     * @param fim Fim do intervalo de tempo
     */
    private void enviarLembretesParaIntervalo(LocalDateTime inicio, LocalDateTime fim) {
        List<Consulta> consultas = consultaRepository.findByDataHoraBetween(inicio, fim);
        
        if (consultas.isEmpty()) {
            return;
        }
        
        log.info("Encontradas {} consultas para envio de lembretes no intervalo {} - {}", 
                consultas.size(), inicio, fim);
        
        for (Consulta consulta : consultas) {
            try {
                boolean resultado = notificacaoService.enviarLembreteConsulta(consulta);
                if (resultado) {
                    log.info("Lembrete enviado com sucesso para consulta {}", consulta.getId());
                } else {
                    log.warn("Falha ao enviar lembrete para consulta {}", consulta.getId());
                }
            } catch (Exception e) {
                log.error("Erro ao enviar lembrete para consulta {}: {}", consulta.getId(), e.getMessage(), e);
            }
        }
    }
}

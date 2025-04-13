
package com.example.psicowise_backend_spring.service.consultas;

import com.example.psicowise_backend_spring.entity.consulta.Consulta;
import com.example.psicowise_backend_spring.service.mensagens.NotificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendamentoLembreteService {
    
    private final ConsultaRepository consultaRepository;
    private final NotificacaoService notificacaoService;

    @Scheduled(cron = "0 0 * * * *") // Roda a cada hora
    public void enviarLembretes() {
        LocalDateTime agora = LocalDateTime.now();
        List<Consulta> consultas = consultaRepository.findByDataHoraBetween(
            agora, agora.plusDays(4));
        
        for (Consulta consulta : consultas) {
            long horasAteConsulta = ChronoUnit.HOURS.between(agora, consulta.getDataHora());
            
            if (horasAteConsulta == 24 || horasAteConsulta == 72 || horasAteConsulta == 1) {
                notificacaoService.enviarLembreteConsulta(consulta);
            }
        }
    }
}

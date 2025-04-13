
package com.example.psicowise_backend_spring.service.mensagens;

import com.example.psicowise_backend_spring.entity.consulta.Consulta;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AgendadorLembretesService {
    private final NotificacaoService notificacaoService;
    private final ConsultaRepository consultaRepository;

    @Scheduled(fixedRate = 3600000) // 1 hora
    public void verificarLembretes() {
        LocalDateTime agora = LocalDateTime.now();
        List<Consulta> consultas = consultaRepository.findByDataHoraBetween(
            agora, 
            agora.plusHours(25)
        );
        
        for (Consulta consulta : consultas) {
            agendarLembretes(consulta);
        }
    }

    private void agendarLembretes(Consulta consulta) {
        Duration tempoAteConsulta = Duration.between(LocalDateTime.now(), consulta.getDataHora());
        
        if (tempoAteConsulta.toHours() <= 24) {
            notificacaoService.enviarLembreteEmail(consulta);
        }
        
        if (tempoAteConsulta.toHours() <= 1) {
            notificacaoService.enviarLembreteWhatsapp(consulta);
        }
    }
}

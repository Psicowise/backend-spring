
package com.example.psicowise_backend_spring.service.notificacao;

import com.example.psicowise_backend_spring.entity.consulta.Consulta;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LembreteService {
    
    public void agendarLembretes(Consulta consulta) {
        agendarLembreteWhatsApp(consulta, 72); // 3 dias
        agendarLembreteWhatsApp(consulta, 24); // 24h
        agendarLembreteWhatsApp(consulta, 1);  // 1h
        agendarLembreteEmail(consulta, 24);    // 24h
    }

    @Scheduled(fixedRate = 300000) // Executa a cada 5 minutos
    public void verificarLembretesAgendados() {
        // Implementar lógica de verificação e envio
    }

    private void agendarLembreteWhatsApp(Consulta consulta, int horas) {
        // Implementar integração com WhatsApp
    }

    private void agendarLembreteEmail(Consulta consulta, int horas) {
        // Implementar envio de email
    }
}

package com.example.psicowise_backend_spring.service.consultas;

import com.example.psicowise_backend_spring.entity.consulta.Consulta;
import com.example.psicowise_backend_spring.entity.consulta.SalaVideo;
import com.example.psicowise_backend_spring.repository.consulta.ConsultaRepository;
import com.example.psicowise_backend_spring.repository.consulta.SalaVideoRepository;
import com.example.psicowise_backend_spring.service.notificacao.LembreteService;
import com.example.psicowise_backend_spring.service.notificacao.AgendadorLembretesService; // Added import
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // Added import for logging
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConsultaService {

    private static final Logger log = LoggerFactory.getLogger(ConsultaService.class); // Added logger

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private SalaVideoRepository salaVideoRepository;

    @Autowired
    private LembreteService lembreteService;

    @Autowired
    private AgendadorLembretesService agendadorLembretesService; // Added dependency injection


    public Consulta criarConsulta(Consulta consulta) {
        Consulta novaConsulta = consultaRepository.save(consulta);
        criarSalaVideo(novaConsulta);
        //lembreteService.agendarLembretes(novaConsulta); //Commented out - replaced with the new scheduler
        try {
            agendadorLembretesService.agendarLembretesParaNovaConsulta(novaConsulta);
        } catch (Exception e) {
            log.error("Erro ao agendar lembretes para consulta {}: {}", novaConsulta.getId(), e.getMessage());
            // Não interrompe o fluxo se falhar o agendamento de lembretes
        }
        return novaConsulta;
    }

    private void criarSalaVideo(Consulta consulta) {
        SalaVideo sala = new SalaVideo();
        sala.setConsulta(consulta);
        String salaId = "psicowise_" + consulta.getId().toString().substring(0, 8);
        sala.setSalaId(salaId);
        sala.setLinkAcesso("https://meet.jit.si/" + salaId);
        sala.setLinkHost("https://meet.jit.si/" + salaId + "#config.startWithVideoMuted=false");
        sala.setAtiva(false);
        salaVideoRepository.save(sala);
    }
}


// Added AgendadorLembretesService (Simplified Implementation)
@Service
class AgendadorLembretesService {

    private final LembreteService lembreteService; //Uses the existing LembreteService

    @Autowired
    public AgendadorLembretesService(LembreteService lembreteService) {
        this.lembreteService = lembreteService;
    }

    public void agendarLembretesParaNovaConsulta(Consulta consulta) {
        //  This is a simplified example.  Replace with actual WhatsApp sending logic.
        //  This would require a library for WhatsApp API interaction.
        lembreteService.enviarLembreteWhatsApp(consulta, 1, "1 hora antes da sua consulta!");
        lembreteService.enviarLembreteWhatsApp(consulta, 24, "24 horas antes da sua consulta!");
        lembreteService.enviarLembreteWhatsApp(consulta, 72, "72 horas antes da sua consulta!");
    }
}
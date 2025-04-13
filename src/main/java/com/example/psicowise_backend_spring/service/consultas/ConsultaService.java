package com.example.psicowise_backend_spring.service.consultas;

import com.example.psicowise_backend_spring.entity.consulta.Consulta;
import com.example.psicowise_backend_spring.entity.consulta.SalaVideo;
import com.example.psicowise_backend_spring.repository.consulta.ConsultaRepository;
import com.example.psicowise_backend_spring.repository.consulta.SalaVideoRepository;
import com.example.psicowise_backend_spring.service.notificacao.LembreteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConsultaService {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private SalaVideoRepository salaVideoRepository;

    @Autowired
    private LembreteService lembreteService;

    @Autowired
    private SalaVideoService salaVideoService; // Added SalaVideoService

    public Consulta criarConsulta(Consulta consulta) {
        Consulta novaConsulta = consultaRepository.save(consulta);
        salaVideoService.criarSalaParaConsulta(novaConsulta); // Integrate video room creation
        lembreteService.agendarLembretes(novaConsulta);
        return novaConsulta;
    }


}

@Service
class SalaVideoService {
    @Autowired
    private SalaVideoRepository salaVideoRepository;

    public void criarSalaParaConsulta(Consulta consulta) {
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
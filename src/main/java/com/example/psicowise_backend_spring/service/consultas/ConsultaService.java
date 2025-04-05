
package com.example.psicowise_backend_spring.service.consultas;

import com.example.psicowise_backend_spring.entity.consulta.Consulta;
import com.example.psicowise_backend_spring.entity.consulta.SalaVideo;
import com.example.psicowise_backend_spring.repository.consulta.ConsultaRepository;
import com.example.psicowise_backend_spring.repository.consulta.SalaVideoRepository;
import com.example.psicowise_backend_spring.service.lembrete.LembreteService;
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

    public Consulta criarConsulta(Consulta consulta) {
        Consulta novaConsulta = consultaRepository.save(consulta);
        criarSalaVideo(novaConsulta);
        lembreteService.agendarLembretes(novaConsulta);
        return novaConsulta;
    }

    private void criarSalaVideo(Consulta consulta) {
        SalaVideo sala = new SalaVideo();
        sala.setConsulta(consulta);
        sala.setLink("https://meet.jit.si/" + consulta.getId());
        salaVideoRepository.save(sala);
    }
}

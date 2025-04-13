package com.example.psicowise_backend_spring.service.mensagens;

import com.example.psicowise_backend_spring.entity.consulta.Consulta;
import com.example.psicowise_backend_spring.repository.consulta.ConsultaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendadorLembretesService {

    private final ConsultaRepository consultaRepository;

    @Scheduled(cron = "0 0 8 * * *") // Executa todos os dias às 8h
    public void enviarLembretes() {
        LocalDateTime hoje = LocalDateTime.now();
        LocalDateTime amanha = hoje.plusDays(1);

        List<Consulta> consultasProximas = consultaRepository.findByDataHoraBetween(hoje, amanha);

        for (Consulta consulta : consultasProximas) {
            // Lógica para enviar lembretes
        }
    }
}
package com.example.psicowise_backend_spring.service.consultas;

import com.example.psicowise_backend_spring.dto.consultas.CriarNotaConsultaDto;
import com.example.psicowise_backend_spring.dto.consultas.NotaConsultaDto;
import com.example.psicowise_backend_spring.entity.consulta.Consulta;
import com.example.psicowise_backend_spring.entity.consulta.NotaConsulta;
import com.example.psicowise_backend_spring.entity.consulta.Psicologo;
import com.example.psicowise_backend_spring.repository.consulta.ConsultaRepository;
import com.example.psicowise_backend_spring.repository.consulta.NotaConsultaRepository;
import com.example.psicowise_backend_spring.repository.consulta.PsicologoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotaConsultaService {

    private final NotaConsultaRepository notaConsultaRepository;
    private final ConsultaRepository consultaRepository;
    private final PsicologoRepository psicologoRepository;

    @Autowired
    public NotaConsultaService(
            NotaConsultaRepository notaConsultaRepository,
            ConsultaRepository consultaRepository,
            PsicologoRepository psicologoRepository) {
        this.notaConsultaRepository = notaConsultaRepository;
        this.consultaRepository = consultaRepository;
        this.psicologoRepository = psicologoRepository;
    }

    /**
     * Cria uma nova nota para uma consulta
     *
     * @param notaDto DTO com os dados da nota a ser criada
     * @return A nota criada
     */
    @Transactional
    public ResponseEntity<NotaConsultaDto> criarNota(CriarNotaConsultaDto notaDto) {
        try {
            // Verificar se a consulta existe
            Consulta consulta = consultaRepository.findById(notaDto.consultaId())
                    .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

            // Verificar se o psicólogo autenticado é responsável pela consulta
            Psicologo psicologoAutenticado = getPsicologoAutenticado();
            if (!consulta.getPsicologo().getId().equals(psicologoAutenticado.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(null);
            }

            // Criar a nota
            NotaConsulta nota = new NotaConsulta();
            nota.setConsulta(consulta);
            nota.setTitulo(notaDto.titulo());
            nota.setConteudo(notaDto.conteudo());
            nota.setDataNota(notaDto.dataNota() != null ? notaDto.dataNota() : LocalDateTime.now());

            NotaConsulta notaSalva = notaConsultaRepository.save(nota);

            // Converter para DTO e retornar
            return ResponseEntity.ok(converterParaDto(notaSalva));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Atualiza uma nota existente
     *
     * @param id ID da nota a ser atualizada
     * @param notaDto DTO com os novos dados da nota
     * @return A nota atualizada
     */
    @Transactional
    public ResponseEntity<NotaConsultaDto> atualizarNota(UUID id, CriarNotaConsultaDto notaDto) {
        try {
            // Verificar se a nota existe
            NotaConsulta nota = notaConsultaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Nota não encontrada"));

            // Verificar se o psicólogo autenticado é responsável pela consulta
            Psicologo psicologoAutenticado = getPsicologoAutenticado();
            if (!nota.getConsulta().getPsicologo().getId().equals(psicologoAutenticado.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(null);
            }

            // Atualizar os dados da nota
            if (notaDto.titulo() != null) {
                nota.setTitulo(notaDto.titulo());
            }

            if (notaDto.conteudo() != null) {
                nota.setConteudo(notaDto.conteudo());
            }

            if (notaDto.dataNota() != null) {
                nota.setDataNota(notaDto.dataNota());
            }

            NotaConsulta notaAtualizada = notaConsultaRepository.save(nota);

            // Converter para DTO e retornar
            return ResponseEntity.ok(converterParaDto(notaAtualizada));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Busca uma nota pelo ID
     *
     * @param id ID da nota
     * @return A nota encontrada
     */
    public ResponseEntity<NotaConsultaDto> buscarNotaPorId(UUID id) {
        try {
            // Verificar se a nota existe
            Optional<NotaConsulta> notaOpt = notaConsultaRepository.findById(id);

            if (notaOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            NotaConsulta nota = notaOpt.get();

            // Verificar se o psicólogo autenticado é responsável pela consulta
            Psicologo psicologoAutenticado = getPsicologoAutenticado();
            if (!nota.getConsulta().getPsicologo().getId().equals(psicologoAutenticado.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(null);
            }

            // Converter para DTO e retornar
            return ResponseEntity.ok(converterParaDto(nota));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Lista todas as notas de uma consulta
     *
     * @param consultaId ID da consulta
     * @return Lista de notas
     */
    public ResponseEntity<List<NotaConsultaDto>> listarNotasPorConsulta(UUID consultaId) {
        try {
            // Verificar se a consulta existe
            Consulta consulta = consultaRepository.findById(consultaId)
                    .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

            // Verificar se o psicólogo autenticado é responsável pela consulta
            Psicologo psicologoAutenticado = getPsicologoAutenticado();
            if (!consulta.getPsicologo().getId().equals(psicologoAutenticado.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(null);
            }

            // Buscar as notas da consulta ordenadas por data
            List<NotaConsulta> notas = notaConsultaRepository.findByConsultaIdOrderByDataNotaDesc(consultaId);

            // Converter para DTOs e retornar
            List<NotaConsultaDto> notasDto = notas.stream()
                    .map(this::converterParaDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(notasDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Exclui uma nota
     *
     * @param id ID da nota a ser excluída
     * @return Mensagem de sucesso
     */
    @Transactional
    public ResponseEntity<String> excluirNota(UUID id) {
        try {
            // Verificar se a nota existe
            NotaConsulta nota = notaConsultaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Nota não encontrada"));

            // Verificar se o psicólogo autenticado é responsável pela consulta
            Psicologo psicologoAutenticado = getPsicologoAutenticado();
            if (!nota.getConsulta().getPsicologo().getId().equals(psicologoAutenticado.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Acesso negado");
            }

            // Excluir a nota
            notaConsultaRepository.delete(nota);

            return ResponseEntity.ok("Nota excluída com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao excluir nota: " + e.getMessage());
        }
    }

    /**
     * Lista todas as notas de um paciente
     *
     * @param pacienteId ID do paciente
     * @return Lista de notas
     */
    public ResponseEntity<List<NotaConsultaDto>> listarNotasPorPaciente(UUID pacienteId) {
        try {
            // Verificar se o psicólogo autenticado tem acesso ao paciente
            Psicologo psicologoAutenticado = getPsicologoAutenticado();

            // Buscar o paciente e verificar se pertence ao psicólogo autenticado
            boolean temAcesso = psicologoRepository.findById(psicologoAutenticado.getId())
                    .map(p -> p.getPacientes().stream()
                            .anyMatch(paciente -> paciente.getId().equals(pacienteId)))
                    .orElse(false);

            if (!temAcesso) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }

            // Buscar as notas do paciente ordenadas por data
            List<NotaConsulta> notas = notaConsultaRepository.findByPacienteIdOrderByDataNotaDesc(pacienteId);

            // Converter para DTOs e retornar
            List<NotaConsultaDto> notasDto = notas.stream()
                    .map(this::converterParaDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(notasDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Obtém o psicólogo autenticado
     *
     * @return O psicólogo autenticado
     */
    private Psicologo getPsicologoAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String idString = auth.getName();
        UUID usuarioId = UUID.fromString(idString);

        return psicologoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Psicólogo não encontrado"));
    }

    /**
     * Converte uma entidade NotaConsulta para DTO
     *
     * @param nota Entidade a ser convertida
     * @return DTO correspondente
     */
    private NotaConsultaDto converterParaDto(NotaConsulta nota) {
        return new NotaConsultaDto(
                nota.getId(),
                nota.getConsulta().getId(),
                nota.getTitulo(),
                nota.getConteudo(),
                nota.getDataNota()
        );
    }
}
package com.example.psicowise_backend_spring.service.consultas;

import com.example.psicowise_backend_spring.dto.consultas.EditarPagamentoConsultaDto;
import com.example.psicowise_backend_spring.entity.consulta.Consulta;
import com.example.psicowise_backend_spring.entity.consulta.Faturamento;
import com.example.psicowise_backend_spring.entity.consulta.Paciente;
import com.example.psicowise_backend_spring.entity.consulta.Psicologo;
import com.example.psicowise_backend_spring.entity.consulta.configuracao_financeira.ConfiguracaoPagamento;
import com.example.psicowise_backend_spring.entity.consulta.configuracao_financeira.Doacao;
import com.example.psicowise_backend_spring.entity.consulta.configuracao_financeira.PagamentoAvulso;
import com.example.psicowise_backend_spring.entity.consulta.configuracao_financeira.PagamentoMensal;
import com.example.psicowise_backend_spring.entity.consulta.configuracao_financeira.PlanoSaude;
import com.example.psicowise_backend_spring.enums.consulta.StatusFaturamentoPlano;
import com.example.psicowise_backend_spring.enums.consulta.TipoPagamento;
import com.example.psicowise_backend_spring.repository.consulta.ConsultaRepository;
import com.example.psicowise_backend_spring.repository.consulta.FaturamentoRepository;
import com.example.psicowise_backend_spring.repository.consulta.PsicologoRepository;
import com.example.psicowise_backend_spring.repository.consulta.configuracao_pagamento.ConfiguracaoPagamentoRepository;
import com.example.psicowise_backend_spring.repository.consulta.configuracao_pagamento.DoacaoRepository;
import com.example.psicowise_backend_spring.repository.consulta.configuracao_pagamento.PagamentoAvulsoRepository;
import com.example.psicowise_backend_spring.repository.consulta.configuracao_pagamento.PagamentoMensalRepository;
import com.example.psicowise_backend_spring.repository.consulta.configuracao_pagamento.PlanoSaudeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Service
public class PagamentoConsultaService {

    private final ConsultaRepository consultaRepository;
    private final FaturamentoRepository faturamentoRepository;
    private final PsicologoRepository psicologoRepository;
    private final ConfiguracaoPagamentoRepository configuracaoPagamentoRepository;
    private final PagamentoAvulsoRepository pagamentoAvulsoRepository;
    private final PagamentoMensalRepository pagamentoMensalRepository;
    private final PlanoSaudeRepository planoSaudeRepository;
    private final DoacaoRepository doacaoRepository;

    @Autowired
    public PagamentoConsultaService(
            ConsultaRepository consultaRepository,
            FaturamentoRepository faturamentoRepository,
            PsicologoRepository psicologoRepository,
            ConfiguracaoPagamentoRepository configuracaoPagamentoRepository,
            PagamentoAvulsoRepository pagamentoAvulsoRepository,
            PagamentoMensalRepository pagamentoMensalRepository,
            PlanoSaudeRepository planoSaudeRepository,
            DoacaoRepository doacaoRepository) {
        this.consultaRepository = consultaRepository;
        this.faturamentoRepository = faturamentoRepository;
        this.psicologoRepository = psicologoRepository;
        this.configuracaoPagamentoRepository = configuracaoPagamentoRepository;
        this.pagamentoAvulsoRepository = pagamentoAvulsoRepository;
        this.pagamentoMensalRepository = pagamentoMensalRepository;
        this.planoSaudeRepository = planoSaudeRepository;
        this.doacaoRepository = doacaoRepository;
    }

    /**
     * Edita a forma de pagamento de uma consulta
     *
     * @param pagamentoDto DTO com os dados do pagamento
     * @return Mensagem de sucesso ou erro
     */
    @Transactional
    public ResponseEntity<String> editarPagamentoConsulta(EditarPagamentoConsultaDto pagamentoDto) {
        try {
            // Verificar se a consulta existe
            Consulta consulta = consultaRepository.findById(pagamentoDto.consultaId())
                    .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

            // Verificar se o psicólogo autenticado é responsável pela consulta
            Psicologo psicologoAutenticado = getPsicologoAutenticado();
            if (!consulta.getPsicologo().getId().equals(psicologoAutenticado.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Acesso negado: você não é o psicólogo responsável por esta consulta");
            }

            Paciente paciente = consulta.getPaciente();

            // Obter a configuração de pagamento atual do paciente
            ConfiguracaoPagamento configuracaoPagamento = null;

            if (paciente.getConfiguracaoPagamento() != null) {
                configuracaoPagamento = paciente.getConfiguracaoPagamento();
            } else {
                // Criar nova configuração de pagamento
                configuracaoPagamento = new ConfiguracaoPagamento();
                configuracaoPagamento.setPaciente(paciente);
                configuracaoPagamento = configuracaoPagamentoRepository.save(configuracaoPagamento);
                paciente.setConfiguracaoPagamento(configuracaoPagamento);
            }

            // Atualizar o tipo de pagamento
            configuracaoPagamento.setTipoPagamento(pagamentoDto.tipoPagamento());
            configuracaoPagamento = configuracaoPagamentoRepository.save(configuracaoPagamento);

            // Tratar cada tipo de pagamento de forma específica
            switch (pagamentoDto.tipoPagamento()) {
                case CONSULTA_AVULSA:
                    processarPagamentoAvulso(configuracaoPagamento, pagamentoDto);
                    break;
                case MENSAL:
                    processarPagamentoMensal(configuracaoPagamento, pagamentoDto);
                    break;
                case PLANO_SAUDE:
                    processarPlanoSaude(configuracaoPagamento, consulta, pagamentoDto);
                    break;
                case DOACAO:
                    processarDoacao(configuracaoPagamento, pagamentoDto);
                    break;
                case ISENTO:
                    // Para isenção, não há configuração adicional
                    break;
                default:
                    return ResponseEntity.badRequest().body("Tipo de pagamento não reconhecido");
            }

            // Atualizar o faturamento da consulta
            atualizarFaturamentoConsulta(consulta, pagamentoDto);

            return ResponseEntity.ok("Pagamento da consulta atualizado com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar pagamento: " + e.getMessage());
        }
    }

    /**
     * Processa o pagamento avulso
     */
    private void processarPagamentoAvulso(ConfiguracaoPagamento configuracaoPagamento, EditarPagamentoConsultaDto pagamentoDto) {
        // Buscar ou criar o pagamento avulso
        PagamentoAvulso pagamentoAvulso = pagamentoAvulsoRepository.findByConfiguracaoPagamento(configuracaoPagamento)
                .orElse(new PagamentoAvulso());

        pagamentoAvulso.setConfiguracaoPagamento(configuracaoPagamento);
        pagamentoAvulso.setValorConsulta(pagamentoDto.valorConsulta());

        pagamentoAvulsoRepository.save(pagamentoAvulso);
    }

    /**
     * Processa o pagamento mensal
     */
    private void processarPagamentoMensal(ConfiguracaoPagamento configuracaoPagamento, EditarPagamentoConsultaDto pagamentoDto) {
        // Buscar ou criar o pagamento mensal
        PagamentoMensal pagamentoMensal = pagamentoMensalRepository.findByConfiguracaoPagamento(configuracaoPagamento)
                .orElse(new PagamentoMensal());

        pagamentoMensal.setConfiguracaoPagamento(configuracaoPagamento);
        pagamentoMensal.setValorMensal(pagamentoDto.valorConsulta());

        if (pagamentoDto.sessoesIncluidas() != null) {
            pagamentoMensal.setSessoesIncluidas(pagamentoDto.sessoesIncluidas());
        }

        if (pagamentoDto.diaVencimento() != null) {
            pagamentoMensal.setDiaVencimento(pagamentoDto.diaVencimento());
        }

        pagamentoMensalRepository.save(pagamentoMensal);
    }

    /**
     * Processa o pagamento por plano de saúde
     */
    private void processarPlanoSaude(ConfiguracaoPagamento configuracaoPagamento, Consulta consulta, EditarPagamentoConsultaDto pagamentoDto) {
        // Buscar ou criar o plano de saúde
        PlanoSaude planoSaude = planoSaudeRepository.findByConfiguracaoPagamento(configuracaoPagamento)
                .orElse(new PlanoSaude());

        planoSaude.setConfiguracaoPagamento(configuracaoPagamento);
        planoSaude.setConsulta(consulta);
        planoSaude.setValor(pagamentoDto.valorConsulta());

        if (pagamentoDto.nomePlano() != null) {
            planoSaude.setNomePlano(pagamentoDto.nomePlano());
        }

        if (pagamentoDto.numeroAutorizacao() != null) {
            planoSaude.setNumeroAutorizacao(pagamentoDto.numeroAutorizacao());
            planoSaude.setStatus(StatusFaturamentoPlano.APROVADO);
        } else {
            planoSaude.setStatus(StatusFaturamentoPlano.AGUARDANDO_ENVIO);
        }

        planoSaudeRepository.save(planoSaude);
    }

    /**
     * Processa o pagamento por doação
     */
    private void processarDoacao(ConfiguracaoPagamento configuracaoPagamento, EditarPagamentoConsultaDto pagamentoDto) {
        // Buscar ou criar a doação
        Doacao doacao = doacaoRepository.findByConfiguracaoPagamento(configuracaoPagamento)
                .orElse(new Doacao());

        doacao.setConfiguracaoPagamento(configuracaoPagamento);
        doacao.setValorSugerido(pagamentoDto.valorConsulta());

        if (pagamentoDto.observacao() != null) {
            doacao.setObservacao(pagamentoDto.observacao());
        }

        doacaoRepository.save(doacao);
    }

    /**
     * Atualiza o faturamento da consulta
     */
    private void atualizarFaturamentoConsulta(Consulta consulta, EditarPagamentoConsultaDto pagamentoDto) {
        // Buscar faturamento existente ou criar um novo
        Faturamento faturamento = faturamentoRepository.findByConsulta(consulta)
                .stream().findFirst().orElse(new Faturamento());

        faturamento.setConsulta(consulta);
        faturamento.setTipoPagamento(pagamentoDto.tipoPagamento());
        faturamento.setValor(pagamentoDto.valorConsulta());

        if (pagamentoDto.tipoPagamento() == TipoPagamento.ISENTO) {
            faturamento.setPago(true);
            faturamento.setDataPagamento(LocalDateTime.now());
        } else {
            faturamento.setPago(false);
            faturamento.setDataPagamento(null);
        }

        faturamentoRepository.save(faturamento);
    }

    /**
     * Registra um pagamento recebido
     *
     * @param consultaId ID da consulta
     * @param observacoes Observações sobre o pagamento
     * @return Mensagem de sucesso ou erro
     */

    /**
     * Registra um pagamento recebido
     *
     * @param consultaId ID da consulta
     * @param observacoes Observações sobre o pagamento
     * @return Mensagem de sucesso ou erro
     */
    @Transactional
    public ResponseEntity<String> registrarPagamentoRecebido(UUID consultaId, String observacoes) {
        try {
            // Verificar se a consulta existe
            Consulta consulta = consultaRepository.findById(consultaId)
                    .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

            // Verificar se o psicólogo autenticado é responsável pela consulta
            Psicologo psicologoAutenticado = getPsicologoAutenticado();
            if (!consulta.getPsicologo().getId().equals(psicologoAutenticado.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Acesso negado: você não é o psicólogo responsável por esta consulta");
            }

            // Buscar faturamento existente
            Optional<Faturamento> faturamentoOpt = faturamentoRepository.findByConsulta(consulta)
                    .stream().findFirst();

            if (faturamentoOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Nenhum faturamento encontrado para esta consulta");
            }

            Faturamento faturamento = faturamentoOpt.get();

            // Atualizar o status do pagamento
            faturamento.setPago(true);
            faturamento.setDataPagamento(LocalDateTime.now());

            if (observacoes != null && !observacoes.isEmpty()) {
                faturamento.setObservacoes(observacoes);
            }

            faturamentoRepository.save(faturamento);

            // Se for plano de saúde, atualizar o status no faturamento do plano
            if (faturamento.getTipoPagamento() == TipoPagamento.PLANO_SAUDE) {
                ConfiguracaoPagamento configuracaoPagamento = consulta.getPaciente().getConfiguracaoPagamento();
                if (configuracaoPagamento != null) {
                    Optional<PlanoSaude> planoSaudeOpt = planoSaudeRepository.findByConfiguracaoPagamento(configuracaoPagamento);
                    if (planoSaudeOpt.isPresent()) {
                        PlanoSaude planoSaude = planoSaudeOpt.get();
                        planoSaude.setStatus(StatusFaturamentoPlano.PAGO);
                        planoSaude.setDataPagamento(LocalDateTime.now());
                        planoSaudeRepository.save(planoSaude);
                    }
                }
            }

            return ResponseEntity.ok("Pagamento registrado com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao registrar pagamento: " + e.getMessage());
        }
    }

    /**
     * Obtém a configuração de pagamento de uma consulta
     *
     * @param consultaId ID da consulta
     * @return Dados do pagamento
     */
    public ResponseEntity<Object> obterPagamentoConsulta(UUID consultaId) {
        try {
            // Verificar se a consulta existe
            Consulta consulta = consultaRepository.findById(consultaId)
                    .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

            // Verificar se o psicólogo autenticado é responsável pela consulta
            Psicologo psicologoAutenticado = getPsicologoAutenticado();
            if (!consulta.getPsicologo().getId().equals(psicologoAutenticado.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Acesso negado: você não é o psicólogo responsável por esta consulta");
            }

            // Buscar faturamento existente
            Optional<Faturamento> faturamentoOpt = faturamentoRepository.findByConsulta(consulta)
                    .stream().findFirst();

            if (faturamentoOpt.isEmpty()) {
                return ResponseEntity.ok(null); // Nenhum faturamento encontrado
            }

            Faturamento faturamento = faturamentoOpt.get();
            ConfiguracaoPagamento configuracaoPagamento = consulta.getPaciente().getConfiguracaoPagamento();

            if (configuracaoPagamento == null) {
                return ResponseEntity.ok(faturamento); // Retorna apenas o faturamento básico
            }

            // Preparar dados detalhados com base no tipo de pagamento
            return switch (configuracaoPagamento.getTipoPagamento()) {
                case CONSULTA_AVULSA -> {
                    Optional<PagamentoAvulso> pagamentoAvulsoOpt = pagamentoAvulsoRepository.findByConfiguracaoPagamento(configuracaoPagamento);
                    if (pagamentoAvulsoOpt.isPresent()) {
                        var resultado = new HashMap<String, Object>();
                        resultado.put("faturamento", faturamento);
                        resultado.put("detalhesPagamento", pagamentoAvulsoOpt.get());
                        yield ResponseEntity.ok(resultado);
                    }
                    yield ResponseEntity.ok(faturamento);
                }
                case MENSAL -> {
                    Optional<PagamentoMensal> pagamentoMensalOpt = pagamentoMensalRepository.findByConfiguracaoPagamento(configuracaoPagamento);
                    if (pagamentoMensalOpt.isPresent()) {
                        var resultado = new HashMap<String, Object>();
                        resultado.put("faturamento", faturamento);
                        resultado.put("detalhesPagamento", pagamentoMensalOpt.get());
                        yield ResponseEntity.ok(resultado);
                    }
                    yield ResponseEntity.ok(faturamento);
                }
                case PLANO_SAUDE -> {
                    Optional<PlanoSaude> planoSaudeOpt = planoSaudeRepository.findByConfiguracaoPagamento(configuracaoPagamento);
                    if (planoSaudeOpt.isPresent()) {
                        var resultado = new HashMap<String, Object>();
                        resultado.put("faturamento", faturamento);
                        resultado.put("detalhesPagamento", planoSaudeOpt.get());
                        yield ResponseEntity.ok(resultado);
                    }
                    yield ResponseEntity.ok(faturamento);
                }
                case DOACAO -> {
                    Optional<Doacao> doacaoOpt = doacaoRepository.findByConfiguracaoPagamento(configuracaoPagamento);
                    if (doacaoOpt.isPresent()) {
                        var resultado = new HashMap<String, Object>();
                        resultado.put("faturamento", faturamento);
                        resultado.put("detalhesPagamento", doacaoOpt.get());
                        yield ResponseEntity.ok(resultado);
                    }
                    yield ResponseEntity.ok(faturamento);
                }
                default -> ResponseEntity.ok(faturamento);
            };
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao obter pagamento: " + e.getMessage());
        }
    }

    /**
     * Cancela um faturamento de consulta
     *
     * @param consultaId ID da consulta
     * @param motivo Motivo do cancelamento
     * @return Mensagem de sucesso ou erro
     */
    @Transactional
    public ResponseEntity<String> cancelarFaturamento(UUID consultaId, String motivo) {
        try {
            // Verificar se a consulta existe
            Consulta consulta = consultaRepository.findById(consultaId)
                    .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

            // Verificar se o psicólogo autenticado é responsável pela consulta
            Psicologo psicologoAutenticado = getPsicologoAutenticado();
            if (!consulta.getPsicologo().getId().equals(psicologoAutenticado.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Acesso negado: você não é o psicólogo responsável por esta consulta");
            }

            // Buscar faturamento existente
            Optional<Faturamento> faturamentoOpt = faturamentoRepository.findByConsulta(consulta)
                    .stream().findFirst();

            if (faturamentoOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Nenhum faturamento encontrado para esta consulta");
            }

            Faturamento faturamento = faturamentoOpt.get();

            // Se já estiver pago, não permitir cancelamento
            if (faturamento.isPago()) {
                return ResponseEntity.badRequest().body("Não é possível cancelar um faturamento já pago");
            }

            // Atualizar observações com o motivo do cancelamento
            if (motivo != null && !motivo.isEmpty()) {
                faturamento.setObservacoes("CANCELADO: " + motivo);
            } else {
                faturamento.setObservacoes("CANCELADO");
            }

            // Se for plano de saúde, atualizar o status no faturamento do plano
            if (faturamento.getTipoPagamento() == TipoPagamento.PLANO_SAUDE) {
                ConfiguracaoPagamento configuracaoPagamento = consulta.getPaciente().getConfiguracaoPagamento();
                if (configuracaoPagamento != null) {
                    Optional<PlanoSaude> planoSaudeOpt = planoSaudeRepository.findByConfiguracaoPagamento(configuracaoPagamento);
                    if (planoSaudeOpt.isPresent()) {
                        PlanoSaude planoSaude = planoSaudeOpt.get();
                        planoSaude.setStatus(StatusFaturamentoPlano.CANCELADO);
                        planoSaudeRepository.save(planoSaude);
                    }
                }
            }

            // Remover o faturamento
            faturamentoRepository.delete(faturamento);

            return ResponseEntity.ok("Faturamento cancelado com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao cancelar faturamento: " + e.getMessage());
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
}
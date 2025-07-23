package com.phoebus.communityapi.service;

import com.phoebus.communityapi.event.CapacidadeMaximaEvent;
import com.phoebus.communityapi.model.CentroComunitario;
import com.phoebus.communityapi.model.Negociacao;
import com.phoebus.communityapi.repository.CentroComunitarioRepository;
import com.phoebus.communityapi.repository.NegociacaoRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CentroComunitarioService {
    private final CentroComunitarioRepository repository;
    private final NegociacaoRepository negociacaoRepository;
    private final ApplicationEventPublisher eventPublisher;

    private static final int PONTOS_MEDICO = 4;
    private static final int PONTOS_VOLUNTARIO = 3;
    private static final int PONTOS_KIT_MEDICO = 7;
    private static final int PONTOS_VEICULO = 5;
    private static final int PONTOS_CESTA_BASICA = 2;
    private static final double LIMITE_OCUPACAO_ALTA = 0.9;

    public CentroComunitario adicionarCentro(CentroComunitario centro) {
        if (centro.getNome() == null || centro.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome do centro é obrigatório");
        }
        if (centro.getEndereco() == null || centro.getEndereco().isBlank()) {
            throw new IllegalArgumentException("Endereço é obrigatório");
        }
        if (centro.getLocalizacao() == null ||
                centro.getLocalizacao().getLatitude() < -90 || centro.getLocalizacao().getLatitude() > 90 ||
                centro.getLocalizacao().getLongitude() < -180 || centro.getLocalizacao().getLongitude() > 180) {
            throw new IllegalArgumentException("Localização inválida");
        }
        if (centro.getCapacidadeMaxima() <= 0) {
            throw new IllegalArgumentException("Capacidade máxima deve ser maior que 0");
        }
        if (centro.getOcupacaoAtual() < 0) {
            throw new IllegalArgumentException("Ocupação atual não pode ser negativa");
        }
        if (centro.getOcupacaoAtual() > centro.getCapacidadeMaxima()) {
            throw new IllegalArgumentException("Ocupação atual não pode exceder a capacidade máxima");
        }
        if (centro.getRecursos() == null) {
            centro.setRecursos(new CentroComunitario.Recursos());
        }
        if (centro.getRecursos().getMedicos() < 0 ||
                centro.getRecursos().getVoluntarios() < 0 ||
                centro.getRecursos().getKitsMedicos() < 0 ||
                centro.getRecursos().getVeiculos() < 0 ||
                centro.getRecursos().getCestasBasicas() < 0) {
            throw new IllegalArgumentException("Recursos não podem ser negativos");
        }

        return repository.save(centro);
    }

    public CentroComunitario atualizarOcupacao(String id, int novaOcupacao) {
        Optional<CentroComunitario> centroOptional = repository.findById(id);
        if (!centroOptional.isPresent()) {
            throw new IllegalArgumentException("Centro comunitário com ID " + id + " não encontrado");
        }

        CentroComunitario centro = centroOptional.get();
        if (novaOcupacao < 0) {
            throw new IllegalArgumentException("Ocupação atual não pode ser negativa");
        }
        if (novaOcupacao > centro.getCapacidadeMaxima()) {
            throw new IllegalArgumentException("Ocupação atual não pode exceder a capacidade máxima de " + centro.getCapacidadeMaxima());
        }

        centro.setOcupacaoAtual(novaOcupacao);
        CentroComunitario centroAtualizado = repository.save(centro);

        if (centroAtualizado.getOcupacaoAtual() == centroAtualizado.getCapacidadeMaxima()) {
            eventPublisher.publishEvent(new CapacidadeMaximaEvent(this, centroAtualizado));
        }

        return centroAtualizado;
    }

    public Negociacao realizarIntercambio(String centroOrigemId, String centroDestinoId,
                                          CentroComunitario.RecursosIntercambio recursosOrigem,
                                          CentroComunitario.RecursosIntercambio recursosDestino) {
        if (centroOrigemId.equals(centroDestinoId)) {
            throw new IllegalArgumentException("Os centros origem e destino devem ser diferentes");
        }

        Optional<CentroComunitario> origemOptional = repository.findById(centroOrigemId);
        Optional<CentroComunitario> destinoOptional = repository.findById(centroDestinoId);
        if (!origemOptional.isPresent()) {
            throw new IllegalArgumentException("Centro origem com ID " + centroOrigemId + " não encontrado");
        }
        if (!destinoOptional.isPresent()) {
            throw new IllegalArgumentException("Centro destino com ID " + centroDestinoId + " não encontrado");
        }

        CentroComunitario origem = origemOptional.get();
        CentroComunitario destino = destinoOptional.get();

        // Validar recursos não negativos
        if (recursosOrigem.getMedicos() < 0 || recursosOrigem.getVoluntarios() < 0 ||
                recursosOrigem.getKitsMedicos() < 0 || recursosOrigem.getVeiculos() < 0 ||
                recursosOrigem.getCestasBasicas() < 0 ||
                recursosDestino.getMedicos() < 0 || recursosDestino.getVoluntarios() < 0 ||
                recursosDestino.getKitsMedicos() < 0 || recursosDestino.getVeiculos() < 0 ||
                recursosDestino.getCestasBasicas() < 0) {
            throw new IllegalArgumentException("Recursos não podem ser negativos");
        }

        // Validar recursos disponíveis
        if (recursosOrigem.getMedicos() > origem.getRecursos().getMedicos() ||
                recursosOrigem.getVoluntarios() > origem.getRecursos().getVoluntarios() ||
                recursosOrigem.getKitsMedicos() > origem.getRecursos().getKitsMedicos() ||
                recursosOrigem.getVeiculos() > origem.getRecursos().getVeiculos() ||
                recursosOrigem.getCestasBasicas() > origem.getRecursos().getCestasBasicas()) {
            throw new IllegalArgumentException("Centro origem não possui recursos suficientes");
        }
        if (recursosDestino.getMedicos() > destino.getRecursos().getMedicos() ||
                recursosDestino.getVoluntarios() > destino.getRecursos().getVoluntarios() ||
                recursosDestino.getKitsMedicos() > destino.getRecursos().getKitsMedicos() ||
                recursosDestino.getVeiculos() > destino.getRecursos().getVeiculos() ||
                recursosDestino.getCestasBasicas() > destino.getRecursos().getCestasBasicas()) {
            throw new IllegalArgumentException("Centro destino não possui recursos suficientes");
        }

        // Calcular pontos
        int pontosOrigem = calcularPontos(recursosOrigem);
        int pontosDestino = calcularPontos(recursosDestino);

        // Verificar ocupação > 90%
        boolean origemAltaOcupacao = (double) origem.getOcupacaoAtual() / origem.getCapacidadeMaxima() > LIMITE_OCUPACAO_ALTA;
        boolean destinoAltaOcupacao = (double) destino.getOcupacaoAtual() / destino.getCapacidadeMaxima() > LIMITE_OCUPACAO_ALTA;

        if (!origemAltaOcupacao && !destinoAltaOcupacao && pontosOrigem != pontosDestino) {
            throw new IllegalArgumentException("Pontuação dos recursos deve ser igual, a menos que um centro tenha ocupação superior a 90%");
        }

        // Atualizar recursos
        atualizarRecursos(origem, recursosOrigem, recursosDestino);
        atualizarRecursos(destino, recursosDestino, recursosOrigem);

        // Salvar centros atualizados
        repository.save(origem);
        repository.save(destino);

        // Salvar histórico da negociação
        Negociacao negociacao = new Negociacao();
        negociacao.setCentroOrigemId(centroOrigemId);
        negociacao.setCentroDestinoId(centroDestinoId);
        Negociacao.Recursos recursosOrigemNegociacao = new Negociacao.Recursos();
        recursosOrigemNegociacao.setMedicos(recursosOrigem.getMedicos());
        recursosOrigemNegociacao.setVoluntarios(recursosOrigem.getVoluntarios());
        recursosOrigemNegociacao.setKitsMedicos(recursosOrigem.getKitsMedicos());
        recursosOrigemNegociacao.setVeiculos(recursosOrigem.getVeiculos());
        recursosOrigemNegociacao.setCestasBasicas(recursosOrigem.getCestasBasicas());
        Negociacao.Recursos recursosDestinoNegociacao = new Negociacao.Recursos();
        recursosDestinoNegociacao.setMedicos(recursosDestino.getMedicos());
        recursosDestinoNegociacao.setVoluntarios(recursosDestino.getVoluntarios());
        recursosDestinoNegociacao.setKitsMedicos(recursosDestino.getKitsMedicos());
        recursosDestinoNegociacao.setVeiculos(recursosDestino.getVeiculos());
        recursosDestinoNegociacao.setCestasBasicas(recursosDestino.getCestasBasicas());
        negociacao.setRecursosOrigem(recursosOrigemNegociacao);
        negociacao.setRecursosDestino(recursosDestinoNegociacao);
        negociacao.setPontosOrigem(pontosOrigem);
        negociacao.setPontosDestino(pontosDestino);
        negociacao.setDataHora(LocalDateTime.now());

        return negociacaoRepository.save(negociacao);
    }

    private int calcularPontos(CentroComunitario.RecursosIntercambio recursos) {
        return recursos.getMedicos() * PONTOS_MEDICO +
                recursos.getVoluntarios() * PONTOS_VOLUNTARIO +
                recursos.getKitsMedicos() * PONTOS_KIT_MEDICO +
                recursos.getVeiculos() * PONTOS_VEICULO +
                recursos.getCestasBasicas() * PONTOS_CESTA_BASICA;
    }

    private void atualizarRecursos(CentroComunitario centro, CentroComunitario.RecursosIntercambio recursosSaida,
                                   CentroComunitario.RecursosIntercambio recursosEntrada) {
        CentroComunitario.Recursos recursos = centro.getRecursos();
        recursos.setMedicos(recursos.getMedicos() - recursosSaida.getMedicos() + recursosEntrada.getMedicos());
        recursos.setVoluntarios(recursos.getVoluntarios() - recursosSaida.getVoluntarios() + recursosEntrada.getVoluntarios());
        recursos.setKitsMedicos(recursos.getKitsMedicos() - recursosSaida.getKitsMedicos() + recursosEntrada.getKitsMedicos());
        recursos.setVeiculos(recursos.getVeiculos() - recursosSaida.getVeiculos() + recursosEntrada.getVeiculos());
        recursos.setCestasBasicas(recursos.getCestasBasicas() - recursosSaida.getCestasBasicas() + recursosEntrada.getCestasBasicas());
    }

    public List<CentroComunitario> listarCentrosAltaOcupacao() {
        return repository.findAll().stream()
                .filter(centro -> (double) centro.getOcupacaoAtual() / centro.getCapacidadeMaxima() > LIMITE_OCUPACAO_ALTA)
                .collect(Collectors.toList());
    }

    @Data
    public static class RecursosMedia {
        private double medicos;
        private double voluntarios;
        private double kitsMedicos;
        private double veiculos;
        private double cestasBasicas;
    }

    public RecursosMedia calcularMediaRecursos() {
        List<CentroComunitario> centros = repository.findAll();
        if (centros.isEmpty()) {
            return new RecursosMedia();
        }

        double totalCentros = centros.size();
        double somaMedicos = 0, somaVoluntarios = 0, somaKitsMedicos = 0, somaVeiculos = 0, somaCestasBasicas = 0;

        for (CentroComunitario centro : centros) {
            CentroComunitario.Recursos recursos = centro.getRecursos();
            somaMedicos += recursos.getMedicos();
            somaVoluntarios += recursos.getVoluntarios();
            somaKitsMedicos += recursos.getKitsMedicos();
            somaVeiculos += recursos.getVeiculos();
            somaCestasBasicas += recursos.getCestasBasicas();
        }

        RecursosMedia media = new RecursosMedia();
        media.setMedicos(somaMedicos / totalCentros);
        media.setVoluntarios(somaVoluntarios / totalCentros);
        media.setKitsMedicos(somaKitsMedicos / totalCentros);
        media.setVeiculos(somaVeiculos / totalCentros);
        media.setCestasBasicas(somaCestasBasicas / totalCentros);

        return media;
    }

    public List<Negociacao> listarNegociacoesPorCentro(String centroId, LocalDateTime dataInicio) {
        if (centroId == null || centroId.isBlank()) {
            throw new IllegalArgumentException("ID do centro é obrigatório");
        }
        Optional<CentroComunitario> centroOptional = repository.findById(centroId);
        if (!centroOptional.isPresent()) {
            throw new IllegalArgumentException("Centro comunitário com ID " + centroId + " não encontrado");
        }

        if (dataInicio != null && dataInicio.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Data de início não pode ser no futuro");
        }

        List<Negociacao> negociacoes = negociacaoRepository.findAll().stream()
                .filter(negociacao -> negociacao.getCentroOrigemId().equals(centroId) ||
                        negociacao.getCentroDestinoId().equals(centroId))
                .collect(Collectors.toList());

        if (dataInicio != null) {
            negociacoes = negociacoes.stream()
                    .filter(negociacao -> !negociacao.getDataHora().isBefore(dataInicio))
                    .collect(Collectors.toList());
        }

        return negociacoes;
    }
}
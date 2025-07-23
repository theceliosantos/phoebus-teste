package com.phoebus.communityapi.controller;

import com.phoebus.communityapi.model.CentroComunitario;
import com.phoebus.communityapi.model.Negociacao;
import com.phoebus.communityapi.service.CentroComunitarioService;
import com.phoebus.communityapi.service.CentroComunitarioService.RecursosMedia;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/centers")
@RequiredArgsConstructor
public class CentroComunitarioController {
    private final CentroComunitarioService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CentroComunitario adicionarCentro(@RequestBody CentroComunitario centro) {
        return service.adicionarCentro(centro);
    }

    @PutMapping("/{id}/occupancy")
    @ResponseStatus(HttpStatus.OK)
    public CentroComunitario atualizarOcupacao(@PathVariable String id, @RequestBody int novaOcupacao) {
        return service.atualizarOcupacao(id, novaOcupacao);
    }

    @PostMapping("/exchange")
    @ResponseStatus(HttpStatus.CREATED)
    public Negociacao realizarIntercambio(@RequestBody IntercambioRequest request) {
        return service.realizarIntercambio(
                request.getCentroOrigemId(),
                request.getCentroDestinoId(),
                request.getRecursosOrigem(),
                request.getRecursosDestino()
        );
    }

    @GetMapping("/high-occupancy")
    @ResponseStatus(HttpStatus.OK)
    public List<CentroComunitario> listarCentrosAltaOcupacao() {
        return service.listarCentrosAltaOcupacao();
    }

    @GetMapping("/resources-average")
    @ResponseStatus(HttpStatus.OK)
    public RecursosMedia calcularMediaRecursos() {
        return service.calcularMediaRecursos();
    }

    @GetMapping("/{centroId}/exchanges")
    @ResponseStatus(HttpStatus.OK)
    public List<Negociacao> listarNegociacoesPorCentro(
            @PathVariable String centroId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio) {
        return service.listarNegociacoesPorCentro(centroId, dataInicio);
    }

    @Data
    static class IntercambioRequest {
        private String centroOrigemId;
        private String centroDestinoId;
        private CentroComunitario.RecursosIntercambio recursosOrigem;
        private CentroComunitario.RecursosIntercambio recursosDestino;
    }
}
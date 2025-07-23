package com.phoebus.communityapi.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "negociacoes")
public class Negociacao {
    @Id
    private String id;
    private String centroOrigemId;
    private String centroDestinoId;
    private Recursos recursosOrigem;
    private Recursos recursosDestino;
    private int pontosOrigem;
    private int pontosDestino;
    private LocalDateTime dataHora;

    @Data
    public static class Recursos {
        private int medicos;
        private int voluntarios;
        private int kitsMedicos;
        private int veiculos;
        private int cestasBasicas;
    }
}
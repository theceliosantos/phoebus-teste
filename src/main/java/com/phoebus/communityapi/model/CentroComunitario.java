package com.phoebus.communityapi.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "centers")
public class CentroComunitario {
    @Id
    private String id;
    private String nome;
    private String endereco;
    private Localizacao localizacao;
    private int capacidadeMaxima;
    private int ocupacaoAtual;
    private Recursos recursos;

    @Data
    public static class Localizacao {
        private double latitude;
        private double longitude;
    }

    @Data
    public static class Recursos {
        private int medicos;
        private int voluntarios;
        private int kitsMedicos;
        private int veiculos;
        private int cestasBasicas;
    }

    @Data
    public static class RecursosIntercambio {
        private int medicos;
        private int voluntarios;
        private int kitsMedicos;
        private int veiculos;
        private int cestasBasicas;
    }
}

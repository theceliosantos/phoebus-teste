package com.phoebus.communityapi.service;

import com.phoebus.communityapi.model.CentroComunitario;
import com.phoebus.communityapi.model.Negociacao;
import com.phoebus.communityapi.repository.CentroComunitarioRepository;
import com.phoebus.communityapi.repository.NegociacaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CentroComunitarioServiceTest {
    @Mock
    private CentroComunitarioRepository repository;

    @Mock
    private NegociacaoRepository negociacaoRepository;

    @InjectMocks
    private CentroComunitarioService service;

    @Test
    public void testAdicionarCentroValidoComOcupacao() {
        CentroComunitario centro = new CentroComunitario();
        centro.setNome("Centro Comunitário A");
        centro.setEndereco("Rua Exemplo, 123");
        CentroComunitario.Localizacao localizacao = new CentroComunitario.Localizacao();
        localizacao.setLatitude(-23.5505);
        localizacao.setLongitude(-46.6333);
        centro.setLocalizacao(localizacao);
        centro.setCapacidadeMaxima(100);
        centro.setOcupacaoAtual(20);
        CentroComunitario.Recursos recursos = new CentroComunitario.Recursos();
        recursos.setMedicos(2);
        recursos.setVoluntarios(5);
        centro.setRecursos(recursos);

        when(repository.save(any(CentroComunitario.class))).thenReturn(centro);

        CentroComunitario resultado = service.adicionarCentro(centro);

        assertNotNull(resultado);
        assertEquals("Centro Comunitário A", resultado.getNome());
        assertEquals(20, resultado.getOcupacaoAtual());
    }

    @Test
    public void testAdicionarCentroCapacidadeInvalida() {
        CentroComunitario centro = new CentroComunitario();
        centro.setNome("Centro B");
        centro.setEndereco("Rua Teste, 456");
        CentroComunitario.Localizacao localizacao = new CentroComunitario.Localizacao();
        localizacao.setLatitude(-23.5505);
        localizacao.setLongitude(-46.6333);
        centro.setLocalizacao(localizacao);
        centro.setCapacidadeMaxima(0);

        assertThrows(IllegalArgumentException.class, () -> service.adicionarCentro(centro));
    }

    @Test
    public void testAdicionarCentroOcupacaoNegativa() {
        CentroComunitario centro = new CentroComunitario();
        centro.setNome("Centro C");
        centro.setEndereco("Rua Teste, 789");
        CentroComunitario.Localizacao localizacao = new CentroComunitario.Localizacao();
        localizacao.setLatitude(-23.5505);
        localizacao.setLongitude(-46.6333);
        centro.setLocalizacao(localizacao);
        centro.setCapacidadeMaxima(100);
        centro.setOcupacaoAtual(-1);

        assertThrows(IllegalArgumentException.class, () -> service.adicionarCentro(centro));
    }

    @Test
    public void testAdicionarCentroOcupacaoExcedeCapacidade() {
        CentroComunitario centro = new CentroComunitario();
        centro.setNome("Centro D");
        centro.setEndereco("Rua Teste, 101");
        CentroComunitario.Localizacao localizacao = new CentroComunitario.Localizacao();
        localizacao.setLatitude(-23.5505);
        localizacao.setLongitude(-46.6333);
        centro.setLocalizacao(localizacao);
        centro.setCapacidadeMaxima(100);
        centro.setOcupacaoAtual(150);

        assertThrows(IllegalArgumentException.class, () -> service.adicionarCentro(centro));
    }

    @Test
    public void testAtualizarOcupacaoValida() {
        CentroComunitario centro = new CentroComunitario();
        centro.setId("68815b1396ef83016f0fee2e");
        centro.setNome("Centro E");
        centro.setCapacidadeMaxima(100);
        centro.setOcupacaoAtual(50);

        when(repository.findById("68815b1396ef83016f0fee2e")).thenReturn(Optional.of(centro));
        when(repository.save(any(CentroComunitario.class))).thenReturn(centro);

        CentroComunitario resultado = service.atualizarOcupacao("68815b1396ef83016f0fee2e", 75);

        assertNotNull(resultado);
        assertEquals(75, resultado.getOcupacaoAtual());
    }

    @Test
    public void testAtualizarOcupacaoCentroNaoEncontrado() {
        when(repository.findById("68815b1396ef83016f0fee2e")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.atualizarOcupacao("68815b1396ef83016f0fee2e", 50));
    }

    @Test
    public void testAtualizarOcupacaoNegativa() {
        CentroComunitario centro = new CentroComunitario();
        centro.setId("68815b1396ef83016f0fee2e");
        centro.setCapacidadeMaxima(100);
        centro.setOcupacaoAtual(50);

        when(repository.findById("68815b1396ef83016f0fee2e")).thenReturn(Optional.of(centro));

        assertThrows(IllegalArgumentException.class, () -> service.atualizarOcupacao("68815b1396ef83016f0fee2e", -1));
    }

    @Test
    public void testAtualizarOcupacaoExcedeCapacidade() {
        CentroComunitario centro = new CentroComunitario();
        centro.setId("68815b1396ef83016f0fee2e");
        centro.setCapacidadeMaxima(100);
        centro.setOcupacaoAtual(50);

        when(repository.findById("68815b1396ef83016f0fee2e")).thenReturn(Optional.of(centro));

        assertThrows(IllegalArgumentException.class, () -> service.atualizarOcupacao("68815b1396ef83016f0fee2e", 150));
    }

    @Test
    public void testRealizarIntercambioValido() {
        CentroComunitario origem = new CentroComunitario();
        origem.setId("68815b1396ef83016f0fee2e");
        origem.setNome("Centro A");
        origem.setCapacidadeMaxima(100);
        origem.setOcupacaoAtual(50);
        CentroComunitario.Recursos recursosOrigem = new CentroComunitario.Recursos();
        recursosOrigem.setMedicos(2);
        recursosOrigem.setVoluntarios(5);
        recursosOrigem.setKitsMedicos(3);
        recursosOrigem.setVeiculos(1);
        recursosOrigem.setCestasBasicas(10);
        origem.setRecursos(recursosOrigem);

        CentroComunitario destino = new CentroComunitario();
        destino.setId("68815ab396ef83016f0fee2d");
        destino.setNome("Centro B");
        destino.setCapacidadeMaxima(100);
        destino.setOcupacaoAtual(50);
        CentroComunitario.Recursos recursosDestino = new CentroComunitario.Recursos();
        recursosDestino.setMedicos(1);
        recursosDestino.setVoluntarios(1);
        recursosDestino.setKitsMedicos(1);
        recursosDestino.setVeiculos(1);
        recursosDestino.setCestasBasicas(5);
        destino.setRecursos(recursosDestino);

        CentroComunitario.RecursosIntercambio recursosOrigemIntercambio = new CentroComunitario.RecursosIntercambio();
        recursosOrigemIntercambio.setVoluntarios(2); // 2 * 3 = 6 pontos
        recursosOrigemIntercambio.setVeiculos(1);   // 1 * 5 = 5 pontos (total: 11 pontos)
        CentroComunitario.RecursosIntercambio recursosDestinoIntercambio = new CentroComunitario.RecursosIntercambio();
        recursosDestinoIntercambio.setMedicos(1);   // 1 * 4 = 4 pontos
        recursosDestinoIntercambio.setKitsMedicos(1); // 1 * 7 = 7 pontos (total: 11 pontos)

        when(repository.findById("68815b1396ef83016f0fee2e")).thenReturn(Optional.of(origem));
        when(repository.findById("68815ab396ef83016f0fee2d")).thenReturn(Optional.of(destino));
        when(repository.save(any(CentroComunitario.class))).thenReturn(origem, destino);
        when(negociacaoRepository.save(any(Negociacao.class))).thenAnswer(i -> i.getArgument(0));

        Negociacao resultado = service.realizarIntercambio("68815b1396ef83016f0fee2e", "68815ab396ef83016f0fee2d",
                recursosOrigemIntercambio, recursosDestinoIntercambio);

        assertNotNull(resultado);
        assertEquals("68815b1396ef83016f0fee2e", resultado.getCentroOrigemId());
        assertEquals("68815ab396ef83016f0fee2d", resultado.getCentroDestinoId());
        assertEquals(11, resultado.getPontosOrigem());
        assertEquals(11, resultado.getPontosDestino());
        assertEquals(2, resultado.getRecursosOrigem().getVoluntarios());
        assertEquals(1, resultado.getRecursosOrigem().getVeiculos());
        assertEquals(1, resultado.getRecursosDestino().getMedicos());
        assertEquals(1, resultado.getRecursosDestino().getKitsMedicos());
    }

    @Test
    public void testRealizarIntercambioAltaOcupacao() {
        CentroComunitario origem = new CentroComunitario();
        origem.setId("68815b1396ef83016f0fee2e");
        origem.setNome("Centro A");
        origem.setCapacidadeMaxima(100);
        origem.setOcupacaoAtual(50);
        CentroComunitario.Recursos recursosOrigem = new CentroComunitario.Recursos();
        recursosOrigem.setMedicos(2);
        origem.setRecursos(recursosOrigem);

        CentroComunitario destino = new CentroComunitario();
        destino.setId("68815ab396ef83016f0fee2d");
        destino.setNome("Centro B");
        destino.setCapacidadeMaxima(100);
        destino.setOcupacaoAtual(91);
        CentroComunitario.Recursos recursosDestino = new CentroComunitario.Recursos();
        recursosDestino.setKitsMedicos(2);
        destino.setRecursos(recursosDestino);

        CentroComunitario.RecursosIntercambio recursosOrigemIntercambio = new CentroComunitario.RecursosIntercambio();
        recursosOrigemIntercambio.setMedicos(1); // 1 * 4 = 4 pontos
        CentroComunitario.RecursosIntercambio recursosDestinoIntercambio = new CentroComunitario.RecursosIntercambio();
        recursosDestinoIntercambio.setKitsMedicos(1); // 1 * 7 = 7 pontos

        when(repository.findById("68815b1396ef83016f0fee2e")).thenReturn(Optional.of(origem));
        when(repository.findById("68815ab396ef83016f0fee2d")).thenReturn(Optional.of(destino));
        when(repository.save(any(CentroComunitario.class))).thenReturn(origem, destino);
        when(negociacaoRepository.save(any(Negociacao.class))).thenAnswer(i -> i.getArgument(0));

        Negociacao resultado = service.realizarIntercambio("68815b1396ef83016f0fee2e", "68815ab396ef83016f0fee2d",
                recursosOrigemIntercambio, recursosDestinoIntercambio);

        assertNotNull(resultado);
        assertEquals(4, resultado.getPontosOrigem());
        assertEquals(7, resultado.getPontosDestino());
    }

    @Test
    public void testRealizarIntercambioRecursosInsuficientes() {
        CentroComunitario origem = new CentroComunitario();
        origem.setId("68815b1396ef83016f0fee2e");
        origem.setNome("Centro A");
        origem.setCapacidadeMaxima(100);
        origem.setOcupacaoAtual(50);
        CentroComunitario.Recursos recursosOrigem = new CentroComunitario.Recursos();
        recursosOrigem.setMedicos(1);
        origem.setRecursos(recursosOrigem);

        CentroComunitario destino = new CentroComunitario();
        destino.setId("68815ab396ef83016f0fee2d");
        destino.setNome("Centro B");
        destino.setCapacidadeMaxima(100);
        destino.setOcupacaoAtual(50);
        CentroComunitario.Recursos recursosDestino = new CentroComunitario.Recursos();
        recursosDestino.setKitsMedicos(1);
        destino.setRecursos(recursosDestino);

        CentroComunitario.RecursosIntercambio recursosOrigemIntercambio = new CentroComunitario.RecursosIntercambio();
        recursosOrigemIntercambio.setMedicos(2);
        CentroComunitario.RecursosIntercambio recursosDestinoIntercambio = new CentroComunitario.RecursosIntercambio();
        recursosDestinoIntercambio.setKitsMedicos(1);

        when(repository.findById("68815b1396ef83016f0fee2e")).thenReturn(Optional.of(origem));
        when(repository.findById("68815ab396ef83016f0fee2d")).thenReturn(Optional.of(destino));

        assertThrows(IllegalArgumentException.class, () ->
                service.realizarIntercambio("68815b1396ef83016f0fee2e", "68815ab396ef83016f0fee2d",
                        recursosOrigemIntercambio, recursosDestinoIntercambio));
    }

    @Test
    public void testRealizarIntercambioPontuacaoDesigualSemAltaOcupacao() {
        CentroComunitario origem = new CentroComunitario();
        origem.setId("68815b1396ef83016f0fee2e");
        origem.setNome("Centro A");
        origem.setCapacidadeMaxima(100);
        origem.setOcupacaoAtual(50);
        CentroComunitario.Recursos recursosOrigem = new CentroComunitario.Recursos();
        recursosOrigem.setMedicos(2);
        origem.setRecursos(recursosOrigem);

        CentroComunitario destino = new CentroComunitario();
        destino.setId("68815ab396ef83016f0fee2d");
        destino.setNome("Centro B");
        destino.setCapacidadeMaxima(100);
        destino.setOcupacaoAtual(50);
        CentroComunitario.Recursos recursosDestino = new CentroComunitario.Recursos();
        recursosDestino.setKitsMedicos(2);
        destino.setRecursos(recursosDestino);

        CentroComunitario.RecursosIntercambio recursosOrigemIntercambio = new CentroComunitario.RecursosIntercambio();
        recursosOrigemIntercambio.setMedicos(1);
        CentroComunitario.RecursosIntercambio recursosDestinoIntercambio = new CentroComunitario.RecursosIntercambio();
        recursosDestinoIntercambio.setKitsMedicos(1);

        when(repository.findById("68815b1396ef83016f0fee2e")).thenReturn(Optional.of(origem));
        when(repository.findById("68815ab396ef83016f0fee2d")).thenReturn(Optional.of(destino));

        assertThrows(IllegalArgumentException.class, () ->
                service.realizarIntercambio("68815b1396ef83016f0fee2e", "68815ab396ef83016f0fee2d",
                        recursosOrigemIntercambio, recursosDestinoIntercambio));
    }

    @Test
    public void testRealizarIntercambioRecursosNegativos() {
        CentroComunitario origem = new CentroComunitario();
        origem.setId("68815b1396ef83016f0fee2e");
        origem.setNome("Centro A");
        origem.setCapacidadeMaxima(100);
        origem.setOcupacaoAtual(50);
        CentroComunitario.Recursos recursosOrigem = new CentroComunitario.Recursos();
        recursosOrigem.setVoluntarios(5);
        origem.setRecursos(recursosOrigem);

        CentroComunitario destino = new CentroComunitario();
        destino.setId("68815ab396ef83016f0fee2d");
        destino.setNome("Centro B");
        destino.setCapacidadeMaxima(100);
        destino.setOcupacaoAtual(50);
        CentroComunitario.Recursos recursosDestino = new CentroComunitario.Recursos();
        recursosDestino.setMedicos(1);
        destino.setRecursos(recursosDestino);

        CentroComunitario.RecursosIntercambio recursosOrigemIntercambio = new CentroComunitario.RecursosIntercambio();
        recursosOrigemIntercambio.setVoluntarios(-1);
        CentroComunitario.RecursosIntercambio recursosDestinoIntercambio = new CentroComunitario.RecursosIntercambio();
        recursosDestinoIntercambio.setMedicos(1);

        when(repository.findById("68815b1396ef83016f0fee2e")).thenReturn(Optional.of(origem));
        when(repository.findById("68815ab396ef83016f0fee2d")).thenReturn(Optional.of(destino));

        assertThrows(IllegalArgumentException.class, () ->
                service.realizarIntercambio("68815b1396ef83016f0fee2e", "68815ab396ef83016f0fee2d",
                        recursosOrigemIntercambio, recursosDestinoIntercambio));
    }

    @Test
    public void testRealizarIntercambioMesmosCentros() {
        CentroComunitario.RecursosIntercambio recursosOrigemIntercambio = new CentroComunitario.RecursosIntercambio();
        recursosOrigemIntercambio.setVoluntarios(2);
        recursosOrigemIntercambio.setVeiculos(1);
        CentroComunitario.RecursosIntercambio recursosDestinoIntercambio = new CentroComunitario.RecursosIntercambio();
        recursosDestinoIntercambio.setMedicos(1);
        recursosDestinoIntercambio.setKitsMedicos(1);

        assertThrows(IllegalArgumentException.class, () ->
                service.realizarIntercambio("68815b1396ef83016f0fee2e", "68815b1396ef83016f0fee2e",
                        recursosOrigemIntercambio, recursosDestinoIntercambio));
    }

    @Test
    public void testRealizarIntercambioCestasBasicas() {
        CentroComunitario origem = new CentroComunitario();
        origem.setId("68815b1396ef83016f0fee2e");
        origem.setNome("Centro A");
        origem.setCapacidadeMaxima(100);
        origem.setOcupacaoAtual(50);
        CentroComunitario.Recursos recursosOrigem = new CentroComunitario.Recursos();
        recursosOrigem.setCestasBasicas(10);
        origem.setRecursos(recursosOrigem);

        CentroComunitario destino = new CentroComunitario();
        destino.setId("68815ab396ef83016f0fee2d");
        destino.setNome("Centro B");
        destino.setCapacidadeMaxima(100);
        destino.setOcupacaoAtual(50);
        CentroComunitario.Recursos recursosDestino = new CentroComunitario.Recursos();
        recursosDestino.setVoluntarios(5);
        destino.setRecursos(recursosDestino);

        CentroComunitario.RecursosIntercambio recursosOrigemIntercambio = new CentroComunitario.RecursosIntercambio();
        recursosOrigemIntercambio.setCestasBasicas(3);
        CentroComunitario.RecursosIntercambio recursosDestinoIntercambio = new CentroComunitario.RecursosIntercambio();
        recursosDestinoIntercambio.setVoluntarios(2);

        when(repository.findById("68815b1396ef83016f0fee2e")).thenReturn(Optional.of(origem));
        when(repository.findById("68815ab396ef83016f0fee2d")).thenReturn(Optional.of(destino));
        when(repository.save(any(CentroComunitario.class))).thenReturn(origem, destino);
        when(negociacaoRepository.save(any(Negociacao.class))).thenAnswer(i -> i.getArgument(0));

        Negociacao resultado = service.realizarIntercambio("68815b1396ef83016f0fee2e", "68815ab396ef83016f0fee2d",
                recursosOrigemIntercambio, recursosDestinoIntercambio);

        assertNotNull(resultado);
        assertEquals(6, resultado.getPontosOrigem());
        assertEquals(6, resultado.getPontosDestino());
        assertEquals(3, resultado.getRecursosOrigem().getCestasBasicas());
        assertEquals(2, resultado.getRecursosDestino().getVoluntarios());
    }

    @Test
    public void testListarCentrosAltaOcupacao() {
        CentroComunitario centro1 = new CentroComunitario();
        centro1.setId("1");
        centro1.setNome("Centro A");
        centro1.setCapacidadeMaxima(100);
        centro1.setOcupacaoAtual(91); // > 90%
        CentroComunitario centro2 = new CentroComunitario();
        centro2.setId("2");
        centro2.setNome("Centro B");
        centro2.setCapacidadeMaxima(100);
        centro2.setOcupacaoAtual(50); // < 90%

        when(repository.findAll()).thenReturn(Arrays.asList(centro1, centro2));

        List<CentroComunitario> resultado = service.listarCentrosAltaOcupacao();

        assertEquals(1, resultado.size());
        assertEquals("Centro A", resultado.get(0).getNome());
    }

    @Test
    public void testListarCentrosAltaOcupacaoSemCentros() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        List<CentroComunitario> resultado = service.listarCentrosAltaOcupacao();

        assertTrue(resultado.isEmpty());
    }

    @Test
    public void testCalcularMediaRecursos() {
        CentroComunitario centro1 = new CentroComunitario();
        centro1.setId("1");
        CentroComunitario.Recursos recursos1 = new CentroComunitario.Recursos();
        recursos1.setMedicos(2);
        recursos1.setVoluntarios(4);
        recursos1.setKitsMedicos(3);
        recursos1.setVeiculos(1);
        recursos1.setCestasBasicas(10);
        centro1.setRecursos(recursos1);

        CentroComunitario centro2 = new CentroComunitario();
        centro2.setId("2");
        CentroComunitario.Recursos recursos2 = new CentroComunitario.Recursos();
        recursos2.setMedicos(4);
        recursos2.setVoluntarios(6);
        recursos2.setKitsMedicos(1);
        recursos2.setVeiculos(3);
        recursos2.setCestasBasicas(5);
        centro2.setRecursos(recursos2);

        when(repository.findAll()).thenReturn(Arrays.asList(centro1, centro2));

        CentroComunitarioService.RecursosMedia resultado = service.calcularMediaRecursos();

        assertEquals(3.0, resultado.getMedicos(), 0.01);
        assertEquals(5.0, resultado.getVoluntarios(), 0.01);
        assertEquals(2.0, resultado.getKitsMedicos(), 0.01);
        assertEquals(2.0, resultado.getVeiculos(), 0.01);
        assertEquals(7.5, resultado.getCestasBasicas(), 0.01);
    }

    @Test
    public void testCalcularMediaRecursosSemCentros() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        CentroComunitarioService.RecursosMedia resultado = service.calcularMediaRecursos();

        assertEquals(0.0, resultado.getMedicos(), 0.01);
        assertEquals(0.0, resultado.getVoluntarios(), 0.01);
        assertEquals(0.0, resultado.getKitsMedicos(), 0.01);
        assertEquals(0.0, resultado.getVeiculos(), 0.01);
        assertEquals(0.0, resultado.getCestasBasicas(), 0.01);
    }

    @Test
    public void testListarNegociacoesPorCentro() {
        CentroComunitario centro = new CentroComunitario();
        centro.setId("68815b1396ef83016f0fee2e");

        Negociacao negociacao1 = new Negociacao();
        negociacao1.setCentroOrigemId("68815b1396ef83016f0fee2e");
        negociacao1.setCentroDestinoId("68815ab396ef83016f0fee2d");
        negociacao1.setDataHora(LocalDateTime.now().minusHours(2));

        Negociacao negociacao2 = new Negociacao();
        negociacao2.setCentroOrigemId("68815ab396ef83016f0fee2d");
        negociacao2.setCentroDestinoId("68815b1396ef83016f0fee2e");
        negociacao2.setDataHora(LocalDateTime.now().minusHours(1));

        Negociacao negociacao3 = new Negociacao();
        negociacao3.setCentroOrigemId("other");
        negociacao3.setCentroDestinoId("another");
        negociacao3.setDataHora(LocalDateTime.now());

        when(repository.findById("68815b1396ef83016f0fee2e")).thenReturn(Optional.of(centro));
        when(negociacaoRepository.findAll()).thenReturn(Arrays.asList(negociacao1, negociacao2, negociacao3));

        List<Negociacao> resultado = service.listarNegociacoesPorCentro("68815b1396ef83016f0fee2e", null);

        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(negociacao1));
        assertTrue(resultado.contains(negociacao2));
    }

    @Test
    public void testListarNegociacoesPorCentroComDataInicio() {
        CentroComunitario centro = new CentroComunitario();
        centro.setId("68815b1396ef83016f0fee2e");

        Negociacao negociacao1 = new Negociacao();
        negociacao1.setCentroOrigemId("68815b1396ef83016f0fee2e");
        negociacao1.setCentroDestinoId("68815ab396ef83016f0fee2d");
        negociacao1.setDataHora(LocalDateTime.now().minusHours(4));

        Negociacao negociacao2 = new Negociacao();
        negociacao2.setCentroOrigemId("68815b1396ef83016f0fee2e");
        negociacao2.setCentroDestinoId("68815ab396ef83016f0fee2d");
        negociacao2.setDataHora(LocalDateTime.now().minusHours(2));

        when(repository.findById("68815b1396ef83016f0fee2e")).thenReturn(Optional.of(centro));
        when(negociacaoRepository.findAll()).thenReturn(Arrays.asList(negociacao1, negociacao2));

        LocalDateTime dataInicio = LocalDateTime.now().minusHours(3);
        List<Negociacao> resultado = service.listarNegociacoesPorCentro("68815b1396ef83016f0fee2e", dataInicio);

        assertEquals(1, resultado.size());
        assertEquals(negociacao2, resultado.get(0));
    }

    @Test
    public void testListarNegociacoesPorCentroCentroNaoEncontrado() {
        when(repository.findById("68815b1396ef83016f0fee2e")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                service.listarNegociacoesPorCentro("68815b1396ef83016f0fee2e", null));
    }

    @Test
    public void testListarNegociacoesPorCentroDataFutura() {
        CentroComunitario centro = new CentroComunitario();
        centro.setId("68815b1396ef83016f0fee2e");

        when(repository.findById("68815b1396ef83016f0fee2e")).thenReturn(Optional.of(centro));

        LocalDateTime dataFutura = LocalDateTime.now().plusHours(1);
        assertThrows(IllegalArgumentException.class, () ->
                service.listarNegociacoesPorCentro("68815b1396ef83016f0fee2e", dataFutura));
    }
}
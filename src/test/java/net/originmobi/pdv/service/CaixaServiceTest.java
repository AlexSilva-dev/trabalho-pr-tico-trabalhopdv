package net.originmobi.pdv.service;

import net.originmobi.pdv.enumerado.caixa.CaixaTipo;
import net.originmobi.pdv.enumerado.caixa.EstiloLancamento;
import net.originmobi.pdv.enumerado.caixa.TipoLancamento;
import net.originmobi.pdv.filter.BancoFilter;
import net.originmobi.pdv.filter.CaixaFilter;
import net.originmobi.pdv.model.Caixa;
import net.originmobi.pdv.model.CaixaLancamento;
import net.originmobi.pdv.model.Usuario;
import net.originmobi.pdv.repository.CaixaRepository;
import net.originmobi.pdv.repository.CaixaLancamentoRepository;
import net.originmobi.pdv.singleton.Aplicacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CaixaServiceTest {

    @Mock
    private CaixaRepository caixaRepository;

    @Mock
    private CaixaLancamentoRepository caixaLancamentoRepository;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private CaixaLancamentoService caixaLancamentoService;

    @InjectMocks
    private CaixaService caixaService;

    private Caixa caixaAberto;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        caixaAberto = new Caixa();
        caixaAberto.setCodigo(1L);
        caixaAberto.setValor_total(100.0);
        caixaAberto.setTipo(CaixaTipo.CAIXA);

        usuario = new Usuario();
        usuario.setCodigo(1L);
        usuario.setSenha(new BCryptPasswordEncoder().encode("senha"));
    }


    @Test
    void testCadastro_jaExisteAberto() {
        when(caixaRepository.caixaAberto()).thenReturn(Optional.of(caixaAberto));

        Caixa novoCaixa = new Caixa();
        novoCaixa.setTipo(CaixaTipo.CAIXA);
        novoCaixa.setDescricao("Caixa Teste");
        novoCaixa.setValor_abertura(100.0);

        RuntimeException thrown = assertThrows(
            RuntimeException.class,
            () -> caixaService.cadastro(novoCaixa),
            "Esperava lançar RuntimeException"
        );

        assertTrue(thrown.getMessage().contains("Existe caixa de dias anteriores em aberto, favor verifique"));
    }

    @Test
    void testCadastro_valorAberturaNegativo() {
        Caixa novoCaixa = new Caixa();
        novoCaixa.setTipo(CaixaTipo.CAIXA);
        novoCaixa.setDescricao("Caixa Teste");
        novoCaixa.setValor_abertura(-100.0);

        RuntimeException thrown = assertThrows(
            RuntimeException.class,
            () -> caixaService.cadastro(novoCaixa),
            "Esperava lançar RuntimeException"
        );

        assertTrue(thrown.getMessage().contains("Valor informado é inválido"));
    }

    @Test
    void testCaixaIsAberto_true() {
        when(caixaRepository.caixaAberto()).thenReturn(Optional.of(caixaAberto));

        boolean resultado = caixaService.caixaIsAberto();

        assertTrue(resultado);
    }

    @Test
    void testCaixaIsAberto_false() {
        when(caixaRepository.caixaAberto()).thenReturn(Optional.empty());

        boolean resultado = caixaService.caixaIsAberto();

        assertFalse(resultado);
    }

    @Test
    void testListaTodos() {
        List<Caixa> caixas = Arrays.asList(caixaAberto);
        when(caixaRepository.findByCodigoOrdenado()).thenReturn(caixas);

        List<Caixa> resultado = caixaService.listaTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    void testListarCaixas_comFiltro() {
        CaixaFilter filter = new CaixaFilter();
        filter.setData_cadastro("2023-06-29");

        List<Caixa> caixas = Arrays.asList(caixaAberto);
        when(caixaRepository.buscaCaixasPorDataAbertura(any(Date.class))).thenReturn(caixas);

        List<Caixa> resultado = caixaService.listarCaixas(filter);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }


    @Test
    void testCaixaAberto() {
        when(caixaRepository.caixaAberto()).thenReturn(Optional.of(caixaAberto));

        Optional<Caixa> resultado = caixaService.caixaAberto();

        assertTrue(resultado.isPresent());
        assertEquals(caixaAberto.getCodigo(), resultado.get().getCodigo());
    }

    @Test
    void testCaixasAbertos() {
        List<Caixa> caixas = Arrays.asList(caixaAberto);
        when(caixaRepository.caixasAbertos()).thenReturn(caixas);

        List<Caixa> resultado = caixaService.caixasAbertos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    void testBusca() {
        when(caixaRepository.findById(anyLong())).thenReturn(Optional.of(caixaAberto));

        Optional<Caixa> resultado = caixaService.busca(caixaAberto.getCodigo());

        assertTrue(resultado.isPresent());
        assertEquals(caixaAberto.getCodigo(), resultado.get().getCodigo());
    }


    @Test
    void testListaBancos() {
        List<Caixa> caixas = Arrays.asList(caixaAberto);
        when(caixaRepository.buscaBancos(CaixaTipo.BANCO)).thenReturn(caixas);

        List<Caixa> resultado = caixaService.listaBancos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    void testListaCaixasAbertosTipo() {
        List<Caixa> caixas = Arrays.asList(caixaAberto);
        when(caixaRepository.buscaCaixaTipo(any(CaixaTipo.class))).thenReturn(caixas);

        List<Caixa> resultado = caixaService.listaCaixasAbertosTipo(CaixaTipo.CAIXA);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    void testListaBancosAbertosTipoFilterBanco_comFiltro() {
        BancoFilter filter = new BancoFilter();
        filter.setData_cadastro("2023-06-29");

        List<Caixa> caixas = Arrays.asList(caixaAberto);
        when(caixaRepository.buscaCaixaTipoData(any(CaixaTipo.class), any(Date.class))).thenReturn(caixas);

        List<Caixa> resultado = caixaService.listaBancosAbertosTipoFilterBanco(CaixaTipo.BANCO, filter);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    void testListaBancosAbertosTipoFilterBanco_semFiltro() {
        BancoFilter filter = new BancoFilter();

        List<Caixa> caixas = Arrays.asList(caixaAberto);
        when(caixaRepository.buscaCaixaTipo(CaixaTipo.BANCO)).thenReturn(caixas);

        List<Caixa> resultado = caixaService.listaBancosAbertosTipoFilterBanco(CaixaTipo.BANCO, filter);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }
}
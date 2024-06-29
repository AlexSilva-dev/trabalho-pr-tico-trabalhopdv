package net.originmobi.pdv.service;

import net.originmobi.pdv.enumerado.caixa.EstiloLancamento;
import net.originmobi.pdv.enumerado.caixa.TipoLancamento;
import net.originmobi.pdv.model.Caixa;
import net.originmobi.pdv.model.CaixaLancamento;
import net.originmobi.pdv.repository.CaixaLancamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CaixaLancamentoServiceTest {

    @Mock
    private CaixaLancamentoRepository caixaLancamentoRepository;

    @InjectMocks
    private CaixaLancamentoService caixaLancamentoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLancamento_semCaixaAberto_lancaExcecao() {
        CaixaLancamento lancamento = new CaixaLancamento();
        lancamento.setCaixa(null);

        RuntimeException thrown = assertThrows(
            RuntimeException.class,
            () -> caixaLancamentoService.lancamento(lancamento),
            "Esperava lançar RuntimeException"
        );

        assertTrue(thrown.getMessage().contains("Nenhum caixa aberto"));
    }

    @Test
    void testLancamento_saldoInsuficiente_lancaExcecao() {
        Caixa caixa = new Caixa();
        caixa.setValor_total(100.0);

        CaixaLancamento lancamento = new CaixaLancamento();
        lancamento.setCaixa(caixa);
        lancamento.setEstilo(EstiloLancamento.SAIDA);
        lancamento.setValor(200.0);

        String resultado = caixaLancamentoService.lancamento(lancamento);

        assertEquals("Saldo insuficiente para realizar esta operação", resultado);
    }

    @Test
    void testLancamento_observacaoCorreta() {
        Caixa caixa = new Caixa();
        caixa.setValor_total(100.0);

        CaixaLancamento lancamento = new CaixaLancamento();
        lancamento.setCaixa(caixa);
        lancamento.setEstilo(EstiloLancamento.ENTRADA);
        lancamento.setTipo(TipoLancamento.SANGRIA);
        lancamento.setValor(50.0);
        lancamento.setObservacao("");

        caixaLancamentoService.lancamento(lancamento);

        assertEquals("Sangria de caixa", lancamento.getObservacao());
    }

    @Test
    void testLancamentosDoCaixa_retornaListaCorreta() {
        Caixa caixa = new Caixa();
        CaixaLancamento lancamento1 = new CaixaLancamento();
        CaixaLancamento lancamento2 = new CaixaLancamento();
        List<CaixaLancamento> lancamentosEsperados = Arrays.asList(lancamento1, lancamento2);

        when(caixaLancamentoRepository.findByCaixaEquals(any(Caixa.class))).thenReturn(lancamentosEsperados);

        List<CaixaLancamento> lancamentosObtidos = caixaLancamentoService.lancamentosDoCaixa(caixa);

        assertEquals(lancamentosEsperados, lancamentosObtidos);
        assertTrue(lancamentosObtidos.contains(lancamento1));
        assertTrue(lancamentosObtidos.contains(lancamento2));
    }
}
package net.originmobi.pdv.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.time.Instant;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import net.originmobi.pdv.model.Receber;
import net.originmobi.pdv.repository.ReceberRepository;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig
class ReceberServiceTest {

    @MockBean
    private ReceberRepository receberRepo;

    @InjectMocks
    private ReceberService receberService;

    @Test
    void testCadastrar_Success() {
        // Arrange
        Receber receber = new Receber();
        receber.setObservacao("Teste");
        receber.setValorTotal(100.0);
        receber.setValorRecebido(50.0);

        // Act
        receberService.cadastrar(receber);

        // Assert
        // Verifica se o método save foi chamado no receberRepo com o objeto receber
        org.mockito.Mockito.verify(receberRepo).save(receber);
    }

    @Test
    void testCadastrar_Exception() {
        // Arrange
        Receber receber = new Receber();
        when(receberRepo.save(receber)).thenThrow(new RuntimeException("Erro ao salvar"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> receberService.cadastrar(receber));
    }

    @Test
    void testLancaReceber_Success() {
        // Arrange
        String observacao = "Teste";
        Double valorTotal = 100.0;
        Double valorRecebido = 50.0;
        Double valorDesconto = 10.0;
        Double valorAcrescimo = 5.0;
        Double valorRestante = 55.0;
        int quitado = 0;
        int sequencia = 1;
        Timestamp dataCadastro = Timestamp.from(Instant.now());
        String dataVencimento = "2024-01-01";
        Long pessoaCodigo = 1L;

        // Act
        receberService.lancaReceber(observacao, valorTotal, valorRecebido, valorDesconto, valorAcrescimo,
                valorRestante, quitado, sequencia, dataCadastro, dataVencimento, pessoaCodigo);

        // Assert
        org.mockito.Mockito.verify(receberRepo).lancaReceber(observacao, valorTotal, valorRecebido, valorDesconto,
                valorAcrescimo, valorRestante, quitado, sequencia, dataCadastro, dataVencimento, pessoaCodigo);
    }

    @Test
    void testTotalAReceber_Success() {
        String totalAReceber = "R$ 100,00";
        when(receberRepo.total_a_receber()).thenReturn(totalAReceber);

        String result = receberService.total_a_receber();
        assertEquals(totalAReceber, result);
    }
}

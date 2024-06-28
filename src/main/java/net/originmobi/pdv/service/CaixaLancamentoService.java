package net.originmobi.pdv.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.originmobi.pdv.enumerado.caixa.EstiloLancamento;
import net.originmobi.pdv.enumerado.caixa.TipoLancamento;
import net.originmobi.pdv.model.Caixa;
import net.originmobi.pdv.model.CaixaLancamento;
import net.originmobi.pdv.repository.CaixaLancamentoRepository;

@Service
public class CaixaLancamentoService {

    private final CaixaLancamentoRepository caixaLancamento;
    final UsuarioService usuarios;
    private Timestamp dataHoraAtual;

    public CaixaLancamentoService(CaixaLancamentoRepository caixaLancamento, UsuarioService usuarios) {
        this.caixaLancamento = caixaLancamento;
        this.usuarios = usuarios;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public String lancamento(CaixaLancamento lancamento) {
        dataHoraAtual = new Timestamp(System.currentTimeMillis());
        lancamento.setData_cadastro(dataHoraAtual);

        if (!isCaixaValido(lancamento)) {
            throw new RuntimeException("Nenhum caixa aberto");
        }

        if (isSaidaComSaldoInsuficiente(lancamento)) {
            return "Saldo insuficiente para realizar esta operação";
        }

        ajustarValorParaSaida(lancamento);
        ajustarObservacao(lancamento);

        try {
            caixaLancamento.save(lancamento);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao realizar lançamento, chame o suporte", e);
        }

        return "Lançamento realizado com sucesso";
    }

    private boolean isCaixaValido(CaixaLancamento lancamento) {
        return lancamento.getCaixa().isPresent() && !lancamento.getCaixa().map(Caixa::getData_fechamento).isPresent();
    }

    private boolean isSaidaComSaldoInsuficiente(CaixaLancamento lancamento) {
        if (lancamento.getEstilo().equals(EstiloLancamento.SAIDA)) {
            Optional<Double> vlTotalCaixa = lancamento.getCaixa().map(Caixa::getValor_total);
            return lancamento.getValor() > vlTotalCaixa.orElse(0.0);
        }
        return false;
    }

    private void ajustarValorParaSaida(CaixaLancamento lancamento) {
        if (lancamento.getEstilo().equals(EstiloLancamento.SAIDA) && lancamento.getValor() > 0) {
            lancamento.setValor(lancamento.getValor() * -1);
        }
    }

    private void ajustarObservacao(CaixaLancamento lancamento) {
        if (lancamento.getObservacao().isEmpty()) {
            if (lancamento.getTipo().equals(TipoLancamento.SANGRIA)) {
                lancamento.setObservacao("Sangria de caixa");
            } else if (lancamento.getTipo().equals(TipoLancamento.SUPRIMENTO)) {
                lancamento.setObservacao("Suprimento de caixa");
            }
        }
    }

    public List<CaixaLancamento> lancamentosDoCaixa(Caixa caixa) {
        return caixaLancamento.findByCaixaEquals(caixa);
    }
}
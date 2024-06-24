package net.originmobi.pdv.service;

import java.sql.Timestamp;


import org.springframework.stereotype.Service;

import net.originmobi.pdv.model.Receber;
import net.originmobi.pdv.repository.ReceberRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ReceberService {

	private static final Logger logger = LoggerFactory.getLogger(ReceberService.class);

    private final ReceberRepository receberRepo;

    public ReceberService(ReceberRepository receberRepo) {
        this.receberRepo = receberRepo;
    }

	public void cadastrar(Receber receber) {
		try {
			receberRepo.save(receber);
		} catch (Exception e) {
			logger.error("" + e.getMessage(), e);
			throw new RuntimeException();
		}
	}

	public void lancaReceber(String observacao, Double valor_total, Double valor_recebido, Double valor_desconto,
			Double valor_acrescimo, Double valor_restante, int quitado, int sequencia, Timestamp data_cadastro,
			String data_vencimento, Long pessoa_codigo) {
		receberRepo.lancaReceber(observacao, valor_total, valor_recebido, valor_desconto, valor_acrescimo,
				valor_restante, quitado, sequencia, data_cadastro, data_vencimento, pessoa_codigo);
	}
	
    public String totalAReceber() {
        return receberRepo.total_a_receber();
    }

}
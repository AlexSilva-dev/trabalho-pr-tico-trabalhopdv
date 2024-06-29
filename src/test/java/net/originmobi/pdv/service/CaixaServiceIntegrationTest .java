package net.originmobi.pdv.service;

import net.originmobi.pdv.enumerado.caixa.CaixaTipo;
import net.originmobi.pdv.enumerado.caixa.EstiloLancamento;
import net.originmobi.pdv.enumerado.caixa.TipoLancamento;
import net.originmobi.pdv.model.Caixa;
import net.originmobi.pdv.model.CaixaLancamento;
import net.originmobi.pdv.model.Usuario;
import net.originmobi.pdv.repository.CaixaRepository;
import net.originmobi.pdv.repository.UsuarioRepository;
import net.originmobi.pdv.repository.CaixaLancamentoRepository;
import net.originmobi.pdv.service.CaixaService;
import net.originmobi.pdv.service.UsuarioService;
import net.originmobi.pdv.service.CaixaLancamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({CaixaService.class, UsuarioService.class, CaixaLancamentoService.class, BCryptPasswordEncoder.class})
class CaixaServiceIntegrationTest {

    @Autowired
    private CaixaService caixaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CaixaRepository caixaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CaixaLancamentoService caixaLancamentoService;

    @Autowired
    private CaixaLancamentoRepository caixaLancamentoRepository;

    private Caixa caixaAberto;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setCodigo(1L);
        usuario.setUser("Teste Usuário");
        usuario.setSenha(new BCryptPasswordEncoder().encode("senha"));
        usuarioRepository.save(usuario);

        caixaAberto = new Caixa();
        caixaAberto.setCodigo(1L);
        caixaAberto.setValor_total(100.0);
        caixaAberto.setTipo(CaixaTipo.CAIXA);
        caixaAberto.setDescricao("Caixa Teste");
        caixaAberto.setValor_abertura(100.0);
        caixaAberto.setUsuario(usuario);
    }

    @Test
    @Transactional
    void testCadastroCaixa() {
        Long codigo = caixaService.cadastro(caixaAberto);

        Optional<Caixa> caixaOptional = caixaRepository.findById(codigo);
        assertTrue(caixaOptional.isPresent());

        Caixa caixaSalvo = caixaOptional.get();
        assertEquals(caixaAberto.getDescricao(), caixaSalvo.getDescricao());
        assertEquals(caixaAberto.getValor_abertura(), caixaSalvo.getValor_abertura());
        assertTrue(caixaSalvo.isAberto());
        assertEquals(usuario.getUser(), caixaSalvo.getUsuario().getUser());
    }

    @Test
    @Transactional
    void testListaTodosCaixas() {
        caixaRepository.save(caixaAberto);

        List<Caixa> caixas = caixaService.listaTodos();
        assertNotNull(caixas);
        assertEquals(1, caixas.size());

        Caixa caixaListada = caixas.get(0);
        assertEquals(caixaAberto.getDescricao(), caixaListada.getDescricao());
        assertEquals(caixaAberto.getValor_abertura(), caixaListada.getValor_abertura());
    }
}
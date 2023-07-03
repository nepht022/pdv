package net.originmobi.pdv.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

import net.originmobi.pdv.enumerado.VendaSituacao;
import net.originmobi.pdv.model.Pessoa;
import net.originmobi.pdv.model.Usuario;
import net.originmobi.pdv.model.Venda;
import net.originmobi.pdv.model.VendaProduto;
import net.originmobi.pdv.singleton.Aplicacao;

@SpringBootTest
@RunWith(SpringRunner.class)
public class VendaServiceTest {

    @Autowired
    VendaService vendaService;

    @Before
    public void init() {
        TestingAuthenticationToken testingAuth = new TestingAuthenticationToken("gerente", "123");
        
        SecurityContextHolder.getContext().setAuthentication(testingAuth);
    }

    @Test
    public void abreVendaDeveRetornarCodigoNaoNulo() {
        assertNotNull(vendaService.abreVenda(vendaNova()));
        assertNotNull(vendaService.abreVenda(vendaExistente()));
    }

    @Test
    public void abreVendaDeveRetornarCodigoDiferente_seVendaNova() {
        assertNotEquals(vendaNova().getCodigo(), vendaService.abreVenda(vendaNova()));
    }

    @Test
    public void abreVendaDeveRetornarMesmoCodigo_seVendaExiste() {
        assertEquals(vendaExistente().getCodigo(), vendaService.abreVenda(vendaExistente()));
    }

    @Test
    public void removeProdutoDeveRetornarOk_seVendaAberta() {
        vendaService.abreVenda(vendaAberta());

        String actualReturn = vendaService.removeProduto(vendaProdutoAberta().getCodigo() ,vendaAberta().getCodigo());

        boolean isEqual = actualReturn.equals("ok");

        assertTrue(isEqual);
    }

    @Test
    public void removeProdutoDeveRetornarVendaFechada_seVendaFechada() {
        vendaService.abreVenda(vendaFechada());

        String actualReturn = vendaService.removeProduto(vendaProdutoFechada().getCodigo() ,vendaAberta().getCodigo());

        boolean isEqual = actualReturn.equals("ok");

        assertTrue(isEqual);
    }

    private Venda vendaNova() {
        return new Venda(null, null, null, null, 
        null, null, null, null, null, null, null);
    }

    private Venda vendaExistente() {
        Pessoa pessoa = new Pessoa("Leoncio", "Leo", "11823667898", null, null, 
        null, null, null);
        Usuario usuario = new Usuario(null, Aplicacao.getInstancia().getUsuarioAtual(), null, null, pessoa, null, null);
        
        Venda venda = new Venda(null, null, null, null, null, 
        null, null, null, null, pessoa, usuario);
        venda.setCodigo(Long.valueOf(94));

        return venda;
    }

    private Venda vendaAberta() {
        Pessoa pessoa = new Pessoa("Leoncio", "Leo", "11823667898", null, null, 
        null, null, null);
        Usuario usuario = new Usuario(null, Aplicacao.getInstancia().getUsuarioAtual(), null, null, pessoa, null, null);
        
        Venda venda = new Venda(null, null, null, null, null, 
        VendaSituacao.ABERTA, null, null, null, pessoa, usuario);
        venda.setCodigo(Long.valueOf(95));

        return venda;
    }

    private Venda vendaFechada() {
        Pessoa pessoa = new Pessoa("Leoncio", "Leo", "11823667898", null, null, 
        null, null, null);
        Usuario usuario = new Usuario(null, Aplicacao.getInstancia().getUsuarioAtual(), null, null, pessoa, null, null);
        
        Venda venda = new Venda(null, null, null, null, null, 
        VendaSituacao.FECHADA, null, null, null, pessoa, usuario);
        venda.setCodigo(Long.valueOf(96));

        return venda;
    }

    private VendaProduto vendaProdutoAberta() {
        return new VendaProduto(Long.valueOf(1), Long.valueOf(95), 99.99);
    }

    private VendaProduto vendaProdutoFechada() {
        return new VendaProduto(Long.valueOf(2), Long.valueOf(96), 99.99);
    }
}

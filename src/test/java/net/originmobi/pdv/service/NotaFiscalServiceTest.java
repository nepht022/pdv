package net.originmobi.pdv.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;

import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import net.originmobi.pdv.service.notafiscal.NotaFiscalService;
import net.originmobi.pdv.service.notafiscal.NotaFiscalTotaisServer;
import net.originmobi.pdv.xml.nfe.GeraXmlNfe;
import net.originmobi.pdv.enumerado.TelefoneTipo;
import net.originmobi.pdv.enumerado.notafiscal.NotaFiscalTipo;
import net.originmobi.pdv.model.Cidade;
import net.originmobi.pdv.model.Empresa;
import net.originmobi.pdv.model.Endereco;
import net.originmobi.pdv.model.Estado;
import net.originmobi.pdv.model.EmpresaParametro;
import net.originmobi.pdv.model.RegimeTributario;
import net.originmobi.pdv.model.Telefone;
import net.originmobi.pdv.repository.notafiscal.NotaFiscalRepository;
import net.originmobi.pdv.model.Pessoa;
import net.originmobi.pdv.model.FreteTipo;
import net.originmobi.pdv.model.NotaFiscalFinalidade;
import net.originmobi.pdv.model.NotaFiscalTotais;
import net.originmobi.pdv.model.Pais;
import net.originmobi.pdv.model.NotaFiscal;

@SpringBootTest(classes = NotaFiscalServiceTest.class)
public class NotaFiscalServiceTest {
	
	private NotaFiscalService NFService;
	private NotaFiscalRepository mockNFR;
	private GeraXmlNfe mockXMLNFE;
	private NotaFiscal notaFiscal ;
	private Empresa empresa;
	private NotaFiscalFinalidade notaFiscalFin;
	private NotaFiscalTotais notaFiscalTot;
	private Pais pais;
	private Estado estado;
	private Cidade cidade;
	private Pessoa pessoa;
	private Endereco endereco;
	private RegimeTributario rtb;
	private EmpresaParametro empP;
	private FreteTipo ft;
	private List<NotaFiscal> lnf;
	
	@BeforeEach
	public void inicializa() {
		NFService = new NotaFiscalService();
		
		pais = new Pais();
		pais.setCodigo(22l);
		pais.setCodigo_pais("Br");
		pais.setNome("Brasil");
		
		estado = new Estado();
		estado.setCodigo(2l);
		estado.setNome("Rio de Janeiro");
		estado.setPais(pais);
		estado.setSigla("RJ");
		estado.setCodigo(1l);
		
		cidade = new Cidade();
		cidade.setEstado(estado);
		cidade.setCodigo(24l);
		cidade.setCodigo_municipio("55");
		cidade.setNome("Niteroi");
		
		rtb = new RegimeTributario();
		rtb.setCodigo(91l);
		rtb.setDescricao("descricao");
		rtb.setTipoRegime(2);
		
		empP = new EmpresaParametro();
		empP.setAmbiente(5);
		empP.setCodigo(5l);
		empP.setpCredSN(500.6);
		empP.setSerie_nfe(2);
		
		List<Telefone> lt = new ArrayList<Telefone>();
		Telefone t1 = new Telefone();
		t1.setCodigo(1234l);
		t1.setData_cadastro(new Date(10/03/2002));
		t1.setFone("f");
		t1.setTipo(TelefoneTipo.valueOf("FIXO"));
		lt.add(t1);
				
		endereco = new Endereco("cardoso", "centro", "145", "45675-754", "ref", new Date(22/01/1991), cidade);
		empresa = new Empresa("empresa", "emp", "3312313123213", "ie", rtb, endereco, empP);
		pessoa = new Pessoa("Rodrigo", "rd", "335.312.352-6", "observa", new Date(20/10/2000), new Date(02/10/2021), endereco, lt);
		
		ft = new FreteTipo();
		ft.setCodigo(44l);
		ft.setDescricao("frete desc");
		ft.setTipo(8);
		
		notaFiscalFin = new NotaFiscalFinalidade();
		notaFiscalFin.setCodigo(32l);
		notaFiscalFin.setDescricao("nota fiscal finalidade desc");
		notaFiscalFin.setTipo(77);
		
		notaFiscalTot = new NotaFiscalTotais(1.1, 2.2, 2.3, 3.4, 5.0, 3.1, 8.8, 8.2, 7.9, 1.0, 2.9, 12.2, 5.2);
		
		notaFiscal = new NotaFiscal(6l, 5, NotaFiscalTipo.valueOf("ENTRADA"), "natureza", 6, empresa, pessoa, 7, "verpro", ft, notaFiscalFin, notaFiscalTot, 5, new Date(20/10/2021));
		notaFiscal.setChave_acesso("chave");
		notaFiscal.setCodigo(1l);
		
		lnf = new ArrayList<NotaFiscal>();
		lnf.add(notaFiscal);
	}
	
	@Test
	public void testList() {
		mockNFR = Mockito.mock(NotaFiscalRepository.class);
		NotaFiscalService nfs = new NotaFiscalService(mockNFR);
				
		assertTrue(nfs.lista().isEmpty());
		//O metodo findAll retorna uma lista vazia
				
		Mockito.when(mockNFR.findAll()).thenReturn(lnf);
		List<NotaFiscal> lista = nfs.lista();
		assertNotNull(lista);
		//O metodo findAll encontrou pelo menos uma Nota Fiscal
		
		assertArrayEquals(lista.toArray(), lnf.toArray());
		//As duas listas sao iguais
	}

	@Test
	public void testEmitidas() {		
		mockNFR = Mockito.mock(NotaFiscalRepository.class);
		NotaFiscalService nfs = new NotaFiscalService(mockNFR);
		Mockito.when(mockNFR.totalNotaFiscalEmitidas()).thenReturn(5);
				
		nfs.totalNotaFiscalEmitidas();
		
		verify(mockNFR).totalNotaFiscalEmitidas();
		
		assertNotNull(nfs.totalNotaFiscalEmitidas());
		assertTrue(nfs.totalNotaFiscalEmitidas()>0);
		assertEquals(5, nfs.totalNotaFiscalEmitidas());
		//Verificando o total de notas fiscais emitidas
	}
	
	@Test
	public void testGeraDv() {
		assertNotEquals(Integer.valueOf(0), NFService.geraDV("DV14"));
		//Variavel 'Resto' é 10, retornando 1
		
		assertEquals(Integer.valueOf(0), NFService.geraDV("DV15"));
		//Variavel 'Resto' é 1, retornando 0
		
		assertEquals(Integer.valueOf(0), NFService.geraDV("DV556435768"));
		//Testando o condicional de peso == 10
		
		assertEquals(Integer.valueOf(0), NFService.geraDV(null));
		//Testando o catch do metodo
	}
	
	@Test
	public void testSalvaXml() throws IOException {	
		assertNotNull(notaFiscal);
		//A intancia de NotaFiscal existe
		
		assertNotNull(notaFiscal.getChave_acesso());
		//A chave de acesso da nota fiscal nao é nula
		
		//diretorio ate 'pdv'. Ex: C:\Users\Pichau\Desktop\pdv		
		String path = "C:\\Users\\Pichau\\Desktop\\pdv";
		String caminho = path+NotaFiscalService.CAMINHO_XML;
		NFService.salvaXML("Gravado com sucesso! em "+caminho, notaFiscal.getChave_acesso());
		
		assertEquals(NFService.diretorio, caminho);
		//O diretorio do metodo salva e do metodo de teste sao iguais
	}

	@Test
	public void testRemoveXml() {
		//diretorio ate 'pdv'. Ex: C:\Users\Pichau\Desktop\pdv	
		File file = new File("C:\\Users\\Pichau\\Desktop\\pdv"+NotaFiscalService.CAMINHO_XML+File.separator+notaFiscal.getChave_acesso()+".xml");
		try {
			assertFalse(!file.exists());
			//O arquivo a ser removido existe
		}catch(Exception e) {
			fail(e.getMessage());
			//O arquivo nao existe
		}
		assertNotNull(notaFiscal.getChave_acesso());
		//Verifica se a nota fiscal temchave de acesso
		assertTrue(NFService.removeXml(notaFiscal.getChave_acesso()));
		//Retorna 'true' porque o arquivo foi removido com sucesso		
	}
	
	@Test
	public void testBusca() {
		assertThrows(NullPointerException.class,() -> NFService.busca(1l));
		//Busca retorna um valor nulo
		
		mockNFR = Mockito.mock(NotaFiscalRepository.class);
		NotaFiscalService nfs = new NotaFiscalService(mockNFR);		
		java.util.Optional<NotaFiscal> notaFiscalOp = java.util.Optional.of(notaFiscal);
		Mockito.when(mockNFR.findById(anyLong())).thenReturn(notaFiscalOp);
		
		assertNotNull(nfs.busca(1l));
		//O metodo de busca encontrou algo
		
		verify(mockNFR).findById(notaFiscal.getCodigo());
		
		assertSame(notaFiscalOp, nfs.busca(notaFiscal.getCodigo()));
		//As duas notas fiscais sao as mesmas
	}
	
	@Test
	public void testCadastroSemSerie() {
		EmpresaService mockES = Mockito.mock(EmpresaService.class);
		empresa.getParametro().setSerie_nfe(0);
		java.util.Optional<Empresa> empresaOp = java.util.Optional.of(empresa);
		
		PessoaService mockPS = Mockito.mock(PessoaService.class);
		java.util.Optional<Pessoa> pessoaOp = java.util.Optional.of(pessoa);
				
		NotaFiscalService nfs = new NotaFiscalService(mockES, mockPS);
		
		Mockito.when(mockPS.buscaPessoa(anyLong())).thenReturn(pessoaOp);						
		Mockito.when(mockES.verificaEmpresaCadastrada()).thenReturn(empresaOp);
		
		assertEquals(0, empresa.getParametro().getSerie_nfe());
		//Verifica se a serie realmente ta null
		assertThrows(IllegalArgumentException.class, () -> nfs.cadastrar(1l, "natureza", NotaFiscalTipo.valueOf("ENTRADA")));
		//Não existe série cadastrada para o modelo ou falta alguma informação

	}
	
	@Test
	public void testCadastroSemSave() {
		PessoaService mockPS = Mockito.mock(PessoaService.class);
		java.util.Optional<Pessoa> pessoaOp = java.util.Optional.of(pessoa);
		
		EmpresaService mockES = Mockito.mock(EmpresaService.class);
		java.util.Optional<Empresa> empresaOp = java.util.Optional.of(empresa);
		
		NotaFiscalTotaisServer mockNFTS = Mockito.mock(NotaFiscalTotaisServer.class);
		Mockito.when(mockNFTS.cadastro(any(NotaFiscalTotais.class))).thenReturn(notaFiscalTot);
		
		NotaFiscalService nfs = new NotaFiscalService(mockPS, mockES,  mockNFTS, mockNFR);
		
		Mockito.when(mockPS.buscaPessoa(anyLong())).thenReturn(pessoaOp);						
		Mockito.when(mockES.verificaEmpresaCadastrada()).thenReturn(empresaOp);
		
		assertThrows(IllegalArgumentException.class, () -> nfs.cadastrar(1l, "natureza", NotaFiscalTipo.valueOf("ENTRADA")));
		//exceção lançada ao tentar salvar uma nota fical em um repositorio de nota fiscal null
	}
	
	@Test
	public void testCadastroSemTot() {
		PessoaService mockPS = Mockito.mock(PessoaService.class);
		java.util.Optional<Pessoa> pessoaOp = java.util.Optional.of(pessoa);
		
		EmpresaService mockES = Mockito.mock(EmpresaService.class);
		java.util.Optional<Empresa> empresaOp = java.util.Optional.of(empresa);
		
		mockNFR = Mockito.mock(NotaFiscalRepository.class);
		Mockito.when(mockNFR.save(any(NotaFiscal.class))).thenReturn(notaFiscal);
		
		NotaFiscalService nfs = new NotaFiscalService(mockES, mockPS, mockNFR);
		
		Mockito.when(mockPS.buscaPessoa(anyLong())).thenReturn(pessoaOp);						
		Mockito.when(mockES.verificaEmpresaCadastrada()).thenReturn(empresaOp);
		assertThrows(IllegalArgumentException.class, () -> nfs.cadastrar(1l, "natureza", NotaFiscalTipo.valueOf("ENTRADA")));
		//exceção lançada ao tentar cadastrar um nota fiscal totais a um nota fiscal totais server
	}
	
	@Test
	public void testCadastro() {		
		assertThrows(NullPointerException.class, ()->NFService.cadastrar(1l, "natureza", NotaFiscalTipo.valueOf("ENTRADA")));
		//Variavel empresa é nula, logo, exceção lançada
	
		PessoaService mockPS = Mockito.mock(PessoaService.class);
		java.util.Optional<Pessoa> pessoaOp = java.util.Optional.of(pessoa);
		
		EmpresaService mockES = Mockito.mock(EmpresaService.class);
		java.util.Optional<Empresa> empresaOp = java.util.Optional.of(empresa);
				
		mockNFR = Mockito.mock(NotaFiscalRepository.class);
		Mockito.when(mockNFR.save(any(NotaFiscal.class))).thenReturn(notaFiscal);
		
		NotaFiscalTotaisServer mockNFTS = Mockito.mock(NotaFiscalTotaisServer.class);
		Mockito.when(mockNFTS.cadastro(any(NotaFiscalTotais.class))).thenReturn(notaFiscalTot);
		
		NotaFiscalService nfs = new NotaFiscalService(mockPS, mockES,  mockNFTS, mockNFR);
		
		Mockito.when(mockPS.buscaPessoa(anyLong())).thenReturn(pessoaOp);						
		Mockito.when(mockES.verificaEmpresaCadastrada()).thenReturn(empresaOp);
		String id = nfs.cadastrar(1l, "natureza", NotaFiscalTipo.valueOf("ENTRADA"));
		
		assertEquals(id, notaFiscal.getCodigo().toString());
		//Verifica a igualdade do codigo da nota fical com o valor da variavel
		assertEquals(notaFiscal.getTipo_ambiente(), empresaOp.get().getParametro().getAmbiente());
		//Verifica a igualdade do tipo de ambiente da nota fiscal e da empresa opcional
	}
	
	@Test
	public void testEmite() {
		mockXMLNFE = Mockito.mock(GeraXmlNfe.class);
		Mockito.when(mockXMLNFE.gerarXML(any(NotaFiscal.class))).thenReturn(notaFiscal.getChave_acesso());
		
		mockNFR = Mockito.mock(NotaFiscalRepository.class);
		Mockito.when(mockNFR.save(any(NotaFiscal.class))).thenReturn(notaFiscal);
		
		NotaFiscalService nfs = new NotaFiscalService(mockNFR, mockXMLNFE);
		
		nfs.emitir(notaFiscal);
		
		assertNotNull(mockXMLNFE.gerarXML(new NotaFiscal()));
		//Verifica se o mock nao retorna um null
		assertFalse(mockXMLNFE.gerarXML(new NotaFiscal()).length()<1);
		//Verifica se o mock retorna uma chave valida
		assertEquals(notaFiscal.getChave_acesso(), mockXMLNFE.gerarXML(new NotaFiscal()));
		//Verifica se o mock esta retornando a chave que deveria
	}
}

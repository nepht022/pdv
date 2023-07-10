package net.originmobi.pdv.service.notafiscal;

import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.originmobi.pdv.enumerado.notafiscal.NotaFiscalTipo;
import net.originmobi.pdv.model.Empresa;
import net.originmobi.pdv.model.FreteTipo;
import net.originmobi.pdv.model.NotaFiscal;
import net.originmobi.pdv.model.NotaFiscalFinalidade;
import net.originmobi.pdv.model.NotaFiscalTotais;
import net.originmobi.pdv.model.Pessoa;
import net.originmobi.pdv.repository.notafiscal.NotaFiscalRepository;
import net.originmobi.pdv.service.EmpresaService;
import net.originmobi.pdv.service.PessoaService;
import net.originmobi.pdv.xml.nfe.GeraXmlNfe;

@Service
public class NotaFiscalService {
	@Autowired
	private GeraXmlNfe geraXmlNfe;

	@Autowired
	private NotaFiscalRepository notasFiscais;

	@Autowired
	private EmpresaService empresas;

	@Autowired
	private NotaFiscalTotaisServer notaTotais;

	@Autowired
	private PessoaService pessoas;

	public static final String CAMINHO_XML = "\\src\\main\\resources\\xmlNfe";

	public String diretorio;

	public NotaFiscalService() {
	}

	public NotaFiscalService(NotaFiscalRepository mockNFR) {
		this.notasFiscais = mockNFR;
	}

	public NotaFiscalService(NotaFiscalRepository mockNFR, GeraXmlNfe mockXMLNFE) {
		this.notasFiscais = mockNFR;
		this.geraXmlNfe = mockXMLNFE;
	}

	public NotaFiscalService(EmpresaService mockES, PessoaService mockPS) {
		this.pessoas = mockPS;
		this.empresas = mockES;
	}

	public NotaFiscalService(EmpresaService mockES, PessoaService mockPS, NotaFiscalRepository mockNFR) {
		this.pessoas = mockPS;
		this.empresas = mockES;
		this.notasFiscais = mockNFR;
	}

	public NotaFiscalService(PessoaService mockPS, EmpresaService mockES, NotaFiscalTotaisServer mockNFTS,
			NotaFiscalRepository mockNFR) {
		this.pessoas = mockPS;
		this.empresas = mockES;
		this.notaTotais = mockNFTS;
		this.notasFiscais = mockNFR;
	}

	public List<NotaFiscal> lista() {
		return notasFiscais.findAll();
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public String cadastrar(Long coddesti, String natureza, NotaFiscalTipo tipo) {
		Optional<Empresa> empresa = empresas.verificaEmpresaCadastrada();
		Optional<Pessoa> pessoa = pessoas.buscaPessoa(coddesti);

		// prepara informações iniciais da nota fiscal
		FreteTipo frete = new FreteTipo();
		frete.setCodigo(4L);
		NotaFiscalFinalidade finalidade = new NotaFiscalFinalidade();
		finalidade.setCodigo(1L);
		int modelo = 55;
		int serie = empresa.map(Empresa::getParametro).get().getSerie_nfe();

		if (empresa.map(Empresa::getParametro).get().getSerie_nfe() == 0)
			throw new IllegalArgumentException("Não existe série cadastrada para o modelo 55, verifique");

		// opção 1 é emissão normal, as outras opções (2, 3, 4, 5) são para contigência
		int tipoEmissao = 1;
		LocalDate dataAtual = LocalDate.now();
		Date cadastro = Date.valueOf(dataAtual);
		String verProc = "0.0.1-beta";
		int tipoAmbiente = empresa.get().getParametro().getAmbiente();

		// cadastra os totais iniciais da nota fiscal
		NotaFiscalTotais totais = new NotaFiscalTotais(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
		try {
			notaTotais.cadastro(totais);
		} catch (RuntimeException e) {
			throw new IllegalArgumentException("Erro ao cadastrar a nota, chame o suporte");
		}

		// cadastra a nota fiscal
		NotaFiscal nota = null;
		try {
			// pega ultima nota cadastradas + 1
			Long numeroNota = notasFiscais.buscaUltimaNota(serie);

			NotaFiscal notaFiscal = new NotaFiscal(numeroNota, modelo, tipo, natureza, serie, empresa.get(),
					pessoa.get(), tipoEmissao, verProc, frete, finalidade, totais, tipoAmbiente, cadastro);

			nota = notasFiscais.save(notaFiscal);

		} catch (Exception e) {
			Log.debug("Erro " + e);
			throw new IllegalArgumentException("Erro ao cadastrar a nota, chame o suporte");
		}

		return nota.getCodigo().toString();
	}

	public Integer geraDV(String codigo) {
		try {
			int total = 0;
			int peso = 2;

			for (int i = 0; i < codigo.length(); i++) {
				total += (codigo.charAt((codigo.length() - 1) - i) - '0') * peso;
				peso++;
				if (peso == 10) {
					peso = 2;
				}
			}
			int resto = total % 11;
			return (resto == 0 || resto == 1) ? 0 : (11 - resto);
		} catch (Exception e) {
			return 0;
		}
	}

	public void salvaXML(String xml, String chaveNfe) {
		Path DIRETORIO;
		String contexto = "";

		try {
			contexto = new File(".").getCanonicalPath();
		} catch (Exception e) {
			Log.debug("Erro ao pegar o contexto " + e);
		}

		DIRETORIO = Paths.get(contexto + CAMINHO_XML);
		diretorio = DIRETORIO.toString();

		try {
			PrintWriter out = new PrintWriter(new FileWriter(DIRETORIO.toString() + "/" + chaveNfe + ".xml"));
			out.write(xml);
			out.close();
			Log.debug("Arquivo gravado com sucesso em " + DIRETORIO.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * responsável por remover o xml quando o mesmo já existe na nota que foi
	 * regerada
	 */
	public void cleanUp(Path path) throws IOException {
		Files.delete(path);
	}

	public boolean removeXml(String chave_acesso) {
		String contexto = "";

		try {
			contexto = new File(".").getCanonicalPath();
		} catch (Exception e) {
			Log.debug("Erro ao pegar o contexto " + e);
		}

		try {
			File file = new File(contexto + CAMINHO_XML + File.separator + chave_acesso + ".xml");
			if (file.exists()) {
				cleanUp(file.toPath());
				Log.info("File " + file + " deleted");
			}
		} catch (Exception e) {
			Log.debug("Erro ao deletar XML " + e);
			return false;
		}
		return true;
	}

	public Optional<NotaFiscal> busca(Long codnota) {
		return notasFiscais.findById(codnota);
	}

	public void emitir(NotaFiscal notaFiscal) {
		// gera o xml e pega a chave de acesso do mesmo
		String chaveNfe = geraXmlNfe.gerarXML(notaFiscal);

		// seta a chave de acesso na nota fiscal para gravala no banco
		notaFiscal.setChave_acesso(chaveNfe);

		notasFiscais.save(notaFiscal);
	}

	public int totalNotaFiscalEmitidas() {
		return notasFiscais.totalNotaFiscalEmitidas();
	}
}

package net.originmobi.pdv.service.notafiscal;

import java.io.File;
<<<<<<< HEAD

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
=======
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
>>>>>>> 8ed96d5d727adec0606a912a0b4a2c65bc0d54fd
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

<<<<<<< HEAD
import org.jfree.util.Log;
=======
>>>>>>> 8ed96d5d727adec0606a912a0b4a2c65bc0d54fd
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
	private NotaFiscalRepository notasFiscais;

	@Autowired
	private EmpresaService empresas;

	@Autowired
	private NotaFiscalTotaisServer notaTotais;

	@Autowired
	private PessoaService pessoas;

<<<<<<< HEAD
	public static final String CAMINHO_XML = "\\src\\main\\resources\\xmlNfe";

	public String diretorio;

	public NotaFiscalService() {
	}

	public NotaFiscalService(NotaFiscalRepository mockNFR) {
		this.notasFiscais = mockNFR;
	}

	public NotaFiscalService(PessoaService mockPS, EmpresaService mockES, NotaFiscalTotaisServer mockNFTS,
			NotaFiscalRepository mockNFR) {
		this.pessoas = mockPS;
		this.empresas = mockES;
		this.notaTotais = mockNFTS;
		this.notasFiscais = mockNFR;
	}
=======
	private LocalDate dataAtual;

	private static final String CAMINHO_XML = "/src/main/resources/xmlNfe/";
>>>>>>> 8ed96d5d727adec0606a912a0b4a2c65bc0d54fd

	public List<NotaFiscal> lista() {
		return notasFiscais.findAll();
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public String cadastrar(Long coddesti, String natureza, NotaFiscalTipo tipo) {
		Optional<Empresa> empresa = empresas.verificaEmpresaCadastrada();
		Optional<Pessoa> pessoa = pessoas.buscaPessoa(coddesti);

		if (!empresa.isPresent())
<<<<<<< HEAD
			throw new IllegalArgumentException("Nenhuma empresa cadastrada, verifique");

		if (!pessoa.isPresent())
			throw new IllegalArgumentException("Favor, selecione o destinatário");
=======
			throw new RuntimeException("Nenhuma empresa cadastrada, verifique");

		if (!pessoa.isPresent())
			throw new RuntimeException("Favor, selecione o destinatário");
>>>>>>> 8ed96d5d727adec0606a912a0b4a2c65bc0d54fd

		// prepara informações iniciais da nota fiscal
		FreteTipo frete = new FreteTipo();
		frete.setCodigo(4L);
		NotaFiscalFinalidade finalidade = new NotaFiscalFinalidade();
		finalidade.setCodigo(1L);
		int modelo = 55;
		int serie = empresa.map(Empresa::getParametro).get().getSerie_nfe();

		if (empresa.map(Empresa::getParametro).get().getSerie_nfe() == 0)
<<<<<<< HEAD
			throw new IllegalArgumentException("Não existe série cadastrada para o modelo 55, verifique");

		// opção 1 é emissão normal, as outras opções (2, 3, 4, 5) são para contigência
		int tipoEmissao = 1;
		LocalDate dataAtual = LocalDate.now();
=======
			throw new RuntimeException("Não existe série cadastrada para o modelo 55, verifique");

		// opção 1 é emissão normal, as outras opções (2, 3, 4, 5) são para contigência
		int tipoEmissao = 1;
		dataAtual = LocalDate.now();
>>>>>>> 8ed96d5d727adec0606a912a0b4a2c65bc0d54fd
		Date cadastro = Date.valueOf(dataAtual);
		String verProc = "0.0.1-beta";
		int tipoAmbiente = empresa.get().getParametro().getAmbiente();

		// cadastra os totais iniciais da nota fiscal
		NotaFiscalTotais totais = new NotaFiscalTotais(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
		try {
			notaTotais.cadastro(totais);
<<<<<<< HEAD
		} catch (RuntimeException e) {
			throw new IllegalArgumentException("Erro ao cadastrar a nota, chame o suporte");
=======
		} catch (Exception e) {
			throw new RuntimeException("Erro ao cadastrar a nota, chame o suporte");
>>>>>>> 8ed96d5d727adec0606a912a0b4a2c65bc0d54fd
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
<<<<<<< HEAD
			Log.debug("Erro " + e);
			throw new IllegalArgumentException("Erro ao cadastrar a nota, chame o suporte");
=======
			System.out.println("Erro " + e);
			throw new RuntimeException("Erro ao cadastrar a nota, chame o suporte");
>>>>>>> 8ed96d5d727adec0606a912a0b4a2c65bc0d54fd
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
<<<<<<< HEAD
			Log.debug("Erro ao pegar o contexto " + e);
		}

		DIRETORIO = Paths.get(contexto + CAMINHO_XML);
		diretorio = DIRETORIO.toString();
=======
			System.out.println("Erro ao pegar o contexto " + e);
		}

		DIRETORIO = Paths.get(contexto + CAMINHO_XML);
>>>>>>> 8ed96d5d727adec0606a912a0b4a2c65bc0d54fd

		try {
			PrintWriter out = new PrintWriter(new FileWriter(DIRETORIO.toString() + "/" + chaveNfe + ".xml"));
			out.write(xml);
			out.close();
<<<<<<< HEAD
			Log.debug("Arquivo gravado com sucesso em " + DIRETORIO.toString());
=======
			System.out.println("Arquivo gravado com sucesso em " + DIRETORIO.toString());
>>>>>>> 8ed96d5d727adec0606a912a0b4a2c65bc0d54fd
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * responsável por remover o xml quando o mesmo já existe na nota que foi
	 * regerada
	 */
<<<<<<< HEAD
	public void cleanUp(Path path) throws IOException {
		Files.delete(path);
	}

	public boolean removeXml(String chave_acesso) {
=======
	public void removeXml(String chave_acesso) {
>>>>>>> 8ed96d5d727adec0606a912a0b4a2c65bc0d54fd
		String contexto = "";

		try {
			contexto = new File(".").getCanonicalPath();
		} catch (Exception e) {
<<<<<<< HEAD
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
=======
			System.out.println("Erro ao pegar o contexto " + e);
		}

		try {
			File file = new File(contexto + CAMINHO_XML + "/" + chave_acesso + ".xml");
			System.out.println("XML para deletar " + file.toString());
			if (file.exists())
				file.delete();
		} catch (Exception e) {
			System.out.println("Erro ao deletar XML " + e);
		}
>>>>>>> 8ed96d5d727adec0606a912a0b4a2c65bc0d54fd
	}

	public Optional<NotaFiscal> busca(Long codnota) {
		return notasFiscais.findById(codnota);
	}

	public void emitir(NotaFiscal notaFiscal) {
		GeraXmlNfe geraXmlNfe = new GeraXmlNfe();

		// gera o xml e pega a chave de acesso do mesmo
		String chaveNfe = geraXmlNfe.gerarXML(notaFiscal);

		// seta a chave de acesso na nota fiscal para gravala no banco
		notaFiscal.setChave_acesso(chaveNfe);

		notasFiscais.save(notaFiscal);
	}

	public int totalNotaFiscalEmitidas() {
		return notasFiscais.totalNotaFiscalEmitidas();
	}
<<<<<<< HEAD
=======

>>>>>>> 8ed96d5d727adec0606a912a0b4a2c65bc0d54fd
}

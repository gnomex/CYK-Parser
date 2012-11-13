package br.unioeste.fnc.parser;

import java.util.ArrayList;
import java.util.StringTokenizer;

import br.unioeste.fnc.exceptions.GrammarNotFNCFormat;
import br.unioeste.grammar.Estado;
import br.unioeste.grammar.NaoTerminal;
import br.unioeste.grammar.Producao;
import br.unioeste.grammar.Terminal;
import br.unioeste.loaderfiles.LoadFile;
import br.unioeste.loaderfiles.OutputFile;

public class FNC {

	private Producao inicial; //Simbolo inicial

	private ArrayList<Estado> estados; //Estados possiveis e suas produções

	private ArrayList<NaoTerminal> naoTerminais; //Não Terminais
	private ArrayList<Terminal> terminais; //Terminais

	private ArrayList<Estado> terminaisZ; //Para novos não terminais Z gerados

	private ArrayList<Estado> estadosNaFNC; //Resultados do processamento

	private ParserListener listener;

	public FNC( ParserListener processamentoListener ){
		inicial = new Producao();
		estados = new ArrayList<Estado>();
		estadosNaFNC = new ArrayList<Estado>();

		naoTerminais = new ArrayList<NaoTerminal>();
		terminais = new ArrayList<Terminal>();
		terminaisZ = new ArrayList<Estado>();

		listener = processamentoListener;
	}

	/**	Considerar Gramática previamente Simplificada
	 * 
	 * 	public ArrayList<Estado> simplificacao(){

		}
	 * */

	/**
	 * Passo 4
	 * 	Remove produçoes com terminais das produções, gerando produçoes Z<n>
	 * 
	 * */

	public void variaveisLadoDireito() throws Exception{

		try{
			ArrayList<Estado> novosEstados = new ArrayList<Estado>();

			//Percorre todos os estados
			for(Estado estado : estados){
				//Em cada estado percorre todas as produções

				ArrayList<Producao> semTerminal = new ArrayList<Producao>();

				for(Producao pr : estado.getProducoes()){

					//Pega produção
					//Se for unico terminal, faz nada
					//Se for terminal Com não Terminal
					//Cria produção Z
					//Se for apenas NaoTerminal, faz nada
					//Adiciona resultado no array
					listener.statusProcessamento("Processando produção: " + pr.getProducao());

					if(pr.getProducao().length() > 1){ 

						Producao novaProd = new Producao(); //Cria novo elemento
						novaProd = trasnformaTerminal(pr , "");

						semTerminal.add(novaProd);
					}else{
						semTerminal.add(pr);
					}

				}

				Estado nEstado = new Estado();
				nEstado.setEstado(estado.getEstado());
				nEstado.setProducoes(semTerminal);

				novosEstados.add(nEstado);

			}

			//mergeZ(novosEstados);

			estadosNaFNC = novosEstados;

		}catch (Exception e) {
			// TODO: handle exception
		}


	}

	/**
	 * 	Merge Nova lista de estados com os estados Z gerados
	 * */

	public void mergeZ(ArrayList<Estado> estadosComZ){

		try{
			if(!estadosNaFNC.isEmpty()){
				estadosNaFNC.clear();
			}

			for(Estado est : estadosComZ){
				estadosNaFNC.add(est);
			}
			for(Estado es : terminaisZ){
				estadosNaFNC.add(es);
			}

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	/*	Cria produções de NaoTerminais Z para todos os terminais presentes nas produõoes 
	 * 
	 * 	Verefica produções, gera nova regra Z se necessário, e retorna produção com somente NaoTerminais 
	 * 		
	 * */

	public Producao trasnformaTerminal(Producao prod , String splitRegex) throws Exception{

		try{

			ArrayList<NaoTerminal> naoTerminaisZ = new ArrayList<NaoTerminal>();

			//Transforma produção em String
			String[] testeZ;
			testeZ = prod.getProducao().split(splitRegex);

			//Percorre String atrás de terminais
			for(String str : testeZ){

				if(!str.isEmpty()){
					listener.statusProcessamento("  -- Processando: " + str);

					NaoTerminal ntZ = new NaoTerminal();

					if(isSimboloTerminal(str)){ //Se encontrou terminal

						listener.statusProcessamento("  -- -> Isterminal " );

						Producao nZprod = new Producao();
						nZprod.setProducao(str);

						//Pega regra Z
						ntZ = criaZ(nZprod);

						listener.statusProcessamento(" ## " + ntZ.getNaoTerminais());

					}else{
						ntZ.setNaoTerminais(str + "|");
					}
					//Adiciona ao array de regras a serem concatenadas posteriormente
					naoTerminaisZ.add(ntZ);
				}
			}

			Producao prZ = new Producao();
			prZ = concatenaNTerminaisToProd(naoTerminaisZ);

			listener.statusProcessamento(" Produção final do processamento: " + prZ.getProducao());

			return prZ;

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new Exception("Erro: Nao foi possivel encontrar terminais");
		}
	}

	/*	Concatena lista de NãoTerminais
	 * 		Retorna Produção com regra de Somente NãoTerminais
	 * */

	public Producao concatenaNTerminaisToProd(ArrayList<NaoTerminal> nTerminais) throws Exception{

		//Concatena todos os Nao Terminais
		//Retorna uma produção com o resultado da concatenação
		String concat = "";

		for(NaoTerminal nt : nTerminais){
			concat = concat + nt.getNaoTerminais();
		}

		Producao pd = new Producao();
		pd.setProducao(concat);

		return pd;

	}

	/*	Valida se é terminal
	 * 
	 * */

	public Boolean isSimboloTerminal(String term){

		//Pesquisa se o elemento é terminal
		try{
			for(Terminal tm : terminais){
				if(tm.getTerminal().equals(term)){
					return true;
				}
			}

		}catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}

	/*	Cria nova produção Z<n>
	 * 
	 * */

	public NaoTerminal criaZ(Producao producao) throws Exception{

		try{

			if(!terminaisZ.isEmpty()){
				//Vereficar se já existe z para produção
				for(Estado es : terminaisZ){

					for(Producao pd : es.getProducoes()){
						//Se existir retorna a regra Z<n>
						if(pd.getProducao().equals(producao.getProducao())){

							listener.statusProcessamento(" ---> Regra Z encontrada");

							return es.getEstado();
						}

					}	
				}
			}
			//Se não existir cria nova regra Z<n>
			listener.statusProcessamento("  # Regra Z  NAO encontrada");

			Estado estado = new Estado();

			NaoTerminal nt = new NaoTerminal();
			nt.setNaoTerminais("Z"+terminaisZ.size() + "|");


			estado.setEstado(nt);
			estado.addProducao(producao);

			this.terminaisZ.add(estado);

			listener.statusProcessamento("  >> Regra Z criada " + nt.getNaoTerminais());

			return nt;


		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new Exception("Erro: nao foi possivel Criar Z");
		}

	}

	/**
	 * 	Passo 5
	 * 		Formata tudo na forma
	 * 			A->BC {Exatamente 2 NãoTerminais
	 * 			A->a	{Apenas Terminal sozinho
	 * 
	 * 
	 * */

	public void somente2VariaveisLadoDireito() throws GrammarNotFNCFormat, Exception{

		listener.statusProcessamento("  ##\n	Passo 5\n ##");

		ArrayList<Estado> estadosFinaisComZ  = new ArrayList<Estado>();

		for(Estado estZ : estadosNaFNC){

			ArrayList<Producao> prodEmFNC = new ArrayList<Producao>();

			for(Producao pdZ : estZ.getProducoes()){

				listener.statusProcessamento("  Processando produção: " + pdZ.getProducao());

				Producao pd = new Producao();
				pd = trasnformaNaoTerminais(pdZ, "|");

				prodEmFNC.add(pd);

			}

			Estado nEstado = new Estado();
			nEstado.setEstado(estZ.getEstado());
			nEstado.setProducoes(prodEmFNC);

			estadosFinaisComZ.add(nEstado);

		}

		mergeZ(estadosFinaisComZ);
		updateTerminais();
		imprimeResultado();
	}


	public Producao trasnformaNaoTerminais(Producao prod , String splitRegex) throws GrammarNotFNCFormat{


		ArrayList<NaoTerminal> naoTerminaisZ =null;

		try{

			naoTerminaisZ = new ArrayList<NaoTerminal>();

			StringTokenizer st = new StringTokenizer(prod.getProducao() , splitRegex);
			int stTokens = st.countTokens();

			if(stTokens < 2 ){//Se for menor de 2 tokens
				if(!isSimboloTerminal(st.nextToken())){//Se nao For terminal

					throw new GrammarNotFNCFormat("Error: Gramatica não válida");

				}

			}

			if(stTokens > 2){ //Se tiver mais de 2 tokens

				while(st.countTokens()>2){

					String concat = st.nextToken() + st.nextToken(); //Pega dois próximos Tokens
					//concat = concat.replaceAll("|", "");

					NaoTerminal ntZ = new NaoTerminal();
					Producao nZprod = new Producao();

					nZprod.setProducao(concat);

					ntZ = criaZ(nZprod);

					naoTerminaisZ.add(ntZ);
				}

			}else{
				//Faz Nada
				return prod;
			}


		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}


		Producao prZ = new Producao();
		try {
			prZ = concatenaNTerminaisToProd(naoTerminaisZ);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(" Produção final do processamento: " + prZ.getProducao());

		return prZ;

	}

	/**
	 * 	Demais Métodos
	 * 
	 * */

	public void loadEstadosFromFile(String filename){

		try {
			LoadFile lf = new LoadFile(filename);
			lf.carregarRecursos();

			this.estados = lf.getEstados();
			listener.entradaProcessamento("Estados: " + estados.size());

			this.inicial = lf.getInicial();
			listener.entradaProcessamento("Estado Inicial: " + inicial.getProducao());

			this.naoTerminais = lf.getNaoTerminais();
			listener.entradaProcessamento("Nao terminais: " + naoTerminais.size() );

			this.terminais = lf.getTerminais();
			listener.entradaProcessamento("terminais: " + terminais.size() );

			imprimeGramaticaEntrada();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	private void imprimeGramaticaEntrada(){

		for(Estado estado : estados){

			listener.entradaProcessamento("Produção: " + estado.getEstado().getNaoTerminais());

			for(Producao pr : estado.getProducoes()){

				listener.entradaProcessamento("  ->" + pr.getProducao());

			}
		}

	}

	public void saveProcessamentoToFile(String filepath){

		ArrayList<String> resultadoFNC = new ArrayList<String>();

		resultadoFNC.add("Variáveis");
		String naoTerminais = "";
		for(NaoTerminal nt : this.naoTerminais){
			naoTerminais = naoTerminais + nt.getNaoTerminais() +",";
		}
		naoTerminais = naoTerminais.replaceAll("\\|", "");

		
		resultadoFNC.add(naoTerminais);

		resultadoFNC.add("Terminais");
		String terms = "";
		for(Terminal tr : terminais){
			terms = terms + tr.getTerminal() + ",";
		}
		resultadoFNC.add(terms);

		resultadoFNC.add("Produções");

		for(Estado es : estadosNaFNC){

			for(Producao pd : es.getProducoes()){
				String pdes = es.getEstado().getNaoTerminais() + "->"+pd.getProducao();

				pdes =	pdes.replaceAll("\\|" , "");

				resultadoFNC.add(pdes);

			}			
		}

		resultadoFNC.add("Inicial");
		resultadoFNC.add(inicial.getProducao());

		OutputFile.gravaArquivoResultados(resultadoFNC, filepath + "/saidaFNC.txt" , false);

		listener.statusProcessamento("Arquivo salvo em: " + filepath + "/saidaFNC.txt" );

	}


	private void updateTerminais(){

		ArrayList<NaoTerminal> novosNaoTermianais = new ArrayList<NaoTerminal>();

		for(Estado est : estadosNaFNC){
			novosNaoTermianais.add(est.getEstado());
		}

		naoTerminais = novosNaoTermianais;

	}


	/**	Demais Setters and Getters
	 * ========================================================================
	 * */

	public void imprimeResultado(){

		for(Estado es : estadosNaFNC){

			listener.resultadoProcessamento("Estado : " + es.getEstado().getNaoTerminais());


			for(Producao pd : es.getProducoes()){
				listener.resultadoProcessamento(" -> : " + pd.getProducao());
			}

		}


	}


	public Producao getInicial() {
		return inicial;
	}
	public void setInicial(Producao inicial) {
		this.inicial = inicial;
	}
	public ArrayList<Estado> getEstados() {
		return estados;
	}
	public void setEstados(ArrayList<Estado> estados) {
		this.estados = estados;
	}
	public ArrayList<Estado> getEstadosNaFNC() {
		return estadosNaFNC;
	}
	public void setEstadosNaFNC(ArrayList<Estado> estadosNaFNC) {
		this.estadosNaFNC = estadosNaFNC;
	}

	public ArrayList<NaoTerminal> getNaoTerminais() {
		return naoTerminais;
	}

	public void setNaoTerminais(ArrayList<NaoTerminal> naoTerminais) {
		this.naoTerminais = naoTerminais;
	}

	public ArrayList<Terminal> getTerminais() {
		return terminais;
	}

	public void setTerminais(ArrayList<Terminal> terminais) {
		this.terminais = terminais;
	}


}

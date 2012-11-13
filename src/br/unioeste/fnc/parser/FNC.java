package br.unioeste.fnc.parser;

import java.util.ArrayList;
import java.util.StringTokenizer;

import br.unioeste.fnc.exceptions.GrammarNotFNCFormat;
import br.unioeste.grammar.Estado;
import br.unioeste.grammar.NaoTerminal;
import br.unioeste.grammar.Producao;
import br.unioeste.grammar.Terminal;
import br.unioeste.loaderfiles.LoadFile;

public class FNC {

	private Producao inicial; //Simbolo inicial

	private ArrayList<Estado> estados; //Estados possiveis e suas produções

	private ArrayList<NaoTerminal> naoTerminais; //Não Terminais
	private ArrayList<Terminal> terminais; //Terminais

	private ArrayList<Estado> terminaisZ; //Para novos não terminais Z gerados

	private ArrayList<Estado> estadosNaFNC; //Resultados do processamento

	public FNC(){
		inicial = new Producao();
		estados = new ArrayList<Estado>();
		estadosNaFNC = new ArrayList<Estado>();

		naoTerminais = new ArrayList<NaoTerminal>();
		terminais = new ArrayList<Terminal>();
		terminaisZ = new ArrayList<Estado>();
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
					//1
					//Se for unico terminal, faz nada
					//2
					//Se for terminal Com não Terminal
					//Cria produção Z
					//3
					//Se for apenas NaoTerminal, faz nada
					//Adiciona resultado no array

					System.out.println("Processando produção: " + pr.getProducao());
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

			mergeZ(novosEstados);


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
					System.out.println("  -- Processando: " + str );

					NaoTerminal ntZ = new NaoTerminal();

					if(isSimboloTerminal(str)){ //Se encontrou terminal
						System.out.println("  -- -> Isterminal " );

						Producao nZprod = new Producao();
						nZprod.setProducao(str);

						//Pega regra Z
						ntZ = criaZ(nZprod);

						System.out.println(" ## " + ntZ.getNaoTerminais());

					}else{
						ntZ.setNaoTerminais(str + "|");
					}
					//Adiciona ao array de regras a serem concatenadas posteriormente
					naoTerminaisZ.add(ntZ);
				}
			}

			Producao prZ = new Producao();
			prZ = concatenaNTerminaisToProd(naoTerminaisZ);

			System.out.println(" Produção final do processamento: " + prZ.getProducao());

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

							System.out.println(" ---> Regra Z encontrada");

							return es.getEstado();
						}

					}	
				}
			}
			//Se não existir cria nova regra Z<n>
			System.out.println("  # Regra Z  NAO encontrada");

			Estado estado = new Estado();

			NaoTerminal nt = new NaoTerminal();
			nt.setNaoTerminais("Z"+terminaisZ.size() + "|");


			estado.setEstado(nt);
			estado.addProducao(producao);

			this.terminaisZ.add(estado);

			System.out.println("  >> Regra Z criada " + nt.getNaoTerminais());

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


	public ArrayList<Estado> somente2VariaveisLadoDireito(){

	}
	 * */



	public static void main(String[] args){

		FNC fnc = new FNC();

		fnc.loadEstadosFromFile("entrada2.txt");

		try {
			fnc.variaveisLadoDireito();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		fnc.imprimeParaTeste();


	}

	public Producao trasnformaNaoTerminais(Producao prod , String splitRegex) throws GrammarNotFNCFormat{

		try{

			ArrayList<NaoTerminal> naoTerminaisZ = new ArrayList<NaoTerminal>();

			StringTokenizer st = new StringTokenizer(prod.getProducao() , splitRegex);
			int stTokens = st.countTokens();

			if(stTokens < 2 ){
				if(!isSimboloTerminal(st.nextToken())){

					throw new GrammarNotFNCFormat("Error: Gramatica não válida");

				}

			}

			if(stTokens > 2){ //Se tiver mais de 2 tokens
				//Apenas para Teste
				System.out.println("  -- Processando: " + str );

				NaoTerminal ntZ = new NaoTerminal();
				Producao nZprod = new Producao();
				nZprod.setProducao(str);

				//Pega regra Z
				ntZ = criaZ(nZprod);

				System.out.println(" ## " + ntZ.getNaoTerminais());

				naoTerminaisZ.add(ntZ);
				//cria nova regra Z

			}else{
				//Faz Nada
			}
			Producao prZ = new Producao();
			prZ = concatenaNTerminaisToProd(naoTerminaisZ);

			System.out.println(" Produção final do processamento: " + prZ.getProducao());

			return prZ;

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new Exception("Erro: Nao foi possivel encontrar terminais");
		}



	}






	private void loadEstadosFromFile(String filename){

		try {
			LoadFile lf = new LoadFile(filename);
			lf.carregarRecursos();

			this.estados = lf.getEstados();
			System.out.println("Estados: " + estados.size() );
			this.inicial = lf.getInicial();
			System.out.println("Estado Inicial: " + inicial.getProducao() );
			this.naoTerminais = lf.getNaoTerminais();
			System.out.println("Nao terminais: " + naoTerminais.size() );
			this.terminais = lf.getTerminais();
			System.out.println("terminais: " + terminais.size() );

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}


	/**	Demais Setters and Getters
	 * ========================================================================
	 * */

	public void imprimeParaTeste(){

		for(Estado es : estadosNaFNC){

			System.out.println("Estado : " + es.getEstado().getNaoTerminais());

			for(Producao pd : es.getProducoes()){
				System.out.println(" -> : " + pd.getProducao());
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

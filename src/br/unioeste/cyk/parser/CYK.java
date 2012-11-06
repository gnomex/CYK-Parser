package br.unioeste.cyk.parser;

import java.util.ArrayList;

import br.unioeste.exceptions.CYKParserCadeiaEntradaError;
import br.unioeste.grammar.NaoTerminal;
import br.unioeste.loaderfiles.LoadFile;

public class CYK {

	private ProducoesForParser[][] matrizprocessamento; //Matriz para processamento do Algoritmo CYK

	private int tamMatriz; //Tamanho da matriz. A matriz é uma matriz Quadrada

	private Producao inicial; //Simbolo inicial

	private ArrayList<String> cadeiaEntrada; //Alfabeto de entrada para teste de aceitação

	private ArrayList<Estado> estados; //Estados possiveis e suas produções

	private Boolean cadeiaAceita; //Entrada reconhecida ou nao ?

	private final String TransacaoVazia = "#";

	/*
	 * Construtor
	 * 	Recebe como parametro a cadeia de entrada para teste
	 * */

	public CYK(ArrayList<String> cadeiaAlfabetoEntrada) throws Exception{

		tamMatriz = cadeiaAlfabetoEntrada.size();
		cadeiaEntrada = new ArrayList<String>();
		this.cadeiaEntrada = cadeiaAlfabetoEntrada;

		try{
			matrizprocessamento = new ProducoesForParser[tamMatriz][tamMatriz];
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new CYKParserCadeiaEntradaError("Cadeia de entrada muito grande");
		}

		inicial = new Producao();
		estados = new ArrayList<Estado>();
	}


	/*	Inicializa a matriz de processamento
	 *		Faz a leitura da cadeia de entrada e seta na ultima linha da matriz seus geradores(se existirem) 
	 * */

	public void inicializaEstadosMatriz() throws Exception{
		//Indices da matriz
		int linha = tamMatriz -1; //Ultima linha
		int coluna = 0; //Primeira coluna

		try{

			for(String atest : cadeiaEntrada){
				matrizprocessamento[linha][coluna] = new ProducoesForParser(); //Instancia Objeto na matriz
				//Cria producao para simbolo terminal
				Producao p = new Producao();
				p.setProducao(atest);
				//Procura gerador(es) e insere na matriz
				matrizprocessamento[linha][coluna] = queGera(p);
				coluna++; //Inrementa coluna
			}

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new Exception("Erro: Nao foi possivel inicializar Matriz de processamento");
		}

	}

	/**
	 * Algoritmo CYK
	 * 
	 * Algoritmo da Roldana
	 * 	Execução da direita para esquerda
	 * 	Pivo inicial da linha é o elemento da diagonal principal
	 * 		Para novo elemento, é pego os NaoTerminais da linha anterior com mesma coluna
	 * 		e linha anterior com coluna anterior
	 * 		Concatena nao terminais gerando produçoes
	 * 	É vereficado quem gera as produçoes
	 * 	O novo elemento recebe os Nao terminais geradores
	 * 
	 * */

	public void cykParser() throws Exception{

		try {
			inicializaEstadosMatriz(); //É preciso matriz estar previamente inicializada

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Deu pau ao carregar matriz");
		}

		try{

			int diagonalPrincipal = (tamMatriz -2); //Começa na penultima linha da matriz, pois a ultima já é previamente inicializada

			int linhaAnterior; //Linha Anterior
			int linhaAtual;	//Linha Atual
			int coluna;	//Coluna Atual
			int colunaAnt;	//Coluna Anterior

			/*	Inicio do algoritmo roldana
			 * 		Tem como pivo de referencia a diagonal principal
			 * */

			for(;diagonalPrincipal >= 0; diagonalPrincipal--){

				linhaAnterior=diagonalPrincipal + 1; //Linha Anterior
				linhaAtual=diagonalPrincipal; //Linha atual

				//System.out.println("Diagonal Principal: " +diagonalPrincipal);
				//System.out.println("Linha A ser Processada: " +linhaAnterior );

				//Percorre colunas da linha da vez e processa a Roldana
				for(int davez = diagonalPrincipal ;davez >= 0; davez--){

					coluna=davez; //Coluna da roldana
					colunaAnt = coluna +1; //Coluna anterior

					//System.out.println("Coluna ser Processada: " +coluna + "Coluna Ant: " + colunaAnt );

					//Array temporario para concatenar produçoes
					ArrayList<Producao> paraconcat = new ArrayList<Producao>();

					//Concatena Produçoes
					paraconcat = concatenaProducoes(matrizprocessamento[linhaAnterior][coluna],
							matrizprocessamento[linhaAnterior][colunaAnt]);

					//Remove produções duplicadas
					paraconcat = removeProducoesDuplicadas(paraconcat);


					//Varivel que recebe resultado do processamento
					//Elemento que vai ser setado na matriz
					ProducoesForParser roldana = new ProducoesForParser();
					ArrayList<NaoTerminal> nTroldana = new ArrayList<NaoTerminal>();

					//quemgera as produções
					for(Producao np : paraconcat){
						ProducoesForParser aux = new ProducoesForParser();
						aux = queGera(np);
						
						for(NaoTerminal nt : aux.getProducoes()){
							nTroldana.add(nt);
						}
					}
					
					
					//Remove NaoTerminais duplicados
					nTroldana = removeNaoTerminaisDuplicados(nTroldana);
					roldana.setProducoes(nTroldana);

					//seta resultado da roldana
					matrizprocessamento[(linhaAtual)][davez] = new ProducoesForParser();
					matrizprocessamento[(linhaAtual)][davez] = roldana;

				}	

			}

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new Exception("Erro: Algoritmo nao funcionou como deveria");
		}
	}

	public Boolean cadeiaFoiAceita(){

		/*	Verifica se Estado inicial está no topo da matriz diagonal
		 * 
		 * */

		ProducoesForParser test = new ProducoesForParser();
		test = matrizprocessamento[0][0]; //Elemento topo da matriz

		//Se contiver elementos, procura inicial			
		if(!test.getProducoes().isEmpty()){
			//Pesquisa inicial
			for(NaoTerminal testB : test.getProducoes()){
				if(testB.getNaoTerminais().equals(inicial.getProducao())){
					return true;
				}
			}	
		}
		//Se não encontrou
		return false;


	}


	/*	Procura estado gerador das produções
	 * 
	 * */

	public ProducoesForParser queGera(Producao producao) throws Exception{

		try{
			//Lista de regras geradoras
			ArrayList<NaoTerminal> regras = new ArrayList<NaoTerminal>();

			
			
			//Pesquisa em todos os estados
			for(Estado es : estados){
				ArrayList<Producao> prds = new ArrayList<Producao>();
				prds = es.getProducoes();
				//Pesquisa em todas as produções de cada estado
				for(Producao p : prds){
					//Pesquisa se o estado leva a regra de produção ou simbolo terminal
					if(p.getProducao().equals(producao.getProducao())){
						NaoTerminal ntgerador = new NaoTerminal();
						ntgerador = es.getEstado();	 //Pega NaoTerminal
						regras.add(ntgerador);
					}
				}
			}
			//Caso nao tenha gerador
			/**
			 * Essa parte do código está gerando Bug !
			 * 
			if(regras.isEmpty()){
				NaoTerminal ntgerador = new NaoTerminal();
				ntgerador.setNaoTerminais(TransacaoVazia);	 //Insere Simbolo Vazio
				regras.add(ntgerador);
			}
			*/

			ProducoesForParser pfp = new ProducoesForParser();
			pfp.setProducoes(regras);
			return pfp;

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new Exception("Erro: não foi possível encontrar geradores");
		}

	}


	public String concatenaTransacaoVazia(String producao1 , String producao2) throws Exception{

		try{
			String rmVazia;
			System.out.println("#p1: "  + producao1 + "  p2: "+ producao2);
			if(producao1.equals(TransacaoVazia) && producao2.equals(TransacaoVazia)){
				rmVazia = TransacaoVazia;
			}else{
				rmVazia = producao1 + producao2;
				rmVazia = rmVazia.replaceAll(TransacaoVazia, "");
				System.out.println("##Sem vazia: " + rmVazia);
			}

			return rmVazia;

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new Exception("Erro: nao foi possivel remover produção vazia");
		}

	}

	/*	Concatena produções
	 */

	public ArrayList<Producao> concatenaProducoes(ProducoesForParser linhaColuna, ProducoesForParser LinhaColunaAnt) throws Exception{

		try{

			ArrayList<Producao> concatenados = new ArrayList<Producao>();

			if(!(linhaColuna.getProducoes().isEmpty())  &&  !(LinhaColunaAnt.getProducoes().isEmpty())){//Se ninguem for vazio
				//Percorre lista de nao terminais da linhacoluna
				for(NaoTerminal ntLC : linhaColuna.getProducoes()){
					//Percorre lista de não terminais da Linha ColunaAnterior
					for(NaoTerminal ntLCAnt : LinhaColunaAnt.getProducoes()){

						Producao prodConcat = new Producao();

						String parse;
						//Concatena Nao terminais
						parse = concatenaTransacaoVazia(ntLC.getNaoTerminais(), ntLCAnt.getNaoTerminais());
						//Concatenação vira produção
						prodConcat.setProducao(parse);
						concatenados.add(prodConcat);
					}

				}
			}else
				//Se linhaColuna nao for vazio
				if(!(linhaColuna.getProducoes().isEmpty())){

					for(NaoTerminal ntLC : linhaColuna.getProducoes()){

						Producao prodConcat = new Producao(); //Nao terminais viram produções

						String parse;
						parse = concatenaTransacaoVazia(ntLC.getNaoTerminais(), TransacaoVazia);

						prodConcat.setProducao(parse);
						concatenados.add(prodConcat);

					}


				}else
					//Se linhaColunaAnterior não for vazio
					if(!(LinhaColunaAnt.getProducoes().isEmpty())){

						for(NaoTerminal ntLCAnt : LinhaColunaAnt.getProducoes()){
							Producao prodConcat = new Producao(); //Nao terminais viram produções

							String parse;

							parse = concatenaTransacaoVazia(ntLCAnt.getNaoTerminais(), TransacaoVazia);

							prodConcat.setProducao(parse);
							concatenados.add(prodConcat);
						}
					}

			return concatenados;

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new Exception("Erro: nao foi possivel concatenar");
		}

	}

	/**
	 * TODO: BUGFIX
	 * 	Melhorar Algoritmo 
	 * */
	//Remove Produções duplicadas
	public ArrayList<Producao> removeProducoesDuplicadas(ArrayList<Producao> producoes) throws Exception{

		try{
			ArrayList<Producao> prodSemDuplas = new ArrayList<Producao>();

			if(!(producoes.isEmpty())){
				for(Producao testA : producoes){
					//se nao for vazio
					if(!(prodSemDuplas.isEmpty())){

						Boolean achou = false; //Flag
						for(Producao semdupla : prodSemDuplas ){
							//Se encontrou elemento igual
							if(semdupla.getProducao().equals(testA.getProducao())){
								achou = true;
								break;
							}
						}
						if(!achou){
							prodSemDuplas.add(testA);
						}

					}else{
						prodSemDuplas.add(testA);
					}

				}
			}

			return prodSemDuplas;

		}catch (Exception e) {
			// TODO: handle exception
			throw new Exception("Erro: nao foi possivel remover duplicados");
		}

	}

	public ArrayList<NaoTerminal> removeNaoTerminaisDuplicados(ArrayList<NaoTerminal> nterminais) throws Exception{

		try{
			ArrayList<NaoTerminal> prodSemDuplas = new ArrayList<NaoTerminal>();

			if(!(nterminais.isEmpty())){
				for(NaoTerminal testA : nterminais){
					//se nao for vazio
					if(!(prodSemDuplas.isEmpty())){

						Boolean achou = false; //Flag
						for(NaoTerminal semdupla : prodSemDuplas ){
							//Se encontrou elemento igual
							if(semdupla.getNaoTerminais().equals(testA.getNaoTerminais())){
								achou = true;
								break;
							}
						}
						if(!achou){//Se nao encontrou elemento 
							prodSemDuplas.add(testA);
						}

					}else{
						prodSemDuplas.add(testA);
					}

				}
			}

			return prodSemDuplas;

		}catch (Exception e) {
			// TODO: handle exception
			throw new Exception("Erro: nao foi possivel remover duplicados");
		}


	}

	/*	Carrega dados do arquivo
	 * */
	private void loadEstadosFromFile(String filename){

		try {
			LoadFile lf = new LoadFile(filename);
			lf.carregarRecursos();

			this.estados = lf.getEstados();
			this.inicial = lf.getInicial();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}


	public void imprimeParaTeste(){

		try{

			System.out.println("Simbolo Inicial : "+ inicial.getProducao());

			for(int i=0; i<tamMatriz; i++){
				for(int j=0; j<tamMatriz; j++){

					if(matrizprocessamento[i][j] != null){

						System.out.println("M["+i+","+j+"] \n  {" ); //+ matrizprocessamento[i][j]

						for(NaoTerminal nt : matrizprocessamento[i][j].getProducoes()){
							System.out.println(nt.getNaoTerminais());
						}
						System.out.println("}" );
					}
				}
			}

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static void main(String[] args){
		try{

			ArrayList<String> testeEntrada = new ArrayList<String>();
			testeEntrada.add("a");
			testeEntrada.add("b");
			testeEntrada.add("a");
			testeEntrada.add("a");
			testeEntrada.add("b");


			CYK cyk = new CYK(testeEntrada);
			cyk.loadEstadosFromFile("entrada.txt");

			for(Estado et : cyk.getEstados()){
				System.out.println("Terminal: " + et.getEstado().getNaoTerminais());
				for(Producao pt : et.getProducoes()){
					System.out.println(" ->" + pt.getProducao());
				}
			}

			cyk.cykParser();

			cyk.imprimeParaTeste();

			if(cyk.cadeiaFoiAceita()){

				System.out.println("\n## Funfo");
			}else{

				System.out.println("\n## Nao Funfo");
			}


		}catch (Exception e) {
			// TODO: handle exception
		}
	}


	/**
	 * ========================================================================
	 * */


	public ProducoesForParser[][] getMatrizprocessamento() {
		return matrizprocessamento;
	}

	public int getTamMatriz() {
		return tamMatriz;
	}

	public Producao getInicial() {
		return inicial;
	}

	public ArrayList<String> getCadeiaEntrada() {
		return cadeiaEntrada;
	}

	public ArrayList<Estado> getEstados() {
		return estados;
	}

	public Boolean getCadeiaAceita() {
		return cadeiaAceita;
	}

	public void setInicial(Producao inicial) {
		this.inicial = inicial;
	}

	public void setEstados(ArrayList<Estado> estados) {
		this.estados = estados;
	}




}

package br.unioeste.cyk.parser;

import java.util.ArrayList;

import oracle.jrockit.jfr.tools.ConCatRepository;

import br.unioeste.exceptions.CYKParserCadeiaEntradaError;
import br.unioeste.grammar.NaoTerminal;
import br.unioeste.loaderfiles.LoadFile;

public class CYK {

	private ProducoesForParser[][] matrizprocessamento;

	private int tamMatriz; 

	private Producao inicial;

	private ArrayList<String> cadeiaEntrada; //Alfabeto de entrada para teste de aceitação

	private ArrayList<Estado> estados; //Estados possiveis e suas produções

	private Boolean cadeiaAceita; //Entrada reconhecida ou nao ?



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
	 * */

	public boolean cykParser(){

		try {
			inicializaEstadosMatriz();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Deu pau ao carregar matriz");
		}

		try{

			int diagonalPrincipal = (tamMatriz -2);

			int linhaatual;
			int coluna;
			int colunaAnt;
			
			for(;diagonalPrincipal >= 0; diagonalPrincipal --){
				
				linhaatual=diagonalPrincipal + 1; //Linha Anterior
				
				//Percorre colunas da linha da vez e processa a Roldana
				for(int davez = diagonalPrincipal ;davez > 0; davez--){
					
					
					coluna=davez; //Coluna da roldana
					colunaAnt = coluna +1;
					
					ArrayList<Producao> paraconcat = new ArrayList<Producao>();
					
					//Concatena
					paraconcat = concatenaProducoes(matrizprocessamento[linhaatual][coluna],
							matrizprocessamento[linhaatual][colunaAnt]);
					
					//Remove produções duplicadas
					paraconcat = removeProducoesDuplicadas(paraconcat);
					
					
					ProducoesForParser roldana = new ProducoesForParser();
					ArrayList<NaoTerminal> nTroldana = new ArrayList<NaoTerminal>();
					
					//quemgera as produções
					for(Producao np : paraconcat){
						ProducoesForParser aux = new ProducoesForParser();
						aux = queGera(np);
						nTroldana.addAll(aux.getProducoes());						
					}
					//Remove NaoTerminais duplicados
					nTroldana = removeNaoTerminaisDuplicados(nTroldana);
					roldana.setProducoes(nTroldana);
					
					//seta resultado da roldana	
					
					matrizprocessamento[(linhaatual - 1)][davez] = new ProducoesForParser();
					matrizprocessamento[(linhaatual - 1)][davez] = roldana;
					
				}	

			}
			//Lógica a ser implementada ainda
			if(matrizprocessamento[0][0].getProducoes() == inicial){
				return true;
			}else{
				return false;
			}

		}catch (Exception e) {
			// TODO: handle exception
		}


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

			ProducoesForParser pfp = new ProducoesForParser();
			pfp.setProducoes(regras);
			return pfp;

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new Exception("Erro: não foi possível encontrar geradores");
		}

	}

	/*	Concatena produções
	 */

	public ArrayList<Producao> concatenaProducoes(ProducoesForParser linhaColuna, ProducoesForParser LinhaColunaAnt) throws Exception{

		try{

			ArrayList<Producao> concatenados = new ArrayList<Producao>();

			if(!(linhaColuna.getProducoes().isEmpty())){//Se linha anterior nao for vazia
				if(!(LinhaColunaAnt.getProducoes().isEmpty())){//Se linha anterior e coluna anterior Nao for vazia

					for(NaoTerminal ntLC : linhaColuna.getProducoes()){
						for(NaoTerminal ntLCAnt : LinhaColunaAnt.getProducoes()){
							Producao prodConcat = new Producao(); //Nao terminais viram produções

							String parse = ntLC.getNaoTerminais() + ntLCAnt.getNaoTerminais();
							parse = parse.trim();

							prodConcat.setProducao(parse);
							concatenados.add(prodConcat);
						}
					}
				}else{//Se Linha coluna anterior vazia, somente processa linha anterior
					for(NaoTerminal ntLC : linhaColuna.getProducoes()){

						Producao prodConcat = new Producao(); //Nao terminais viram produções

						String parse = ntLC.getNaoTerminais();
						parse = parse.trim();

						prodConcat.setProducao(parse);
						concatenados.add(prodConcat);

					}
				}
			}else if(!(LinhaColunaAnt.getProducoes().isEmpty())){//Se Linha anterior vazia, somente processa linha Coluna anterior
				for(NaoTerminal ntLCAnt : LinhaColunaAnt.getProducoes()){
					Producao prodConcat = new Producao(); //Nao terminais viram produções

					String parse =ntLCAnt.getNaoTerminais();
					parse = parse.trim();

					prodConcat.setProducao(parse);
					concatenados.add(prodConcat);
				}
			}//Se todos vazias, retorna Array vazio!

			return concatenados;

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new Exception("Erro: nao foi possivel concatenar");
		}

	}

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
							if(semdupla.getProducao().equalsIgnoreCase(testA.getProducao())){
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
							if(semdupla.getNaoTerminais().equalsIgnoreCase(testA.getNaoTerminais())){
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
	public void loadEstadosFromFile(){

		try {
			LoadFile lf = new LoadFile("entrada.txt");
			lf.carregarRecursos();

			this.estados = lf.getEstados();
			this.inicial = lf.getInicial();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}


	public void imprimeParaTeste(){

		for(int i=0; i<tamMatriz; i++){
			for(int j=0; j<tamMatriz; j++){
				System.out.println("M["+i+","+j+"]" + matrizprocessamento[i][j]);
			}
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

			if(cyk.cykParser()){
				cyk.imprimeParaTeste();
			}

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

}

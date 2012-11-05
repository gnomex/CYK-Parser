package br.unioeste.loaderfiles;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import br.unioeste.cyk.parser.Estado;
import br.unioeste.cyk.parser.Producao;
import br.unioeste.grammar.NaoTerminal;
import br.unioeste.grammar.Terminal;

public class LoadFile {

	private String pathFilename; //Arquivo a ser lido


	private ArrayList<NaoTerminal> naoTerminais; //Não Terminais
	private ArrayList<Terminal> terminais; //Terminais

	private ArrayList<Estado> estados; //Estados com as transações

	private Producao inicial; //Estado inicial

	private BufferedReader arquivo;

	public LoadFile(String filepath) throws Exception{

		this.pathFilename = filepath;

		naoTerminais = new ArrayList<NaoTerminal>();
		terminais = new ArrayList<Terminal>();
		estados = new ArrayList<Estado>();	
		inicial = new Producao();
	}

	public void carregarRecursos() throws Exception{
		
		carregaNaoTerminais();
		carregaTerminais();
		montaEstadoComNTerminais();
		carregaProducoes();
		carregaInicial();
		
		
		
	}
	
	
	public void carregaTerminais() throws IOException{
		try{
			arquivo = new BufferedReader(new FileReader(pathFilename));
		}catch (FileNotFoundException e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new FileNotFoundException();
		}

		String linha ="";

		try{
			while((linha=arquivo.readLine()) !=null){
				//Se não for linha vazia
				if(linha.equals("Terminais")){

					//Carrega os simbolos terminais até encontrar as produções
					while((linha=arquivo.readLine()) !=null && !((linha.equals("Produções") || linha.equals("Producoes")))){
						//Se não for linha vazia
						if(!(linha.equals(""))){

							StringTokenizer st = new StringTokenizer(linha, ",");

							while(st.hasMoreTokens()){
								Terminal term = new Terminal();
								term.setTerminal(st.nextToken());
								terminais.add(term);
							}
						}
					}

					break;
				}
			}

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			arquivo.close();
		}

	}


	public void carregaProducoes() throws Exception{
		try{
			arquivo = new BufferedReader(new FileReader(pathFilename));
		}catch (FileNotFoundException e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new FileNotFoundException();
		}

		String linha ="";

		try{
			while((linha=arquivo.readLine()) !=null){
				//Se for a linha das Produçoes
				if((linha.equals("Produções")) || (linha.equals("Producoes"))){

					//Carrega as produções até encontrar o Inicial
					while((linha=arquivo.readLine()) !=null && !(linha.equals("Inicial"))){
						//Se não for linha vazia
						if(!(linha.equals(""))){
							String naoterminal;
							String prod;

							naoterminal = linha.substring(0, linha.indexOf("-")); //Pega simbolo inicial
							prod = linha.substring(linha.indexOf(">") + 1, linha.length()); //Pega produçoes
							
							System.out.println("NaoTerminal: " + naoterminal + " Producao: "+ prod);

							Producao pr = new Producao();
							pr.setProducao(prod);

							insereProducao(naoterminal, pr);

						}
					}
					break;
				}
			}

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		}finally{
			arquivo.close();
		}

	}

	/*	Carrega Simbolo Inicial
	 * 
	 * */
	public void carregaInicial() throws Exception{
		try{
			arquivo = new BufferedReader(new FileReader(pathFilename));
		}catch (FileNotFoundException e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new FileNotFoundException();
		}

		String linha ="";


		try{
			while((linha=arquivo.readLine()) !=null){
				//Se não for linha vazia
				if(linha.equals("Inicial")){
					linha = arquivo.readLine();
					Producao prodInicial = new Producao();
					prodInicial.setProducao(linha);
					inicial = prodInicial;
				}
			}

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new Exception("Erro ao carregar estado inicial");
		}finally{
			arquivo.close();
		}

	}

	/*	Carrega os simbolos Não Terminais
	 * 
	 * */

	public void carregaNaoTerminais() throws IOException{

		try{
			arquivo = new BufferedReader(new FileReader(pathFilename));

		}catch (FileNotFoundException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		String linha ="";

		try{
			while((linha=arquivo.readLine()) !=null){
				//Se não for linha vazia
				if(linha.equals("Variaveis") || linha.equals("Variáveis")){

					//Carrega os não terminais até encontrar a linha de inicio dos terminais
					while((linha=arquivo.readLine()) !=null && !(linha.equals("Terminais"))){
						//Se não for linha vazia
						if(!(linha.equals(""))){

							StringTokenizer st = new StringTokenizer(linha, ",");

							while(st.hasMoreTokens()){
								NaoTerminal nTerm = new NaoTerminal();
								nTerm.setNaoTerminais(st.nextToken());
								naoTerminais.add(nTerm);
							}
						}
					}

					break;
				}
			}

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			arquivo.close();
		}
	}

	//Inicializa os Estados com os terminais carregados
	public void montaEstadoComNTerminais(){
		try{
			
			for(NaoTerminal nt : naoTerminais){
				Estado nEstado = new Estado();
				nEstado.setEstado(nt);
				
				estados.add(nEstado);
			}
		
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void insereProducao(String naoTerminal, Producao producao){

		try{
			
			ArrayList<Estado> updEstados = new ArrayList<Estado>();
			
			for(Estado es : estados){
				if(es.getEstado().getNaoTerminais().equals(naoTerminal)){
					
					es.addProducao(producao);
				}
				updEstados.add(es);
			}
			estados.clear();
			estados = updEstados;
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

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
	public ArrayList<Estado> getEstados() {
		return estados;
	}
	public void setEstados(ArrayList<Estado> estados) {
		this.estados = estados;
	}
	public Producao getInicial() {
		return inicial;
	}
	public void setInicial(Producao inicial) {
		this.inicial = inicial;
	}

	public static void main(String[] args){
		try{
			
			LoadFile input = new LoadFile("entrada.txt");
			input.carregarRecursos();
			
			ArrayList<Estado> estados = new ArrayList<Estado>();
			estados = input.getEstados();
			
			System.out.println("\n##\n ");
			
			for(Estado es : estados){
				System.out.println("Estado: " + es.getEstado().getNaoTerminais());
				
				ArrayList<Producao> prds = new ArrayList<Producao>();
				prds = es.getProducoes();
				System.out.println("Produçoes: "+ prds.size());
				for(Producao prod : prds){
					System.out.println("  -> " + prod.getProducao());
				}
			}
			
			System.out.println("Simbolo Inicial: " + input.getInicial().getProducao());

		}catch (FileNotFoundException e) {
			// TODO: handle exception
			e.printStackTrace();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
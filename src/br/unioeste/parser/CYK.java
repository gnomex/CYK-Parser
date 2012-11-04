package br.unioeste.parser;

import java.util.ArrayList;

import br.unioeste.grammar.NaoTerminal;
import br.unioeste.grammar.Terminal;

public class CYK {
	
	private Producao[][] matrizprocessaento;

	private ArrayList<String> cadeiaEntrada; //Alfabeto de entrada para teste de aceitação

	private ArrayList<Estado> estados; //Estados possiveis e suas produções

	private Boolean cadeiaAceita; //Entrada reconhecida ou nao ?

	public CYK(){
		iniciarMatriz();
	}
	
	/*	Procura estado gerador das produções
	 * se existir, retorna lista de estado(s)
	 * se não existir, retorna lista vazia
	 * */
	public ArrayList<Terminal> queGera(ArrayList<Producao> producoes){

	}

	/*	Concatena produções
	 * */
	public ArrayList<Producao> concatenaProducoes(ArrayList<Producao> prodLinhaAnt, ArrayList<Producao> prodColunaAnt){
		

	}
	
	//Remove Produções duplicadas
	public ArrayList<Producao> removeProducoesDuplicadas(ArrayList<Producao> producoes){
		
	}
	
	/*	Inicializa a matriz para processamento do algoritmo
	 * Lança Exception
	 * */
	public void iniciarMatriz() throws Exception{
		
	}
	
	public void processar() throws Exception{
		
	}

}

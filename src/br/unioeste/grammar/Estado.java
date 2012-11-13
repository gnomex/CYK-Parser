package br.unioeste.grammar;

import java.util.ArrayList;


public class Estado {
	
	private NaoTerminal estado; //Nome do não-terminal
	private ArrayList<Producao> producoes;//Produções
	
	public Estado(){
		
		producoes = new ArrayList<Producao>();
		
	}

	
	public NaoTerminal getEstado() {
		return estado;
	}



	public void setEstado(NaoTerminal estado) {
		this.estado = estado;
	}



	public ArrayList<Producao> getProducoes() {
		return producoes;
	}

	public void setProducoes(ArrayList<Producao> producoes) {
		this.producoes = producoes;
	}
	
	
	public void addProducao(Producao prod) throws Exception{
		producoes.add(prod);
	}

}

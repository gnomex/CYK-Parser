package br.unioeste.cyk.parser;

import java.util.ArrayList;

import br.unioeste.grammar.NaoTerminal;

public class ProducoesForParser {
	
	private ArrayList<NaoTerminal> producoes = new ArrayList<NaoTerminal>();

	public ArrayList<NaoTerminal> getProducoes() {
		return producoes;
	}

	public void setProducoes(ArrayList<NaoTerminal> producoes) {
		this.producoes = producoes;
	}

	
}

package br.unioeste.fnc.parser;

public interface ParserListener {

	public void statusProcessamento( String status);
	
	public void entradaProcessamento( String entrada );
	
	public void resultadoProcessamento ( String resultado);
	
	
}

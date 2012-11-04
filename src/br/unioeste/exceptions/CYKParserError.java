package br.unioeste.exceptions;

public class CYKParserError extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CYKParserError(String mensagem){
		super(mensagem);
	}

}

package br.unioeste.loaderfiles;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import br.unioeste.grammar.NaoTerminal;
import br.unioeste.grammar.Terminal;
import br.unioeste.parser.Producao;

public class LoadFile {

	private String pathFilename; //Arquivo a ser lido
	BufferedReader arq;

	private ArrayList<NaoTerminal> naoTerminais; //Não Terminais
	private ArrayList<Terminal> terminais; //Terminais
	private ArrayList<Producao> producoes; //produções, sem tratamento
	private Producao inicial; //Estado inicial


	public LoadFile(String _file) throws FileNotFoundException{
		this.pathFilename = _file;
		
		arq = new BufferedReader(new FileReader(pathFilename));
	}


	public void carregaNaoTerminais() throws Exception{
		String aux = null;
		
		try{
			
			while((aux=arq.readLine()) != null ){
				

			}
		}catch (Exception e) {
			// TODO: handle exception
			throw new Exception("Erro ao tentar carregar arquivo");
		}
	}

	public void carregaTerminais() throws FileNotFoundException{

	}

	public void carregaProducoes() throws FileNotFoundException{

	}

	public void carregaEstadoInicial() throws FileNotFoundException{

	}

}
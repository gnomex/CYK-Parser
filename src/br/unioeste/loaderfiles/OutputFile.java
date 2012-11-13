package br.unioeste.loaderfiles;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class OutputFile {

	public static void gravaArquivoResultados(List<String> resultados, String filename , Boolean rw) {

		FileWriter writer = null;
		PrintWriter saida = null;
		try {
			writer = new FileWriter(new File(filename),rw);
			saida = new PrintWriter(writer,rw); 
			
			System.out.println("Iniciando gravação do arquivo em: " + filename);

			for(String res : resultados){
				saida.println(res);
			}
			
			System.out.println("Arquivo gravado");

			if(writer != null){
				saida.close();
				writer.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args){
		try{
			ArrayList<String> teste  = new ArrayList<String>();
			
			teste.add("a");
			teste.add("b");
			teste.add("a");
			teste.add("b");
			teste.add("a");
			teste.add("b");
			teste.add("a");
			teste.add("b");
			teste.add("a");
			teste.add("b");
			
			OutputFile.gravaArquivoResultados(teste, "saida.txt" , true);
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	

}

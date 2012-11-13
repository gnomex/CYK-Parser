package br.unioeste.cyk.GUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JList;

import br.unioeste.common.ProducoesForParser;
import br.unioeste.cyk.parser.CYK;
import br.unioeste.grammar.Estado;
import br.unioeste.grammar.NaoTerminal;
import br.unioeste.grammar.Producao;
import br.unioeste.loaderfiles.LoadFile;
import br.unioeste.loaderfiles.OutputFile;

public class CYKParserGUI extends JFrame implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	private JButton btnCarregarArquivo;
	private JButton btnSalvar;
	private JButton btnExecutarTeste;
	private JButton btnInserirCadeiaDe;
	private JButton btnLimpar;

	private JLabel lblGramtica;
	private JLabel lblProcessamento;

	private DefaultListModel<String> listProcessamento;
	private DefaultListModel<String> listGramatica;

	private String currentFile;

	private ArrayList<String> cadeiaEntrada = new ArrayList<String>();

	private LoadFile lf;
	private CYK cyk;



	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CYKParserGUI frame = new CYKParserGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public CYKParserGUI() {

		super("CYK Parser");

		try {	/**Pegar variaveis de ambiente*/
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
		} catch (Exception e) {
			System.out.println("Erro ao obter variaveis de ambiente");
			e.printStackTrace();
		}

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 893, 670);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panelGramatica = new JPanel();
		panelGramatica.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		panelGramatica.setBounds(12, 12, 413, 545);
		contentPane.add(panelGramatica);
		panelGramatica.setLayout(null);

		lblGramtica = new JLabel("Gramática");
		lblGramtica.setBounds(12, 12, 389, 15);
		panelGramatica.add(lblGramtica);

		listGramatica = new DefaultListModel<String>(); 

		JList<String> list_1 = new JList<String>(listGramatica);
		list_1.setBounds(22, 39, 379, 494);
		panelGramatica.add(list_1);

		list_1.setSelectionBackground(Color.ORANGE);

		JScrollPane scrollPane = new JScrollPane(list_1);
		scrollPane.setBounds(22, 38, 379, 495);
		panelGramatica.add(scrollPane);

		JPanel panelBotoes = new JPanel();
		panelBotoes.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		panelBotoes.setBounds(12, 569, 865, 61);
		contentPane.add(panelBotoes);
		panelBotoes.setLayout(null);

		btnCarregarArquivo = new JButton("Carregar Arquivo");
		btnCarregarArquivo.setBounds(12, 12, 154, 37);
		panelBotoes.add(btnCarregarArquivo);
		btnCarregarArquivo.addActionListener(this);

		btnSalvar = new JButton("Salvar");
		btnSalvar.setBounds(178, 12, 142, 37);
		panelBotoes.add(btnSalvar);
		btnSalvar.setEnabled(false);
		btnSalvar.addActionListener(this);

		btnExecutarTeste = new JButton("Executar Teste");
		btnExecutarTeste.setBounds(699, 12, 154, 37);
		panelBotoes.add(btnExecutarTeste);
		btnExecutarTeste.setEnabled(false);
		btnExecutarTeste.addActionListener(this);

		btnInserirCadeiaDe = new JButton("Inserir Cadeia de Teste");
		btnInserirCadeiaDe.setBounds(332, 12, 208, 37);
		btnInserirCadeiaDe.setEnabled(false);
		panelBotoes.add(btnInserirCadeiaDe);
		btnInserirCadeiaDe.addActionListener(this);

		btnLimpar = new JButton("LIMPAR !");
		btnLimpar.setBounds(552, 12, 135, 37);
		panelBotoes.add(btnLimpar);
		btnLimpar.setEnabled(false);
		btnLimpar.addActionListener(this);

		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		panel.setBounds(437, 12, 440, 545);
		contentPane.add(panel);
		panel.setLayout(null);

		lblProcessamento = new JLabel("Processamento");
		lblProcessamento.setBounds(12, 12, 416, 15);
		panel.add(lblProcessamento);

		listProcessamento = new DefaultListModel<String>();

		JList<String> list = new JList<String>(listProcessamento);
		list.setVisibleRowCount(16);
		list.setBounds(22, 38, 406, 495);
		panel.add(list);

		list.setSelectionBackground(Color.ORANGE);

		JScrollPane scrollPane_1 = new JScrollPane(list);
		scrollPane_1.setBounds(22, 39, 406, 494);
		panel.add(scrollPane_1);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		//botao sair
		if(obj == btnCarregarArquivo){

			JFileChooser arquivoEntrada = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
			arquivoEntrada.setFileFilter(filter);
			arquivoEntrada.addChoosableFileFilter(filter);

			int res = arquivoEntrada.showOpenDialog(null);
			if (res == JFileChooser.APPROVE_OPTION) {
				File dir = arquivoEntrada.getSelectedFile();
				currentFile = dir.getPath();

				System.out.println("File: " + currentFile);

				carregaArquivo();

				btnExecutarTeste.setEnabled(true);
				btnLimpar.setEnabled(true);
				btnInserirCadeiaDe.setEnabled(true);


			}else if(res == JFileChooser.CANCEL_OPTION){
				System.out.println("Cancelado carregamento de arquivo");
			}
		}

		if(obj == btnSalvar){

			try{

				OutputFile.gravaArquivoResultados(this.resultadosToList(), "saida.txt" , true);

				JOptionPane.showMessageDialog(CYKParserGUI.this, "Arquivo gravado com sucesso !\n\nArquivo: saida.txt");

				limparRecursos();

			}catch (Exception e5) {
				// TODO: handle exception
				e5.printStackTrace();
			}
		}

		if(obj == btnInserirCadeiaDe){
			try{
				String entrada;
				entrada = JOptionPane.showInputDialog(CYKParserGUI.this, "Informe a entrada de teste");

				entrada = entrada.toLowerCase();	//Tudo para minusculo
				entrada = entrada.replaceAll(" ", ""); //Remove espaços em branco

				if(!cadeiaEntrada.isEmpty()){
					cadeiaEntrada.clear();
				}

				String[] castEntrada;
				castEntrada = entrada.split("");

				for(String str : castEntrada){
					if(!str.trim().isEmpty()){
						cadeiaEntrada.add(str);
					}
				}
				listGramatica.addElement("Cadeia de Entrada: " + entrada);

			}catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}

		}
		if(obj == btnExecutarTeste){

			try{

				inicializaCYK();
				btnSalvar.setEnabled(true);

			}catch (Exception e3) {
				// TODO: handle exception
				e3.printStackTrace();
				JOptionPane.showMessageDialog(CYKParserGUI.this, "Algoritmo não pode ser executado");
			}

		}
		if(obj == btnLimpar){


			limparRecursos();

		}
	}

	public void limparRecursos(){
		try{

			System.out.println("Limpando recursos ...");

			this.listProcessamento.removeAllElements();
			this.listGramatica.removeAllElements();
			cadeiaEntrada.clear();

			btnSalvar.setEnabled(false);
			btnExecutarTeste.setEnabled(false);
			btnLimpar.setEnabled(false);
			btnInserirCadeiaDe.setEnabled(false);

			System.out.println("Ok !");

		}catch (Exception e) {
			// TODO: handle exception
		}
	}


	public void inicializaCYK() throws Exception{

		try{

			if((lf != null) && !(cadeiaEntrada.isEmpty()) ){

				cyk = new CYK(cadeiaEntrada);
				cyk.setEstados(lf.getEstados());
				cyk.setInicial(lf.getInicial());
				cyk.cykParser();

				imprimeProcessamento();

			}else{
				throw new Exception("Erro: Arquivo entrada inválido");
			}

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	public void imprimeProcessamento(){
		try{
			int tamMatriz = cyk.getTamMatriz();
			ProducoesForParser[][] matriz = new ProducoesForParser[tamMatriz][tamMatriz];
			matriz = cyk.getMatrizprocessamento();

			listProcessamento.addElement("Linha : Produçoes por coluna");
			for(int i=0; i<tamMatriz; i++){
				String linha = i + ": { ";
				for(int j=0; j<tamMatriz; j++){
					if(matriz[i][j] != null){
						String coluna ="";
						for(NaoTerminal nt : matriz[i][j].getProducoes()){
							coluna += nt.getNaoTerminais() + " , ";
						}
						linha += j+ ": { " + coluna +"}";
					}
				}
				linha += "}";
				listProcessamento.addElement(linha);
			}

			listProcessamento.addElement("Cadeia Aceita ?");
			//listProcessamento.addElement(" " + cyk.cadeiaFoiAceita());

			if(cyk.cadeiaFoiAceita()){
				listProcessamento.addElement("Sim - Cadeia foi aceita!");
			}else{
				listProcessamento.addElement("Não - Cadeia não foi aceita!");
			}

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void carregaArquivo(){

		try {
			if(!currentFile.isEmpty()){
				lf = new LoadFile(currentFile);
				lf.carregarRecursos();

				listGramatica.addElement("Simbolo Inicial: " + lf.getInicial().getProducao());

				for(Estado est : lf.getEstados()){
					listGramatica.addElement(est.getEstado().getNaoTerminais());

					for(Producao pd : est.getProducoes()){
						listGramatica.addElement(" -> " + pd.getProducao());
					}
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<String> resultadosToList() throws Exception{

		try{

			ArrayList<String> resultados = new ArrayList<String>();

			for(int i=0; i<listProcessamento.getSize(); i++){
				resultados.add(listProcessamento.getElementAt(i));
			}

			return resultados;

		}catch (Exception e) {
			// TODO: handle exception
			throw new Exception("Erro: nao foi possivel pegar Resultados");
		}

	}

}

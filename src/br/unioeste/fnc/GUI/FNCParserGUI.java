package br.unioeste.fnc.GUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import br.unioeste.fnc.exceptions.GrammarNotFNCFormat;
import br.unioeste.fnc.parser.FNC;
import br.unioeste.fnc.parser.ParserListener;
import javax.swing.JTabbedPane;
import java.awt.Color;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

public class FNCParserGUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;

	private JTextArea textAreaEntrada;
	private JTextArea textAreaSaida;

	private JTextArea textAreaProcessamento;

	private ParserListener processamentoListener;

	private JFileChooser fileChooser = new JFileChooser();

	private FNC fnc;

	private JButton btnCarregarArquivo;
	private JButton btnSalvarResutado;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FNCParserGUI frame = new FNCParserGUI();
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
	public FNCParserGUI() {

		super("FNC Parser");

		try {	/**Pegar variaveis de ambiente*/
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
		} catch (Exception e) {
			System.out.println("Erro ao obter variaveis de ambiente");
		}

		processamentoListener = new Processamento();

		fnc = new FNC(processamentoListener);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 786, 601);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(12, 12, 758, 54);
		contentPane.add(panel);
		panel.setLayout(null);

		btnCarregarArquivo = new JButton("Carregar Arquivo");
		btnCarregarArquivo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

				int res = fileChooser.showOpenDialog(null);
				if (res == JFileChooser.APPROVE_OPTION) {
					File dir = fileChooser.getSelectedFile();
					String currentFile = dir.getPath();

					fnc.loadEstadosFromFile(currentFile);

				}
				btnCarregarArquivo.setEnabled(false);
			}
		});
		
		btnCarregarArquivo.setBounds(12, 12, 154, 25);
		panel.add(btnCarregarArquivo);

		JButton btnIniciarParser = new JButton("Iniciar Parser");
		btnIniciarParser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				try {
					fnc.variaveisLadoDireito();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					fnc.somente2VariaveisLadoDireito();
				} catch (GrammarNotFNCFormat e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					JOptionPane.showConfirmDialog(FNCParserGUI.this, "A gramática não foi simplificada ou possui regras válidas!");
				}catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}
				btnSalvarResutado.setEnabled(true);
			}
		});
		btnIniciarParser.setBounds(592, 12, 154, 25);
		panel.add(btnIniciarParser);

		btnSalvarResutado = new JButton("Salvar Resutado");
		btnSalvarResutado.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){
					fnc.saveProcessamentoToFile(fileChooser.getSelectedFile().getPath());
				}
				
			}
		});
		btnSalvarResutado.setBounds(190, 12, 162, 25);
		panel.add(btnSalvarResutado);
		
		btnSalvarResutado.setEnabled(false);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(12, 78, 758, 483);
		contentPane.add(tabbedPane);

		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Resultado", null, panel_1, null);
		panel_1.setLayout(null);

		textAreaEntrada = new JTextArea();
		textAreaEntrada.setBounds(1, 1, 360, 443);
		panel_1.add(textAreaEntrada);
		textAreaEntrada.setEditable(false);

		textAreaSaida = new JTextArea();
		textAreaSaida.setBounds(377, 27, 364, 365);
		panel_1.add(textAreaSaida);
		textAreaSaida.setEditable(false);

		JLabel lblEntrada = new JLabel("Entrada");
		lblEntrada.setBounds(12, 0, 122, 27);
		panel_1.add(lblEntrada);

		JLabel lblSaida = new JLabel("Saida");
		lblSaida.setBounds(377, 6, 87, 15);
		panel_1.add(lblSaida);

		JScrollPane scrollPane = new JScrollPane(textAreaEntrada);
		scrollPane.setBounds(12, 27, 353, 417);
		panel_1.add(scrollPane);

		JScrollPane scrollPane_1 = new JScrollPane(textAreaSaida);
		scrollPane_1.setBounds(377, 24, 364, 420);
		panel_1.add(scrollPane_1);

		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Processamento", null, panel_2, null);
		panel_2.setLayout(null);

		textAreaProcessamento = new JTextArea();
		textAreaProcessamento.setBounds(12, 29, 729, 363);
		panel_2.add(textAreaProcessamento);
		textAreaProcessamento.setEditable(false);

		JLabel lblProcessamentoDoParser = new JLabel("Processamento do Parser");
		lblProcessamentoDoParser.setBounds(12, 12, 270, 15);
		panel_2.add(lblProcessamentoDoParser);

		JScrollPane scrollPane_2 = new JScrollPane(textAreaProcessamento);
		scrollPane_2.setBounds(12, 24, 729, 368);
		panel_2.add(scrollPane_2);
	}

	private class Processamento implements ParserListener{

		@Override
		public void statusProcessamento(String status) {

			textAreaProcessamento.append("\n" + status);

		}

		@Override
		public void entradaProcessamento(String entrada) {

			textAreaEntrada.append("\n" + entrada);

		}

		@Override
		public void resultadoProcessamento(String resultado) {

			textAreaSaida.append("\n" + resultado);

		}



	}
}

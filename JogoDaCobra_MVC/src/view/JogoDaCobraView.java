package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import controller.JogoDaCobraController;
import model.JogoDaCobraModel;
import utils.JogoDaCobraUtils;
import utils.JogoDaCobraUtils.Bloco;

public class JogoDaCobraView extends JPanel {
	private JogoDaCobraController controller;
	private JogoDaCobraModel model;
	
	
	private  JButton botaoJogar;//Botão na tela inicial que inicia o jogo.
	private  JButton botaoReset;//Botão verde que reinicia o jogo. 
	private  JButton botaoSair;//Botão vermelho que encerra o jogo e retorna a tela inicial.
	private  JButton botaoExibirPlacares;//Botão que mostra o histórico de arquivos (.txt) com o resultado final do jogo.
	
	private  JLabel tituloDoJogo;//Título do jogo.
	
	public JogoDaCobraView(int larguraTela, int alturaTela) {
		this.larguraTela = larguraTela;
		this.alturaTela = alturaTela;
		setPreferredSize(new Dimension(this.larguraTela, this.alturaTela));
		setBackground(Color.black);
		addKeyListener(this);
		setFocusable(true);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!controller.isInicioDoJogo()) {
			return;
		}
		desenhar(g);
	}
	
	public void desenhar(Graphics g) {
		//Desenho das linhas do tabuleiro.
		for (int i = 0; i < larguraTela / model.getTamanhoBloco(); i++) {
			g.drawLine(i*model.getTamanhoBloco(), 0, i*model.getTamanhoBloco(), alturaTela);//Desenho da linha vertical.
			g.drawLine(0, i*model.getTamanhoBloco(), larguraTela, i*model.getTamanhoBloco());//Desenho da linha horizontal.
		}
		
		//Comida.
		g.setColor(Color.red);
		g.fill3DRect(comida.x*model.getTamanhoBloco(), comida.y*model.getTamanhoBloco(), model.getTamanhoBloco(), model.getTamanhoBloco(), true);
		
		//Cabeça da cobra.
		g.setColor(Color.green);
		g.fill3DRect(model.getCabecaCobra().x*model.getTamanhoBloco(), model.getCabecaCobra().y*model.getTamanhoBloco(), model.getTamanhoBloco(), model.getTamanhoBloco(), true);
		
		//Corpo da cobra.
		g.setColor(new Color(46, 125, 50));
		for (int i = 0; i < model.getCorpoCobra().size(); i++) {
			Bloco parteCobra = model.getCorpoCobra().get(i);
			g.fill3DRect(parteCobra.x*model.getTamanhoBloco(), parteCobra.y*model.getTamanhoBloco(), model.getTamanhoBloco(), model.getTamanhoBloco(), true);
		}
		
		//Placar.
		g.setFont(new Font("Poppins", Font.PLAIN, 16));
		if (controller.isFimDeJogo()) {
			g.setColor(Color.red);
			g.drawString("Fim de Jogo: " + valorComida + " pontos.", model.getTamanhoBloco() - 16, model.getTamanhoBloco());
			botaoResetarJogo(g);
			botaoSairDoJogo(g);
			botaoExibirHistoricoPlacares(g);
		} else {
			g.drawString("Placar: " + valorComida, model.getTamanhoBloco() - 16, model.getTamanhoBloco());
		}
	}
	
	public void botaoResetarJogo(Graphics g) {
		botaoReset = new JButton("Tentar de Novo");
		botaoReset.setBounds(15, 50, 160, 25);
		botaoReset.setFont(new Font("Poppins", Font.BOLD, 16));
		botaoReset.setForeground(Color.black);
		botaoReset.setBackground(Color.green);
		botaoReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.resetarJogo();
			}
		});
		setLayout(null);
		add(botaoReset);
	}
	
	public void botaoSairDoJogo(Graphics g) {
		botaoSair = new JButton("Sair do Jogo");
		botaoSair.setBounds(15, 90, 160, 25);
		botaoSair.setFont(new Font("Poppins", Font.BOLD, 16));
		botaoSair.setForeground(Color.white);
		botaoSair.setBackground(Color.red);
		botaoSair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.sairJogo(g);
			}
		});
		setLayout(null);
		add(botaoSair);
	}
	
	public void telaInicial(Graphics g) {
		botaoJogar = new JButton("Jogar");
		botaoJogar.setBounds(larguraTela / 2 - 100, alturaTela / 2, 210, 50);
		botaoJogar.setFont(new Font("Poppins", Font.BOLD, 28));
		botaoJogar.setForeground(Color.black);
		botaoJogar.setBackground(Color.green);
		botaoJogar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.iniciarJogo();
			}
		});
		setLayout(null);
		add(botaoJogar);
		
		//Título na tela inicial.
		tituloDoJogo = new JLabel("Jogo da Cobrinha");
		tituloDoJogo.setBounds(larguraTela / 3, alturaTela / 2 - 100, 500, 55);
		tituloDoJogo.setFont(new Font("Arial", Font.BOLD, 48));
		tituloDoJogo.setForeground(Color.green);
		setLayout(null);
		add(tituloDoJogo);
	}
	
	public void botaoExibirHistoricoPlacares(Graphics g) {
		botaoExibirPlacares = new JButton("Exibir Placares");
		botaoExibirPlacares.setBounds(15, 130, 160, 25);
		botaoExibirPlacares.setFont(new Font("Poppins", Font.BOLD, 16));
		botaoExibirPlacares.setForeground(new Color(37, 90, 255));
		botaoExibirPlacares.setBackground(new Color(243, 127, 25));
		botaoExibirPlacares.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.exibirPlacar();
			}
		});
		setLayout(null);
		add(botaoExibirPlacares);
	}
	
	public JButton getBotaoJogar() {
		return botaoJogar;
	}

	public void setBotaoJogar(JButton botaoJogar) {
		this.botaoJogar = botaoJogar;
	}

	public JButton getBotaoReset() {
		return botaoReset;
	}

	public void setBotaoReset(JButton botaoReset) {
		this.botaoReset = botaoReset;
	}

	public JButton getBotaoSair() {
		return botaoSair;
	}

	public void setBotaoSair(JButton botaoSair) {
		this.botaoSair = botaoSair;
	}

	public JButton getBotaoExibirPlacares() {
		return botaoExibirPlacares;
	}

	public void setBotaoExibirPlacares(JButton botaoExibirPlacares) {
		this.botaoExibirPlacares = botaoExibirPlacares;
	}

	public JLabel getTituloDoJogo() {
		return tituloDoJogo;
	}

	public void setTituloDoJogo(JLabel tituloDoJogo) {
		this.tituloDoJogo = tituloDoJogo;
	}
}

package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import controller.JogoDaCobraController;
import model.Bloco;

public class JogoDaCobraView extends JPanel implements ActionListener, KeyListener {
	private JogoDaCobraController controller;

	private JButton botaoJogar;//Botão na tela inicial que inicia o jogo.
	private JButton botaoReset;//Botão verde que reinicia o jogo. 
	private JButton botaoSair;//Botão vermelho que encerra o jogo e retorna a tela inicial.
	private JButton botaoExibirPlacares;//Botão que mostra o histórico de arquivos (.txt) com o resultado final do jogo.
	
	private JLabel tituloDoJogo;//Título do jogo.
	
	public JogoDaCobraView(JogoDaCobraController controller) {
		this.controller = controller;
		
		setPreferredSize(new Dimension(controller.getLarguraTela(), controller.getAlturaTela()));
		setBackground(Color.black);
		addKeyListener(controller);
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
		for (int i = 0; i < controller.getLarguraTela() / controller.getTamanhoBloco(); i++) {
			g.drawLine(i*controller.getTamanhoBloco(), 0, i*controller.getTamanhoBloco(), controller.getLarguraTela());//Desenho da linha vertical.
			g.drawLine(0, i*controller.getTamanhoBloco(), controller.getLarguraTela(), i*controller.getTamanhoBloco());//Desenho da linha horizontal.
		}
		
		//Comida.
		g.setColor(Color.red);
		g.fill3DRect(controller.getComida().x*controller.getTamanhoBloco(), controller.getComida().y*controller.getTamanhoBloco(), controller.getTamanhoBloco(), controller.getTamanhoBloco(), true);
		
		//Cabeça da cobra.
		g.setColor(Color.green);
		g.fill3DRect(controller.getCabecaCobra().x*controller.getTamanhoBloco(), controller.getCabecaCobra().y*controller.getTamanhoBloco(), controller.getTamanhoBloco(), controller.getTamanhoBloco(), true);
		
		//Corpo da cobra.
		g.setColor(new Color(46, 125, 50));
		for (int i = 0; i < controller.getCorpoCobra().size(); i++) {
			Bloco parteCobra = controller.getCorpoCobra().get(i);
			g.fill3DRect(parteCobra.x*controller.getTamanhoBloco(), parteCobra.y*controller.getTamanhoBloco(), controller.getTamanhoBloco(), controller.getTamanhoBloco(), true);
		}
		
		//Placar.
		g.setFont(new Font("Poppins", Font.PLAIN, 16));
		if (controller.isFimDeJogo()) {
			g.setColor(Color.red);
			g.drawString("Fim de Jogo: " + controller.getValorComida() + " pontos.", controller.getTamanhoBloco() - 16, controller.getTamanhoBloco());
			botaoResetarJogo(g);
			botaoSairDoJogo(g);
			botaoExibirHistoricoPlacares(g);
		} else {
			g.drawString("Placar: " + controller.getValorComida(), controller.getTamanhoBloco() - 16, controller.getTamanhoBloco());
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
				controller.resetarJogo();
				botaoReset.setVisible(false);
				botaoSair.setVisible(false);
				botaoExibirPlacares.setVisible(false);
				repaint();
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
				controller.sairJogo(g);
				botaoReset.setVisible(false);
				botaoSair.setVisible(false);
				botaoExibirPlacares.setVisible(false);
				repaint();
				telaInicial(g);
			}
		});
		setLayout(null);
		add(botaoSair);
	}
	
	public void telaInicial(Graphics g) {
		botaoJogar = new JButton("Jogar");
		botaoJogar.setBounds(controller.getAlturaTela() / 2 + 135, controller.getAlturaTela() / 2, 210, 50);
		botaoJogar.setFont(new Font("Poppins", Font.BOLD, 28));
		botaoJogar.setForeground(Color.black);
		botaoJogar.setBackground(Color.green);
		botaoJogar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.iniciarJogo();
				botaoJogar.setVisible(false);
				tituloDoJogo.setVisible(false);
				repaint();
			}
		});
		setLayout(null);
		add(botaoJogar);
		
		//Título na tela inicial.
		tituloDoJogo = new JLabel("Jogo da Cobrinha");
		tituloDoJogo.setBounds(controller.getLarguraTela() / 3, controller.getAlturaTela() / 2 - 100, 500, 55);
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
	
	@Override
	public void actionPerformed(ActionEvent e) {
		controller.realizarAcaoDoJogo();
		repaint();
		
		if (e.getSource() == getBotaoExibirPlacares()) {
			botaoExibirHistoricoPlacares(null);
		}
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

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}

package projeto_poo_java;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import javax.swing.*;

public class JogoDaCobra extends JPanel implements ActionListener, KeyListener {
	private class Bloco {
		int x;
		int y;
		
		Bloco(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
	
	int larguraTela;
	int alturaTela;
	int tamanhoBloco = 30;
	
	//cobra
	Bloco cabecaCobra;
	ArrayList<Bloco> corpoCobra;
	
	//comida
	Bloco comida;
	Random random;
	int valorComida = 0;
	
	//logica do jogo
	int direcaoX;
	int direcaoY;
	Timer loopJogo;
	
	boolean fimDeJogo = false;//Variável qeu controla se o jogo está em andamento ou se o jogo terminou (o jogador perdeu).
	boolean inicioDoJogo = false;//Variável que controla o estado inicial do jogo e a transição para o jogo em si.
	boolean jogoRodando = false;//Variável que controla se o jogo está em execução ou pausado.
	
	JButton botaoJogar;//Botão na tela inicial que inicia o jogo.
	JButton botaoReset;//Botão verde que reinicia o jogo. 
	JButton botaoSair;//Botão vermelho que encerra o jogo e retorna a tela inicial.
	JButton botaoExibirPlacares;//Botão que mostra o histórico de arquivos (.txt) com o resultado final do jogo.
	
	JLabel tituloDoJogo;//Título do jogo.
	
	JogoDaCobra (int larguraTela, int alturaTela) {
		this.larguraTela = larguraTela;
		this.alturaTela = alturaTela;
		setPreferredSize(new Dimension(this.larguraTela, this.alturaTela));
		setBackground(Color.black);
		addKeyListener(this);
		setFocusable(true);
		
		cabecaCobra = new Bloco(10, 10);
		corpoCobra = new ArrayList<Bloco>();
		
		comida = new Bloco(10, 10);
		random = new Random();
		posicaoComida();
		
		direcaoX = 1;
		direcaoY = 0;
		
		//Tempo do jogo
		loopJogo = new Timer(100, this);//Tempo em milissegundos que a cabeça da cobra muda de quadro.
		loopJogo.start();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!inicioDoJogo) {
			return;
		}
		desenhar(g);
	}
	
	public void desenhar(Graphics g) {
		//Desenho das linhas do tabuleiro.
		for (int i = 0; i < larguraTela / tamanhoBloco; i++) {
			g.drawLine(i*tamanhoBloco, 0, i*tamanhoBloco, alturaTela);
			g.drawLine(0, i*tamanhoBloco, larguraTela, i*tamanhoBloco);
		}
		
		//Comida.
		g.setColor(Color.red);
		g.fill3DRect(comida.x*tamanhoBloco, comida.y*tamanhoBloco, tamanhoBloco, tamanhoBloco, true);
		
		//Cabeça da cobra.
		g.setColor(Color.green);
		g.fill3DRect(cabecaCobra.x*tamanhoBloco, cabecaCobra.y*tamanhoBloco, tamanhoBloco, tamanhoBloco, true);
		
		//Corpo da cobra.
		g.setColor(new Color(46, 125, 50));
		for (int i = 0; i < corpoCobra.size(); i++) {
			Bloco parteCobra = corpoCobra.get(i);
			g.fill3DRect(parteCobra.x*tamanhoBloco, parteCobra.y*tamanhoBloco, tamanhoBloco, tamanhoBloco, true);
		}
		
		//Placar.
		g.setFont(new Font("Poppins", Font.PLAIN, 16));
		if (fimDeJogo) {
			g.setColor(Color.red);
			g.drawString("Fim de Jogo: " + valorComida + " pontos.", tamanhoBloco - 16, tamanhoBloco);
			botaoResetarJogo(g);
			botaoSairDoJogo(g);
			botaoExibirHistoricoPlacares(g);
		} else {
			g.drawString("Placar: " + valorComida, tamanhoBloco - 16, tamanhoBloco);
		}
	}
	
	public void posicaoComida() {
		comida.x = random.nextInt(larguraTela / tamanhoBloco);
		comida.y = random.nextInt(alturaTela / tamanhoBloco);
	}
	
	public void mover() {
		//Comer a comida.
		if (colisao(cabecaCobra, comida)) {
			corpoCobra.add(new Bloco(comida.x, comida.y));
			valorComida += 10;
			//Lógica para aumentar a velocidade de percurso da cobra.
			if (valorComida >= 100) {
				loopJogo.stop();
				int novoLoopJogo = loopJogo.getDelay() - 2;
				loopJogo = new Timer(novoLoopJogo, this);
				loopJogo.start();
			}
			posicaoComida();
		}
		
		//Movimentação do corpo da cobra.
		for (int i = corpoCobra.size() - 1; i >= 0; i--) {
			Bloco parteCobra = corpoCobra.get(i);
			//Cada parte do corpo é movida para a posição da parte anterior.
			if (i == 0) {//antes da cabeça
				parteCobra.x = cabecaCobra.x;
				parteCobra.y = cabecaCobra.y;
			} else {
				Bloco anteriorParteCobra = corpoCobra.get(i - 1);
				parteCobra.x = anteriorParteCobra.x;
				parteCobra.y = anteriorParteCobra.y;
			}
		}
		
		//Movimentação da cabeça da cobra.
		cabecaCobra.x += direcaoX;
		cabecaCobra.y += direcaoY;
		
		//Condições de fim de jogo.
		for (int i = 0; i < corpoCobra.size(); i++) {
			Bloco parteCobra = corpoCobra.get(i);
			
			//Colisão => cabeça + corpo
			if (colisao(cabecaCobra, parteCobra)) {
				fimDeJogo = true;
				resetarVelocidade();
				salvarPlacar(valorComida);
			}
		}
		
		if (cabecaCobra.x*tamanhoBloco < 0 || cabecaCobra.x*tamanhoBloco >= larguraTela || //Cabeça passou da borda da esquerda ou direita
			cabecaCobra.y*tamanhoBloco < 0 || cabecaCobra.y*tamanhoBloco >= alturaTela ) { // Cabeça passou da borda de cima ou embaixo
			fimDeJogo = true;
			resetarVelocidade();
			salvarPlacar(valorComida);
		}
	}
	
	public boolean colisao(Bloco bloco1, Bloco bloco2) {
		return bloco1.x == bloco2.x && bloco1.y == bloco2.y;
	}
	
	//Método criado para cancelar a aceleração da cobra e retornar a sua velocidade normal caso o jogo reinicie.
	public void resetarVelocidade() {
		loopJogo.stop();
		loopJogo = new Timer(100, this);
		loopJogo.start();
	}
	
	public void botaoResetarJogo(Graphics g) {
		botaoReset = new JButton("Tentar de Novo");
		botaoReset.setBounds(15, 50, 160, 25);
		botaoReset.setFont(new Font("Poppins", Font.BOLD, 16));
		botaoReset.setForeground(Color.black);
		botaoReset.setBackground(Color.green);
		botaoReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetarJogo();
			}
		});
		setLayout(null);
		add(botaoReset);
	}
	
	public void resetarJogo() {
		cabecaCobra = new Bloco(10, 10);
		corpoCobra.clear();
		valorComida = 0;
		direcaoX = 1;
		direcaoY = 0;
		fimDeJogo = false;
		posicaoComida();
		
		botaoReset.setVisible(false);
		botaoSair.setVisible(false);
		botaoExibirPlacares.setVisible(false);
		loopJogo.start();
		repaint();
	}
	
	public void botaoSairDoJogo(Graphics g) {
		botaoSair = new JButton("Sair do Jogo");
		botaoSair.setBounds(15, 90, 160, 25);
		botaoSair.setFont(new Font("Poppins", Font.BOLD, 16));
		botaoSair.setForeground(Color.white);
		botaoSair.setBackground(Color.red);
		botaoSair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetarJogo(g);
			}
		});
		setLayout(null);
		add(botaoSair);
	}
	
	public void resetarJogo(Graphics g) {
		inicioDoJogo = false;
		jogoRodando = false;
		botaoReset.setVisible(false);
		botaoSair.setVisible(false);
		botaoExibirPlacares.setVisible(false);
		repaint();
		telaInicial(g);
	}
	
	public void telaInicial(Graphics g) {
		botaoJogar = new JButton("Jogar");
		botaoJogar.setBounds(larguraTela / 2 - 100, alturaTela / 2, 210, 50);
		botaoJogar.setFont(new Font("Poppins", Font.BOLD, 28));
		botaoJogar.setForeground(Color.black);
		botaoJogar.setBackground(Color.green);
		botaoJogar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				iniciarJogo();
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
	
	public void iniciarJogo() {
		cabecaCobra = new Bloco(10, 10);
		corpoCobra.clear();
		valorComida = 0;
		direcaoX = 1;
		direcaoY = 0;
		fimDeJogo = false;
		posicaoComida();
		
		inicioDoJogo = true;
		jogoRodando = true;
		botaoJogar.setVisible(false);
		tituloDoJogo.setVisible(false);
		loopJogo.start();
		repaint();
	}
	
	public void botaoExibirHistoricoPlacares(Graphics g) {
		botaoExibirPlacares = new JButton("Exibir Placares");
		botaoExibirPlacares.setBounds(15, 130, 160, 25);
		botaoExibirPlacares.setFont(new Font("Poppins", Font.BOLD, 16));
		botaoExibirPlacares.setForeground(new Color(37, 90, 255));
		botaoExibirPlacares.setBackground(new Color(243, 127, 25));
		botaoExibirPlacares.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exibirPlacar();
			}
		});
		setLayout(null);
		add(botaoExibirPlacares);
	}
	
	public void exibirPlacar() {
		try {
			String caminhoDaPasta = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Jogo da Cobrinha - Histórico de Placares";
			File pasta = new File(caminhoDaPasta);
			
			System.out.println("Folder path: " + caminhoDaPasta);
			
			File[] arquivos = pasta.listFiles();
			
			if (arquivos == null || arquivos.length == 0) {
				JOptionPane.showMessageDialog(this, "Nenhum arquivo encontrado nesta pasta: " + caminhoDaPasta);
				return;
			}
			
			String[] arquivosNomes = new String[arquivos.length];
			for (int i = 0; i < arquivos.length; i++) {
				arquivosNomes[i] = arquivos[i].getName();
			}
			
			JList<String> arquivosList = new JList<>(arquivosNomes);
			
			JScrollPane scrollPane = new JScrollPane(arquivosList);
			
			JOptionPane.showMessageDialog(this, scrollPane, "Histórico de Placares", JOptionPane.PLAIN_MESSAGE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void salvarPlacar(int placar) {
		try {
			String caminhoDaPasta = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Jogo da Cobrinha - Histórico de Placares";
			File pasta = new File(caminhoDaPasta);
			
			if (!pasta.exists()) {
				File pastaPai = pasta.getParentFile();
				if (pastaPai != null && !pastaPai.exists()) {
					pastaPai.mkdirs();
				}
				pasta.mkdirs();
			}
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss_SSS");
			String dataHoraAtual = dateFormat.format(new Date());
			
			String arquivoDaPasta = caminhoDaPasta + "/Pontuação_" + dataHoraAtual + ".txt";
			
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoDaPasta))) {
				writer.write("Pontuação: " + placar + " pontos.");
				writer.newLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		if (!inicioDoJogo) {
			return;
		}
		mover();
		repaint();
		if (fimDeJogo) {
			loopJogo.stop();
			jogoRodando = false;
		}
		if (e.getSource() == botaoExibirPlacares) {
			botaoExibirHistoricoPlacares(null);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP && direcaoY != 1) {
            direcaoX = 0;
            direcaoY = -1;
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN && direcaoY != -1) {
            direcaoX = 0;
            direcaoY = 1;
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT && direcaoX != 1) {
            direcaoX = -1;
            direcaoY = 0;
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT && direcaoX != -1) {
            direcaoX = 1;
            direcaoY = 0;
        }
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
	}
}



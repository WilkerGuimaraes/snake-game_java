package controller;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import javax.swing.*;

import model.Bloco;
import model.CabecaCobra;
import model.Comida;
import view.JogoDaCobraView;

public class JogoDaCobraController implements ActionListener, KeyListener, JogoDaCobraControllerBusiness {
	private JogoDaCobraView view;
	
	private int larguraTela;
	private int alturaTela;
	private int tamanhoBloco = 30;
	
	//lógica do jogo.
	private int direcaoX;
	private int direcaoY;
	private Timer loopJogo;
	
	//cobra
	private CabecaCobra cabecaCobra;
	private ArrayList<Bloco> corpoCobra;
	
	//comida
	private Comida comida;
	Random random;
	private int valorComida;
	
	private boolean fimDeJogo = false;//Variável qeu controla se o jogo está em andamento ou se o jogo terminou (o jogador perdeu).
	private boolean inicioDoJogo = false;//Variável que controla o estado inicial do jogo e a transição para o jogo em si.
	private boolean jogoRodando = false;//Variável que controla se o jogo está em execução ou pausado.
	
	public void setView(JogoDaCobraView view) {
		this.view = view;
	}
	
	public JogoDaCobraController(int larguraTela, int alturaTela) {
		this.larguraTela = larguraTela;
		this.alturaTela = alturaTela;
		
		cabecaCobra = new CabecaCobra(10, 10);
		corpoCobra = new ArrayList<Bloco>();
		
		comida = new Comida(10, 10);
		random = new Random();
		posicaoComida();
		
		direcaoX = 1;
		direcaoY = 0;
		
		loopJogo = new Timer(100, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.actionPerformed(e);
			}
		});
		loopJogo.start();
	}

	public void posicaoComida() {
		comida.x = random.nextInt(larguraTela / tamanhoBloco);
		comida.y = random.nextInt(alturaTela / tamanhoBloco);
	}
	
	public void resetarJogo() {
		cabecaCobra = new CabecaCobra(10, 10);
		corpoCobra.clear();
		valorComida = 0;
		direcaoX = 1;
		direcaoY = 0;
		fimDeJogo = false;
		posicaoComida();
		loopJogo.start();
	}

	public void sairJogo(Graphics g) {
		inicioDoJogo = false;
		jogoRodando = false;
	}
	
	public void iniciarJogo() {
		cabecaCobra = new CabecaCobra(10, 10);
		corpoCobra.clear();
		valorComida = 0;
		direcaoX = 1;
		direcaoY = 0;
		fimDeJogo = false;
		posicaoComida();
		inicioDoJogo = true;
		jogoRodando = true;
		loopJogo.start();
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
				loopJogo = new Timer(novoLoopJogo, (ActionListener) this);
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
		return bloco1.x == bloco2.x && bloco1.x == bloco2.x;
	}
	
	//Método criado para cancelar a aceleração da cobra e retornar a sua velocidade normal caso o jogo reinicie.
	public void resetarVelocidade() {
		loopJogo.stop();
		loopJogo = new Timer(100, (ActionListener) this);
		loopJogo.start();
	}

	@Override
	public void exibirPlacar() {
		try {
			String caminhoDaPasta = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Jogo da Cobrinha - Histórico de Placares";
			File pasta = new File(caminhoDaPasta);
			
			System.out.println("Diretório: " + caminhoDaPasta);
			
			File[] arquivos = pasta.listFiles();
			
			if (arquivos == null || arquivos.length == 0) {
				JOptionPane.showMessageDialog(null, "Nenhum arquivo encontrado nesta pasta: " + caminhoDaPasta);
				return;
			}
			
			String[] arquivosNomes = new String[arquivos.length];
			for (int i = 0; i < arquivos.length; i++) {
				arquivosNomes[i] = arquivos[i].getName();
			}
			
			JList<String> arquivosList = new JList<>(arquivosNomes);
			
			JScrollPane scrollPane = new JScrollPane(arquivosList);
			
			JOptionPane.showMessageDialog(null, scrollPane, "Histórico de Placares", JOptionPane.PLAIN_MESSAGE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
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
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss_SSS");//Objeto que formata a data e hora e o atribui a variável `dateFormat`.
			String dataHoraAtual = dateFormat.format(new Date());//Obtém a data e hora atual formatada.
			
			String arquivoDaPasta = caminhoDaPasta + "/Pontuação_" + dataHoraAtual + ".txt";//Nomeia o arquivo com o nome formatado.
			
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoDaPasta))) {//Cria um objeto para escrever o nome do arquivo.
				writer.write("Pontuação: " + placar + " pontos.");
				writer.newLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
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

	public int getLarguraTela() {
		return larguraTela;
	}

	public void setLarguraTela(int larguraTela) {
		this.larguraTela = larguraTela;
	}

	public int getAlturaTela() {
		return alturaTela;
	}

	public void setAlturaTela(int alturaTela) {
		this.alturaTela = alturaTela;
	}

	public CabecaCobra getCabecaCobra() {
		return cabecaCobra;
	}

	public void setCabecaCobra(CabecaCobra cabecaCobra) {
		this.cabecaCobra = cabecaCobra;
	}

	public ArrayList<Bloco> getCorpoCobra() {
		return corpoCobra;
	}

	public void setCorpoCobra(ArrayList<Bloco> corpoCobra) {
		this.corpoCobra = corpoCobra;
	}

	public Comida getComida() {
		return comida;
	}

	public void setComida(Comida comida) {
		this.comida = comida;
	}

	public boolean isFimDeJogo() {
		return fimDeJogo;
	}

	public boolean isInicioDoJogo() {
		return inicioDoJogo;
	}

	public void setInicioDoJogo(boolean inicioDoJogo) {
		this.inicioDoJogo = inicioDoJogo;
	}

	public void setFimDeJogo(boolean fimDeJogo) {
		this.fimDeJogo = fimDeJogo;
	}

	public int getTamanhoBloco() {
		return tamanhoBloco;
	}

	public void setTamanhoBloco(int tamanhoBloco) {
		this.tamanhoBloco = tamanhoBloco;
	}

	public int getValorComida() {
		return valorComida;
	}

	public void setValorComida(int valorComida) {
		this.valorComida = valorComida;
	}
	
	public boolean isJogoRodando() {
		return jogoRodando;
	}

	public void setJogoRodando(boolean jogoRodando) {
		this.jogoRodando = jogoRodando;
	}

	public Timer getLoopJogo() {
		return loopJogo;
	}

	public void setLoopJogo(Timer loopJogo) {
		this.loopJogo = loopJogo;
	}
	
	public void realizarAcaoDoJogo() {
		if (inicioDoJogo) {
			mover();
			
			if (fimDeJogo) {
				loopJogo.stop();
				jogoRodando = false;
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}

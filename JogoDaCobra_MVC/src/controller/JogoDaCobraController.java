package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.Timer;

import utils.JogoDaCobraUtils;
import utils.JogoDaCobraUtils.Bloco;
import model.JogoDaCobraModel;
import view.JogoDaCobraView;

public class JogoDaCobraController implements ActionListener, KeyListener {
	private JogoDaCobraUtils utils;
	private JogoDaCobraModel model;
	private JogoDaCobraView view;
	
	//lógica do jogo.
	private int direcaoX;
	private int direcaoY;
	private Timer loopJogo;
	
	private boolean fimDeJogo = false;//Variável qeu controla se o jogo está em andamento ou se o jogo terminou (o jogador perdeu).
	private boolean inicioDoJogo = false;//Variável que controla o estado inicial do jogo e a transição para o jogo em si.
	private boolean jogoRodando = false;//Variável que controla se o jogo está em execução ou pausado. 
	
	//comida
	private Bloco comida;
	Random random;
	private int valorComida = 0;
	
	public boolean isFimDeJogo() {
		return fimDeJogo;
	}

	public void setFimDeJogo(boolean fimDeJogo) {
		this.fimDeJogo = fimDeJogo;
	}

	public boolean isInicioDoJogo() {
		return inicioDoJogo;
	}

	public void setInicioDoJogo(boolean inicioDoJogo) {
		this.inicioDoJogo = inicioDoJogo;
	}

	public boolean isJogoRodando() {
		return jogoRodando;
	}

	public void setJogoRodando(boolean jogoRodando) {
		this.jogoRodando = jogoRodando;
	}

	public int getDirecaoX() {
		return direcaoX;
	}

	public void setDirecaoX(int direcaoX) {
		this.direcaoX = direcaoX;
	}

	public int getDirecaoY() {
		return direcaoY;
	}

	public void setDirecaoY(int direcaoY) {
		this.direcaoY = direcaoY;
	}

	public Bloco getComida() {
		return comida;
	}

	public void setComida(Bloco comida) {
		this.comida = comida;
	}
	
	public Timer getLoopJogo() {
		return loopJogo;
	}

	public void setLoopJogo(Timer loopJogo) {
		this.loopJogo = loopJogo;
	}
	
	public JogoDaCobraController() {
		Bloco comida = new Bloco(10, 10);
		random = new Random();
		posicaoComida();
		
		direcaoX = 1;
		direcaoY = 0;
		
		loopJogo = new Timer(100, this);
		loopJogo.start();
	}

	public void posicaoComida() {
		comida.x = random.nextInt(larguraTela / model.getTamanhoBloco());
		comida.y = random.nextInt(alturaTela / model.getTamanhoBloco());
	}
	
	public void mover() {
		//Comer a comida.
		if (colisao(model.getCabecaCobra(), comida)) {
			model.getCorpoCobra().add(new Bloco(comida.x, comida.y));
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
		for (int i = model.getCorpoCobra().size() - 1; i >= 0; i--) {
			JogoDaCobraUtils.Bloco parteCobra = model.getCorpoCobra().get(i);
			//Cada parte do corpo é movida para a posição da parte anterior.
			if (i == 0) {//antes da cabeça
				parteCobra.x = model.getCabecaCobra().x;
				parteCobra.y = model.getCabecaCobra().y;
			} else {
				JogoDaCobraUtils.Bloco anteriorParteCobra = model.getCorpoCobra().get(i - 1);
				parteCobra.x = anteriorParteCobra.x;
				parteCobra.y = anteriorParteCobra.y;
			}
		}
		
		//Movimentação da cabeça da cobra.
		model.getCabecaCobra().x += direcaoX;
		model.getCabecaCobra().y += direcaoY;
		
		//Condições de fim de jogo.
		for (int i = 0; i < model.getCorpoCobra().size(); i++) {
			JogoDaCobraUtils.Bloco parteCobra = model.getCorpoCobra().get(i);
			
			//Colisão => cabeça + corpo
			if (colisao(model.getCabecaCobra(), parteCobra)) {
				fimDeJogo = true;
				resetarVelocidade();
				salvarPlacar(valorComida);
			}
		}
		
		if (model.getCabecaCobra().x*model.getTamanhoBloco() < 0 || model.getCabecaCobra().x*model.getTamanhoBloco() >= larguraTela || //Cabeça passou da borda da esquerda ou direita
			model.getCabecaCobra().y*model.getTamanhoBloco() < 0 || model.getCabecaCobra().y*model.getTamanhoBloco() >= alturaTela ) { // Cabeça passou da borda de cima ou embaixo
			fimDeJogo = true;
			resetarVelocidade();
			salvarPlacar(valorComida);
		}
	}
	
	public boolean colisao(JogoDaCobraUtils.Bloco bloco1, JogoDaCobraUtils.Bloco bloco2) {
		return bloco1.x == bloco2.x && bloco1.y == bloco2.y;
	}
	
	//Método criado para cancelar a aceleração da cobra e retornar a sua velocidade normal caso o jogo reinicie.
	public void resetarVelocidade() {
		loopJogo.stop();
		loopJogo = new Timer(100, this);
		loopJogo.start();
	}
	
	public void exibirPlacar() {
		try {
			String caminhoDaPasta = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Jogo da Cobrinha - Histórico de Placares";
			File pasta = new File(caminhoDaPasta);
			
			System.out.println("Diretório: " + caminhoDaPasta);
			
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
	public void actionPerformed(ActionEvent e) {
		if (!inicioDoJogo) {
			return;
		}
		mover();
		view.repaint();
		if (fimDeJogo) {
			loopJogo.stop();
			jogoRodando = false;
		}
		if (e.getSource() == view.getBotaoExibirPlacares()) {
			view.botaoExibirHistoricoPlacares(null);
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

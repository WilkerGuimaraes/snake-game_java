package model;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;

import utils.JogoDaCobraUtils.Bloco;
import view.JogoDaCobraView;
import controller.JogoDaCobraController;

public class JogoDaCobraModel {
	private JogoDaCobraController controller;
	private JogoDaCobraView view;
	
	private int larguraTela;
	private int alturaTela;
	private int tamanhoBloco = 30;
	
	//cobra
	private Bloco cabecaCobra;
	private ArrayList<Bloco> corpoCobra;

	//comida
	private Bloco comida;
	Random random;
	private int valorComida = 0;
	
	public void resetarJogo() {
		cabecaCobra = new Bloco(10, 10);
		corpoCobra.clear();
		valorComida = 0;
		controller.setDirecaoX(1);
		controller.setDirecaoY(0);
		controller.setFimDeJogo(false);
		controller.posicaoComida();
		
		view.getBotaoReset().setVisible(false);
		view.getBotaoSair().setVisible(false);
		view.getBotaoExibirPlacares().setVisible(false);
		controller.getLoopJogo().start();
		view.repaint();
	}
	
	public void sairJogo(Graphics g) {
		controller.setInicioDoJogo(false);
		controller.setJogoRodando(false);
		view.getBotaoReset().setVisible(false);
		view.getBotaoSair().setVisible(false);
		view.getBotaoExibirPlacares().setVisible(false);
		view.repaint();
		view.telaInicial(g);
	}
	
	public void iniciarJogo() {
		cabecaCobra = new Bloco(10, 10);
		corpoCobra.clear();
		valorComida = 0;
		controller.setDirecaoX(1);
		controller.setDirecaoY(0);
		controller.setFimDeJogo(false);
		controller.posicaoComida();
		
		controller.setInicioDoJogo(true);
		controller.setJogoRodando(true);
		view.getBotaoJogar().setVisible(false);
		view.getTituloDoJogo().setVisible(false);
		controller.getLoopJogo().start();
		view.repaint();
	}
	
	public int getTamanhoBloco() {
		return tamanhoBloco;
	}

	public void setTamanhoBloco(int tamanhoBloco) {
		this.tamanhoBloco = tamanhoBloco;
	}

	public Bloco getCabecaCobra() {
		return cabecaCobra;
	}

	public void setCabecaCobra(Bloco cabecaCobra) {
		this.cabecaCobra = cabecaCobra;
	}

	public ArrayList<Bloco> getCorpoCobra() {
		return corpoCobra;
	}

	public void setCorpoCobra(ArrayList<Bloco> corpoCobra) {
		this.corpoCobra = corpoCobra;
	}
}

package Main;

import javax.swing.*;

import view.JogoDaCobraView;
import controller.JogoDaCobraController;

public class Main {
	public static void main(String[] args) throws Exception {
		int larguraTela = 1200;
		int alturaTela = 720;
		
		JFrame frame = new JFrame("Jogo da Cobrinha");
		frame.setVisible(true);
		frame.setSize(larguraTela, alturaTela);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JogoDaCobraController jogoController = new JogoDaCobraController(larguraTela, alturaTela);
		JogoDaCobraView jogoView = new JogoDaCobraView(jogoController);
		
		jogoController.setView(jogoView);
		
		frame.add(jogoView);
		frame.pack();
		jogoView.telaInicial(null);
		jogoView.requestFocus();
	}
}

package projeto_poo_java;

import javax.swing.*;

public class App {
	public static void main(String[] args) throws Exception {
		int larguraTela = 1200;
		int alturaTela = 720;
		
		JFrame frame = new JFrame("Jogo da Cobrinha");
		frame.setVisible(true);
		frame.setSize(larguraTela, alturaTela);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JogoDaCobra jogoDaCobra = new JogoDaCobra(larguraTela, alturaTela);
		frame.add(jogoDaCobra);
		frame.pack();
		jogoDaCobra.telaInicial(null);
		jogoDaCobra.requestFocus();
	}
}

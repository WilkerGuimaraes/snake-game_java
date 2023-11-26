package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import model.Tile;
import controller.SnakeGameController;

public class SnakeGameView extends JPanel implements ActionListener, KeyListener {
	private SnakeGameController controller;
	
	private JButton playButton; // Botão na tela inicial responsável por iniciar o jogo.
	private JButton exitButton; // Botão na tela inicial responsável por fechar a aplicação.
	private JButton resetButton; // Botão responsável por reiniciar o jogo.
	private JButton returnButton; // Botão responsável por sair do jogo retornando à tela inicial.
	private JButton scoreButton; // Botão responsável por exibir na tela final o histórico de placares.
	private JButton directoryButton; // Botão responsável por exibir o diretório na tela com os arquivos contendo o histórico de placares de partidas anteriores.
	
	private JLabel gameTitle; // Título do jogo exibido na tela inicial.
	
	public SnakeGameView(SnakeGameController controller) {
		this.controller = controller;
		
		setPreferredSize(new Dimension(controller.getBoardWidth(), controller.getBoardHeight()));
		setBackground(Color.black);
		addKeyListener(controller);
		setFocusable(true);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(!controller.isStartGame()) {
			return;
		}
		draw(g);
	}
	
	public void draw(Graphics g) {
		// Linhas de grade
		for (int i = 0; i < controller.getBoardWidth() / controller.getTileSize(); i++) {
			g.drawLine(i * controller.getTileSize(), 0, i * controller.getTileSize(), controller.getBoardWidth()); // Desenho da linha vertical.
			g.drawLine(0, i * controller.getTileSize(), controller.getBoardWidth(), i * controller.getTileSize()); // Desenho da linha horizontal.
		}
		
		// Comida
		g.setColor(Color.red);
		g.fill3DRect(controller.getFood().x * controller.getTileSize(), controller.getFood().y * controller.getTileSize(), controller.getTileSize(), controller.getTileSize(), true);
		
		// Cabeça da cobra
		g.setColor(Color.green);
		g.fill3DRect(controller.getSnakeHead().x * controller.getTileSize(), controller.getSnakeHead().y * controller.getTileSize(), controller.getTileSize(), controller.getTileSize(), true);
		
		// Corpo da cobra
		g.setColor(new Color(46, 125, 50));
		for (int i = 0; i < controller.getSnakeBody().size(); i++) {
			Tile snakePart = controller.getSnakeBody().get(i);
			g.fill3DRect(snakePart.x * controller.getTileSize(), snakePart.y * controller.getTileSize(), controller.getTileSize(), controller.getTileSize(), true);
		}
		
		// Placar
		g.setFont(new Font("Poppins", Font.PLAIN, 16));
		if (controller.isGameOver()) {
			g.setColor(Color.red);
			g.drawString("Game Over: " + controller.getFoodValue() + " points.", controller.getTileSize() - 16, controller.getTileSize());
			resetGameButton(g);
			returnMenuButton(g);
			scoreHistoricButton(g);
			openDirectoryButton(g);
		} else {
			g.drawString("Score: " + controller.getFoodValue(), controller.getTileSize() - 16, controller.getTileSize());
		}
	}
	
	public void initialScreen(Graphics g) {
		//Definições do botão Jogar.
		playButton = new JButton("Play");
		playButton.setBounds(controller.getBoardWidth() / 2 - 105, controller.getBoardHeight() / 2 - 30, 210, 50);
		playButton.setFont(new Font("Poppins", Font.BOLD, 28));
		playButton.setForeground(Color.black);
		playButton.setBackground(Color.green);
		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.gameStarted();
				playButton.setVisible(false);
				gameTitle.setVisible(false);
				exitButton.setVisible(false);
				repaint();
			}
		});
		setLayout(null);
		add(playButton);
		
		//Definições do título ma tela inicial.
		gameTitle = new JLabel("Snake game");
		gameTitle.setBounds(controller.getBoardWidth() / 2 - 145, controller.getBoardHeight() / 2 - 150, 290, 55);
		gameTitle.setFont(new Font("Poppins", Font.BOLD, 48));
		gameTitle.setForeground(Color.green);
		setLayout(null);
		add(gameTitle);
		
		//Definições do botão "Fechar o jogo".
		exitButton = new JButton("Exit game");
		exitButton.setBounds(controller.getBoardWidth() / 2 - 105, controller.getBoardHeight() / 2 + 50, 210, 50);
		exitButton.setFont(new Font("Poppins", Font.BOLD, 28));
		exitButton.setForeground(Color.white);
		exitButton.setBackground(Color.red);
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.exitGame();
			}
		});
		setLayout(null);
		add(exitButton);
	}
	
	public void resetGameButton(Graphics g) {
		//Definições do botão "Tentar de Novo".
		resetButton = new JButton("Try again");
		resetButton.setBounds(15, 50, 160, 25);
		resetButton.setFont(new Font("Poppins", Font.BOLD, 16));
		resetButton.setForeground(Color.black);
		resetButton.setBackground(Color.green);
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.resetGame();
				resetButton.setVisible(false);
				returnButton.setVisible(false);
				scoreButton.setVisible(false);
				directoryButton.setVisible(false);
				repaint();
			}
		});
		setLayout(null);
		add(resetButton);
	}
	
	public void returnMenuButton(Graphics g) {
		//Definições do botão "Retornar ao menu".
		returnButton = new JButton("Return to Menu");
		returnButton.setBounds(15, 90, 160, 25);
		returnButton.setFont(new Font("Poppins", Font.BOLD, 16));
		returnButton.setForeground(Color.white);
		returnButton.setBackground(Color.red);
		returnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.returnToMenu();
				resetButton.setVisible(false);
				returnButton.setVisible(false);
				scoreButton.setVisible(false);
				directoryButton.setVisible(false);
				repaint();
				initialScreen(g);
				
			}
		});
		setLayout(null);
		add(returnButton);
	}
	
	public void scoreHistoricButton(Graphics g) {
		//Definições do botão "Exibir placares"
		scoreButton = new JButton("Show Historic");
		scoreButton.setBounds(15, 130, 160, 25);
		scoreButton.setFont(new Font("Poppins", Font.BOLD, 16));
		scoreButton.setForeground(Color.blue);
		scoreButton.setBackground(Color.orange);
		scoreButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.showScores();
			}
		});
		setLayout(null);
		add(scoreButton);
	}
	
	public void openDirectoryButton(Graphics h) {
		directoryButton = new JButton("Open Directory");
		directoryButton.setBounds(15, 170, 160, 25);
		directoryButton.setFont(new Font("Poppins", Font.BOLD, 16));
		directoryButton.setForeground(Color.DARK_GRAY);
		directoryButton.setBackground(Color.cyan);
		directoryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.accessDirectory();
			}
		});
		setLayout(null);
		add(directoryButton);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		controller.performGameAction();
		repaint();
		
		if (e.getSource() == scoreButton) {
			scoreHistoricButton(null);
		}
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

package controller;

import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import javax.swing.*;

import model.Tile;
import model.SnakeHead;
import model.Food;
import view.SnakeGameView;

public class SnakeGameController implements ActionListener, KeyListener, GameLogicController {
	private SnakeGameView view;
	
	private int boardWidth;
	private int boardHeight;
	private int tileSize = 30;
	
	//Lógica do jogo
	private int directionX;
	private int directionY;
	private Timer gameLoop;
	
	//Cobra
	private SnakeHead snakeHead;
	private ArrayList<Tile> snakeBody;
	
	//Comida
	private Food food;
	Random random;
	private int foodValue = 0;
	
	private boolean gameOver = false; // Variável que define se o jogo está em andamento ou se terminou(o jogador perdeu).
	private boolean startGame = false; // Variável que controla o estado inicial do jogo e a transição para o jogo em si.
	
	private String folderPath = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Jogo da Cobrinha - Arquivos de Pontuação"; // Variável que determina onde a pasta que será contendo os arquivos com o histórico de placares será criada; 
	private String filePath; // Variável que determina em qual arquivo será armazenado o histórico de placares e qual será exibido no final do jogo.
	
	public void setView(SnakeGameView view) { // Método setter criado para permitir a interação da classe controller com a classe view. 
		this.view = view;
	}
	
	public SnakeGameController(int boardWidth, int boardHeight) {
		this.boardWidth = boardWidth;
		this.boardHeight = boardHeight;
		
		snakeHead = new SnakeHead(10, 10);
		snakeBody = new ArrayList<Tile>();
		
		food = new Food(10, 10);
		random = new Random();
		placeFood();
		
		directionX = 1;
		directionY = 0;
		
		gameLoop = new Timer(100, new ActionListener() { // Tempo em milissegundos passados entre os quadros.
			public void actionPerformed(ActionEvent e) {
				view.actionPerformed(e);
			}
		});
			gameLoop.start();	
	}

	public void placeFood() {
		food.x = random.nextInt(boardWidth / tileSize);
		food.y = random.nextInt(boardHeight / tileSize);
	}
	
	public void gameStarted() {
		snakeHead = new SnakeHead(10, 10);
		snakeBody.clear();
		foodValue = 0;
		directionX = 1;
		directionY = 0;
		gameOver = false;
		startGame = true;
		placeFood();
		gameLoop.start();
		
		execLog();
	}
	
	@Override
	public void execLog() {
		try {
			File folderLog = new File(folderPath);
			
			if (!folderLog.exists()) {
				File fatherFolder = folderLog.getParentFile();
				if (fatherFolder != null && !fatherFolder.exists()) {
					fatherFolder.mkdirs();
				}
				folderLog.mkdirs();
			}
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss_SSS"); // Objeto que formata a data e hora e o atribui a variável `dateFormat`.
			String currentDateTime = dateFormat.format(new Date()); // Obtém a data e hora atuais formatadas.
			
			String fileName = "Log_" + currentDateTime + ".txt";
			filePath = folderPath + File.separator + fileName;
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
			writer.write("---- Snake Game - Score History ----");
			writer.newLine();
			writer.newLine();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void exitGame() {
		System.exit(0);
	}
	
	public void resetGame() {
		snakeHead = new SnakeHead(10, 10);
		snakeBody.clear();
		foodValue = 0;
		directionX = 1;
		directionY = 0;
		gameOver = false;
		placeFood();
		gameLoop.start();
	}
	
	@Override
	public void showScores() {
		File folderLog = new File(folderPath);
		
		File[] files = folderLog.listFiles();
		if (files == null || files.length == 0) {
			JOptionPane.showMessageDialog(null, "No files found in this folder: " + folderPath);
			return;
		}
		
		File lastFile = files[files.length - 1];
		String contentLastFile = obtainContentFile(lastFile);
		
		JOptionPane.showMessageDialog(null, contentLastFile, "Score Record", JOptionPane.PLAIN_MESSAGE);
	}
	
	public String obtainContentFile(File file) {
		StringBuilder content = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line).append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content.toString();
	}
	
	public void accessDirectory() {
		File folderLog = new File(folderPath);
		
		File[] files = folderLog.listFiles();
		if (files == null || files.length == 0) {
			JOptionPane.showMessageDialog(null, "No files found in this folder: " + folderPath);
			return;
		}
		
		String[] filesNames = new String[files.length];
		for (int i = 0; i < files.length; i++) {
			filesNames[i] = files[i].getName();
		}
		
		//Mostrar os arquivos disponíveis.
		String fileChoose = (String) JOptionPane.showInputDialog(null, "Choose a file", "Open directory", JOptionPane.QUESTION_MESSAGE, null, filesNames, filesNames[0]);
		
		if (fileChoose != null) {
			// O usuário escolheu um arquivo, exibir seu conteúdo.
			File choosenFile = new File(folderPath + File.separator + fileChoose);
			String fileContent = obtainContentFile(choosenFile);
			JOptionPane.showMessageDialog(null, fileContent, "File Content", JOptionPane.PLAIN_MESSAGE);
		}
	}
	
	public void move() {
		//Comer a comida
		if (collision(snakeHead, food)) {
			snakeBody.add(new Tile(food.x, food.y));
			foodValue += 10;
			//Lógica para aumentar a velocidade de percurso da cobra.
			if (foodValue >= 100) {
				gameLoop.stop();
				int newGameLoop = gameLoop.getDelay() - 2;
				gameLoop = new Timer(newGameLoop, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						view.actionPerformed(e);
					}
				});
				gameLoop.start();
			}
			placeFood();
		}
		
		//Movimentação do corpo da cobra.
		for (int i = snakeBody.size() - 1; i >= 0; i--) {
			Tile snakePart = snakeBody.get(i);
			if (i == 0) {
				snakePart.x = snakeHead.x;
				snakePart.y = snakeHead.y;
			} else {
				Tile previousSnakePart = snakeBody.get(i - 1);
				snakePart.x = previousSnakePart.x;
				snakePart.y = previousSnakePart.y;
			}
		}
		
		//Movimentação da cabeça da cobra.
		snakeHead.x += directionX;
		snakeHead.y += directionY;
		
		//Condições de fim de jogo.
		for (int i = 0; i < snakeBody.size(); i++) {
			Tile snakePart = snakeBody.get(i);
			
			//Colisão => cabeça + corpo
			if (collision(snakeHead, snakePart)) {
				gameOver = true;
				speedReset();
				saveScore(foodValue);
			}
		}
		
		if (snakeHead.x * tileSize < 0 || snakeHead.x * tileSize >= boardWidth || // Cabeça passou das bordas laterais.
			snakeHead.y * tileSize < 0 || snakeHead.y * tileSize >= boardHeight) { // Cabeça passou da borda superior ou inferior.
			gameOver = true;
			speedReset();
			saveScore(foodValue);
		}
	}
	
	public boolean collision(Tile tile1, Tile tile2) {
		return tile1.x == tile2.x && tile1.y == tile2.y;
	}
	
	public void speedReset() {
		gameLoop.stop();
		gameLoop = new Timer(100, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.actionPerformed(e);
			}
		});
		gameLoop.start();
	}

	@Override
	public void saveScore(int score) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
			writer.write(score + " points.");
			writer.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void performGameAction() {
		if (startGame) {
			move();
			
			if (gameOver) {
				gameLoop.stop();
			}
		}
	}
	
	public void returnToMenu() {
		startGame = false;
	}
	
	public int getBoardWidth() {
		return boardWidth;
	}

	public void setBoardWidth(int boardWidth) {
		this.boardWidth = boardWidth;
	}

	public int getBoardHeight() {
		return boardHeight;
	}

	public void setBoardHeight(int boardHeight) {
		this.boardHeight = boardHeight;
	}

	public boolean isStartGame() {
		return startGame;
	}

	public void setStartGame(boolean startGame) {
		this.startGame = startGame;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}

	public int getTileSize() {
		return tileSize;
	}

	public void setTileSize(int tileSize) {
		this.tileSize = tileSize;
	}

	public SnakeHead getSnakeHead() {
		return snakeHead;
	}

	public void setSnakeHead(SnakeHead snakeHead) {
		this.snakeHead = snakeHead;
	}

	public ArrayList<Tile> getSnakeBody() {
		return snakeBody;
	}

	public void setSnakeBody(ArrayList<Tile> snakeBody) {
		this.snakeBody = snakeBody;
	}

	public Food getFood() {
		return food;
	}

	public void setFood(Food food) {
		this.food = food;
	}

	public int getFoodValue() {
		return foodValue;
	}

	public void setFoodValue(int foodValue) {
		this.foodValue = foodValue;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP && directionY != 1) {
            directionX = 0;
            directionY = -1;
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN && directionY != -1) {
            directionX = 0;
            directionY = 1;
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT && directionX != 1) {
            directionX = -1;
            directionY = 0;
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT && directionX != -1) {
            directionX = 1;
            directionY = 0;
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

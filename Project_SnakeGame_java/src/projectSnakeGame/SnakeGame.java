package projectSnakeGame;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
	private class Tile {
        int x;
        int y;

        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }  

    int boardWidth;
    int boardHeight;
    int tileSize = 30;
    
    Tile snakeHead;
    ArrayList<Tile> snakeBody;

    Tile food;
    Random random;
    int foodValue = 0;

    int velocityX;
    int velocityY;
    Timer gameLoop;

    boolean gameOver = false;
    
    JButton resetButton;//Green button at the end of the game
    JButton exitGame;//Red button at the end of the game
    JButton playGame;//Button that start game
    JLabel titleLabel;//Title the game
    
    boolean gameStarted = false;//Variável que controla o estado inicial do jogo e a transição para o jogo em si.
    boolean gameRunning = false;//Variável que controla se o jogo está em execução ou pausado

    SnakeGame(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);

        snakeHead = new Tile(10, 10);
        snakeBody = new ArrayList<Tile>();

        food = new Tile(10, 10);
        random = new Random();
        placeFood();

        velocityX = 1;
        velocityY = 0;
        
		gameLoop = new Timer(100, this);  
        gameLoop.start();
	}	
    
    public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!gameStarted) {
			return;
		}
		draw(g);
	}

	public void draw(Graphics g) {
        
        for(int i = 0; i < boardWidth/tileSize; i++) {
            //(x1, y1, x2, y2)
            g.drawLine(i*tileSize, 0, i*tileSize, boardHeight);
            g.drawLine(0, i*tileSize, boardWidth, i*tileSize); 
        }

        //Food
        g.setColor(Color.red);
        g.fill3DRect(food.x*tileSize, food.y*tileSize, tileSize, tileSize, true);

        //Snake Head
        g.setColor(Color.green);
        g.fill3DRect(snakeHead.x*tileSize, snakeHead.y*tileSize, tileSize, tileSize, true);
        
        //Snake Body
        g.setColor(new Color(46, 125, 50));
        for (int i = 0; i < snakeBody.size(); i++) {
            Tile snakePart = snakeBody.get(i);
            g.fill3DRect(snakePart.x*tileSize, snakePart.y*tileSize, tileSize, tileSize, true);
		}

        //Score
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        if (gameOver) {
            g.setColor(Color.red);
            g.drawString("Game Over: " + foodValue, tileSize - 16, tileSize);
            buttonResetGame(g);
            buttonExitGame(g);
        }
        else {
            g.drawString("Score: " + foodValue, tileSize - 16, tileSize);
        }
	}

    public void placeFood(){
        food.x = random.nextInt(boardWidth/tileSize);
		food.y = random.nextInt(boardHeight/tileSize);
	}

    public void move() {
        //eat food
        if (collision(snakeHead, food)) {
            snakeBody.add(new Tile(food.x, food.y));
            foodValue += 10;
            if (foodValue >= 100) {
            	gameLoop.stop();
            	int newGameLoop = gameLoop.getDelay() - 2;
            	gameLoop = new Timer(newGameLoop, this);
            	gameLoop.start();
            }
            placeFood();
        }

        //move snake body
        for (int i = snakeBody.size()-1; i >= 0; i--) {
            Tile snakePart = snakeBody.get(i);
            if (i == 0) { 
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            }
            else {
                Tile prevSnakePart = snakeBody.get(i-1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }
        
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        //condições para game over
        for (int i = 0; i < snakeBody.size(); i++) {
            Tile snakePart = snakeBody.get(i);

            //colisão com a cabeça da cobra
            if (collision(snakeHead, snakePart)) {
                gameOver = true;
                resetVelocity();
            }
        }

        if (snakeHead.x*tileSize < 0 || snakeHead.x*tileSize > boardWidth || //cabeça passou o limite do lado direito ou esquerdo da tela
            snakeHead.y*tileSize < 0 || snakeHead.y*tileSize > boardHeight ) { //cabeça passou do limite do lado de cima ou em baixo da tela
            gameOver = true;
            resetVelocity();
        }
    }
    
    //reset velocity if gameOver = true
    public void resetVelocity() {
    	gameLoop.stop();
    	gameLoop = new Timer(100, this);
    	gameLoop.start();
    }
    
    public void buttonResetGame(Graphics g) {
    	resetButton = new JButton("Try Again");
    	resetButton.setBounds(140, 6, 120, 25);
    	resetButton.setFont(new Font("Poppins", Font.BOLD, 16));
    	resetButton.setForeground(Color.black);
    	resetButton.setBackground(Color.green);
    	resetButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			resetGame();
    		}
    	});
    	setLayout(null);
    	add(resetButton);
    }
    
    public void resetGame() {
    	snakeHead = new Tile(10, 10);
    	snakeBody.clear();
    	foodValue = 0;
    	velocityX = 1;
    	velocityY = 0;
    	gameOver = false;
    	placeFood();
    	
    	resetButton.setVisible(false);
    	exitGame.setVisible(false);
    	gameLoop.start();
    	repaint();
    }
    
    public void buttonExitGame(Graphics g) {
    	exitGame = new JButton("Exit Game");
    	exitGame.setBounds(280, 6, 120, 25);
    	exitGame.setFont(new Font("Poppins", Font.BOLD, 16));
    	exitGame.setForeground(Color.white);
    	exitGame.setBackground(Color.red);
    	exitGame.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			restartGame(g);
    		}
    	});
    	setLayout(null);
    	add(exitGame);
    }
    
    public void restartGame(Graphics g) {
    	gameStarted = false;
    	gameRunning = false;
    	resetButton.setVisible(false);
    	exitGame.setVisible(false);
    	repaint();
		createStartScreen(g);
    }

    public boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }
    
    public void createStartScreen(Graphics g) {
    	playGame = new JButton("Play Game");
    	playGame.setBounds(boardWidth / 2 - 100, boardHeight / 2, 210, 50);
    	playGame.setFont(new Font("Poppins", Font.BOLD, 28));
    	playGame.setForeground(Color.black);
    	playGame.setBackground(Color.green);
    	
    	playGame.addActionListener(new ActionListener() {
    		@Override
    		public void actionPerformed(ActionEvent e) {
    			startGame();
    		}
    	});
    	
    	setLayout(null);
    	add(playGame);
    	
    	titleLabel = new JLabel("Snake Game");
    	titleLabel.setBounds(boardWidth / 2 - 135, boardHeight / 2 - 100, 350, 50);
    	titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
    	titleLabel.setForeground(Color.green);
    	
    	setLayout(null);
    	add(titleLabel);
    	
    }
    
    public void startGame() {
    	snakeHead = new Tile(10, 10);
        snakeBody.clear();
        foodValue = 0;
        velocityX = 1;
        velocityY = 0;
        gameOver = false;
        placeFood();
    	
    	gameStarted = true;
    	gameRunning = true;
    	removeStartScreen();
    	gameLoop.start();
    	repaint();
    }
    
    public void removeStartScreen() {
    	playGame.setVisible(false);
    	titleLabel.setVisible(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) { 
    	if (!gameStarted) {
    		return;
    	}
    	move();
    	repaint();
    	if (gameOver) {
    		gameLoop.stop();
    		gameRunning = false;
    	}
    }  

    @Override
    public void keyPressed(KeyEvent e) {
        
        if (e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1) {
            velocityX = 0;
            velocityY = -1;
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1) {
            velocityX = 0;
            velocityY = 1;
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1) {
            velocityX = -1;
            velocityY = 0;
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1) {
            velocityX = 1;
            velocityY = 0;
        }
    }

    //métodos abstratos herdados de KeyListener.keyTyped(KeyEvent) e KeyListener.keyTyped(KeyEvent)
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
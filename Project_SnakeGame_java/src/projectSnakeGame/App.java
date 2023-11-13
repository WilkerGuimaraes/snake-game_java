package projectSnakeGame;

import javax.swing.*;

import projectSnakeGame.SnakeGame;

public class App {
	public static void main(String[] args) throws Exception {
        int boardWidth = 1200;
        int boardHeight = 900;

        JFrame frame = new JFrame("Snake");
        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SnakeGame snakeGame = new SnakeGame(boardWidth, boardHeight);
        frame.add(snakeGame);
        frame.pack();
        snakeGame.createStartScreen(null);
        snakeGame.requestFocus();
    }
}

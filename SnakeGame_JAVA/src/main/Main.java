package main;

import javax.swing.*;

import view.SnakeGameView;
import controller.SnakeGameController;

public class Main {
	public static void main(String[] args) throws Exception {
		int boardWidth = 1200;
		int boardHeight = 720;
		
		JFrame frame = new JFrame("Snake Game");
		frame.setVisible(true);
		frame.setSize(boardWidth, boardHeight);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		SnakeGameController snakeGameController = new SnakeGameController(boardWidth, boardHeight);
		SnakeGameView snakeGameView = new SnakeGameView(snakeGameController);
		
		snakeGameController.setView(snakeGameView);
		
		frame.add(snakeGameView);
		frame.pack();
		snakeGameView.initialScreen(null);
		snakeGameView.requestFocus();
	}
}

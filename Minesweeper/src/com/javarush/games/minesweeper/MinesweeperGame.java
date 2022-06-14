package com.javarush.games.minesweeper;

import com.javarush.engine.cell.*;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
	private static final int SIDE = 9;
	private GameObject[][] gameField = new GameObject[SIDE][SIDE];
	private int countClosedTiles = SIDE * SIDE;
	private int countMinesOnField;
	private int countFlags;
	private int score;
	private boolean isGameStopped;

	private static final String FLAG = "\uD83D\uDEA9";
	private static final String MINE = "\uD83D\uDCA3";

	@Override public void initialize() {
		setScreenSize(SIDE, SIDE);
		isGameStopped = false;
		createGame();
	}

	@Override public void onMouseLeftClick(int x, int y) {
		if(isGameStopped){
			restart();
			return;
		}
		openTile(x, y);
	}

	@Override public void onMouseRightClick(int x, int y) {
		markTile(x, y);
	}

	private void createGame() {
		for (int y = 0; y < SIDE; y++) {
			for (int x = 0; x < SIDE; x++) {
				boolean isMine = getRandomNumber(10) < 1;
				if (isMine) {
					countMinesOnField++;
				}
				gameField[y][x] = new GameObject(x, y, isMine);
				setCellColor(x, y, Color.ORANGE);
				setCellValue(x, y, "");
			}
		}
		countMineNeighbors();
		countFlags = countMinesOnField;
	}

	private List<GameObject> getNeighbors(GameObject gameObject) {
		List<GameObject> result = new ArrayList<>();
		for (int y = gameObject.y-1; y <= gameObject.y+1; y++) {
			for (int x = gameObject.x-1; x <= gameObject.x+1; x++) {
				if (y < 0 || y >= SIDE) {
					continue;
				}
				if (x < 0 || x >= SIDE) {
					continue;
				}
				if (gameField[y][x] == gameObject) {
					continue;
				}
				result.add(gameField[y][x]);
			}
		}
		return result;
	}

	private void countMineNeighbors() {
		for (int y = 0; y < SIDE; y++) {
			for (int x = 0; x < SIDE; x++) {
				GameObject gameObject = gameField[y][x];
				if (!gameObject.isMine) {
					for (GameObject neighbor : getNeighbors(gameObject)) {
						if (neighbor.isMine) {
							gameObject.countMineNeighbors++;
						}
					}
				}
			}
		}
	}

	private void openTile(int x, int y) {
		GameObject gameObject = gameField[y][x];
		if (!gameObject.isFlag && !gameObject.isOpen && !isGameStopped) {
			gameObject.isOpen = true;
			countClosedTiles--;
			setCellColor(x, y, Color.GREEN);
			if (gameObject.isMine) {
				setCellValueEx(x, y, Color.RED, MINE);
				gameOver();
				return;
			} else if (gameObject.countMineNeighbors != 0) {
				setCellNumber(x, y, gameObject.countMineNeighbors);
				score += 5;
			}
			if (!gameObject.isMine && gameObject.countMineNeighbors == 0) {
				for (GameObject neighbor : getNeighbors(gameObject)) {
					if (!neighbor.isOpen) {
						score+= 5;
						openTile(neighbor.x, neighbor.y);
					}
				}
			}
		}
		setScore(score);
		if(countClosedTiles == countMinesOnField){
			win();
		}
	}

	private void markTile(int x, int y) {
		GameObject gameObject = gameField[y][x];
		if (!gameObject.isOpen) {
			if (countFlags > 0 && !gameObject.isFlag) {
				gameObject.isFlag = true;
				countFlags--;
				setCellValue(x, y, FLAG);
				setCellColor(x, y, Color.YELLOW);
			} else if (gameObject.isFlag) {
				gameObject.isFlag = false;
				countFlags++;
				setCellValue(x, y, "");
			}
		}
	}

	private void gameOver() {
		isGameStopped = true;
		showMessageDialog(Color.ALICEBLUE, "Game is Over", Color.BLACK, 60);
	}

	private void win() {
		isGameStopped = true;
		showMessageDialog(Color.ALICEBLUE, "You win! Bravo", Color.BLACK, 60);
	}
	private void restart() {
		isGameStopped = false;
		score = 0;
		countClosedTiles = SIDE * SIDE;
		countMinesOnField = 0;
		setScore(0);
		createGame();


	}
}
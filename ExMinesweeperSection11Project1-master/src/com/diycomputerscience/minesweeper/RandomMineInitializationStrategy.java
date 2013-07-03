package com.diycomputerscience.minesweeper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomMineInitializationStrategy implements MineInitializationStrategy {

	private Random specificMineRand = new Random();
	
	@Override
	public List<Point> mines(Point boardSize) {
		// TODO: Implement logic to determine and return a List of lines
		// for the specified Board size
		List<Point> mines = new ArrayList<Point>();
		for(int row=0; row<boardSize.row; row++) {
			for(int col=0; col<boardSize.col; col++) {
				int randomMumWithOneThirdProb = specificMineRand.nextInt(2);
				if(randomMumWithOneThirdProb == 0) {
					mines.add(new Point(row, col));
				}
			}
		}
		return mines;
	}

}

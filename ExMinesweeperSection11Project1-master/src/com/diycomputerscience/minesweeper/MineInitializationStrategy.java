package com.diycomputerscience.minesweeper;

import java.util.List;

public interface MineInitializationStrategy {
	
	/**
	 * Determines the points which should be mines for a Board with the specified max size
	 * @param boardSize The Point which represents the outer edge of the Board
	 * @return A List containing Point objects for mines
	 */
	public List<Point> mines(Point boardSize);
	
}

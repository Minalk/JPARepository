package com.diycomputerscience.minesweeper.utils;

import java.util.ArrayList;
import java.util.List;

import com.diycomputerscience.minesweeper.Board;
import com.diycomputerscience.minesweeper.Point;

public class MinesweeperUtils {
	/**
	 * Computes all valid neighbors for the specified {@link Point} 
	 * @param point The specified Point
	 * @return A List containing {@link Point} objects that denote a valid neighbor for the specified Point
	 */
	public static List<Point> computeNeibhbours(Point point) {
		
		//Notice how we add generic type information to the List and ArrayList declaration
		List<Point> neighbours = new ArrayList<Point>();
		// row-1, col-1
		if(point.row-1 >=0 && point.col-1 >= 0) {
			neighbours.add(new Point(point.row-1, point.col-1));
		}
		
		// row-1, col
		if(point.row-1 >= 0) {
			neighbours.add(new Point(point.row-1, point.col));
		}
		
		// row-1, col+1
		if(point.row-1 >= 0 && point.col+1 < Board.MAX_COLS) {
			neighbours.add(new Point(point.row-1, point.col+1));
		}
		
		// row, col+1
		if(point.col+1 < Board.MAX_COLS) {
			neighbours.add(new Point(point.row, point.col+1));
		}
		
		// row+1, col+1
		if(point.row+1 < Board.MAX_ROWS && point.col+1 < Board.MAX_COLS) {
			neighbours.add(new Point(point.row+1, point.col+1));
		}
		
		// row+1, col
		if(point.row+1 < Board.MAX_ROWS) {
			neighbours.add(new Point(point.row+1, point.col));
		}
		
		// row+1, col-1
		if(point.row+1 < Board.MAX_ROWS && point.col-1 >= 0) {
			neighbours.add(new Point(point.row+1, point.col-1));
		}
		
		// row, col-1
		if(point.col-1 >= 0) {
			neighbours.add(new Point(point.row, point.col-1));
		}		
		
		return neighbours;
	}
}

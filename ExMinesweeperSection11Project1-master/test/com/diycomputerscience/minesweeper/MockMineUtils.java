package com.diycomputerscience.minesweeper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.diycomputerscience.minesweeper.model.FilePersistenceStrategy;

public class MockMineUtils {
	private static Map<Point, Integer> counts;
	
	static {
		counts = new HashMap<Point, Integer>();
		// row 0
		// X X X 2 0 0
		counts.put(new Point(0,0), 0);
		counts.put(new Point(0,1), 0);
		counts.put(new Point(0,2), 0);
		counts.put(new Point(0,3), 2);
		counts.put(new Point(0,4), 0);
		counts.put(new Point(0,5), 0);
		
		// row 1
		// X 8 X 3 0 0
		counts.put(new Point(1,0), 0);
		counts.put(new Point(1,1), 8);
		counts.put(new Point(1,2), 0);
		counts.put(new Point(1,3), 3);
		counts.put(new Point(1,4), 0);
		counts.put(new Point(1,5), 0);
		
		// row 2
		// X X X 3 1 1
		counts.put(new Point(2,0), 0);
		counts.put(new Point(2,1), 0);
		counts.put(new Point(2,2), 0);
		counts.put(new Point(2,3), 3);
		counts.put(new Point(2,4), 1);
		counts.put(new Point(2,5), 1);
		
		// row 3
		// 4 5 3 2 X 1
		counts.put(new Point(3,0), 4);
		counts.put(new Point(3,1), 5);
		counts.put(new Point(3,2), 3);
		counts.put(new Point(3,3), 2);
		counts.put(new Point(3,4), 0);
		counts.put(new Point(3,5), 1);
		
		// row 4
		// X X 1 1 1 1
		counts.put(new Point(4,0), 0);
		counts.put(new Point(4,1), 0);
		counts.put(new Point(4,2), 1);
		counts.put(new Point(4,3), 1);
		counts.put(new Point(4,4), 1);
		counts.put(new Point(4,5), 1);
		
		// row 5
		// 2 2 1 0 0 0
		counts.put(new Point(5,0), 2);
		counts.put(new Point(5,1), 2);
		counts.put(new Point(5,2), 1);
		counts.put(new Point(5,3), 0);
		counts.put(new Point(5,4), 0);
		counts.put(new Point(5,5), 0);
	}

	
	//	X X X 2 0 0
	//	X 8 X 3 0 0
	//	X X X 3 1 1
	//	4 5 3 2 X 1
	//	X X 1 1 1 1
	//	2 2 1 0 0 0
	//  There are 11 mines on the above board
	public static List<Point> mines(Point boardSize) {
		List<Point> mines = new ArrayList<Point>();
		mines.add(new Point(0,0));
		mines.add(new Point(0,1));
		mines.add(new Point(0,2));
		mines.add(new Point(1,0));
		mines.add(new Point(1,2));
		mines.add(new Point(2,0));
		mines.add(new Point(2,1));
		mines.add(new Point(2,2));
		mines.add(new Point(3,4));
		mines.add(new Point(4,0));
		mines.add(new Point(4,1));		
		return mines;
	}
	
	public static int getSquareCount(Point point) {
		return counts.get(point);		
	}
	
	public static String getSquareCountAsString(Point point) {
		int count = counts.get(point); 
		return String.valueOf(count);		
	}
	
	public static List<String> buildSavedLayout(Board board) {
		List<String> savedBoardLayout = new ArrayList<String>();
		Square squares[][] = board.getSquares();
		for(int row=0; row<squares.length; row++) {
			Square squaresRow[] = squares[row];
			for(int col=0; col<squaresRow.length; col++) {
				Square square = squaresRow[col];
				savedBoardLayout.add(FilePersistenceStrategy.dataForSquare(row, col, square));
			}
		}
		return savedBoardLayout;
	}
	
	public static Board buildBoardFromLayout(String savedLayout[]) {
		Square squares[][] = new Square[Board.MAX_ROWS][Board.MAX_COLS];
		// Compile the regex pattern
		Pattern regexPattern = Pattern.compile(FilePersistenceStrategy.SQUARE_LOAD_REGEX);
		
		// For every line
		for(String line : savedLayout) {
			// Create a matcher object to perform matching on the line using the regex pattern
			Matcher matcher = regexPattern.matcher(line);
			matcher.find();
			
			// Get the first matching group. ie the pattern enclosed in the first () representing the row co-ordinate
			String sRow = matcher.group(1);
			int row = Integer.parseInt(sRow);
			
			// Get the second matching group, representing the col co-ordinate
			String sCol = matcher.group(2);
			int col = Integer.parseInt(sCol);
			
			// Get the fourth matching group, representing whether the square has been uncovered by the user
			String sState = matcher.group(3);
			Square.SquareState status = Square.SquareState.valueOf(sState);
			
			// Get the third matching group, representing whether the square is a mine
			String sIsMine = matcher.group(4);
			boolean mine = Boolean.parseBoolean(sIsMine);
			
			// Build a square object from the information gathered above
			Square square = new Square();
			square.setMine(mine);
			
			square.setState(status);
			squares[row][col] = square;
		}
		Board board = new Board();
		board.setSquares(squares);
		board.computeCounts();
		return board;
	}
	
	public static final String expectedLinesInSavedBoard[] = {
		"0,0:COVERED-true",
		"0,1:COVERED-true",
		"0,2:COVERED-true",
		"0,3:COVERED-false",
		"0,4:COVERED-false",
		"0,5:COVERED-false",
		"1,0:COVERED-true",
		"1,1:COVERED-false",
		"1,2:COVERED-true",
		"1,3:UNCOVERED-false",
		"1,4:COVERED-false",
		"1,5:COVERED-false",
		"2,0:COVERED-true",
		"2,1:COVERED-true",
		"2,2:COVERED-true",
		"2,3:COVERED-false",
		"2,4:COVERED-false",
		"2,5:COVERED-false",
		"3,0:COVERED-false",
		"3,1:COVERED-false",
		"3,2:COVERED-false",
		"3,3:COVERED-false",
		"3,4:COVERED-true",
		"3,5:COVERED-false",
		"4,0:COVERED-true",
		"4,1:COVERED-true",
		"4,2:COVERED-false",
		"4,3:COVERED-false",
		"4,4:COVERED-false",
		"4,5:COVERED-false",
		"5,0:COVERED-false",
		"5,1:COVERED-false",
		"5,2:COVERED-false",
		"5,3:COVERED-false",
		"5,4:COVERED-false",
		"5,5:COVERED-false",
		};
}

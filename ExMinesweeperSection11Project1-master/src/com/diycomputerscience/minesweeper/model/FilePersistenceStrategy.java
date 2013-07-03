package com.diycomputerscience.minesweeper.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.diycomputerscience.minesweeper.Board;
import com.diycomputerscience.minesweeper.ConfigurationException;
import com.diycomputerscience.minesweeper.Square;
import com.diycomputerscience.minesweeper.Square.SquareState;

public class FilePersistenceStrategy implements PersistenceStrategy {

	private String fileName;
	// Format for saving a square 'row,col:SqaureStatus-isMine'
	public static final String SQUARE_FORMAT = "%d,%d:%s-%b";
	public static final String SQUARE_LOAD_REGEX = "(\\d*),(\\d*):(COVERED|UNCOVERED|FLAGGED)-(true|false)";
	
	private Logger logger = Logger.getLogger(FilePersistenceStrategy.class);
	
	@Override
	public void configure(Properties properties) throws ConfigurationException {
		if(properties == null) {
			throw new NullPointerException("properties cannot be null");
		}		
		
		this.fileName = properties.getProperty("persistence.filename");
		if(this.fileName == null) {
			throw new ConfigurationException("fileName is not specified in the configuration properties file");
		}
	}
	
	@Override
	public void save(Board board) throws PersistenceException {
		if(board == null) {
			throw new NullPointerException("board cannot be null");
		}
		logger.info("Saving board to file '" + this.fileName + "'");
		PrintWriter printWriter = null;
		try {
			printWriter = new PrintWriter(this.fileName);
			Square squares[][] = board.getSquares();
			for(int row=0; row<squares.length; row++) {
				Square squareRow[] = squares[row];
				for(int col=0; col<squareRow.length; col++) {
					Square square = squareRow[col];
					String data = dataForSquare(row, col, square);
					printWriter.println(data);
				}
			}
		} catch(IOException ioe) {
			//Notice that we are not logging here
			throw new PersistenceException("Could not save data", ioe);
		} finally {
			if(printWriter != null) {
				printWriter.close();
			}
		}
	}

	@Override
	public Board load() throws PersistenceException {
		logger.info("Loading board from file '" + this.fileName + "'");
		Square squares[][] = new Square[Board.MAX_ROWS][Board.MAX_COLS];
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(this.fileName));
			String data = null;
			
			// Compile the regex pattern
			Pattern regexPattern = Pattern.compile(SQUARE_LOAD_REGEX);
			String line = "";
			// For every line
			while((line = reader.readLine()) != null) {
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
		} catch(IOException ioe) {
			throw new PersistenceException("Could not load data", ioe);
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch(IOException ioe) {
					logger.warn("Could not close reader to db file");
				}
			}
		}		
	}
	
	public static String dataForSquare(int row, int col, Square square) {
		String squareRep = String.format(SQUARE_FORMAT, 
				 						 row, 
				 						 col,
				 						 square.getState(),
				 						 square.isMine()
				 						 );
		return squareRep;
		//return row + " " + col + " " + square.getState() + " " + square.getCount() + " " + square.isMine();
	}

}

package com.diycomputerscience.minesweeper;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class BoardTest {

	private Board board;
	private MineInitializationStrategy mineInitializationStrategy;
	
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	@Before
	public void setUp() throws Exception {
		this.mineInitializationStrategy = EasyMock.createMock(MineInitializationStrategy.class);
		Point boardSize = new Point(Board.MAX_ROWS, Board.MAX_ROWS);
		expect(this.mineInitializationStrategy.mines(boardSize)).andReturn(MockMineUtils.mines(boardSize));
		replay(this.mineInitializationStrategy);
		
		this.board = new Board(mineInitializationStrategy);
	}

	@After
	public void tearDown() throws Exception {
		this.board = null;
	}
		
	@Test
	public void testMineInitializationStrategyNullCheckInConstructor() {
		expectedEx.expect(RuntimeException.class);
		expectedEx.expectMessage("mineInitializationStrategy cannot be null");
		Board board = new Board(null);
	}
	
	@Test
	public void testSquaresNotNull() {
		Square squares[][] = this.board.getSquares();		
		for(Square squareRow[] : squares) {			
			for(Square aSquare : squareRow) {
				assertNotNull(aSquare);
			}
		}
	}
	
	@Test
	public void testAtleastOneSquareShouldBeAMine() throws Exception {
		boolean mineFound = false;
		Square squares[][] = this.board.getSquares();
		for(int row=0; row<squares.length; row++) {
			Square squareRow[] = squares[row];
			for(int col=0; col<squareRow.length; col++) {
				Square square = squareRow[col];
				if(square.isMine()) {
					// this code can be refactored to avoid unnecassary looping if a mine is found once
					// however we have kept it as is for the sake of simplicity
					mineFound = true;
				}
			}
		}
		// test will fail if at least one mine has not been found
		assertTrue(mineFound);
	}
	
	@Test
	public void testUncoverSquaresWhichAreNotMines() throws Exception {
		Square squares[][] = this.board.getSquares();
		for(int row=0; row<squares.length; row++) {
			Square squareRow[] = squares[row];
			for(int col=0; col<squareRow.length; col++) {
				Square square = squareRow[col];
				if(!square.isMine()) {
					// this should uncover the Square object we are holding
					this.board.uncover(new Point(row, col));
					Square squareFromBoard = getSquareUsingReflection(this.board, new Point(row, col));
					assertEquals(Square.SquareState.UNCOVERED, squareFromBoard.getState());
				}
			}
		}
	}
	
	@Test
	public void testUncoverSquaresWhichAreMines() throws Exception {
		Square squares[][] = this.board.getSquares();
		for(int row=0; row<squares.length; row++) {
			Square squareRow[] = squares[row];
			for(int col=0; col<squareRow.length; col++) {
				Square square = squareRow[col];
				if(square.isMine()) {
					try {
						// expect an exception
						this.board.uncover(new Point(row, col));
						// control should never come here, because invoking the line above should throw an Exception
						fail("Exception was not thrown when a mine was uncovered");
					} catch(UncoveredMineException eme) {
						// this is what we expect... so the code passes
					}
				}
			}
		}
	}
	
	@Test
	public void testUncoverSquaresWhichAreAlreadyUncovered() throws Exception {
		Square squares[][] = this.board.getSquares();
		for(int row=0; row<squares.length; row++) {
			Square squareRow[] = squares[row];
			for(int col=0; col<squareRow.length; col++) {
				Square square = squareRow[col];
				if(!square.isMine()) {
					this.board.uncover(new Point(row, col));
					assertEquals(Square.SquareState.UNCOVERED, 
								 getSquareUsingReflection(this.board, new Point(row, col)).getState());
					// uncover the same square and verify that it's state is still UNCOVERED
					this.board.uncover(new Point(row, col));
					assertEquals(Square.SquareState.UNCOVERED, 
								 getSquareUsingReflection(this.board, new Point(row, col)).getState());
				}
			}
		}
	}
	
	@Test
	public void testMarkSquare() throws Exception {
		Square squares[][] = this.board.getSquares();
		for(int row=0; row<squares.length; row++) {
			Square squareRow[] = squares[row];
			for(int col=0; col<squareRow.length; col++) {
				this.board.mark(new Point(row, col));
				assertEquals(Square.SquareState.MARKED, 
							 getSquareUsingReflection(this.board, new Point(row, col)).getState());				
			}
		}
	}
	
	@Test
	public void testMarkSquaresWhichAreAlreadyMarked() throws Exception {
		Square squares[][] = this.board.getSquares();
		for(int row=0; row<squares.length; row++) {
			Square squareRow[] = squares[row];
			for(int col=0; col<squareRow.length; col++) {
				Square square = squareRow[col];
				this.board.mark(new Point(row, col));
				assertEquals(Square.SquareState.MARKED, 
							 getSquareUsingReflection(this.board, new Point(row, col)).getState());
				// mark the square again and verify that the state is changed to COVERED
				this.board.mark(new Point(row, col));
				assertEquals(Square.SquareState.COVERED, square.getState());
			}
		}
	}

	@Test
	public void testMarkSquaresWhichAreUncovered() throws Exception {
		Square squares[][] = this.board.getSquares();
		for(int row=0; row<squares.length; row++) {
			Square squareRow[] = squares[row];
			for(int col=0; col<squareRow.length; col++) {
				Square square = squareRow[col];
				if(!square.isMine()) {
					// this should uncover the Square object we are holding
					this.board.uncover(new Point(row, col));
					assertEquals(Square.SquareState.UNCOVERED, getSquareUsingReflection(this.board, new Point(row, col)).getState());
					// marking an uncovered Square should have no effect
					this.board.uncover(new Point(row, col));
					assertEquals(Square.SquareState.UNCOVERED, getSquareUsingReflection(this.board, new Point(row, col)).getState());
				}
			}
		}
	}
	
	@Test
	public void testUncoverASquareWhichIsAlreadyMarked() throws Exception {
		Square squares[][] = this.board.getSquares();
		for(int row=0; row<squares.length; row++) {
			Square squareRow[] = squares[row];
			for(int col=0; col<squareRow.length; col++) {
				Square square = squareRow[col];
				this.board.mark(new Point(row, col));
				assertEquals(Square.SquareState.MARKED, getSquareUsingReflection(this.board, new Point(row, col)).getState());
				// uncovering a marked square should have no effect
				this.board.uncover(new Point(row, col));
				assertEquals(Square.SquareState.MARKED, getSquareUsingReflection(this.board, new Point(row, col)).getState());
			}
		}
	}
	
	@Test
	public void testSquareCount() throws Exception {
		Square squares[][] = this.board.getSquares();
		for(int row=0; row<Board.MAX_ROWS; row++) {
			for(int col=0; col<Board.MAX_COLS; col++) {
				assertEquals(MockMineUtils.getSquareCount(new Point(row, col)),
							 squares[row][col].getCount());
			}
		}
	}
	
	// Defensive coding tests
	
	@Test
	public void testSquaresGridSize() throws Exception {
		Square squares[][] = this.board.getSquares();
		assertEquals(Board.MAX_ROWS, squares.length);
		for(Square squareRow[] : squares) {
			assertEquals(Board.MAX_COLS, squareRow.length);
		}
	}
	
	@Test
	public void testDefensiveCopyingSetSquares() throws Exception {
		// create squares which we will set
		Square squares[][] = new Square[Board.MAX_ROWS][Board.MAX_COLS];
		for(int row=0; row<Board.MAX_ROWS; row++) {
			for(int col=0; col<Board.MAX_COLS; col++) {
				squares[row][col] = new Square();
			}
		}
		// set them
		this.board.setSquares(squares);
		
		//change an original square and verify that the change is not reflected in the corresponding square in the board
		squares[0][0].setMine(true);
		
		Square squareInBoard = getSquareUsingReflection(this.board, new Point(0, 0));
		assertFalse(squareInBoard.isMine());
	}
	
	@Test
	public void testDefensiveCopyingGetSquares() throws Exception {
		Square squaresFromGetSquares[][] = this.board.getSquares();
		// we know from HardcodedMineInitializationStrategy that Point 0,0 is a mine
		
		squaresFromGetSquares[0][0].setMine(false); // changing to not a mine
		
		// verify that the change is not reflected in the Board
		Square squareInBoard = getSquareUsingReflection(this.board, new Point(0, 0));
		assertTrue(squareInBoard.isMine());
	}
	
	@Test
	public void testDefensiveCopyFromGetSquaresIsAccurate() throws Exception {
		Square squaresFromGetSquares[][] = this.board.getSquares();
		
		// verify the squares we obtained are accurate		
		List<Point> mines = MockMineUtils.mines(new Point(Board.MAX_ROWS,Board.MAX_COLS));
		for(int row=0; row<squaresFromGetSquares.length; row++) {
			Square squareRow[] = squaresFromGetSquares[row];
			for(int col=0; col<squareRow.length; col++) {
				Square square = squareRow[col];
				Point squarePoint = new Point(row, col);
				assertEquals(MockMineUtils.getSquareCount(squarePoint), square.getCount());
				if(mines.contains(squarePoint)) {
					assertTrue(square.isMine());
				} else {
					assertFalse(square.isMine());
				}
			}
		}
		
		
	}
	
	
	
	
	private Square getSquareUsingReflection(Board board, Point p) 
			throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field boardFieldSquare = Board.class.getDeclaredField("squares");
		boardFieldSquare.setAccessible(true);
		Square squares[][] = (Square[][])boardFieldSquare.get(board);
		return squares[p.row][p.col];
	}
}

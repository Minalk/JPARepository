package com.diycomputerscience.minesweeper.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.diycomputerscience.minesweeper.Board;
import com.diycomputerscience.minesweeper.MockMineUtils;
import com.diycomputerscience.minesweeper.Point;
import com.diycomputerscience.minesweeper.Square;

public class BoardDaoJDBCTest {

	private Connection conn;
	private BoardDao boardDao;
	
	private static final String URL = "jdbc:hsqldb:mem:jminesweeper";
	private static final String USERNAME = "SA";
	private static final String PASSWORD = "";
	private static final String DB_DRIVER = "org.hsqldb.jdbcDriver";
	
	@Before
	public void setUp() throws Exception {
		this.boardDao = new BoardDaoJDBC();
		
		Class.forName(DB_DRIVER).newInstance();
		conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
		DBInit.buildSchema(conn);
		DBInit.populateSquareStatus(conn);
	}
	
	@After
	public void tearDown() throws Exception {
		Statement stmt = conn.createStatement();
		stmt.execute("SHUTDOWN");
		conn.close();
	}
	
	@Test
	public void testLoad() throws Exception {
		createDataForBoard();
		Board board = this.boardDao.load(conn);
		Assert.assertNotNull(board);
	}		

	@Test
	public void testSave() throws Exception {
		Board board = MockMineUtils.buildBoardFromLayout(MockMineUtils.expectedLinesInSavedBoard);
		this.boardDao.save(conn, board);
		
		verifyRowCountInBoard(36);		
		
		// Verify a few rows
		String squaresQuery = "SELECT b.row, b.col, b.is_mine, b.status_id, ss.status FROM BOARD as b INNER JOIN SQUARE_STATUS as ss ON b.status_id = ss.id;";		
		Statement stmt = conn.createStatement();		
		ResultSet rs = stmt.executeQuery(squaresQuery);
		
		while(rs.next()) {
			int row = rs.getInt("row");
			int col = rs.getInt("col");
			Square expectedSquare = getSquareFromExpectedData(new Point(row, col));
			Assert.assertEquals(expectedSquare.isMine(), rs.getBoolean("is_mine"));
			Assert.assertEquals(expectedSquare.getState().toString(), rs.getString("status"));
		}
	}			

	@Test
	public void testDelete() throws Exception {
		createDataForBoard();
		this.boardDao.delete(conn);
		verifyRowCountInBoard(0);
	}

	private void createDataForBoard() throws Exception {		
		
		Map<String, Integer> squareStatusMap = buildSquareStatusMap();
		
		PreparedStatement pStmt = this.conn.prepareStatement("INSERT INTO BOARD(row, col, is_mine, status_id) VALUES(?, ?, ?, ?);");
		
		Pattern regexPattern = Pattern.compile(FilePersistenceStrategy.SQUARE_LOAD_REGEX);
		
		for(String line : MockMineUtils.expectedLinesInSavedBoard) {
			// Create a matcher object to perform matching on the line using the regex pattern
			Matcher matcher = regexPattern.matcher(line);
			matcher.find();
			
			// Get the first matching group. ie the pattern enclosed in the first () representing the row co-ordinate
			String sRow = matcher.group(1);
			int row = Integer.parseInt(sRow);
			
			// Get the second matching group, representing the col co-ordinate
			String sCol = matcher.group(2);
			int col = Integer.parseInt(sCol);
			
			// Get the third matching group, representing whether the square has been uncovered by the user
			String sState = matcher.group(3);			
			
			// Get the fourth matching group, representing whether the square is a mine
			String sIsMine = matcher.group(4);
			boolean mine = Boolean.parseBoolean(sIsMine);
			
			pStmt.setInt(1, row);
			pStmt.setInt(2, col);
			pStmt.setBoolean(3, mine);
			pStmt.setInt(4, squareStatusMap.get(sState));
			
			pStmt.executeUpdate();			
		}
	}

	private Map<String, Integer> buildSquareStatusMap() throws SQLException {
		Statement squareStatusRetrieveStmt = null;
		try {
			Map<String, Integer> squareStatusMap = new HashMap<String, Integer>();
			String SquareStateQuery = "SELECT * FROM SQUARE_STATUS;";
			squareStatusRetrieveStmt = this.conn.createStatement();
			ResultSet rs = squareStatusRetrieveStmt.executeQuery(SquareStateQuery);
			while(rs.next()) {
				int id = rs.getInt("id");
				String status = rs.getString("status");
				squareStatusMap.put(status, id);
			}
			return squareStatusMap;
		} finally {
			squareStatusRetrieveStmt.close();
		}
	}
	
	private Square getSquareFromExpectedData(Point point) {
		Pattern regexPattern = Pattern.compile(FilePersistenceStrategy.SQUARE_LOAD_REGEX);
		
		for(String line : MockMineUtils.expectedLinesInSavedBoard) {
			// Create a matcher object to perform matching on the line using the regex pattern
			Matcher matcher = regexPattern.matcher(line);
			matcher.find();
			
			// Get the first matching group. ie the pattern enclosed in the first () representing the row co-ordinate
			String sRow = matcher.group(1);
			int row = Integer.parseInt(sRow);
			
			// Get the second matching group, representing the col co-ordinate
			String sCol = matcher.group(2);
			int col = Integer.parseInt(sCol);
			
			// Get the third matching group, representing whether the square has been uncovered by the user
			String sState = matcher.group(3);			
			
			// Get the fourth matching group, representing whether the square is a mine
			String sIsMine = matcher.group(4);
			boolean mine = Boolean.parseBoolean(sIsMine);
			
			if(row == point.row && col == point.col) {
				Square square = new Square();
				square.setMine(mine);
				square.setState(Square.SquareState.valueOf(sState));
				return square;
			}
			
		}
		throw new IllegalArgumentException("Could not find Square data for Point " + point);
	}
	
	private void verifyRowCountInBoard(int expectedCount) throws Exception {
		String squareCountQuery = "select COUNT(*) AS squares from BOARD;";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(squareCountQuery);
		int squareCount = -1;
		while(rs.next()) {
			squareCount = rs.getInt("squares");
		}
		Assert.assertEquals(expectedCount, squareCount);		
	}
}

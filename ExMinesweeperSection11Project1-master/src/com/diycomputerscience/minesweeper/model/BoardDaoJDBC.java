package com.diycomputerscience.minesweeper.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.diycomputerscience.minesweeper.Board;
import com.diycomputerscience.minesweeper.Point;
import com.diycomputerscience.minesweeper.Square;

public class BoardDaoJDBC implements BoardDao {

	private static Logger logger = Logger.getLogger(BoardDaoJDBC.class);
	
	/**
	 * @see BoardDao
	 */
	@Override	
	public Board load(Connection conn) throws PersistenceException {
		// TODO: Implement this method
		Board board = new Board();
		
		Square squares[][] = new Square[Board.MAX_ROWS][Board.MAX_COLS];
		
		String squaresQuery = "SELECT b.row, b.col, b.is_mine, b.status_id, ss.status FROM BOARD as b INNER JOIN SQUARE_STATUS as ss ON b.status_id = ss.id";		
		try{
			Statement stmt = conn.createStatement();		
			ResultSet rs = stmt.executeQuery(squaresQuery);
			if(rs != null){
				while(rs.next()) {
					
				int row = rs.getInt("ROW");
				int col = rs.getInt("COL");
				
				
				boolean ismine = rs.getBoolean("IS_MINE");
				int statusInt = rs.getInt(4);
				String statusStr = rs.getString(5);
				
				Square.SquareState  status = Square.SquareState.valueOf(statusStr);
				Square expectedSquare = new Square();
				
				expectedSquare.setMine(ismine);
				expectedSquare.setState(status);
				squares[row][col] = expectedSquare;
				}
			}
			}
			catch(SQLException sqlEx){
				return null;
			}
			if(squares!= null){
				board.setSquares(squares);
				board.computeCounts();
				
			}
			return board;
		}	
	

	
	@Override
	public void save(Connection conn, Board board) throws PersistenceException {
		// TODO; Implement this method
		Square squares[][] = board.getSquares();
		
		PreparedStatement insertSquareStmt = null;
		Map<String, Integer> squareStatusMap = this.getStatusMap(conn);
		
		String insertSquareStr = "Insert into Board values(?,?,?,?)";
		try {
			insertSquareStmt = conn.prepareStatement(insertSquareStr);
			int row=-1;
			int col= -1;
			boolean ismine= false;
						
			for(int i=0;i< squares.length;i++){
						
				for(int j=0;j<squares[i].length;j++){
					row = i;
					col = j;
					ismine = squares[i][j].isMine();
					Square.SquareState statusStr = squares[i][j].getState();
					
					insertSquareStmt.setInt(1, row);
					insertSquareStmt.setInt(2, col);
					insertSquareStmt.setBoolean(3, ismine);
					insertSquareStmt.setInt(4,squareStatusMap.get(statusStr.toString()));
			
					insertSquareStmt.addBatch();
				}
				
			}
			
			insertSquareStmt.executeBatch();
			
			
			
		}
		catch(SQLException sqlEx){
			System.out.println("SQLException in save "+sqlEx);
		}	
		
			
	}

	private Map<String, Integer> getStatusMap(Connection conn) {
		// TODO Auto-generated method stub
		
		String selectStatus = "SELECT * FROM SQUARE_STATUS";
		Map<String, Integer> squareStatusMap = new HashMap<String, Integer>();
		
		try{
			Statement stmt = conn.createStatement();		
			ResultSet rs = stmt.executeQuery(selectStatus );
			
			while(rs.next()) {
				int id = rs.getInt(1);
				String status = rs.getString(2);
				squareStatusMap.put(status, id);
			}	
		}
		catch(SQLException sqlEx){
			System.out.println("Exception while selecting data from Square table "+sqlEx);
		}
		
		return squareStatusMap;
	}



	@Override
	public void delete(Connection conn) throws PersistenceException {
		// TODO: Implement this method
		try{
			Statement sqlStatement = conn.createStatement();
			
			String deleteTableStr = "DELETE FROM BOARD";
			int deletecnt = sqlStatement.executeUpdate(deleteTableStr);
			if(deletecnt >0)
				conn.commit();
		}
		catch(SQLException sqlEx){
			System.out.println("Exception while delete "+sqlEx);
		}
			
	}

}

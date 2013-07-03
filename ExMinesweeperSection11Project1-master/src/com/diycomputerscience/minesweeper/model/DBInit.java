package com.diycomputerscience.minesweeper.model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.diycomputerscience.minesweeper.Square;

public class DBInit {

	public static boolean schemaExists(Connection conn) throws SQLException {
		DatabaseMetaData dbMeta = conn.getMetaData();
		
		ResultSet rs = dbMeta.getTables(null, null, "BOARD", null);
		if(!rs.next()) {
			return false;
		}
		
		rs = dbMeta.getTables(null, null, "SQUARE_STATUS", null);
		if(!rs.next()) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Creates the following 2 tables
	 * 
	 * TableName: SQUARE_STATUS
	 * ****************************************
	 * * id     * INT PRIMARY KEY             *
	 * ****************************************
	 * * status * VARCHAR(128) UNIQUE NOT NULL*
	 * ****************************************
	 * 
	 * TableName: BOARD
	 * ***************************************************
	 * * row       * INT NOT NULL                        *
	 * ***************************************************
	 * * col       * INT NOT NULL                        *
	 * ***************************************************
	 * * is_mine   * BOOLEAN NOT NULL                    *
	 * ***************************************************
	 * * status_id * INT NOT NULL                        *
	 * ***************************************************
	 * * PRIMARY KEY (row, col)                          *
	 * * FOREIGN KEY status_id to the SQUARE_STATUS table*
	 * ***************************************************
	 * 
	 */
	public static boolean buildSchema(Connection conn) throws SQLException {
		// TODO: Implement this method
		Statement sqlStatement = conn.createStatement();
		
		String createTableStr = "CREATE TABLE SQUARE_STATUS(ID INTEGER PRIMARY KEY ,STATUS VARCHAR(128)UNIQUE NOT NULL)";
		sqlStatement.execute(createTableStr);
		createTableStr = "CREATE TABLE BOARD(ROW INTEGER NOT NULL,COL INTEGER NOT NULL,IS_MINE BOOLEAN NOT NULL,STATUS_ID INTEGER NOT NULL,PRIMARY KEY(ROW,COL),FOREIGN KEY(STATUS_ID) REFERENCES SQUARE_STATUS(ID))";
		sqlStatement.execute(createTableStr);
		
		return true;
	}
	
	/**
	 * Adds data to the SQUARE_STATUS table
	 * ************************************
	 * * id    * status                   *
	 * ************************************
	 * * 1     * COVERED                  *
	 * ************************************
	 * * 1     * UNCOVERED                *
	 * ************************************
	 * * 1     * MARKED                   *
	 * ************************************
	 * @param conn
	 * @throws SQLException
	 */
	public static void populateSquareStatus(Connection conn) throws SQLException {
		// TODO: Implement this method
		// TODO: Do not hard code the values, rather use a PreparedStatement and iterate through the SquareState enum
		PreparedStatement insertSquareStatus = null;
		
		String insertSquareStr = "Insert into Square_Status values(?,?)";
		insertSquareStatus = conn.prepareStatement(insertSquareStr);
		
		insertSquareStatus.setInt(1, 1);
		insertSquareStatus.setString(2, Square.SquareState.COVERED.toString());
		insertSquareStatus.executeUpdate();
		
		insertSquareStatus.setInt(1, 2);
		insertSquareStatus.setString(2, Square.SquareState.UNCOVERED.toString());
		insertSquareStatus.executeUpdate();
		
		insertSquareStatus.setInt(1, 3);
		insertSquareStatus.setString(2,Square.SquareState.MARKED.toString());
		insertSquareStatus.executeUpdate();
	}
}

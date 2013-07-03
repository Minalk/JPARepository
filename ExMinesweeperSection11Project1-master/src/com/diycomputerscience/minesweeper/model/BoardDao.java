package com.diycomputerscience.minesweeper.model;

import java.sql.Connection;

import com.diycomputerscience.minesweeper.Board;

/**
 * This interface defines the methods that a Board Data Access Object would need to
 * persist it's data in a database. The schema of the database is described in the class
 * {@link DBInit}
 */
public interface BoardDao {

	/**
	 * Queries the database to get the state of the last saved Board, and creates 
	 * an instance of Board from this data 
	 * 
	 * @param conn The JDBC Connection object to use for querying the database
	 * @return The constructed Board object
	 * @throws PersistenceException If data could not be read from the data source
	 */
	public Board load(Connection conn) throws PersistenceException;
	
	/**
	 * Saves the state of the specified Board object to the database
	 * 
	 * @param conn A JDBC connection object to connect to the database
	 * @param board The Board object to save
	 * @throws PersistenceException If the data could not be saved to the database
	 */
	public void save(Connection conn, Board board) throws PersistenceException;
	
	/**
	 * Since at the moment we assume that only the last saved Board data is
	 * relevant, this method should delete all rows from the BOARD table
	 * 
	 * @param conn The JDBC Connection to use to connect to the database
	 * @throws PersistenceException If the data could not be deleted
	 */
	public void delete(Connection conn) throws PersistenceException;
	
}

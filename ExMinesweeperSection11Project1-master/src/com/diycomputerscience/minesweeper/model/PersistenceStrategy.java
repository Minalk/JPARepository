package com.diycomputerscience.minesweeper.model;

import java.util.Properties;

import com.diycomputerscience.minesweeper.Board;
import com.diycomputerscience.minesweeper.ConfigurationException;

public interface PersistenceStrategy {
	
	/**
	 * Configures this instance of PersistenceStrategy with the supplied properties 
	 * @param properties The properties containing all keys that this  instance of PersistenceStrategy needs
	 * @throws ConfigurationException If a property key is missing or if a key has an incorrect value 
	 */
	public void configure(Properties properties) throws ConfigurationException;
	
	
	/**
	 * Save the specified Board to some form of (persistent) storage. We work with a basic assumption
	 * that only one Board object will persist at a time in the storage.
	 * @param board The specified Board to save
	 * @throws PersistenceException If the Board cannot be saved for some reason
	 */
	public void save(Board board) throws PersistenceException;
	
	
	/**
	 * Loads the last saved Board object from persistent storage and returns a corresponding
	 * Board object
	 * @return A Board object created from the last saved object
	 * @throws PersistenceException
	 */
	public Board load() throws PersistenceException;
}

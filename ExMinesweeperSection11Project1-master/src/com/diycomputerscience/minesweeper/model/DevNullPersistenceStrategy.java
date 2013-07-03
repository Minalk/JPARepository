package com.diycomputerscience.minesweeper.model;

import java.util.Properties;

import com.diycomputerscience.minesweeper.Board;
import com.diycomputerscience.minesweeper.ConfigurationException;

public class DevNullPersistenceStrategy implements PersistenceStrategy {

	public void configure(Properties configProps) throws ConfigurationException {
		// we do not want to do anything
	}
	
	@Override
	public void save(Board board) throws PersistenceException {		
		throw new PersistenceException("DevNullPersistenceStrategy cannot save the Board");
	}

	@Override
	public Board load() throws PersistenceException {
		throw new PersistenceException("DevNullPersistenceStrategy cannot load the Board");
	}

}

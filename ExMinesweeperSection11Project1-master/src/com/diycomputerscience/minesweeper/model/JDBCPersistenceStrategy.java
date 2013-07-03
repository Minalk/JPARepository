package com.diycomputerscience.minesweeper.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.diycomputerscience.minesweeper.Board;
import com.diycomputerscience.minesweeper.ConfigurationException;

public class JDBCPersistenceStrategy implements PersistenceStrategy {
	
	private String url;
	private String username;
	private String password;
	
	private BoardDao boardDao;
	
	public static final String DB_URL_CONFIG_KEY = "db.url";	
	public static final String DB_USERNAME_CONFIG_KEY = "db.username";
	public static final String DB_PASSWORD_CONFIG_KEY = "db.password";
	public static final String DB_DRIVER_CONFIG_KEY = "db.driver";
	
	private static final Logger logger = Logger.getLogger(JDBCPersistenceStrategy.class); 			

	public JDBCPersistenceStrategy() {
		
	}

	@Override
	public void configure(Properties config) throws ConfigurationException {
		this.url = config.getProperty(DB_URL_CONFIG_KEY);
		this.username = config.getProperty(DB_USERNAME_CONFIG_KEY);
		this.password = config.getProperty(DB_PASSWORD_CONFIG_KEY);
		String dbDriver = config.getProperty(DB_DRIVER_CONFIG_KEY);
		
		if(this.url == null) {
			throw new ConfigurationException("Property not found '" + DB_URL_CONFIG_KEY + "'");			
		}
		
		if(this.username == null) {
			throw new ConfigurationException("Property not found '" + DB_USERNAME_CONFIG_KEY + "'");
		}
		
		if(this.password == null) {
			throw new ConfigurationException("Property not found '" + DB_PASSWORD_CONFIG_KEY + "'");
		}
		
		if(dbDriver == null) {
			throw new ConfigurationException("Property not found '" + DB_DRIVER_CONFIG_KEY + "'");
		}
		
		try {
			Class.forName(dbDriver);
			buildSchema(url, username, password);
		} catch (ClassNotFoundException ce) {
			throw new ConfigurationException("Could not load database driver", ce);
		} catch(PersistenceException pe) {
			throw new ConfigurationException("Could not create initial schema", pe);
		}
		
		this.boardDao = new BoardDaoJDBC();
	}
	
	public void setBoardDao(BoardDao boardDao) {
		this.boardDao = boardDao;
	}
	
	@Override
	public void save(Board board) throws PersistenceException {			
		if(board == null) {
			throw new NullPointerException("board cannot be null");
		}
		
		Connection conn = null;					
		
		try {
			
			conn = DriverManager.getConnection(url, username, password);
			// delete the current board (let the participant figure this out !)
			this.boardDao.delete(conn);
			this.boardDao.save(conn, board);
		} catch(SQLException sqle) {
			String msg = "Could not save Board";
			logger.error(msg, sqle);
			throw new PersistenceException(msg, sqle);
		} finally {
			try {
				if(conn != null) {
					conn.close();
				}				
			} catch(SQLException sqle) {
				logger.warn("Could not close database connection", sqle);
			}
		}
	}

	@Override
	public Board load() throws PersistenceException {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, username, password);
			return this.boardDao.load(conn);
		} catch(SQLException sqle) {
			throw new PersistenceException("Could not load Board", sqle);
		} finally {
			try {
				if(conn != null) {
					conn.close();
				}				
			} catch(SQLException sqle) {
				logger.warn("Could not close database connection", sqle);
			}
		}
	}
	
	private static void buildSchema(String url, String username, String password) throws PersistenceException {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, username, password);
			if(!DBInit.schemaExists(conn)) {
				DBInit.buildSchema(conn);
				DBInit.populateSquareStatus(conn);
			}
		} catch(SQLException sqle) {
			throw new PersistenceException("Could not build initial schema", sqle);
		} finally {
			try {
				if(conn != null) {
					conn.close();
				}				
			} catch(SQLException sqle) {
				logger.warn("Could not close database connection", sqle);
			}
		}		
	}	

}

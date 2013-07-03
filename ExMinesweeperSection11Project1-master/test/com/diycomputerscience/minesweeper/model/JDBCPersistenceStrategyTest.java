package com.diycomputerscience.minesweeper.model;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.diycomputerscience.minesweeper.Board;
import com.diycomputerscience.minesweeper.ConfigurationException;
import com.diycomputerscience.minesweeper.MineInitializationStrategy;
import com.diycomputerscience.minesweeper.MockMineUtils;
import com.diycomputerscience.minesweeper.Point;

public class JDBCPersistenceStrategyTest {

	private MineInitializationStrategy mineInitializationStrategy;
	private Board board;
	private JDBCPersistenceStrategy persistenceStrategy;
	private Properties config;
	private static final String URL = "jdbc:hsqldb:mem:jminesweeper";
	private static final String USERNAME = "SA";
	private static final String PASSWORD = "";
	private static final String DB_DRIVER = "org.hsqldb.jdbcDriver";		
	
	@Before
	public void setUp() throws Exception {
		this.mineInitializationStrategy = EasyMock.createMock(MineInitializationStrategy.class);
		Point boardSize = new Point(Board.MAX_ROWS, Board.MAX_ROWS);
		expect(this.mineInitializationStrategy.mines(boardSize)).andReturn(MockMineUtils.mines(boardSize));
		replay(this.mineInitializationStrategy);
		
		this.board = new Board(mineInitializationStrategy);
		
		this.config = new Properties();
		config.setProperty(JDBCPersistenceStrategy.DB_URL_CONFIG_KEY, URL);
		config.setProperty(JDBCPersistenceStrategy.DB_USERNAME_CONFIG_KEY, USERNAME);
		config.setProperty(JDBCPersistenceStrategy.DB_PASSWORD_CONFIG_KEY, PASSWORD);
		config.setProperty(JDBCPersistenceStrategy.DB_DRIVER_CONFIG_KEY, DB_DRIVER);
	}

	@After
	public void tearDown() throws Exception {
		Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
		Statement stmt = conn.createStatement();
		stmt.execute("SHUTDOWN");
		conn.close();
	}

	@Test
	public void testSuccessfullBuild() throws Exception {				
		this.persistenceStrategy = new JDBCPersistenceStrategy();
		this.persistenceStrategy.configure(config);
		
		Assert.assertNotNull(this.persistenceStrategy);
	}
	
	@Test(expected=ConfigurationException.class)
	public void testUnsuccessfullBuildBecauseDriverIsNull() throws Exception {		
		config.remove(JDBCPersistenceStrategy.DB_DRIVER_CONFIG_KEY);
		this.persistenceStrategy = new JDBCPersistenceStrategy();
		this.persistenceStrategy.configure(config);
	}
	
	@Test(expected=ConfigurationException.class)
	public void testUnsuccessfullBuildBecauseUrlIsNull() throws Exception {		
		config.remove(JDBCPersistenceStrategy.DB_URL_CONFIG_KEY);
		this.persistenceStrategy = new JDBCPersistenceStrategy();
		this.persistenceStrategy.configure(config);
	}
	
	@Test(expected=ConfigurationException.class)
	public void testUnsuccessfullBuildBecauseUsernameIsNull() throws Exception {
		config.remove(JDBCPersistenceStrategy.DB_USERNAME_CONFIG_KEY);
		this.persistenceStrategy = new JDBCPersistenceStrategy();
		this.persistenceStrategy.configure(config);
	}
	
	@Test(expected=ConfigurationException.class)
	public void testUnsuccessfullBuildBecausePasswordIsNull() throws Exception {
		config.remove(JDBCPersistenceStrategy.DB_PASSWORD_CONFIG_KEY);		
		this.persistenceStrategy = new JDBCPersistenceStrategy();
		this.persistenceStrategy.configure(config);
	}
	
	@Test
	public void testSave() throws Exception {
		BoardDao mockBoardDao = EasyMock.createMock(BoardDao.class);
		mockBoardDao.delete((Connection)EasyMock.anyObject());
		mockBoardDao.save((Connection)EasyMock.anyObject(), eq(this.board));
		replay(mockBoardDao);
		
		this.persistenceStrategy = new JDBCPersistenceStrategy();
		this.persistenceStrategy.configure(this.config);
		this.persistenceStrategy.setBoardDao(mockBoardDao);
		this.persistenceStrategy.save(this.board);
		verify(mockBoardDao);
	}
	
	@Test(expected=NullPointerException.class)
	public void testSaveForNullBoard() throws Exception {
		BoardDao mockBoardDao = EasyMock.createMock(BoardDao.class);
		mockBoardDao.save((Connection)EasyMock.anyObject(), eq(this.board));
		replay(mockBoardDao);
		
		this.persistenceStrategy = new JDBCPersistenceStrategy();
		this.persistenceStrategy.configure(this.config);
		this.persistenceStrategy.setBoardDao(mockBoardDao);
		this.persistenceStrategy.save(null);
	}
	
	@Test(expected=ConfigurationException.class)
	public void testSaveForBadURL() throws Exception {
		BoardDao mockBoardDao = EasyMock.createMock(BoardDao.class);		
		replay(mockBoardDao);
		
		this.persistenceStrategy = new JDBCPersistenceStrategy();
		this.config.setProperty(JDBCPersistenceStrategy.DB_URL_CONFIG_KEY, "bad_url");
		this.persistenceStrategy.configure(this.config);
		this.persistenceStrategy.setBoardDao(mockBoardDao);
		this.persistenceStrategy.save(this.board);
	}
	
	@Test
	public void testLoad() throws Exception {
		Board expectedBoard = new Board();
		BoardDao mockBoardDao = EasyMock.createMock(BoardDao.class);
		expect(mockBoardDao.load((Connection)anyObject())).andReturn(expectedBoard);
		replay(mockBoardDao);
		
		this.persistenceStrategy = new JDBCPersistenceStrategy();
		this.persistenceStrategy.configure(this.config);
		this.persistenceStrategy.setBoardDao(mockBoardDao);
		
		Board board = this.persistenceStrategy.load();
		Assert.assertEquals(expectedBoard, board);
		verify(mockBoardDao);
	}
	
	@Test(expected=ConfigurationException.class)
	public void testLoadWithBadURL() throws Exception {
		Board expectedBoard = new Board();
		BoardDao mockBoardDao = EasyMock.createMock(BoardDao.class);
		expect(mockBoardDao.load((Connection)anyObject())).andReturn(expectedBoard);
		replay(mockBoardDao);
		
		this.persistenceStrategy = new JDBCPersistenceStrategy();
		this.config.setProperty(JDBCPersistenceStrategy.DB_URL_CONFIG_KEY, "bad_url");
		this.persistenceStrategy.configure(this.config);
		this.persistenceStrategy.setBoardDao(mockBoardDao);
		
		Board board = this.persistenceStrategy.load();		
	}

}

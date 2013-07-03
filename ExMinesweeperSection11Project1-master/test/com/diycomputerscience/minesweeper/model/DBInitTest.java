package com.diycomputerscience.minesweeper.model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.diycomputerscience.minesweeper.Square;

public class DBInitTest {
	
	private Connection conn;
	
	@Before
	public void setUp() throws Exception {
		Class.forName("org.hsqldb.jdbcDriver").newInstance();
		conn = DriverManager.getConnection("jdbc:hsqldb:mem:jminesweeper", "SA", "");
	}

	@After
	public void tearDown() throws Exception {
		Statement stmt = this.conn.createStatement();
		stmt.execute("SHUTDOWN");
		conn.close();
	}

	@Test
	public void testBuildSchema() throws Exception {
		DBInit.buildSchema(this.conn);
		
		// Verify that the schema was built
		DatabaseMetaData dbMeta = conn.getMetaData();
		
		ResultSet rs = dbMeta.getTables(null, null, "BOARD", null);
		if(!rs.next()) {
			Assert.fail("Schema was not created");
		}
	}
	
	@Test
	public void testPopulateSquareStatus() throws Exception {
		DBInit.buildSchema(conn);
		DBInit.populateSquareStatus(conn);
		String query = "SELECT * FROM SQUARE_STATUS";
		
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		
		
		List<String> statusLst = new ArrayList<String>();
		while(rs.next()) {
			String status = rs.getString("status");			
			statusLst.add(status);
		}
		
		// verify that 3 status codes were added
		Assert.assertEquals(Square.SquareState.values().length, statusLst.size());
		for(Square.SquareState squareStatus : Square.SquareState.values()) {
			Assert.assertTrue(statusLst.contains(squareStatus.toString()));
		}
		
	}

}

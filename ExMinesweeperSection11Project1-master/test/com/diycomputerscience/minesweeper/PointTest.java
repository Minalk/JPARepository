package com.diycomputerscience.minesweeper;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PointTest {

	private Point p;
	private int row = 4;
	private int col = 2;
	
	@Before
	public void setUp() throws Exception {
		this.p = new Point(this.row, this.col);
	}

	@After
	public void tearDown() throws Exception {
		this.p = null;
	}

	@Test
	public void test() {
		assertEquals(row, this.p.row);
		assertEquals(col, this.p.col);
	}

}

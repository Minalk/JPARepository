package com.diycomputerscience.minesweeper.utils;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diycomputerscience.minesweeper.Board;
import com.diycomputerscience.minesweeper.Point;

public class MinesweeperUtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testComputeNeibhboursTopLeftCorner() {
		List<Point> neighbours = MinesweeperUtils.computeNeibhbours(new Point(0, 0));
		assertEquals(3, neighbours.size());
		assertTrue(neighbours.contains(new Point(0, 1)));
		assertTrue(neighbours.contains(new Point(1, 1)));
		assertTrue(neighbours.contains(new Point(1, 0)));
	}
	
	@Test
	public void testComputeNeibhboursTopRow() {
		List<Point> neighbours = MinesweeperUtils.computeNeibhbours(new Point(0, 1));
		assertEquals(5, neighbours.size());		
		assertTrue(neighbours.contains(new Point(0, 2)));
		assertTrue(neighbours.contains(new Point(1, 2)));
		assertTrue(neighbours.contains(new Point(1, 1)));
		assertTrue(neighbours.contains(new Point(1, 0)));
		assertTrue(neighbours.contains(new Point(0, 0)));
	}
	
	@Test
	public void testComputeNeibhboursTopRightCorner() {
		List<Point> neighbours = MinesweeperUtils.computeNeibhbours(new Point(0, Board.MAX_COLS-1));
		assertEquals(3, neighbours.size());		
		assertTrue(neighbours.contains(new Point(1, Board.MAX_COLS-1)));
		assertTrue(neighbours.contains(new Point(1, Board.MAX_COLS-2)));
		assertTrue(neighbours.contains(new Point(0, Board.MAX_COLS-2)));		
	}
	
	@Test
	public void testComputeNeibhboursRightRow() {
		List<Point> neighbours = MinesweeperUtils.computeNeibhbours(new Point(1, Board.MAX_COLS-1));
		assertEquals(5, neighbours.size());		
		assertTrue(neighbours.contains(new Point(2, Board.MAX_COLS-1)));
		assertTrue(neighbours.contains(new Point(2, Board.MAX_COLS-2)));
		assertTrue(neighbours.contains(new Point(1, Board.MAX_COLS-2)));
		assertTrue(neighbours.contains(new Point(0, Board.MAX_COLS-2)));
		assertTrue(neighbours.contains(new Point(0, Board.MAX_COLS-1)));
	}
	
	@Test
	public void testComputeNeibhboursBottomRightCorner() {
		List<Point> neighbours = MinesweeperUtils.computeNeibhbours(new Point(Board.MAX_ROWS-1, Board.MAX_COLS-1));
		assertEquals(3, neighbours.size());
		assertTrue(neighbours.contains(new Point(Board.MAX_ROWS-1, Board.MAX_COLS-2)));
		assertTrue(neighbours.contains(new Point(Board.MAX_ROWS-2, Board.MAX_COLS-2)));
		assertTrue(neighbours.contains(new Point(Board.MAX_ROWS-2, Board.MAX_COLS-1)));
	}
	
	@Test
	public void testComputeNeibhboursBottomRow() {
		List<Point> neighbours = MinesweeperUtils.computeNeibhbours(new Point(Board.MAX_ROWS-1, Board.MAX_COLS-2));
		assertEquals(5, neighbours.size());
		assertTrue(neighbours.contains(new Point(Board.MAX_ROWS-1, Board.MAX_COLS-1)));
		assertTrue(neighbours.contains(new Point(Board.MAX_ROWS-1, Board.MAX_COLS-3)));
		assertTrue(neighbours.contains(new Point(Board.MAX_ROWS-2, Board.MAX_COLS-3)));
		assertTrue(neighbours.contains(new Point(Board.MAX_ROWS-2, Board.MAX_COLS-2)));
		assertTrue(neighbours.contains(new Point(Board.MAX_ROWS-2, Board.MAX_COLS-1)));
	}
	
	@Test
	public void testComputeNeibhboursBottomLeftCorner() {
		List<Point> neighbours = MinesweeperUtils.computeNeibhbours(new Point(Board.MAX_ROWS-1, 0));
		assertEquals(3, neighbours.size());
		assertTrue(neighbours.contains(new Point(Board.MAX_ROWS-1, 1)));
		assertTrue(neighbours.contains(new Point(Board.MAX_ROWS-2, 0)));
		assertTrue(neighbours.contains(new Point(Board.MAX_ROWS-2, 1)));
	}
	
	@Test
	public void testComputeNeibhboursLeftColumn() {
		List<Point> neighbours = MinesweeperUtils.computeNeibhbours(new Point(1, 0));
		assertEquals(5, neighbours.size());
		assertTrue(neighbours.contains(new Point(1, 1)));
		assertTrue(neighbours.contains(new Point(2, 1)));
		assertTrue(neighbours.contains(new Point(2, 0)));
		assertTrue(neighbours.contains(new Point(0, 0)));
		assertTrue(neighbours.contains(new Point(0, 1)));
	}
	
	@Test
	public void testComputeNeibhboursCenter() {
		List<Point> neighbours = MinesweeperUtils.computeNeibhbours(new Point(1, 1));
		assertEquals(8, neighbours.size());
		assertTrue(neighbours.contains(new Point(1, 2)));
		assertTrue(neighbours.contains(new Point(2, 2)));
		assertTrue(neighbours.contains(new Point(2, 1)));
		assertTrue(neighbours.contains(new Point(2, 0)));
		assertTrue(neighbours.contains(new Point(1, 0)));
		assertTrue(neighbours.contains(new Point(0, 0)));
		assertTrue(neighbours.contains(new Point(0, 1)));
		assertTrue(neighbours.contains(new Point(0, 2)));
	}	
	
}

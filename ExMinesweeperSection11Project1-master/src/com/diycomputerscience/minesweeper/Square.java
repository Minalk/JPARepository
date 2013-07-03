package com.diycomputerscience.minesweeper;

import org.apache.log4j.Logger;

public class Square {

	public enum SquareState {COVERED, UNCOVERED, MARKED}
	
	private boolean mine;
	private int count;
	private SquareState state;
	
	private static Logger logger = Logger.getLogger(Square.class);
	
	public Square() {
		this.state = SquareState.COVERED;
	}
	
	/**
	 * Copy constructor
	 * @param square
	 */
	public Square(Square square) {
		if(square == null) {
			throw new NullPointerException("square cannot be null");
		}
		this.mine = square.isMine();
		this.count = square.count;
		this.state = square.state;
	}
	
	public boolean isMine() {
		return mine;
	}
	
	public void setMine(boolean mine) {
		this.mine = mine;
	}
	
	public int getCount() {
		return this.count;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
	public void setState(SquareState state) {
		this.state = state;
	}
	
	public SquareState getState() {
		return this.state;
	}

	public void uncover() throws UncoveredMineException {
		if(this.state.equals(SquareState.MARKED)) {
			return;
		} else {
			if(this.isMine()) {
				throw new UncoveredMineException("Uncovered a mine");
			}			
			this.state = SquareState.UNCOVERED;
		}
		logger.debug("Uncovered Square. New state is " + this.state);
	}

	public void mark() {
		if(this.state.equals(SquareState.UNCOVERED)) {
			return;
		} if(this.state.equals(SquareState.MARKED)) {
			this.state = SquareState.COVERED;
		} else {
			this.state = SquareState.MARKED;
		}
		logger.debug("Marked Square. New state is " + this.state);
	}

	@Override
	public String toString() {
		return this.mine + " " + this.state + " " + this.count;
	}
	
}

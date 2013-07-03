package com.diycomputerscience.minesweeper;

public class UncoveredMineException extends Exception {

	public UncoveredMineException() {}
	
	public UncoveredMineException(String msg) {
		super(msg);
	}
	
	public UncoveredMineException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public UncoveredMineException(Throwable cause) {
		super(cause);
	}
}

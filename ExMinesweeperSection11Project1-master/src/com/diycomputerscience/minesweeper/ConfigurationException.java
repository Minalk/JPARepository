package com.diycomputerscience.minesweeper;

public class ConfigurationException extends Exception {
	
	public ConfigurationException() {
		super();
	}
	
	public ConfigurationException(String msg) {
		super(msg);
	}
	
	public ConfigurationException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public ConfigurationException(Throwable cause) {
		super(cause);
	}
}

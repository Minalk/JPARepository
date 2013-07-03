package com.diycomputerscience.minesweeper.view;

import java.awt.Component;

/**
 * This interface is an abstract representation of an OptionPane meant to get a user's confirmation.
 * It will allow the use of different types of option panes in different scenarious. One possible 
 * use case is to use a JOptionPane in production code and a MockOptionPane in test code.
 *
 */
public interface OptionPane {

	/**
	 * 
	 * @param component The UI component (typically a JFrame) that is requesting the confirmation. null if a UI component is not involved 
	 * @param message The message to display to the user
	 * @param title The title of the option pane
	 * @param optionType Type of options that can be given. Must be one of the constants specified in JOptionPane
	 * @return The users response. Must choose from the options specified in JOptionPane
	 */
	public int userConfirmation(Component component, Object message, String title, int optionType);
	
}

package com.diycomputerscience.minesweeper.view;

import java.awt.Component;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

public class SwingOptionPane implements OptionPane {

	@Override
	public int userConfirmation(Component component, 
								Object message,
								String title, 
								int optionType) {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("MessageBundle");
		Object options[] = new Object[]{resourceBundle.getString("option.yes"), resourceBundle.getString("option.no")};
		return JOptionPane.showOptionDialog(component, message, title, optionType, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
	}

}

package com.diycomputerscience.minesweeper.view;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import org.easymock.Capture;
import org.fest.swing.core.MouseButton;
import org.fest.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class I18NTest extends BaseSwingTestCase {
	
	private FrameFixture window;
	private ResourceBundle resourceBundle;
	
	@Parameters
	public static Collection data() {
		List data = new ArrayList();
		data.add(new Object[]{Locale.US});
		data.add(new Object[]{Locale.FRANCE});
		return data;
	}
	
	public I18NTest(Locale locale) {
		locale.setDefault(locale);
	}
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		this.resourceBundle = ResourceBundle.getBundle("MessageBundle");		
		this.window = new FrameFixture(robot(), createUI());
		this.window.show();
	}

	@After
	public void tearDown() throws Exception {
		this.window.cleanUp();
		super.tearDown();
	}
	
	@Test
	public void testTitle() throws Exception {
		replay(optionPane, persistenceStrategy);
		
		assertEquals(this.resourceBundle.getString("title"), this.window.target.getTitle());		
	}		
	
	@Test
	public void testMenu() throws Exception {
		replay(optionPane, persistenceStrategy);
		
		assertEquals(this.resourceBundle.getString("menuitem.save"),
				     this.window.menuItem("file-save").target.getLabel());		
		
		assertEquals(this.resourceBundle.getString("menuitem.load"),
					 this.window.menuItem("file-load").target.getLabel());
		
		assertEquals(this.resourceBundle.getString("menuitem.close"),
					 this.window.menuItem("file-close").target.getLabel());
		
		assertEquals(this.resourceBundle.getString("menuitem.about"),
					 this.window.menuItem("help-about").target.getLabel());
		
	}
	
	@Test
	public void testGameoverDialogue() throws Exception {
		Capture<Component> captureOfComponent = new Capture<Component>();
		Capture<Object> captureOfObject = new Capture<Object>();
		Capture<String> captureOfString = new Capture<String>();
		Capture<Integer> captureOfInteger = new Capture<Integer>();
		
		expect(this.optionPane.userConfirmation(capture(captureOfComponent), 
													capture(captureOfObject), 
													capture(captureOfString), 
													capture(captureOfInteger))).andReturn(JOptionPane.NO_OPTION);		
		replay(optionPane, persistenceStrategy);
		
		this.window.button("0,0").click(MouseButton.LEFT_BUTTON);
		
		// verify that the MockOptionPane was called
		verify(this.optionPane);
		assertEquals(this.resourceBundle.getString("gameover.dialogue.msg.title"), captureOfString.getValue());
		assertEquals(this.resourceBundle.getString("gameover.dialogue.msg"), captureOfObject.getValue());
		assertNotNull(captureOfComponent.getValue());
		assertEquals(new Integer(JOptionPane.YES_NO_OPTION), captureOfInteger.getValue());		
	}
}

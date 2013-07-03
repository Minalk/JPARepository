package com.diycomputerscience.minesweeper.view;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.plaf.ColorUIResource;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.fest.swing.core.BasicRobot;
import org.fest.swing.core.MouseButton;
import org.fest.swing.core.Robot;
import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.diycomputerscience.minesweeper.Board;
import com.diycomputerscience.minesweeper.MineInitializationStrategy;
import com.diycomputerscience.minesweeper.MockMineUtils;
import com.diycomputerscience.minesweeper.Point;
import com.diycomputerscience.minesweeper.Square;
import com.diycomputerscience.minesweeper.model.PersistenceStrategy;

public class UITest extends BaseSwingTestCase {
	
	private FrameFixture window;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();		
		this.window = new FrameFixture(robot(), createUI());
		this.window.show();
	}

	@After
	public void tearDown() throws Exception {
		this.window.cleanUp();
		super.tearDown();
	}

	@Test
	public void testUIVisibility() {
		replay(optionPane, persistenceStrategy);
		
		GuiActionRunner.execute(new GuiTask() {
			@Override
			protected void executeInEDT() throws Throwable {
				window.requireVisible();
			}			
		});
						
	}

	@Test
	public void testUIDefaultCloseOperation() {
		replay(optionPane, persistenceStrategy);
		
		// Make Swing calls are run on the EDT
		int defaultCloseOperation = GuiActionRunner.execute(new GuiQuery<Integer>() {
			@Override
			protected Integer executeInEDT() throws Throwable {
				return ((JFrame)window.target).getDefaultCloseOperation();			
			}			
		});
		
		// Verify
		assertEquals(JFrame.DISPOSE_ON_CLOSE, defaultCloseOperation);
	}
	
	@Test
	public void testUITitle() {
		replay(optionPane, persistenceStrategy);
		
		// Make Swing calls are run on the EDT
		String actualTitle = GuiActionRunner.execute( new GuiQuery<String>() {
			@Override
			protected String executeInEDT() throws Throwable {
				return window.target.getTitle();			
			}			
		});
		
		// Verify
		assertEquals("Minesweeper", actualTitle);
	}
	
	@Test
	public void testMainPanel() {
		replay(optionPane, persistenceStrategy);
		
		// Make Swing calls are run on the EDT
		GuiActionRunner.execute(new GuiTask() {
			@Override
			protected void executeInEDT() throws Throwable {
				JPanel mainPanel = window.panel("MainPanel").target;
				// verify that the contentPane contains a JPanel called "MainPanel"
				assertNotNull(mainPanel);
				
				// verify that the layoutManaget of the mainPanel is GridLayout		
				assertEquals(GridLayout.class, mainPanel.getLayout().getClass());
				
				// verify the dimensions of the GridLayout
				assertEquals(Board.MAX_ROWS, ((GridLayout)mainPanel.getLayout()).getRows());
				assertEquals(Board.MAX_COLS, ((GridLayout)mainPanel.getLayout()).getColumns());
			}			
		});	
	}
	
	@Test
	public void testSquares() {		
		replay(optionPane, persistenceStrategy);
		
		Component components[] = GuiActionRunner.execute(new GuiQuery<Component[]>(){

			@Override
			protected Component[] executeInEDT() throws Throwable {
				JPanel mainPanel = window.panel("MainPanel").target;				
				return mainPanel.getComponents();
			}
			
		});
				
		// verify that the mainPanel has Board.MAX_ROWS x Board.MAX_COLS components
		assertEquals(Board.MAX_ROWS*Board.MAX_COLS, components.length);
		
		// verify that each component in the mainPanel is a JButton
		for(Component component : components) {
			assertEquals(JButton.class, component.getClass());
		}
	}
	
	@Test
	public void testLeftClickCoveredSquareWhichIsNotAMine() throws Exception {
		replay(optionPane, persistenceStrategy);
		
		Square squares[][] = this.board.getSquares();
		Point point = getFirstCoveredSquareWhichIsNotAMine(squares);
		String expectedCountAfterClick = String.valueOf(squares[point.row][point.col].getCount());
		
		// uncover square
		this.window.button(point.row+","+point.col).click(MouseButton.LEFT_BUTTON);
		
		// verify
		this.window.button(point.row+","+point.col).requireText(expectedCountAfterClick);
	}
	
	@Test
	public void testLeftClickUncoveredSquareWhichIsNotAMine() throws Exception {
		replay(optionPane, persistenceStrategy);
		
		Square squares[][] = this.board.getSquares();
		Point point = getFirstCoveredSquareWhichIsNotAMine(squares);
		this.window.button(point.row+","+point.col).click(MouseButton.LEFT_BUTTON);
		this.window.button(point.row+","+point.col).click(MouseButton.LEFT_BUTTON).equals(squares[point.row][point.col].getCount());
	}
	
	@Test
	public void testLeftClickCoveredSquareWhichIsAMine() throws Exception {
		Capture<Component> captureOfComponent = new Capture<Component>();
		Capture<Object> captureOfObject = new Capture<Object>();
		Capture<String> captureOfString = new Capture<String>();
		Capture<Integer> captureOfInteger = new Capture<Integer>();
		
		expect(optionPane.userConfirmation(capture(captureOfComponent), 
				  						   capture(captureOfObject), 
										   capture(captureOfString), 
										   capture(captureOfInteger))).andReturn(JOptionPane.NO_OPTION);
		replay(optionPane, persistenceStrategy);
		
		Square squares[][] = this.board.getSquares();
		Point point = getFirstCoveredSquareWhichIsAMine(squares);
		
		// uncover
		this.window.button(point.row+","+point.col).click(MouseButton.LEFT_BUTTON);
		
		// verify
		verify(this.optionPane);
		assertEquals("Confirm quit", captureOfString.getValue());
		assertEquals("That was a mine. You have lost the game. Would you like to play again ?", captureOfObject.getValue());
		assertNotNull(captureOfComponent.getValue());
		assertEquals(new Integer(JOptionPane.YES_NO_OPTION), captureOfInteger.getValue());
	}
	
	@Test
	public void testRightClickCoveredSquareWhichIsNotAMine() throws Exception {
		replay(optionPane, persistenceStrategy);
		
		Square squares[][] = this.board.getSquares();
		Point point = getFirstCoveredSquareWhichIsNotAMine(squares);
		
		// click to mark and verify
		this.window.button(point.row+","+point.col).click(MouseButton.RIGHT_BUTTON).background().requireEqualTo(ColorUIResource.MAGENTA);
	}
	
	@Test
	public void testRightClickCoveredSquareWhichIsAMine() throws Exception {
		replay(optionPane, persistenceStrategy);
		
		Square squares[][] = this.board.getSquares();
		Point point = getFirstCoveredSquareWhichIsAMine(squares);
		
		// click to mark and verify
		this.window.button(point.row+","+point.col).click(MouseButton.RIGHT_BUTTON).background().requireEqualTo(ColorUIResource.MAGENTA);
	}
	
	@Test
	public void testRightClickMarkedSquareWhichIsNotAMine() throws Exception {
		replay(optionPane, persistenceStrategy);
		
		Square squares[][] = this.board.getSquares();
		Point point = getFirstCoveredSquareWhichIsNotAMine(squares);
		
		this.window.button(point.row+","+point.col).click(MouseButton.RIGHT_BUTTON);
		
		// click to mark and verify
		this.window.button(point.row+","+point.col).click(MouseButton.RIGHT_BUTTON).background().requireEqualTo(new ColorUIResource(238, 238, 238));
	}
	
	@Test
	public void testRightClickMarkedSquareWhichIsAMine() throws Exception {
		replay(optionPane, persistenceStrategy);
		
		Square squares[][] = this.board.getSquares();
		Point point = getFirstCoveredSquareWhichIsAMine(squares);
		
		// click to mark
		this.window.button(point.row+","+point.col).click(MouseButton.RIGHT_BUTTON);
		
		// click to mark again, and verify
		this.window.button(point.row+","+point.col).click(MouseButton.RIGHT_BUTTON).background().requireEqualTo(new ColorUIResource(238, 238, 238));
	}
	
	@Test
	public void testLeftClickMarkedSquareWhichIsNotAMine() throws Exception {
		replay(optionPane, persistenceStrategy);
		
		Square squares[][] = this.board.getSquares();
		Point point = getFirstCoveredSquareWhichIsNotAMine(squares);
		
		// click to mark
		this.window.button(point.row+","+point.col).click(MouseButton.RIGHT_BUTTON);
		
		// click to uncover, and verify
		this.window.button(point.row+","+point.col).click(MouseButton.LEFT_BUTTON).background().requireEqualTo(ColorUIResource.MAGENTA);
	}
	
	@Test
	public void testLeftClickMarkedSquareWhichIsAMine() throws Exception {
		replay(optionPane, persistenceStrategy);
		
		Square squares[][] = this.board.getSquares();
		Point point = getFirstCoveredSquareWhichIsAMine(squares);
		
		// click to mark
		this.window.button(point.row+","+point.col).click(MouseButton.RIGHT_BUTTON);
		
		// click to uncover and verify
		this.window.button(point.row+","+point.col).click(MouseButton.LEFT_BUTTON).background().requireEqualTo(ColorUIResource.MAGENTA);
	}
	
	@Test
	public void testRightClickUncoveredSquareWhichIsNotAMine() throws Exception {
		replay(optionPane, persistenceStrategy);
		
		Square squares[][] = this.board.getSquares();
		Point point = getFirstCoveredSquareWhichIsNotAMine(squares);
		String expectedCount = String.valueOf(squares[point.row][point.col].getCount());
		
		// click to uncover
		this.window.button(point.row+","+point.col).click(MouseButton.LEFT_BUTTON);
		
		// click to mark 
		this.window.button(point.row+","+point.col).click(MouseButton.RIGHT_BUTTON);
		
		// verify text
		this.window.button(point.row+","+point.col).requireText(expectedCount);
		
		// verify color
		this.window.button(point.row+","+point.col).background().requireEqualTo(new ColorUIResource(238, 238, 238));
	}		
	
	private Point getFirstCoveredSquareWhichIsAMine(Square[][] squares) {
		for(int row=0; row<squares.length; row++) {
			for(int col=0; col<squares[row].length; col++) {
				if(squares[row][col].getState().equals(Square.SquareState.COVERED)  && squares[row][col].isMine()) {
					return new Point(row, col);
				}
			}
		}
		return null;
	}
	
	private Point getFirstCoveredSquareWhichIsNotAMine(Square[][] squares) {
		for(int row=0; row<squares.length; row++) {
			for(int col=0; col<squares[row].length; col++) {
				if(squares[row][col].getState().equals(Square.SquareState.COVERED)  && !squares[row][col].isMine()) {
					return new Point(row, col);
				}
			}
		}
		return null;
	}
	
}

package com.diycomputerscience.minesweeper.view;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.util.List;

import org.easymock.Capture;
import org.easymock.CaptureType;
import org.easymock.EasyMock;
import org.fest.swing.core.BasicRobot;
import org.fest.swing.core.MouseButton;
import org.fest.swing.core.Robot;
import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.diycomputerscience.minesweeper.Board;
import com.diycomputerscience.minesweeper.MineInitializationStrategy;
import com.diycomputerscience.minesweeper.MockMineUtils;
import com.diycomputerscience.minesweeper.Point;
import com.diycomputerscience.minesweeper.model.PersistenceStrategy;

public class UIPersistenceTest extends BaseSwingTestCase {

	private FrameFixture window;
	private Capture<Board> captureOfBoard;
	
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
	public void testSave() throws Exception {
		this.captureOfBoard = new Capture<Board>(CaptureType.ALL);
		persistenceStrategy.save(capture(captureOfBoard));
		replay(optionPane, persistenceStrategy);
		
		// uncover a Square		
		this.window.button(1+","+3).click(MouseButton.LEFT_BUTTON);
		
		// save from the UI menu
		this.window.menuItem("file-save").click();		
		
		// verify data in MockPersistenceStrategy		
		List<String> savedBoardLayout = MockMineUtils.buildSavedLayout(this.captureOfBoard.getValue());
		Assert.assertEquals(MockMineUtils.expectedLinesInSavedBoard.length, savedBoardLayout.size());
		
		for(int i=0; i<MockMineUtils.expectedLinesInSavedBoard.length; i++) {
			Assert.assertEquals(MockMineUtils.expectedLinesInSavedBoard[i], savedBoardLayout.get(i));
		}
	}
	
	@Test
	public void testLoad() throws Exception {
		expect(persistenceStrategy.load()).andReturn(MockMineUtils.buildBoardFromLayout(MockMineUtils.expectedLinesInSavedBoard));
		replay(optionPane, persistenceStrategy);
		
		// click the load file menu
		this.window.menuItem("file-load").click();
		Assert.assertEquals("3", this.window.button(1+","+3).text());		
	}
}

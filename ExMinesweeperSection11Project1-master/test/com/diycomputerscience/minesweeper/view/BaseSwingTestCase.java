package com.diycomputerscience.minesweeper.view;

/*
 * Created on Jan 17, 2009
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright @2009 the original author or authors.
 */

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import org.easymock.EasyMock;
import org.fest.swing.core.Robot;
import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.testing.FestSwingTestCaseTemplate;
import org.junit.BeforeClass;

import com.diycomputerscience.minesweeper.Board;
import com.diycomputerscience.minesweeper.MineInitializationStrategy;
import com.diycomputerscience.minesweeper.MockMineUtils;
import com.diycomputerscience.minesweeper.Point;
import com.diycomputerscience.minesweeper.model.PersistenceStrategy;

/**
 * Understands a template for test cases that use FEST-Swing and JUnit. This
 * template installs a <code>{@link FailOnThreadViolationRepaintManager}</code>
 * to catch violations of Swing thread rules and manages both creation and clean
 * up of a <code>{@link Robot}</code>.
 * 
 * @since 1.1
 * 
 */
public class BaseSwingTestCase extends FestSwingTestCaseTemplate {

	protected Board board;
	protected MineInitializationStrategy mineInitializationStrategy;
	protected OptionPane optionPane;
	protected PersistenceStrategy persistenceStrategy;
	
	/**
	 * Installs a <code>{@link FailOnThreadViolationRepaintManager}</code> to
	 * catch violations of Swing threading rules.
	 */
	@BeforeClass
	public static final void setUpOnce() {
		FailOnThreadViolationRepaintManager.install();
	}

	/**
	 * Sets up this test's fixture, starting from creation of a new
	 * <code>{@link Robot}</code>.
	 * 
	 * @see #setUpRobot()
	 * @see #onSetUp()
	 */
	public void setUp() throws Exception {
		setUpRobot();
	}

	public void tearDown() throws Exception {
		cleanUp();
	}

	public UI createUI() {
		
		this.mineInitializationStrategy = EasyMock.createMock(MineInitializationStrategy.class);
		Point boardSize = new Point(Board.MAX_ROWS, Board.MAX_ROWS);
		expect(this.mineInitializationStrategy.mines(boardSize)).andReturn(MockMineUtils.mines(boardSize));
		replay(this.mineInitializationStrategy);
		
		this.persistenceStrategy = EasyMock.createMock(PersistenceStrategy.class);
		optionPane = EasyMock.createMock(OptionPane.class);
		
		board = new Board(mineInitializationStrategy);
		
		return GuiActionRunner.execute(new GuiQuery<UI>() {

			@Override
			protected UI executeInEDT() throws Throwable {
				return UI.build(board, optionPane, persistenceStrategy);
			}

		});
	}
}

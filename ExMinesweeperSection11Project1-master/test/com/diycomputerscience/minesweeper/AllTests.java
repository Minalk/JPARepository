package com.diycomputerscience.minesweeper;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.diycomputerscience.minesweeper.model.BoardDaoJDBCTest;
import com.diycomputerscience.minesweeper.model.DBInitTest;
import com.diycomputerscience.minesweeper.model.JDBCPersistenceStrategy;
import com.diycomputerscience.minesweeper.model.JDBCPersistenceStrategyTest;
import com.diycomputerscience.minesweeper.utils.MinesweeperUtilsTest;
import com.diycomputerscience.minesweeper.view.I18NTest;
import com.diycomputerscience.minesweeper.view.UIPersistenceTest;
import com.diycomputerscience.minesweeper.view.UITest;

@RunWith(Suite.class)
@SuiteClasses({ PointTest.class,
				BoardTest.class, 
				SquareTest.class,
				MinesweeperUtilsTest.class,
				FilePersistenceStrategyTest.class,
				BoardDaoJDBCTest.class,
				DBInitTest.class,
				JDBCPersistenceStrategyTest.class,
				UITest.class,
				UIPersistenceTest.class,
				I18NTest.class,
				})
public class AllTests {

}

package de.ptb.epics.eve.data.measuringstation.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.data.measuringstation.Function;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.FunctionMother;

/**
 * @author Marcus Michalsky
 * @since 1.26
 */
public class FunctionTest {
	private List<Function> functions;
	
	@Test
	public void testClone() {
		for (Function function : functions) {
			Function clone = (Function)function.clone();
			
			assertEquals(function.getAccess(), clone.getAccess());
			assertEquals(function.getValue(), clone.getValue());
		}
	}
	
	/**
	 * Class wide setup method of the test
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
	}

	/**
	 * Class wide tear down method.
	 */
	@AfterClass
	public static void tearDownAfterClass() {
	}

	/**
	 * Test wide set up method.
	 */
	@Before
	public void setUp() {
		this.functions = new ArrayList<>();
		this.functions.add(FunctionMother.createNewFunction());
	}

	/**
	 * Test wide tear down method.
	 */
	@After
	public void tearDown() {
	}
}
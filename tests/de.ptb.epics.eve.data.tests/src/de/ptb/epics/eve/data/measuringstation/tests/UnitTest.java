package de.ptb.epics.eve.data.measuringstation.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.data.measuringstation.Unit;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.UnitMother;

/**
 * @author Marcus Michalsky
 * @since 1.26
 */
public class UnitTest {
	private List<Unit> units;
	
	@Test
	public void testClone() {
		for (Unit unit : units) {
			Unit clone = (Unit)unit.clone();
			
			assertEquals(unit.getValue(), clone.getValue());
			assertEquals(unit.getAccess(), clone.getAccess());
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
		this.units = new ArrayList<>();
		this.units.add(UnitMother.createNewUnit());
	}

	/**
	 * Test wide tear down method.
	 */
	@After
	public void tearDown() {
	}
}
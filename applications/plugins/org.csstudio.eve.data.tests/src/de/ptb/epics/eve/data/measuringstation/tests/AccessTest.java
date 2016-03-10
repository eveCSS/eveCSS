package de.ptb.epics.eve.data.measuringstation.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.data.measuringstation.Access;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.AccessMother;

/**
 * @author Marcus Michalsky
 * @since 1.26
 */
public class AccessTest {
	private List<Access> accessList;

	@Test
	public void testClone() {
		for (Access access : accessList) {
			Access clone = (Access)access.clone();
			
			assertEquals(access.getMethod(), clone.getMethod());
			assertEquals(access.getType(), clone.getType());
			assertEquals(access.getCount(), clone.getCount());
			assertEquals(access.getVariableID(), clone.getVariableID());
			assertEquals(access.getTransport(), clone.getTransport());
			assertEquals(access.getTimeout(), clone.getTimeout(), 0);
			assertEquals(access.getMonitor(), clone.getMonitor());
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
		this.accessList = new ArrayList<>();
		this.accessList.add(AccessMother.createNewAccess());
	}

	/**
	 * Test wide tear down method.
	 */
	@After
	public void tearDown() {
	}
}
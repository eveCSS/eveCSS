package de.ptb.epics.eve.data.scandescription.defaults.tests;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.data.scandescription.Stepfunctions;
import de.ptb.epics.eve.data.scandescription.defaults.DefaultsAxis;


/**
 * @author Marcus Michalsky
 * @since 1.8
 */
public class DefaultAxisTest {
	private DefaultsAxis axis;
	
	
	
	/**
	 * 
	 */
	@Test(expected=IllegalStateException.class)
	public void testGetFileStepfunctionList() {
		this.axis.setStepfunction(Stepfunctions.POSITIONLIST);
		this.axis.getFile();
	}
	
	/**
	 * 
	 */
	@Test(expected=IllegalStateException.class)
	public void testGetPositionListStepfunctionFile() {
		this.axis.setStepfunction(Stepfunctions.FILE);
		this.axis.getPositionList();
	}
	
	// **********************************************************************
	// **************************** Setup ***********************************
	// **********************************************************************

	/**
	 * Class wide setup.
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
	}

	/**
	 * Class wide tear down.
	 */
	@AfterClass
	public static void tearDownAfterClass() {
	}

	/**
	 * Test wide set up.
	 */
	@Before
	public void setUp() {
		this.axis = new DefaultsAxis();
	}

	/**
	 * Test wide tear down.
	 */
	@After
	public void tearDown() {
	}
}
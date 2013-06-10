package de.ptb.epics.eve.data.scandescription.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.data.PlotModes;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.scandescription.YAxis;


/**
 * @author Marcus Michalsky
 * @since 1.13
 */
public class YAxisTest {

	/**
	 * 
	 */
	@Test
	public void testEquals() {
		YAxis empty1 = new YAxis();
		YAxis empty2 = new YAxis();
		assertEquals(empty1, empty2);
		
		DetectorChannel ch1 = new DetectorChannel();
		ch1.setId("1");
		YAxis axis1 = new YAxis(ch1);
		DetectorChannel ch2 = new DetectorChannel();
		ch2.setId("1");
		YAxis axis2 = new YAxis(ch2);
		assertEquals(axis1, axis2);
		
		axis1.setMode(PlotModes.LOG);
		assertFalse(axis1.equals(axis2));
		
		axis2.setMode(PlotModes.LOG);
		assertEquals(axis1, axis2);
		
		ch2.setId("2");
		assertFalse(axis1.equals(axis2));
	}
	
	// ***********************************************************************

	/**
	 * Class wide setup method of the test
	 */
	@BeforeClass
	public static void runBeforeClass() {
	}

	/**
	 * test wide set up method
	 */
	@Before
	public void beforeEveryTest() {
	}

	/**
	 * test wide tear down method
	 */
	@After
	public void afterEveryTest() {
	}

	/**
	 * class wide tear down method
	 */
	@AfterClass
	public static void afterClass() {
	}
}

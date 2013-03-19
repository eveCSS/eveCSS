package de.ptb.epics.eve.data.measuringstation.tests;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.tests.internal.Configurator;

/**
 * <code>MeasuringStationTest</code> contains 
 * <a href="http://www.junit.org/">JUnit</a>-Tests for 
 * {@link de.ptb.epics.eve.data.measuringstation.MeasuringStation}.
 * 
 * @author Marcus Michalsky
 * @since 0.4.1
 */
public class MeasuringStationTest {
	private static List<IMeasuringStation> stations;

	/**
	 * Should check whether the loaded java model and the xml file entries are 
	 * equal.
	 */
	@Ignore("Not implemented yet")
	@Test
	public void testCompareXMLandJava() {
		
	}
	
	// ***********************************************************************
	// ***********************************************************************
	// ***********************************************************************
	
	/**
	 * Initializes logging and loads the measuring station (Class wide setup 
	 * method of the test).
	 */
	@BeforeClass
	public static void runBeforeClass() {
		stations = Configurator.getMeasuringStations();
		
		for(IMeasuringStation ims : stations) {
			assertNotNull(ims);
		}
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
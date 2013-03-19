package de.ptb.epics.eve.data.measuringstation.tests;

import java.util.List;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.tests.internal.Configurator;

/**
 * <code>MotorAxisTest</code> contains 
 * <a href="http://www.junit.org/">JUnit</a>-Tests for 
 * {@link de.ptb.epics.eve.data.measuringstation.MotorAxis}.
 * 
 * @author Marcus Michalsky
 * @since 0.4.1
 */
public class MotorAxisTest {
	private static List<IMeasuringStation> stations;
	
	/**
	 * Test method for 
	 * {@link de.ptb.epics.eve.data.measuringstation.MotorAxis#clone()} and 
	 * {@link de.ptb.epics.eve.data.measuringstation.MotorAxis#equals(Object)}.
	 */
	@Test
	public void testCloneEquals() {
		for(IMeasuringStation measuringStation : stations) {
			for(Motor m : measuringStation.getMotors()) {
				for(MotorAxis ma : m.getAxes()) {
					MotorAxis clone = (MotorAxis) ma.clone();
				
					assertEquals(ma,ma);
					assertEquals(clone,clone);
					assertEquals(ma,clone);
					assertEquals(clone,ma);
				}
			}
		}
	}
	
	// **********************************************************************
	// **************************** Setup ***********************************
	// **********************************************************************
	
	/**
	 * Class wide setup method of the test
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		stations = Configurator.getMeasuringStations();
		
		for(IMeasuringStation ims : stations) {
			assertNotNull(ims);
		}
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
	}

	/**
	 * Test wide tear down method.
	 */
	@After
	public void tearDown() {
	}
}
package de.ptb.epics.eve.data.measuringstation.tests;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.tests.internal.Configurator;

/**
 * <code>OptionTest</code> contains 
 * <a href="http://www.junit.org/">JUnit</a>-Tests for 
 * {@link de.ptb.epics.eve.data.measuringstation.Option}.
 * 
 * @author Marcus Michalsky
 * @since 0.4.1
 */
public class OptionTest {
	private static List<IMeasuringStation> stations;
	
	/**
	 * Test method for 
	 * {@link de.ptb.epics.eve.data.measuringstation.Option#clone()} and 
	 * {@link de.ptb.epics.eve.data.measuringstation.AbstractPrePostscanDevice#equals(java.lang.Object)}.
	 */
	@Test
	public final void testCloneEquals() {
		for(IMeasuringStation measuringStation : stations) {
			for(Motor m : measuringStation.getMotors()) {
				for(MotorAxis ma : m.getAxes()) {
					for(Option o : ma.getOptions()) {
						Option clone = (Option)o.clone();
						assertTrue(o.equals(clone));
						assertTrue(clone.equals(o));
					}
				}
			}
		}
	}

	// **********************************************************************
	// **************************** Setup ***********************************
	// **********************************************************************
	
	/**
	 * Initializes logging and loads the measuring station (Class wide setup 
	 * method of the test).
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
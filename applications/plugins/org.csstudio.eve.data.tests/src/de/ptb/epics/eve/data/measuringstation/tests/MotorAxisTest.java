package de.ptb.epics.eve.data.measuringstation.tests;

import static de.ptb.epics.eve.data.tests.internal.LogFileStringGenerator.*;

import java.util.List;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
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

	private static Logger logger = 
			Logger.getLogger(MotorAxisTest.class.getName());

	private static List<IMeasuringStation> stations;
	
	/**
	 * Test method for 
	 * {@link de.ptb.epics.eve.data.measuringstation.MotorAxis#clone()} and 
	 * {@link de.ptb.epics.eve.data.measuringstation.MotorAxis#equals(Object)}.
	 */
	@Test
	public void testCloneEquals()
	{
		log_start(logger, "testCloneEquals()");
		
		for(IMeasuringStation measuringStation : stations) {
			logger.info("Testing station " + measuringStation.getName());
			for(Motor m : measuringStation.getMotors()) {
				for(MotorAxis ma : m.getAxes()) {
					logger.info("Testing axis " + deviceString(ma));
				
					MotorAxis clone = (MotorAxis) ma.clone();
				
					assertEquals(ma,ma);
					assertEquals(clone,clone);
					assertEquals(ma,clone);
					assertEquals(clone,ma);
					logger.info("Motor Axis " + deviceString(ma) + 
								" and its clone are equal");
				}
			}
		}
		
		log_end(logger, "testCloneEquals()");
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
		
		Configurator.configureLogging();
		
		((RollingFileAppender)logger.
				getAppender("MotorAxisTestAppender")).rollOver();
		
		stations = Configurator.getMeasuringStations();
		
		for(IMeasuringStation ims : stations) {
			assertNotNull(ims);
		}
		classSetUp(logger);
	}

	/**
	 * Class wide tear down method.
	 */
	@AfterClass
	public static void tearDownAfterClass() {
		classTearDown(logger);
	}

	/**
	 * Test wide set up method.
	 */
	@Before
	public void setUp() {
		
		testSetUp(logger);
	}

	/**
	 * Test wide tear down method.
	 */
	@After
	public void tearDown() {
		testTearDown(logger);
	}	
}
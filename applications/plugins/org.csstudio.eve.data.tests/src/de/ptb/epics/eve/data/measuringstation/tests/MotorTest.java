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
import de.ptb.epics.eve.data.tests.internal.Configurator;

/**
 * <code>MotorTest</code> contains 
 * <a href="http://www.junit.org/">JUnit</a>-Tests for 
 * {@link de.ptb.epics.eve.data.measuringstation.Motor}.
 * 
 * @author Marcus Michalsky
 * @since 0.4.1
 */
public class MotorTest {

	private static Logger logger = Logger.getLogger(MotorTest.class.getName());

	private static List<IMeasuringStation> stations;
	
	/**
	 * Test method for 
	 * {@link de.ptb.epics.eve.data.measuringstation.Motor#clone()} and 
	 * {@link de.ptb.epics.eve.data.measuringstation.Motor#equals(Object)}.
	 */
	@Test
	public void testCloneEquals() {
		
		log_start(logger, "testCloneEquals()");
		
		for(IMeasuringStation measuringStation : stations) {
			for(Motor m : measuringStation.getMotors()) {
				logger.info("Testing motor " + deviceString(m));
				
				Motor clone = (Motor) m.clone();
				
				assertEquals(m, m);
				assertEquals(clone, clone);
				assertEquals(m, clone);
				assertEquals(clone, m);
				logger.info("Motor " + deviceString(m) + 
							" and its clone are equal");
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
				getAppender("MotorTestAppender")).rollOver();
		
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
package de.ptb.epics.eve.data.measuringstation.tests;

import static de.ptb.epics.eve.data.tests.internal.LogFileStringGenerator.*;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.data.measuringstation.Access;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.tests.internal.Configurator;

/**
 * @author Marcus Michalsky
 * @since 0.4.1
 */
public class AccessTest {

	// logging
	private static Logger logger = Logger.getLogger(AccessTest.class.getName());

	// the measuring station
	private static IMeasuringStation measuringStation;
	
	/**
	 * Test method for 
	 * {@link de.ptb.epics.eve.data.measuringstation.Access#equals(java.lang.Object)}.
	 */
	@Test
	public final void testEquals() {
		
		log_start(logger, "testEquals");
		
		for(Motor m : measuringStation.getMotors())
		{
			for(MotorAxis ma : m.getAxes())
			{
				
				for(Option o : ma.getOptions())
				{
					logger.info("Testing Access of option " + deviceString(o));
					
					Access a = o.getValue().getAccess();
					Access clone = (Access)a.clone();
					assertTrue(a.equals(clone));
					assertTrue(clone.equals(a));
					
					logger.info("Access " + a.getVariableID() + 
								" and clone are equal.");
				}
			}
		}
	}

	// **********************************************************************
	// **************************** Setup ***********************************
	// **********************************************************************
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		Configurator.configureLogging();
		
		((RollingFileAppender)logger.
				getAppender("AccessTestAppender")).rollOver();
		
		measuringStation = Configurator.getMeasuringStation();
		
		assertNotNull(measuringStation);
		
		classSetUp(logger);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		classTearDown(logger);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		testSetUp(logger);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		testTearDown(logger);
	}	
}
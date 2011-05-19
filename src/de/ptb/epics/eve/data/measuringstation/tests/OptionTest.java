package de.ptb.epics.eve.data.measuringstation.tests;

import static org.junit.Assert.*;

import static de.ptb.epics.eve.data.tests.internal.LogFileStringGenerator.*;

import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.tests.internal.Configurator;

/**
 * @author Marcus Michalsky
 * @since 0.4.1
 */
public class OptionTest 
{
	// logging
	private static Logger logger = Logger.getLogger(OptionTest.class.getName());
	
	// the "real"/source measuring station
	private static IMeasuringStation measuringStation;
	
	/**
	 * Test method for {@link de.ptb.epics.eve.data.measuringstation.Option#clone()}.
	 */
	@Ignore("Not Implemented Yet!")
	@Test
	public final void testClone() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link de.ptb.epics.eve.data.measuringstation.AbstractPrePostscanDevice#hashCode()}.
	 */
	@Ignore("Not Implemented Yet!")
	@Test
	public final void testHashCode() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for 
	 * {@link de.ptb.epics.eve.data.measuringstation.AbstractPrePostscanDevice#equals(java.lang.Object)}.
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
					logger.info("Testing option " + deviceString(o) + 
								" of motor axis " + deviceString(ma));
					
					Option clone = (Option)o.clone();
					assertTrue(o.equals(clone));
					assertTrue(clone.equals(o));
					
					logger.info("option " + deviceString(o) + 
								" and its clone are equal");
				}
			}
		}
		
		log_end(logger, "testEquals");
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
				getAppender("OptionTestAppender")).rollOver();
		
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
package de.ptb.epics.eve.data.tests;

import static de.ptb.epics.eve.data.tests.internal.LogFileStringGenerator.*;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.ptb.epics.eve.data.TypeValue;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.tests.internal.Configurator;

/**
 * <code>TypeValueTest</code> contains 
 * <a href="http://www.junit.org/">JUnit</a>-Tests for 
 * {@link de.ptb.epics.eve.data.TypeValue}.
 * 
 * @author Marcus Michalsky
 * @since 0.4.1
 */
public class TypeValueTest 
{
	// logging
	private static Logger logger = 
			Logger.getLogger(TypeValueTest.class.getName());

	// the "real"/source measuring station
	private static IMeasuringStation measuringStation;

	/**
	 * Test method for {@link de.ptb.epics.eve.data.TypeValue#hashCode()}.
	 */
	@Ignore("Not Implemented Yet!")
	@Test
	public final void testHashCode() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link de.ptb.epics.eve.data.TypeValue#equals(java.lang.Object)}.
	 */
	@Test
	public final void testCloneEquals() {

		log_start(logger, "testEquals");
		
		for(Motor m : measuringStation.getMotors())
		{
			for(MotorAxis ma : m.getAxes())
			{		
				for(Option o : ma.getOptions())
				{
					if(o.getValue().getValue() != null)
					{
						logger.info("Testing TypeValue of option " + deviceString(o));
						
						TypeValue value = o.getValue().getValue();
						TypeValue clone = (TypeValue)value.clone();
						assertTrue(value.equals(clone));
						assertTrue(clone.equals(value));
						
						logger.info("type value and clone are equal");
					} else {
						logger.info(deviceString(o) + "has no type value");
					}
				}
			}
		}
		
		log_end(logger, "testEquals");
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
				getAppender("TypeValueTestAppender")).rollOver();
		
		measuringStation = Configurator.getMeasuringStation();
		
		assertNotNull(measuringStation);
		
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
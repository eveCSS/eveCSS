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
	public final void testEquals() {

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

	/**
	 * Test method for {@link de.ptb.epics.eve.data.TypeValue#clone()}.
	 */
	@Ignore("Not Implemented Yet!")
	@Test
	public final void testClone() {
		fail("Not yet implemented"); // TODO
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
				getAppender("TypeValueTestAppender")).rollOver();
		
		measuringStation = Configurator.getMeasuringStation();
		
		assertNotNull(measuringStation);
		
		logger.info("*******************************************************");
		logger.info("Class Wide Setup Done (measuring station loaded)");
		logger.info("*******************************************************");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		logger.info("*******************************************************");
		logger.info("Class Wide Tear Down Done (files closed)");
		logger.info("*******************************************************");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		logger.info("**********************************************************");
		logger.info(
			"Test Wide Setup Done (measuring station filter initialized)");
		logger.info("**********************************************************");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		logger.info("**********************************************************");
		logger.info(
			"Test Wide Tear Down Done (measuring station filter garbaged)");
		logger.info("**********************************************************");
	}
	
}

package de.ptb.epics.eve.data.measuringstation.tests;

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

	private static Logger logger = 
		Logger.getLogger(MeasuringStationTest.class.getName());

	private static IMeasuringStation measuringStation;

	/**
	 * 
	 */
	@Ignore("Not implemented yet")
	@Test
	public void testCompareXMLandJava()
	{
		log_start(logger, "testCompareXMLandJava()");
		
		
		
		log_end(logger, "testCompareXMLandJava()");
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
		
		Configurator.configureLogging();
		
		((RollingFileAppender)logger.
				getAppender("MeasuringStationTestAppender")).rollOver();
		
		measuringStation = Configurator.getMeasuringStation();
		
		assertNotNull(measuringStation);
		
		classSetUp(logger);
	}
	
	/**
	 * test wide set up method
	 */
	@Before
	public void beforeEveryTest()
	{
		testSetUp(logger);
	}
	
	/**
	 * test wide tear down method
	 */
	@After
	public void afterEveryTest()
	{
		testTearDown(logger);
	}
	
	/**
	 * class wide tear down method
	 */
	@AfterClass
	public static void afterClass()
	{
		classTearDown(logger);
	}
}
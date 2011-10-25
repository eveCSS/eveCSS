package de.ptb.epics.eve.data.scandescription.processors.tests;

import static de.ptb.epics.eve.data.tests.internal.LogFileStringGenerator.*;

import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.ptb.epics.eve.data.tests.internal.Configurator;

/**
 * 
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class ScanDescriptionLoaderTest {
	
	private static Logger logger = 
			Logger.getLogger(ScanDescriptionLoaderTest.class.getName());
	
	@Ignore("Does not work as expected")
	@Test
	public void bufferRefresh() {

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
				getAppender("ScanDescriptionLoaderTestAppender")).rollOver();

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
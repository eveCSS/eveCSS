package de.ptb.epics.eve.data.scandescription.processors.tests;

import static de.ptb.epics.eve.data.tests.internal.LogFileStringGenerator.classSetUp;
import static de.ptb.epics.eve.data.tests.internal.LogFileStringGenerator.classTearDown;
import static de.ptb.epics.eve.data.tests.internal.LogFileStringGenerator.testSetUp;
import static de.ptb.epics.eve.data.tests.internal.LogFileStringGenerator.testTearDown;

import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.data.tests.internal.Configurator;

/**
 * @author Marcus Michalsky
 * @since 1.2
 */
public class ScanDescriptionSaverTest {

	private static Logger logger = Logger
			.getLogger(ScanDescriptionSaverTest.class.getName());
	
	/**
	 * 
	 */
	@Test
	public void monitorOptionsTest() {
		
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
				getAppender("ScanDescriptionSaverTestAppender")).rollOver();

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
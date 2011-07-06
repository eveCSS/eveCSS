package de.ptb.epics.eve.data.measuringstation.tests;

import static de.ptb.epics.eve.data.tests.internal.LogFileStringGenerator.classSetUp;
import static de.ptb.epics.eve.data.tests.internal.LogFileStringGenerator.classTearDown;
import static de.ptb.epics.eve.data.tests.internal.LogFileStringGenerator.deviceString;
import static de.ptb.epics.eve.data.tests.internal.LogFileStringGenerator.log_end;
import static de.ptb.epics.eve.data.tests.internal.LogFileStringGenerator.log_start;
import static de.ptb.epics.eve.data.tests.internal.LogFileStringGenerator.testSetUp;
import static de.ptb.epics.eve.data.tests.internal.LogFileStringGenerator.testTearDown;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.tests.internal.Configurator;

/**
 * @author Marcus Michalsky
 * @since 0.4.1
 */
public class DeviceTest {
	// logging
	private static Logger logger = 
			Logger.getLogger(DeviceTest.class.getName());

	// the measuring station
	private static List<IMeasuringStation> stations;
	
	/**
	 * 
	 */
	@Test
	public void testClone() {
		log_start(logger, "testClone()");
		
		for(IMeasuringStation measuringStation : stations) {
			
			logger.debug("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
			logger.debug("Testing station " + measuringStation.getLoadedFileName());
			logger.debug("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
			
			for(Device device : measuringStation.getDevices()) {
				logger.info("Testing device " + deviceString(device));
				
				Device clone = (Device)device.clone();
				
				// check inherited properties from AbstractPrepostScanDevice
				assertEquals(device.getClassName(), clone.getClassName());
				logger.debug("class names are equal (" + device.getClassName() + ")");
				assertEquals(device.getDisplaygroup(), clone.getDisplaygroup());
				logger.debug("displaygroups are equal (" + device.getDisplaygroup() + ")");
				//assertEquals(device.getValue(), clone.getValue()); value is a function
				//logger.debug("values are equal (" + ")");
				
				// check inherited properties from AbstractDevice
				assertEquals(device.getName(), clone.getName());
				assertEquals(device.getID(), clone.getID());
				// unit Unit
				// option list List<Options>
				// parent AbstractDevice
				
				logger.debug("-----");
			}
			
		}
		
		log_end(logger, "testClone()");
	}
	
	/**
	 * Test method for 
	 * {@link de.ptb.epics.eve.data.measuringstation.Device#clone()} and 
	 * {@link de.ptb.epics.eve.data.measuringstation.Device#equals(Object)}.
	 */
	@Test
	public void testCloneEquals()
	{
		log_start(logger, "testCloneEquals()");
		
		for(IMeasuringStation measuringStation : stations) {
		
			logger.debug("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
			logger.debug("Testing station " + measuringStation.getLoadedFileName());
			logger.debug("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
			
			for(Device d : measuringStation.getDevices()) {
				
				logger.info("Testing device " + deviceString(d));
					
				Device clone = (Device) d.clone();
					
				assertEquals(d,d);
				assertEquals(clone,clone);
				assertEquals(d,clone);
				assertEquals(clone,d);
				logger.info("Device " + deviceString(d) + 
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
				getAppender("DeviceTestAppender")).rollOver();
		
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
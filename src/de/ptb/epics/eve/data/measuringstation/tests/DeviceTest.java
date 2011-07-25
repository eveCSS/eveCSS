package de.ptb.epics.eve.data.measuringstation.tests;

import static de.ptb.epics.eve.data.tests.internal.LogFileStringGenerator.*;
import static org.junit.Assert.*;

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

	private static Logger logger = 
			Logger.getLogger(DeviceTest.class.getName());

	private static List<IMeasuringStation> stations;
	
	/**
	 * 
	 */
	@Test
	public void testClone() {
		log_start(logger, "testClone()");
		
		for(IMeasuringStation measuringStation : stations) {
			
			log_station(logger, measuringStation);
			
			for(Device device : measuringStation.getDevices()) {
				logger.info("Testing device " + deviceString(device));
				
				Device clone = (Device)device.clone();
				
				// check inherited properties from AbstractPrepostScanDevice
				assertEquals(device.getClassName(), clone.getClassName());
				logger.debug("class names are equal (" + device.getClassName() + ")");
				assertEquals(device.getDisplaygroup(), clone.getDisplaygroup());
				logger.debug("displaygroups are equal (" + device.getDisplaygroup() + ")");
				assertEquals(device.getValue(), clone.getValue());
				logger.debug("values are equal");
				
				// check inherited properties from AbstractDevice
				assertEquals(device.getName(), clone.getName());
				assertEquals(device.getID(), clone.getID());
				assertEquals(device.getUnit(), clone.getUnit());

				// option list List<Options>
				// device options are cloned but the equals method (inherited 
				// from AbstractPrePostScanDevice) doesn't validate them ->
				// two devices one with and one without options could be equal.
				// cannot simply change the method, because Option is also a 
				// sub type of AbstractPrePostscanDevice inheriting the equals
				// (options should not have options)
				
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
		
			log_station(logger, measuringStation);
			
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
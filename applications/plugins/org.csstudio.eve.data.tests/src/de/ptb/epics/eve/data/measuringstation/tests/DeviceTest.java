package de.ptb.epics.eve.data.measuringstation.tests;

import static org.junit.Assert.*;

import java.util.List;

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
	private static List<IMeasuringStation> stations;
	
	/**
	 * 
	 */
	@Test
	public void testClone() {
		for(IMeasuringStation measuringStation : stations) {
			for(Device device : measuringStation.getDevices()) {
				Device clone = (Device)device.clone();
				
				// check inherited properties from AbstractPrepostScanDevice
				assertEquals(device.getClassName(), clone.getClassName());
				assertEquals(device.getDisplaygroup(), clone.getDisplaygroup());
				assertEquals(device.getValue(), clone.getValue());
				
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
			}
			
		}
	}
	
	/**
	 * Test method for 
	 * {@link de.ptb.epics.eve.data.measuringstation.Device#clone()} and 
	 * {@link de.ptb.epics.eve.data.measuringstation.Device#equals(Object)}.
	 */
	@Test
	public void testCloneEquals() {
		for(IMeasuringStation measuringStation : stations) {
			for(Device d : measuringStation.getDevices()) {
				Device clone = (Device) d.clone();
					
				assertEquals(d,d);
				assertEquals(clone,clone);
				assertEquals(d,clone);
				assertEquals(clone,d);
			}
		}
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
		stations = Configurator.getMeasuringStations();
		
		for(IMeasuringStation ims : stations) {
			assertNotNull(ims);
		}
	}

	/**
	 * Class wide tear down method.
	 */
	@AfterClass
	public static void tearDownAfterClass() {
	}

	/**
	 * Test wide set up method.
	 */
	@Before
	public void setUp() {
	}

	/**
	 * Test wide tear down method.
	 */
	@After
	public void tearDown() {
	}
}
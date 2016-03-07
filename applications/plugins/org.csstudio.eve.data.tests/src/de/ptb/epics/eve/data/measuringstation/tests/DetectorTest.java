package de.ptb.epics.eve.data.measuringstation.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.tests.internal.Configurator;

/**
 * @author Marcus Michalsky
 * @since 1.25.1
 */
public class DetectorTest {
	private static List<IMeasuringStation> stations;
	
	/**
	 * 
	 */
	@Test
	public void testClone() {
		for(IMeasuringStation measuringStation : stations) {
			for (Detector detector : measuringStation.getDetectors()) {
				Detector clone = (Detector)detector.clone();
				
				assertEquals(detector.getClassName(), clone.getClassName());
				assertEquals(detector.getName(), clone.getName());
				assertEquals(detector.getID(), clone.getID());
				
				assertEquals(detector.getStop(), clone.getStop());
				assertEquals(detector.getStatus(), clone.getStatus());
				
				for (DetectorChannel channel : detector.getChannels()) {
					// TODO
				}
				
				// TODO options
			}
		}
	}
	
	@Test
	public void testEquals() {
		
	}
	
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
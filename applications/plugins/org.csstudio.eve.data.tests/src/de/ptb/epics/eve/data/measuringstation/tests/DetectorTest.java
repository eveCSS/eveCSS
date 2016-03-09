package de.ptb.epics.eve.data.measuringstation.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.DetectorMother;

/**
 * @author Marcus Michalsky
 * @since 1.26
 */
public class DetectorTest {
	private List<Detector> detectors;
	
	/**
	 * 
	 */
	@Test
	public void testClone() {
		for (Detector detector : detectors) {
			Detector clone = (Detector) detector.clone();

			assertEquals(detector.getClassName(), clone.getClassName());
			assertEquals(detector.getName(), clone.getName());
			assertEquals(detector.getID(), clone.getID());

			assertEquals(detector.getStop(), clone.getStop());
			assertEquals(detector.getStatus(), clone.getStatus());
			assertEquals(detector.getTrigger(), clone.getTrigger());

			assertEquals(detector.getOptions(), clone.getOptions());
			
			assertEquals(detector.getChannels(), clone.getChannels());
		}
	}
	
	@Test
	public void testEquals() {
		
	}
	
	/**
	 * Class wide setup method of the test.
	 */
	@BeforeClass
	public static void setUpBeforeClass() {

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
		this.detectors = new ArrayList<>();
		this.detectors.add(DetectorMother.createNewDetector());
		this.detectors.add(DetectorMother.addOption(
				DetectorMother.createNewDetector()));
		this.detectors.add(DetectorMother.addDetectorChannel(
				DetectorMother.createNewDetector()));
	}

	/**
	 * Test wide tear down method.
	 */
	@After
	public void tearDown() {
	}
}
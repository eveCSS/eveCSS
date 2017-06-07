package de.ptb.epics.eve.data.measuringstation.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;

/**
 * @author Marcus Michalsky
 * @since 1.2
 */
public class AbstractDeviceTest {

	/**
	 * 
	 */
	@Test
	public void testCompareTo() {
		List<AbstractDevice> upperCaseAtoZ = new ArrayList<AbstractDevice>();
		List<AbstractDevice> lowerCaseAtoZ = new ArrayList<AbstractDevice>();
		for (int i = 65; i < 91; i++) {
			String name = new Character((char) i).toString();
			AbstractDevice dev = new ConcreteDevice(name, name);
			upperCaseAtoZ.add(dev);
		}
		for (int i = 97; i < 123; i++) {
			String name = new Character((char) i).toString();
			AbstractDevice dev = new ConcreteDevice(name, name);
			lowerCaseAtoZ.add(dev);
		}
		
		for (AbstractDevice dev : upperCaseAtoZ) {
			for (AbstractDevice other : lowerCaseAtoZ) {
				int compareString = dev.getName().toLowerCase()
						.compareTo(other.getName().toLowerCase());
				int compareAbstractDevice = dev.compareTo(other);
				assertEquals(compareString, compareAbstractDevice);
			}
		}
		
		List<AbstractDevice> upperCopy = new ArrayList<AbstractDevice>(upperCaseAtoZ);
		Collections.reverse(upperCopy);
		for (AbstractDevice dev : upperCaseAtoZ) {
			for (AbstractDevice other : upperCopy) {
				int compareString = dev.getName().toLowerCase()
						.compareTo(other.getName().toLowerCase());
				int compareAbstractDevice = dev.compareTo(other);
				assertEquals(compareString, compareAbstractDevice);
			}
		}
		
		List<AbstractDevice> lowerCopy = new ArrayList<AbstractDevice>(lowerCaseAtoZ);
		Collections.reverse(lowerCopy);
		for (AbstractDevice dev : lowerCaseAtoZ) {
			for (AbstractDevice other : lowerCopy) {
				int compareString = dev.getName().toLowerCase()
						.compareTo(other.getName().toLowerCase());
				int compareAbstractDevice = dev.compareTo(other);
				assertEquals(compareString, compareAbstractDevice);
			}
		}
		
		for (AbstractDevice dev : lowerCopy) {
			for (AbstractDevice other : upperCopy) {
				int compareString = dev.getName().toLowerCase()
						.compareTo(other.getName().toLowerCase());
				int compareAbstractDevice = dev.compareTo(other);
				assertEquals(compareString, compareAbstractDevice);
			}
		}
	}

	// **********************************************************************
	// **************************** Setup ***********************************
	// **********************************************************************

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
	}

	/**
	 * Test wide tear down method.
	 */
	@After
	public void tearDown() {
	}

	// ***********************************************************************

	/**
	 * 
	 * @author Marcus Michalsky
	 * @since 1.2
	 */
	private class ConcreteDevice extends AbstractDevice {

		/**
		 * Constructor.
		 * 
		 * @param name
		 * @param id
		 */
		public ConcreteDevice(String name, String id) {
			super(name, id, null, null, null);
		}
	}
}
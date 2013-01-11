package de.ptb.epics.eve.data.scandescription.defaults.tests;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import javax.xml.datatype.Duration;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import de.ptb.epics.eve.data.scandescription.PositionMode;
import de.ptb.epics.eve.data.scandescription.Stepfunctions;
import de.ptb.epics.eve.data.scandescription.defaults.DefaultsAxis;
import de.ptb.epics.eve.data.scandescription.defaults.DefaultsManager;
import de.ptb.epics.eve.data.tests.internal.Configurator;

/**
 * @author Marcus Michalsky
 * @since 1.8
 */
public class DefaultsManagerTest {
	private static DefaultsManager defaultsManager;
	
	/**
	 * Tests whether {@link DefaultsManager#getAxis(String)} returns 
	 * <code>null</code> if axis does not exist.
	 */
	@Test
	public void testVoidAxis() {
		DefaultsAxis axis = DefaultsManagerTest.defaultsManager
				.getAxis("VoidAxis");
		assertNull(axis);
	}
	
	/**
	 * Tests whether an existing file axis' values are mapped correctly 
	 * from XML to object.
	 */
	@Test
	public void testFileAxis() {
		DefaultsAxis axis = DefaultsManagerTest.defaultsManager
				.getAxis("FileAxis");
		assertNotNull(axis);
		assertEquals("FileAxis", axis.getId());
		assertEquals(Stepfunctions.FILE, axis.getStepfunction());
		assertEquals(PositionMode.RELATIVE, axis.getPositionmode());
		assertEquals("/test/file", axis.getFile());
	}
	
	/**
	 * Tests whether an existing position list axis' values are mapped 
	 * correctly from XML to object.
	 */
	@Test
	public void testPositionlistAxis() {
		DefaultsAxis axis = DefaultsManagerTest.defaultsManager
				.getAxis("PositionlistAxis");
		assertNotNull(axis);
		assertEquals("PositionlistAxis", axis.getId());
		assertEquals(Stepfunctions.POSITIONLIST, axis.getStepfunction());
		assertEquals(PositionMode.ABSOLUTE, axis.getPositionmode());
		assertEquals("4565656,2,4534,22", axis.getPositionList());
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("cast")
	@Test
	public void testStartStopStepIntAxis() {
		DefaultsAxis axis = DefaultsManagerTest.defaultsManager
				.getAxis("StartStopStepIntAxis");
		assertNotNull(axis);
		assertEquals("StartStopStepIntAxis", axis.getId());
		assertEquals(Stepfunctions.ADD, axis.getStepfunction());
		assertEquals(PositionMode.ABSOLUTE, axis.getPositionmode());
		assertEquals(new Integer(1), (Integer)axis.getStart());
		assertEquals(new Integer(2), (Integer)axis.getStop());
		assertEquals(new Integer(3), (Integer)axis.getStepwidth());
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("cast")
	@Test
	public void testStartStopStepDoubleAxis() {
		DefaultsAxis axis = DefaultsManagerTest.defaultsManager
				.getAxis("StartStopStepDoubleAxis");
		assertNotNull(axis);
		assertEquals("StartStopStepDoubleAxis", axis.getId());
		assertEquals(Stepfunctions.ADD, axis.getStepfunction());
		assertEquals(PositionMode.ABSOLUTE, axis.getPositionmode());
		assertEquals(new Double(1.0), (Double)axis.getStart());
		assertEquals(new Double(10.0), (Double)axis.getStop());
		assertEquals(new Double(1.0), (Double)axis.getStepwidth());
	}
	
	/**
	 * 
	 */
	@Test
	public void testStartStopStepDateAxis() {
		DefaultsAxis axis = DefaultsManagerTest.defaultsManager
				.getAxis("StartStopStepDateAxis");
		assertNotNull(axis);
		assertEquals("StartStopStepDateAxis", axis.getId());
		assertEquals(Stepfunctions.ADD, axis.getStepfunction());
		assertEquals(PositionMode.ABSOLUTE, axis.getPositionmode());
		
		Date start = ((Date)axis.getStart());
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(start);

		assertEquals(2013, startCal.get(Calendar.YEAR));
		assertEquals(0, startCal.get(Calendar.MONTH));
		assertEquals(1, startCal.get(Calendar.DAY_OF_MONTH));
		assertEquals(0, startCal.get(Calendar.HOUR));
		assertEquals(0, startCal.get(Calendar.MINUTE));
		assertEquals(0, startCal.get(Calendar.SECOND));
		assertEquals(0, startCal.get(Calendar.MILLISECOND));
		
		Date stop = ((Date)axis.getStop());
		Calendar stopCal = Calendar.getInstance();
		stopCal.setTime(stop);

		assertEquals(2013, stopCal.get(Calendar.YEAR));
		assertEquals(0, stopCal.get(Calendar.MONTH));
		assertEquals(1, stopCal.get(Calendar.DAY_OF_MONTH));
		assertEquals(1, stopCal.get(Calendar.HOUR));
		assertEquals(0, stopCal.get(Calendar.MINUTE));
		assertEquals(0, stopCal.get(Calendar.SECOND));
		assertEquals(0, stopCal.get(Calendar.MILLISECOND));
		
		Date stepwidth = ((Date)axis.getStepwidth());
		Calendar stepwidthCal = Calendar.getInstance();
		stepwidthCal.setTime(stepwidth);

		assertEquals(1970, stepwidthCal.get(Calendar.YEAR));
		assertEquals(0, stepwidthCal.get(Calendar.MONTH));
		assertEquals(1, stepwidthCal.get(Calendar.DAY_OF_MONTH));
		assertEquals(0, stepwidthCal.get(Calendar.HOUR));
		assertEquals(1, stepwidthCal.get(Calendar.MINUTE));
		assertEquals(0, stepwidthCal.get(Calendar.SECOND));
		assertEquals(0, stepwidthCal.get(Calendar.MILLISECOND));
	}
	
	/**
	 * 
	 */
	@Test
	public void testStartStopStepDurationAxis() {
		DefaultsAxis axis = DefaultsManagerTest.defaultsManager
				.getAxis("StartStopStepDurationAxis");
		assertNotNull(axis);
		assertEquals("StartStopStepDurationAxis", axis.getId());
		assertEquals(Stepfunctions.ADD, axis.getStepfunction());
		assertEquals(PositionMode.RELATIVE, axis.getPositionmode());
		
		Duration startDur = (Duration)axis.getStart();
		assertEquals(0, startDur.getYears());
		assertEquals(0, startDur.getMonths());
		assertEquals(0, startDur.getDays());
		assertEquals(0, startDur.getHours());
		assertEquals(0, startDur.getMinutes());
		assertEquals(0, startDur.getSeconds());
		
		Duration stopDur = (Duration)axis.getStop();
		assertEquals(0, stopDur.getYears());
		assertEquals(0, stopDur.getMonths());
		assertEquals(0, stopDur.getDays());
		assertEquals(1, stopDur.getHours());
		assertEquals(0, stopDur.getMinutes());
		assertEquals(0, stopDur.getSeconds());
		
		Duration stepwidthDur = (Duration)axis.getStepwidth();
		assertEquals(0, stepwidthDur.getYears());
		assertEquals(0, stepwidthDur.getMonths());
		assertEquals(0, stepwidthDur.getDays());
		assertEquals(0, stepwidthDur.getHours());
		assertEquals(1, stepwidthDur.getMinutes());
		assertEquals(0, stepwidthDur.getSeconds());
	}
	
	/**
	 * Tests whether loaded defaults are correctly marshaled to XML.
	 */
	@Test
	public void testSaveDefaults() {
		// long now = Calendar.getInstance().getTimeInMillis();
		new File("defaults/defaults.xml.bup").delete();
		DefaultsManagerTest.defaultsManager.save(new File(
				"defaults/defaults.xml"), new File("defaults/defaults.xsd"));
		assertTrue(new File("defaults/defaults.xml.bup").exists());
		// assertTrue(new File("defaults/defaults.xml").lastModified() > now);
	}
	
	// **********************************************************************
	// **************************** Setup ***********************************
	// **********************************************************************

	/**
	 * Class wide setup.
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		Configurator.configureLogging();
		DefaultsManagerTest.defaultsManager =  new DefaultsManager();
		DefaultsManagerTest.defaultsManager.init(new File(
				"defaults/defaults.xml"), new File("defaults/defaults.xsd"));
		assertTrue(DefaultsManagerTest.defaultsManager.isInitialized());
	}

	/**
	 * Class wide tear down.
	 */
	@AfterClass
	public static void tearDownAfterClass() {
	}

	/**
	 * Test wide set up.
	 */
	@Before
	public void setUp() {
	}

	/**
	 * Test wide tear down.
	 */
	@After
	public void tearDown() {
	}
}
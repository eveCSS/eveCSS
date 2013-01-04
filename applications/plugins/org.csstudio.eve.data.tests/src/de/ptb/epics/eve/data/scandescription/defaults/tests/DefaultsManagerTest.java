package de.ptb.epics.eve.data.scandescription.defaults.tests;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
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
	 * Tests whether loaded defaults are correctly marshaled to XML.
	 */
	@Test
	public void testSaveDefaults() {
		new File("defaults/defaults.xml.bup").delete();
		DefaultsManagerTest.defaultsManager.save(new File(
				"defaults/defaults.xml"), new File("defaults/defaults.xsd"));
		assertTrue(new File("defaults/defaults.xml.bup").exists());
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
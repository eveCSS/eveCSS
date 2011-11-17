package de.ptb.epics.eve.data.scandescription.tests;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.tests.internal.Configurator;

public class ScanModuleTest {
	
	private static Logger logger = 
			Logger.getLogger(ScanModuleTest.class.getName());
	
	private ScanModule scanModule;
	private Axis axis;
	
	@Test
	public void foo() {
		
	}
	
	// ***********************************************************************
	
	/**
	 * Initializes logging (Class wide setup 
	 * method of the test).
	 */
	@BeforeClass
	public static void runBeforeClass() {
		Configurator.configureLogging();
	}
	
	/**
	 * test wide set up method
	 */
	@Before
	public void beforeEveryTest() {
		this.scanModule = new ScanModule(1);
		this.axis = new Axis(this.scanModule);
		
		assertEquals(this.axis.getModelErrors().size(), 0);
	}
	
	/**
	 * test wide tear down method
	 */
	@After
	public void afterEveryTest() {
	}
	
	/**
	 * class wide tear down method
	 */
	@AfterClass
	public static void afterClass() {
	}
}
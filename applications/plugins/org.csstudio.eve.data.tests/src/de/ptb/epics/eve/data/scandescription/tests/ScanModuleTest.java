package de.ptb.epics.eve.data.scandescription.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.ScanModule;

public class ScanModuleTest {
	private ScanModule scanModule;
	private Axis axis;
	
	@Ignore
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
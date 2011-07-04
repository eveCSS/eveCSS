package de.ptb.epics.eve.data.scandescription.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.Stepfunctions;
import de.ptb.epics.eve.data.tests.internal.Configurator;

/**
 * <code>AxisTest</code> 
 * 
 * @author Marcus Michalsky
 * @since 0.4.1
 */
public class AxisTest {

	// 
	private ScanDescription scanDescription;
	
	/**
	 * 
	 */
	@Test
	public void testGetModelErrors() {
		assertTrue(scanDescription.getModelErrors().size() == 0);
		
		// run test for each axis we find in the scan description
		for(Chain ch : scanDescription.getChains()) {
			for(ScanModule sm : ch.getScanModules()) {
				for(Axis a : sm.getAxes()) {
					// initially no error should occur
					assertTrue(a.getModelErrors().size() == 0);
					
					if (a.getStepfunctionEnum() == Stepfunctions.ADD ||
						a.getStepfunctionEnum() == Stepfunctions.MULTIPLY) {
						// start, stop, step width case
						
					}
				}
			}
		}
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
		scanDescription = Configurator.getScanDescription();
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

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
import de.ptb.epics.eve.data.scandescription.Stepfunctions;
import de.ptb.epics.eve.data.scandescription.errors.AxisError;
import de.ptb.epics.eve.data.scandescription.errors.AxisErrorTypes;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;

/**
 * <code>AxisTest</code> 
 * 
 * @author Marcus Michalsky
 * @since 0.4.1
 */
public class AxisTest {
	private Axis axis;
	
	/**
	 * 
	 */
	@Test
	@Ignore("Obsolete (see Feature #591)")
	public void testGetModelErrorsStepFunctionsAdd() {
		// before modification, no errors should be present
		assertTrue(axis.getModelErrors().isEmpty());
		
		IModelError startError =  new AxisError(axis, AxisErrorTypes.START_NOT_SET);
		IModelError stopError = new AxisError(axis, AxisErrorTypes.STOP_NOT_SET);
		IModelError stepwidthError = new AxisError(axis, AxisErrorTypes.STEPWIDTH_NOT_SET);
		
		axis.setStepfunction(Stepfunctions.ADD);
		assertTrue(axis.getModelErrors().size() == 3);
		assertTrue(axis.getModelErrors().contains(startError));
		assertTrue(axis.getModelErrors().contains(stopError));
		assertTrue(axis.getModelErrors().contains(stepwidthError));
		
		// TODO without a motor axis set, a null pointer is thrown below
		// TODO maybe a model flaw. the axis could be constructed by passing 
		// a scan module. At least some more description in the usage of the 
		// axis model would be wise.
		// It seems that if one sets a MotorAxis for the Axis, then the 
		// step function is automatically set to "add" and default values are 
		// inserted.
		/*
		axis.setStart("1");
		assertTrue(axis.getModelErrors().size() == 2);
		assertFalse(axis.getModelErrors().contains(startError));
		
		axis.setStop("2");
		assertTrue(axis.getModelErrors().size() == 1);
		assertFalse(axis.getModelErrors().contains(stopError));
		
		axis.setStepwidth("1");
		assertTrue(axis.getModelErrors().isEmpty());
		assertFalse(axis.getModelErrors().contains(stepwidthError));
		*/
		
		// TODO set start and stop to invalid values such that isValuePossible 
		// is false
		
		// stepcount == -1 should produce an error, but when is it set to -1 ? 
		// only in the GUI ? // TODO
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
		ScanModule scanModule = new ScanModule(1);
		axis = new Axis(scanModule);
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
package de.ptb.epics.eve.data.scandescription.tests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.measuringstation.Function;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.Stepfunctions;

public class ScanModuleTest {
	private ScanModule scanModule;
	
	@Test
	public void testMainAxis() {
		Axis axis1 = new Axis(this.scanModule);
		Axis axis2 = new Axis(this.scanModule);
		
		MotorAxis mockAxis = mock(MotorAxis.class);
		when(mockAxis.getType()).thenReturn(DataTypes.DOUBLE);
		when(mockAxis.getDefaultValue()).thenReturn("0");
		Function mockFunction = mock(Function.class);
		when(mockFunction.isDiscrete()).thenReturn(false);
		when(mockAxis.getGoto()).thenReturn(mockFunction);
		axis1.setMotorAxis(mockAxis);
		axis2.setMotorAxis(mockAxis);
		
		axis1.setStepfunction(Stepfunctions.ADD);
		axis2.setStepfunction(Stepfunctions.ADD);
		
		axis1.setStart(0.0);
		axis1.setStop(100.0);
		axis1.setStepwidth(10.0);
		
		axis2.setStart(0.0);
		axis2.setStop(20.0);
		axis2.setStepwidth(1.0);
		
		this.scanModule.add(axis1);
		this.scanModule.add(axis2);
		
		axis1.setMainAxis(true);
		
		assertEquals(2.0, (double) axis2.getStepwidth(), 0);
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
	}
	
	/**
	 * test wide tear down method
	 */
	@After
	public void afterEveryTest() {
		this.scanModule = null;
	}
	
	/**
	 * class wide tear down method
	 */
	@AfterClass
	public static void afterClass() {
	}
}
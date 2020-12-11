package de.ptb.epics.eve.data.scandescription.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.Stepfunctions;
import de.ptb.epics.eve.data.scandescription.axismode.AddMultiplyMode;
import de.ptb.epics.eve.data.scandescription.axismode.AddMultiplyModeDouble;
import de.ptb.epics.eve.data.scandescription.axismode.AdjustParameter;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.MotorAxisMother;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class ScanModuleMainAxisTest {
	private static final double DELTA = 0.00001;
	
	private ScanModule scanModule;
	private boolean scanModuleMainAxisPropFired;
	private boolean axisAdjustParameterPropFired;
	
	/**
	 * Tests whether the ScanModule.MAIN_AXIS_PROP event fired with the correct
	 * new value when setting the main axis.
	 */
	@Test
	public void testSetMainAxis() {
		final Axis axis1 = new Axis(this.scanModule);
		axis1.setMotorAxis(MotorAxisMother.createNewDoubleTypeMotorAxis());
		axis1.setStepfunction(Stepfunctions.ADD);
		axis1.setStart(1.0);
		axis1.setStop(10.0);
		axis1.setStepwidth(1.0);
		this.scanModule.add(axis1);
		this.scanModule.addPropertyChangeListener(ScanModule.MAIN_AXIS_PROP, 
				new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(ScanModule.MAIN_AXIS_PROP)) {
					ScanModuleMainAxisTest.this.scanModuleMainAxisPropFired = true;
					assertEquals(axis1, evt.getNewValue());
				}
			}
		});
		axis1.setMainAxis(true);
		assertEquals(axis1, this.scanModule.getMainAxis());
		assertTrue(this.scanModuleMainAxisPropFired);
		assertTrue(axis1.isMainAxis());
	}
	
	/**
	 * Tests whether the ScanModule.MAIN_AXIS_PROP event fired with null as new
	 * value when resetting the set main axis.
	 */
	@Test
	public void testResetMainAxis() {
		final Axis axis1 = new Axis(this.scanModule);
		axis1.setMotorAxis(MotorAxisMother.createNewDoubleTypeMotorAxis());
		axis1.setStepfunction(Stepfunctions.ADD);
		axis1.setStart(1.0);
		axis1.setStop(10.0);
		axis1.setStepwidth(1.0);
		this.scanModule.add(axis1);
		axis1.setMainAxis(true);
		this.scanModule.addPropertyChangeListener(ScanModule.MAIN_AXIS_PROP, 
				new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(ScanModule.MAIN_AXIS_PROP)) {
					ScanModuleMainAxisTest.this.scanModuleMainAxisPropFired = true;
					assertEquals(null, evt.getNewValue());
				}
			}
		});
		axis1.setMainAxis(false);
		assertNull(this.scanModule.getMainAxis());
		assertTrue(this.scanModuleMainAxisPropFired);
		assertFalse(axis1.isMainAxis());
	}
	
	/**
	 * Tests whether the ScanModule.MAIN_AXIS_PROP event fired with null as new
	 * value when removing the axis set as main axis.
	 */
	@Test
	public void testRemoveMainAxis() {
		final Axis axis1 = new Axis(this.scanModule);
		axis1.setMotorAxis(MotorAxisMother.createNewDoubleTypeMotorAxis());
		axis1.setStepfunction(Stepfunctions.ADD);
		axis1.setStart(1.0);
		axis1.setStop(10.0);
		axis1.setStepwidth(1.0);
		this.scanModule.add(axis1);
		axis1.setMainAxis(true);
		this.scanModule.addPropertyChangeListener(ScanModule.MAIN_AXIS_PROP, 
				new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(ScanModule.MAIN_AXIS_PROP)) {
					ScanModuleMainAxisTest.this.scanModuleMainAxisPropFired = true;
					assertEquals(null, evt.getNewValue());
				}
			}
		});
		assertEquals(axis1, this.scanModule.getMainAxis());
		assertFalse(this.scanModuleMainAxisPropFired);
		this.scanModule.remove(axis1);
		assertNull(this.scanModule.getMainAxis());
		assertTrue(this.scanModuleMainAxisPropFired);
	}
	
	/**
	 * Tests whether the ScanModule.MAIN_AXIS_PROP Event fired with the correct 
	 * new value when replacing the main axis with another.
	 */
	@Test
	public void testReplaceMainAxis() {
		final Axis axis1 = new Axis(this.scanModule);
		axis1.setMotorAxis(MotorAxisMother.createNewDoubleTypeMotorAxis());
		axis1.setStepfunction(Stepfunctions.ADD);
		axis1.setStart(1.0);
		axis1.setStop(10.0);
		axis1.setStepwidth(1.0);
		this.scanModule.add(axis1);
		axis1.setMainAxis(true);
		final Axis axis2 = new Axis(this.scanModule);
		axis2.setMotorAxis(MotorAxisMother.createNewDoubleTypeMotorAxis());
		axis2.setStepfunction(Stepfunctions.ADD);
		((AddMultiplyModeDouble)axis2.getMode()).setAdjustParameter(
				AdjustParameter.STEPCOUNT);
		axis2.setStart(2.0);
		axis2.setStop(20.0);
		axis2.setStepwidth(2.0);
		this.scanModule.add(axis2);
		this.scanModule.addPropertyChangeListener(ScanModule.MAIN_AXIS_PROP,  
				new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(ScanModule.MAIN_AXIS_PROP)) {
					ScanModuleMainAxisTest.this.scanModuleMainAxisPropFired = true;
					assertEquals(axis2, evt.getNewValue());
				}
			}
		});
		axis2.setMainAxis(true);
		assertTrue(this.scanModuleMainAxisPropFired);
		assertTrue(axis2.isMainAxis());
		assertFalse(axis1.isMainAxis());
	}
	
	/**
	 * Tests whether the ScanModule.MAIN_AXIS_PROP event fired with the null 
	 * when changing the stepfunction of the current main axis.
	 */
	@Test
	public void testSetMainAxisChangeStepfunction() {
		final Axis axis1 = new Axis(this.scanModule);
		axis1.setMotorAxis(MotorAxisMother.createNewDoubleTypeMotorAxis());
		axis1.setStepfunction(Stepfunctions.ADD);
		axis1.setStart(1.0);
		axis1.setStop(10.0);
		axis1.setStepwidth(1.0);
		this.scanModule.add(axis1);
		axis1.setMainAxis(true);
		this.scanModule.addPropertyChangeListener(ScanModule.MAIN_AXIS_PROP,  
				new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(ScanModule.MAIN_AXIS_PROP)) {
					ScanModuleMainAxisTest.this.scanModuleMainAxisPropFired = true;
					assertEquals(null, evt.getNewValue());
				}
			}
		});
		assertEquals(axis1, this.scanModule.getMainAxis());
		axis1.setStepfunction(Stepfunctions.FILE);
		assertNull(this.scanModule.getMainAxis());
		assertTrue(this.scanModuleMainAxisPropFired);
		assertFalse(axis1.isMainAxis());
	}
	
	/**
	 * Tests whether the start value of an axis is adjusted if another is set as
	 * main axis.
	 */
	@Test
	public void testSetMainAxisAdjustStart() {
		final Axis axis1 = new Axis(this.scanModule);
		axis1.setMotorAxis(MotorAxisMother.createNewDoubleTypeMotorAxis());
		axis1.setStepfunction(Stepfunctions.ADD);
		((AddMultiplyModeDouble)axis1.getMode()).setAdjustParameter(
				AdjustParameter.STEPCOUNT);
		axis1.setStart(0.0);
		axis1.setStop(100.0);
		axis1.setStepwidth(10.0);
		this.scanModule.add(axis1);
		final Axis axis2 = new Axis(this.scanModule);
		axis2.setMotorAxis(MotorAxisMother.createNewDoubleTypeMotorAxis());
		axis2.setStepfunction(Stepfunctions.ADD);
		axis2.setStart(0.0); // should be 10 after setting axis 1 as main axis
		axis2.setStop(20.0);
		axis2.setStepwidth(1.0);
		((AddMultiplyModeDouble)axis2.getMode()).setAdjustParameter(
				AdjustParameter.START);
		this.scanModule.add(axis2);
		
		assertEquals(0.0, (double) axis2.getStart(), DELTA);
		assertEquals(20.0, axis2.getStepcount(), DELTA);
		axis1.setMainAxis(true);
		assertEquals(10.0, (double) axis2.getStart(), DELTA);
		assertEquals(10.0, axis2.getStepcount(), DELTA);
		assertEquals(AdjustParameter.START, 
				((AddMultiplyModeDouble)axis2.getMode()).getAdjustParameter());
	}
	
	/**
	 * Tests whether the stop value of an axis is adjusted if another is set as
	 * main axis.
	 */
	@Test
	public void testSetMainAxisAdjustStop() {
		final Axis axis1 = new Axis(this.scanModule);
		axis1.setMotorAxis(MotorAxisMother.createNewDoubleTypeMotorAxis());
		axis1.setStepfunction(Stepfunctions.ADD);
		((AddMultiplyModeDouble)axis1.getMode()).setAdjustParameter(
				AdjustParameter.STEPCOUNT);
		axis1.setStart(0.0);
		axis1.setStop(100.0);
		axis1.setStepwidth(10.0);
		this.scanModule.add(axis1);
		final Axis axis2 = new Axis(this.scanModule);
		axis2.setMotorAxis(MotorAxisMother.createNewDoubleTypeMotorAxis());
		axis2.setStepfunction(Stepfunctions.ADD);
		axis2.setStart(0.0);
		axis2.setStop(20.0); // should be 10 after setting axis 1 as main axis
		axis2.setStepwidth(1.0);
		((AddMultiplyModeDouble)axis2.getMode()).setAdjustParameter(
				AdjustParameter.STOP);
		this.scanModule.add(axis2);
		
		assertEquals(20.0, (double) axis2.getStop(), DELTA);
		assertEquals(20.0, axis2.getStepcount(), DELTA);
		axis1.setMainAxis(true);
		assertEquals(10.0, (double) axis2.getStop(), DELTA);
		assertEquals(10.0, axis2.getStepcount(), DELTA);
		assertEquals(AdjustParameter.STOP, 
				((AddMultiplyModeDouble)axis2.getMode()).getAdjustParameter());
	}
	
	/**
	 * Tests whether the stepwidth value of an axis is adjusted if another is 
	 * set as main axis.
	 */
	@Test
	public void testSetMainAxisAdjustStepwidth() {
		final Axis axis1 = new Axis(this.scanModule);
		axis1.setMotorAxis(MotorAxisMother.createNewDoubleTypeMotorAxis());
		axis1.setStepfunction(Stepfunctions.ADD);
		((AddMultiplyModeDouble)axis1.getMode()).setAdjustParameter(
				AdjustParameter.STEPCOUNT);
		axis1.setStart(0.0);
		axis1.setStop(100.0);
		axis1.setStepwidth(10.0);
		this.scanModule.add(axis1);
		final Axis axis2 = new Axis(this.scanModule);
		axis2.setMotorAxis(MotorAxisMother.createNewDoubleTypeMotorAxis());
		axis2.setStepfunction(Stepfunctions.ADD);
		axis2.setStart(0.0);
		axis2.setStop(20.0);
		axis2.setStepwidth(1.0); // should be 2 after setting axis1 as main axis
		((AddMultiplyModeDouble)axis2.getMode()).setAdjustParameter(
				AdjustParameter.STEPWIDTH);
		this.scanModule.add(axis2);
		
		assertEquals(1.0, (double) axis2.getStepwidth(), DELTA);
		assertEquals(20.0, axis2.getStepcount(), DELTA);
		axis1.setMainAxis(true);
		assertEquals(2.0, (double) axis2.getStepwidth(), DELTA);
		assertEquals(10.0, axis2.getStepcount(), DELTA);
		assertEquals(AdjustParameter.STEPWIDTH, 
				((AddMultiplyModeDouble)axis2.getMode()).getAdjustParameter());
	}
	
	/**
	 * Tests whether the stepwidth value of an axis is adjusted if another is 
	 * set as main axis.
	 */
	@Test
	public void testSetMainAxisAdjustStepwidthWithStepcountSet() {
		final Axis axis1 = new Axis(this.scanModule);
		axis1.setMotorAxis(MotorAxisMother.createNewDoubleTypeMotorAxis());
		axis1.setStepfunction(Stepfunctions.ADD);
		((AddMultiplyModeDouble)axis1.getMode()).setAdjustParameter(
				AdjustParameter.STEPCOUNT);
		axis1.setStart(0.0);
		axis1.setStop(100.0);
		axis1.setStepwidth(10.0);
		this.scanModule.add(axis1);
		final Axis axis2 = new Axis(this.scanModule);
		axis2.setMotorAxis(MotorAxisMother.createNewDoubleTypeMotorAxis());
		axis2.setStepfunction(Stepfunctions.ADD);
		((AddMultiplyModeDouble)axis2.getMode()).setAdjustParameter(
				AdjustParameter.STEPCOUNT);
		axis2.setStart(0.0);
		axis2.setStop(20.0);
		axis2.setStepwidth(1.0); // should be 2 after setting axis1 as main axis
		this.scanModule.add(axis2);
		
		assertEquals(1.0, (double) axis2.getStepwidth(), DELTA);
		assertEquals(20.0, axis2.getStepcount(), DELTA);
		axis1.setMainAxis(true);
		assertEquals(2.0, (double) axis2.getStepwidth(), DELTA);
		assertEquals(10.0, axis2.getStepcount(), DELTA);
		assertEquals(AdjustParameter.STEPWIDTH, 
				((AddMultiplyModeDouble)axis2.getMode()).getAdjustParameter());
	}
	
	/**
	 * Tests whether the adjust parameter of an axis is changed from stepcount 
	 * to stepwidth and properly propagated when another axis is set as main 
	 * axis.
	 */
	@Test
	public void testSetMainAxisAdjustSecondAxis() {
		final Axis axis1 = new Axis(this.scanModule);
		axis1.setMotorAxis(MotorAxisMother.createNewDoubleTypeMotorAxis());
		axis1.setStepfunction(Stepfunctions.ADD);
		((AddMultiplyModeDouble)axis1.getMode()).setAdjustParameter(
				AdjustParameter.STEPCOUNT);
		axis1.setStart(0.0);
		axis1.setStop(100.0);
		axis1.setStepwidth(10.0);
		this.scanModule.add(axis1);
		final Axis axis2 = new Axis(this.scanModule);
		axis2.setMotorAxis(MotorAxisMother.createNewDoubleTypeMotorAxis());
		axis2.setStepfunction(Stepfunctions.ADD);
		((AddMultiplyModeDouble)axis2.getMode()).setAdjustParameter(
				AdjustParameter.STEPCOUNT);
		axis2.setStart(0.0);
		axis2.setStop(20.0);
		axis2.setStepwidth(1.0);
		this.scanModule.add(axis2);
		axis2.addPropertyChangeListener(
				AddMultiplyMode.ADJUST_PARAMETER_PROP, 
				new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				ScanModuleMainAxisTest.this.axisAdjustParameterPropFired = true;
				assertEquals(AdjustParameter.STEPWIDTH, evt.getNewValue());
			}
		});
		assertEquals(AdjustParameter.STEPCOUNT, 
				((AddMultiplyModeDouble)axis2.getMode()).getAdjustParameter());
		axis1.setMainAxis(true);
		assertTrue(this.axisAdjustParameterPropFired);
		assertEquals(AdjustParameter.STEPWIDTH, 
				((AddMultiplyModeDouble)axis2.getMode()).getAdjustParameter());
	}
	
	/**
	 * Tests if an exception is thrown when trying to set a stepcount of an axis
	 * when another axis of the scan module is set as main axis.
	 */
	@Test(expected=IllegalStateException.class)
	public void testSetStepcountIfMainAxisIsSet() {
		final Axis axis1 = new Axis(this.scanModule);
		axis1.setMotorAxis(MotorAxisMother.createNewDoubleTypeMotorAxis());
		axis1.setStepfunction(Stepfunctions.ADD);
		((AddMultiplyModeDouble)axis1.getMode()).setAdjustParameter(
				AdjustParameter.STEPCOUNT);
		axis1.setStart(0.0);
		axis1.setStop(100.0);
		axis1.setStepwidth(10.0);
		this.scanModule.add(axis1);
		final Axis axis2 = new Axis(this.scanModule);
		axis2.setMotorAxis(MotorAxisMother.createNewDoubleTypeMotorAxis());
		axis2.setStepfunction(Stepfunctions.ADD);
		((AddMultiplyModeDouble)axis2.getMode()).setAdjustParameter(
				AdjustParameter.STEPCOUNT);
		axis2.setStart(0.0);
		axis2.setStop(20.0);
		axis2.setStepwidth(1.0);
		this.scanModule.add(axis2);
		axis1.setMainAxis(true);
		axis2.setStepcount(1.0);
	}
	
	/**
	 * Tests whether an Axis added to a ScanModule is properly adjusted (set 
	 * stepcount and adjust stepwidth) if an Axis set as Main Axis is already 
	 * present in the ScanModule.
	 */
	@Test
	public void testAddAxisAfterSetMainAxis() {
		final Axis axis1 = new Axis(this.scanModule);
		axis1.setMotorAxis(MotorAxisMother.createNewDoubleTypeMotorAxis());
		axis1.setStepfunction(Stepfunctions.ADD);
		((AddMultiplyModeDouble)axis1.getMode()).setAdjustParameter(
				AdjustParameter.STEPCOUNT);
		axis1.setStart(0.0);
		axis1.setStop(100.0);
		axis1.setStepwidth(10.0);
		this.scanModule.add(axis1);
		axis1.setMainAxis(true);
		final Axis axis2 = new Axis(this.scanModule);
		axis2.setMotorAxis(MotorAxisMother.createNewDoubleTypeMotorAxis());
		axis2.setStepfunction(Stepfunctions.ADD);
		((AddMultiplyModeDouble)axis2.getMode()).setAdjustParameter(
				AdjustParameter.STEPCOUNT);
		axis2.setStart(0.0);
		axis2.setStop(20.0);
		axis2.setStepwidth(1.0);
		this.scanModule.add(axis2);
		
		assertEquals(2.0, (double) axis2.getStepwidth(), DELTA);
		assertEquals(10.0, axis2.getStepcount(), DELTA);
		assertEquals(AdjustParameter.STEPWIDTH, 
				((AddMultiplyModeDouble)axis2.getMode()).getAdjustParameter());
	}
	
	/**
	 * Test whether the stepcount is set and stepwidth adjusted for an existing
	 * axis if another axis with the main axis property set is added to the SM.
	 */
	@Test
	@Ignore
	public void testAddMainAxis() {
		// not possible yet in GUI (newly created Axis never are Main Axis)
	}
	
	@Before
	public void beforeTest() {
		this.scanModule = new ScanModule(1);
		
		this.scanModuleMainAxisPropFired = false;
		this.axisAdjustParameterPropFired = false;
	}
}

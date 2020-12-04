package de.ptb.epics.eve.data.scandescription.tests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.measuringstation.Function;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.Stepfunctions;
import de.ptb.epics.eve.data.scandescription.axismode.AddMultiplyMode;

/**
 * <code>AxisTest</code> 
 * 
 * @author Marcus Michalsky
 * @since 1.23
 */
public class AxisAddMultiplyTest implements PropertyChangeListener {
	private Axis axis;
	
	private boolean start;
	private boolean stop;
	private boolean stepwidth;
	private boolean stepcount;
	
	@Test
	public void testAddMultiplyModeInt() {
		MotorAxis mockAxis = mock(MotorAxis.class);
		when(mockAxis.getType()).thenReturn(DataTypes.INT);
		when(mockAxis.getDefaultValue()).thenReturn("0");
		Function mockFunction = mock(Function.class);
		when(mockFunction.isDiscrete()).thenReturn(false);
		when(mockAxis.getGoto()).thenReturn(mockFunction);
		this.axis.setMotorAxis(mockAxis);
		
		this.axis.setStepfunction(Stepfunctions.ADD);
		
		this.initListener();
		
		this.axis.setStart(1);
		this.axis.setStop(10);
		this.axis.setStepwidth(1);
		
		this.verifyListener();
		
		assertEquals(1, (int) this.axis.getStart());
		assertEquals(10, (int) this.axis.getStop());
		assertEquals(1, (int) this.axis.getStepwidth());
		assertEquals(9, this.axis.getStepcount(), 0);
	}
	
	@Test
	public void testAddMultiplyModeDouble() {
		MotorAxis mockAxis = mock(MotorAxis.class);
		when(mockAxis.getType()).thenReturn(DataTypes.DOUBLE);
		when(mockAxis.getDefaultValue()).thenReturn("0");
		Function mockFunction = mock(Function.class);
		when(mockFunction.isDiscrete()).thenReturn(false);
		when(mockAxis.getGoto()).thenReturn(mockFunction);
		this.axis.setMotorAxis(mockAxis);
		
		this.axis.setStepfunction(Stepfunctions.ADD);
		
		this.initListener();
		
		this.axis.setStart(1.0);
		this.axis.setStop(10.0);
		this.axis.setStepwidth(1.0);
		
		this.verifyListener();
		
		assertEquals(1, (double) this.axis.getStart(), 0);
		assertEquals(10, (double) this.axis.getStop(), 0);
		assertEquals(1, (double) this.axis.getStepwidth(), 0);
		assertEquals(9, this.axis.getStepcount(), 0);
	}
	
	private void initListener() {
		this.start = false;
		this.stop = false;
		this.stepwidth = false;
		this.stepcount = false;
		
		this.axis.addPropertyChangeListener(AddMultiplyMode.START_PROP, this);
		this.axis.addPropertyChangeListener(AddMultiplyMode.STOP_PROP, this);
		this.axis.addPropertyChangeListener(AddMultiplyMode.STEPWIDTH_PROP, this);
		this.axis.addPropertyChangeListener(AddMultiplyMode.STEPCOUNT_PROP, this);
	}
	
	private void verifyListener() {
		this.axis.removePropertyChangeListener(AddMultiplyMode.START_PROP, this);
		this.axis.removePropertyChangeListener(AddMultiplyMode.STOP_PROP, this);
		this.axis.removePropertyChangeListener(AddMultiplyMode.STEPWIDTH_PROP, this);
		this.axis.removePropertyChangeListener(AddMultiplyMode.STEPCOUNT_PROP, this);
		
		assertTrue(this.start);
		assertTrue(this.stop);
		assertTrue(this.stepwidth);
		assertTrue(this.stepcount);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		switch (evt.getPropertyName()) {
		case AddMultiplyMode.START_PROP:
			this.start = true;
			break;
		case AddMultiplyMode.STOP_PROP:
			this.stop = true;
			break;
		case AddMultiplyMode.STEPWIDTH_PROP:
			this.stepwidth = true;
			break;
		case AddMultiplyMode.STEPCOUNT_PROP:
			this.stepcount = true;
			break;
		}
	}
	
	@Before
	public void beforeEveryTest() {
		ScanModule scanModule = new ScanModule(1);
		axis = new Axis(scanModule);
	}
	
	@After
	public void afterEveryTest() {
		axis = null;
	}
}
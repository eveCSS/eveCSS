package de.ptb.epics.eve.data.scandescription.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.Stepfunctions;
import de.ptb.epics.eve.data.scandescription.axismode.AddMultiplyModeInt;
import de.ptb.epics.eve.data.scandescription.axismode.AdjustParameter;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.MotorAxisMother;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class AxisAddMultiplyIntTest {
	private static final Double DELTA = 0.00001;
	private Axis axis;
	private AddMultiplyModeInt axisMode;
	
	@Test(expected=IllegalStateException.class)
	public void testSetStartAdjustStart() {
		this.axisMode.setAdjustParameter(AdjustParameter.START);
		this.axis.setStart(10);
	}
	
	@Test
	public void testSetStopAdjustStart() {
		this.axisMode.setAdjustParameter(AdjustParameter.START);
		this.axis.setStop(20);
		assertEquals("set stop value should adjust start value",
				11, (int) this.axis.getStart());
	}
	
	@Test
	public void testSetStepwidthAdjustStart() {
		this.axisMode.setAdjustParameter(AdjustParameter.START);
		this.axis.setStepwidth(2);
		assertEquals("set stepwidth value should adjust start value",
				-8, (int) this.axis.getStart());
	}
	
	@Test
	public void testSetStepcountAdjustStart() {
		this.axisMode.setAdjustParameter(AdjustParameter.START);
		this.axis.setStepcount(5.0);
		assertEquals("set stepcount value should adjust start value",
				5, (int) this.axis.getStart());
	}
	
	@Test
	public void testSetStartAdjustStop() {
		this.axisMode.setAdjustParameter(AdjustParameter.STOP);
		this.axis.setStart(2);
		assertEquals("set start value should adjust stop value",
				11, (int) this.axis.getStop());
	}
	
	@Test(expected=IllegalStateException.class)
	public void testSetStopAdjustStop() {
		this.axisMode.setAdjustParameter(AdjustParameter.STOP);
		this.axis.setStop(42);
	}
	
	@Test
	public void testSetStepwidthAdjustStop() {
		this.axisMode.setAdjustParameter(AdjustParameter.STOP);
		this.axis.setStepwidth(2);
		assertEquals("set stepwidth value should adjust stop value",
				19, (int) this.axis.getStop());
	}
	
	@Test
	public void testSetStepcountAdjustStop() {
		this.axisMode.setAdjustParameter(AdjustParameter.STOP);
		this.axis.setStepcount(4.0);
		assertEquals("set stepcount value should adjust stop value",
				5, (int) this.axis.getStop());
	}
	
	@Test
	public void testSetStartAdjustStepwidth() {
		this.axisMode.setAdjustParameter(AdjustParameter.STEPWIDTH);
		this.axis.setStart(-8);
		assertEquals("set start value should adjust stepwidth value",
				2, (int) this.axis.getStepwidth());
	}
	
	@Test
	public void testSetStopAdjustStepwidth() {
		this.axisMode.setAdjustParameter(AdjustParameter.STEPWIDTH);
		this.axis.setStop(19);
		assertEquals("set stop value should adjust stepwidth value",
				2, (int) this.axis.getStepwidth());
	}
	
	@Test(expected=IllegalStateException.class)
	public void testSetStepwidthAdjustStepwidth() {
		this.axisMode.setAdjustParameter(AdjustParameter.STEPWIDTH);
		this.axis.setStepwidth(300);
	}
	
	@Test
	public void testSetStepcountAdjustStepwidth() {
		this.axisMode.setAdjustParameter(AdjustParameter.STEPWIDTH);
		this.axis.setStepcount(4.5);
		assertEquals("set stepcount value should adjust stepwidth value",
				2, (int) this.axis.getStepwidth());
	}
	
	@Test
	public void testSetStartAdjustStepcount() {
		this.axisMode.setAdjustParameter(AdjustParameter.STEPCOUNT);
		this.axis.setStart(5);
		assertEquals("set start value should adjust stepcount value",
				5.0, this.axis.getStepcount(), DELTA);
	}
	
	@Test
	public void testSetStopAdjustStepcount() {
		this.axisMode.setAdjustParameter(AdjustParameter.STEPCOUNT);
		this.axis.setStop(20);
		assertEquals("set stop value should adjust stepcount value",
				19.0, this.axis.getStepcount(), DELTA);
	}
	
	@Test
	public void testSetStepwidthAdjustStepcount() {
		this.axisMode.setAdjustParameter(AdjustParameter.STEPCOUNT);
		this.axis.setStepwidth(2);
		assertEquals("set stepwidth value should adjust stepcount value",
				4.5, this.axis.getStepcount(), DELTA);
	}
	
	@Test(expected=IllegalStateException.class)
	public void testSetStepcountAdjustStepcount() {
		this.axisMode.setAdjustParameter(AdjustParameter.STEPCOUNT);
		this.axis.setStepcount(42.0);
	}
	
	@Before
	public void beforeTest() {
		ScanModule scanModule = new ScanModule(1);
		this.axis = new Axis(scanModule);
		axis.setMotorAxis(MotorAxisMother.createNewIntTypeMotorAxis());
		this.axis.setStepfunction(Stepfunctions.ADD);
		
		this.axis.setStart(1);
		this.axis.setStop(10);
		this.axis.setStepwidth(1);
		
		this.axisMode = (AddMultiplyModeInt)this.axis.getMode();
	}
}

package de.ptb.epics.eve.data.scandescription.tests;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.Stepfunctions;
import de.ptb.epics.eve.data.scandescription.axismode.AddMultiplyModeDate;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.MotorAxisMother;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class AxisAddMultiplyDurationTest {
	private Axis axis;
	private AddMultiplyModeDate axisMode;
	
	@Test
	@Ignore
	public void testSetStopAdjustStart() {
		// TODO
	}
	
	@Test
	@Ignore
	public void testSetStepwidthAdjustStart() {
		// TODO
	}
	
	@Test
	@Ignore
	public void testSetStepcountAdjustStart() {
		// TODO
	}
	
	@Test
	@Ignore
	public void testSetStartAdjustStop() {
		// TODO
	}
	
	@Test
	@Ignore
	public void testSetStepwidthAdjustStop() {
		// TODO
	}
	
	@Test
	@Ignore
	public void testSetStepcountAdjustStop() {
		// TODO
	}
	
	@Test
	@Ignore
	public void testSetStartAdjustStepwidth() {
		// TODO
	}
	
	@Test
	@Ignore
	public void testSetStopAdjustStepwdith() {
		// TODO
	}
	
	@Test
	@Ignore
	public void testSetStepcountAdjustStepwidth() {
		// TODO
	}
	
	@Test
	@Ignore
	public void testSetStartAdjustStepcount() {
		// TODO
	}
	
	@Test
	@Ignore
	public void testSetStopAdjustStepcount() {
		// TODO
	}
	
	@Test
	@Ignore
	public void testSetStepwidthAdjustStepcount() {
		// TODO
	}
	
	@Before
	public void beforeTest() {
		ScanModule scanModule = new ScanModule(1);
		this.axis = new Axis(scanModule);
		this.axis.setMotorAxis(MotorAxisMother.createNewDateTypeMotorAxis());
		this.axis.setStepfunction(Stepfunctions.ADD);
	
		this.axisMode = (AddMultiplyModeDate)this.axis.getMode();
	}
}

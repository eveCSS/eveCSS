package de.ptb.epics.eve.data.scandescription.tests;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.Stepfunctions;
import de.ptb.epics.eve.data.scandescription.axismode.AddMultiplyModeDate;
import de.ptb.epics.eve.data.scandescription.axismode.AdjustParameter;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.MotorAxisMother;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class AxisAddMultiplyDurationTest {
	private Axis axis;
	private AddMultiplyModeDate axisMode;
	
	@Test(expected=IllegalStateException.class)
	public void testSetStartAdjustStart() throws DatatypeConfigurationException {
		this.axisMode.setAdjustParameter(AdjustParameter.START);
		this.axis.setStart(DatatypeFactory.newInstance().newDuration(0));
	}
	
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
	
	@Test(expected=IllegalStateException.class)
	public void testSetStopAdjustStop() throws DatatypeConfigurationException {
		this.axisMode.setAdjustParameter(AdjustParameter.STOP);
		this.axis.setStop(DatatypeFactory.newInstance().newDuration(0));
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
	
	@Test(expected=IllegalStateException.class)
	public void testSetStepwidthAdjustStepwidth() throws DatatypeConfigurationException {
		this.axisMode.setAdjustParameter(AdjustParameter.STEPWIDTH);
		this.axis.setStepwidth(DatatypeFactory.newInstance().newDuration(0));
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
	
	@Test(expected=IllegalStateException.class)
	public void testSetStepcountAdjustStepcount() {
		this.axisMode.setAdjustParameter(AdjustParameter.STEPCOUNT);
		this.axis.setStepcount(0);
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

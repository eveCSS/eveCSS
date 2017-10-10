package de.ptb.epics.eve.data.measuringstation.tests;

import java.util.ArrayList;
import java.util.List;


import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.MotorAxisMother;

/**
 * @author Marcus Michalsky
 * @since 1.26
 */
public class MotorAxisTest {
	private List<MotorAxis> motorAxes;
	
	@Test
	public void testClone() {
		for (MotorAxis motorAxis : motorAxes) {
			MotorAxis clone = (MotorAxis) motorAxis.clone();
			
			assertEquals(motorAxis.getPosition(), clone.getPosition());
			assertEquals(motorAxis.getStatus(), clone.getStatus());
			assertEquals(motorAxis.getMoveDone(), clone.getMoveDone());
			assertEquals(motorAxis.getGoto(), clone.getGoto());
			assertEquals(motorAxis.getStop(), clone.getStop());
			assertEquals(motorAxis.getDeadband(), clone.getDeadband());
			assertEquals(motorAxis.getOffset(), clone.getOffset());
			assertEquals(motorAxis.getTweakValue(), clone.getTweakValue());
			assertEquals(motorAxis.getTweakForward(), clone.getTweakForward());
			assertEquals(motorAxis.getTweakReverse(), clone.getTweakReverse());
			assertEquals(motorAxis.getSoftHighLimit(), clone.getSoftHighLimit());
			assertEquals(motorAxis.getSoftLowLimit(), clone.getSoftLowLimit());
			assertEquals(motorAxis.getLimitViolation(), clone.getLimitViolation());
		}
	}
	
	/**
	 * Class wide setup method of the test
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
	}

	/**
	 * Class wide tear down method.
	 */
	@AfterClass
	public static void tearDownAfterClass() {
	}

	/**
	 * Test wide set up method.
	 */
	@Before
	public void setUp() {
		this.motorAxes = new ArrayList<>();
		this.motorAxes.add(MotorAxisMother.createNewMotorAxis());
		this.motorAxes.add(MotorAxisMother.addOption(
				MotorAxisMother.createNewMotorAxis()));
	}

	/**
	 * Test wide tear down method.
	 */
	@After
	public void tearDown() {
	}
}
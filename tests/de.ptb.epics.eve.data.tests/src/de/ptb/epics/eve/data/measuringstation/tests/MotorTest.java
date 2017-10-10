package de.ptb.epics.eve.data.measuringstation.tests;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.MotorMother;

/**
 * @author Marcus Michalsky
 * @since 1.26
 */
public class MotorTest {
	private List<Motor> motors;
	
	@Test
	public void testClone() {
		for (Motor motor : motors) {
			Motor clone = (Motor)motor.clone();
			
			assertEquals(motor.getID(), clone.getID());
			assertEquals(motor.getName(), clone.getName());
			assertEquals(motor.getClassName(), clone.getClassName());
			assertEquals(motor.getAxes(), clone.getAxes());
			assertEquals(motor.getOptions(), clone.getOptions());
		}
	}
	
	/**
	 * Class wide setup method of the test.
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
		this.motors = new ArrayList<>();
		this.motors.add(MotorMother.createNewMotor());
		this.motors.add(MotorMother.addOption(MotorMother.createNewMotor()));
		this.motors.add(MotorMother.addMotorAxis(MotorMother.createNewMotor()));
	}

	/**
	 * Test wide tear down method.
	 */
	@After
	public void tearDown() {
	}
}
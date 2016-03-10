package de.ptb.epics.eve.data.tests.mothers.measuringstation;

import java.util.Calendar;

import de.ptb.epics.eve.data.measuringstation.Motor;

/**
 * Fabricates Motor test fixtures and tailors them.
 * 
 * @author Marcus Michalsky
 * @since 1.26
 */
public class MotorMother {
	
	/**
	 * 
	 * @return
	 */
	public static Motor createNewMotor() {
		Motor motor = new Motor();
		String name = "Motor-" + Calendar.getInstance().getTime().getTime();
		motor.setId(name);
		motor.setName(name);
		motor.setClassName("MotorClass");
		
		return motor;
	}
	
	/**
	 * Adds a motor axis to the given motor.
	 * 
	 * @param motor the motor the motor axis should be added to
	 * @return the given motor with a new motor axis
	 */
	public static Motor addMotorAxis(Motor motor) {
		motor.add(MotorAxisMother.createNewMotorAxis());
		return motor;
	}
	
	/**
	 * Adds an option to the given motor.
	 * 
	 * @param motor the motor the option should be added to
	 * @return the given motor with a new option
	 */
	public static Motor addOption(Motor motor) {
		motor.add(OptionMother.createNewOption());
		return motor;
	}
}
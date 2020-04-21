package de.ptb.epics.eve.data.tests.mothers.measuringstation;

import java.util.Calendar;

import de.ptb.epics.eve.data.measuringstation.MotorAxis;

/**
 * Fabricates MotorAxis test fixtures and tailors them.
 * 
 * @author Marcus Michalsky
 * @since 1.26
 */
public class MotorAxisMother {
	
	public static MotorAxis createNewMotorAxis() {
		return MotorAxisMother.createNewIntTypeMotorAxis();
	}
	
	/**
	 * @since 1.34
	 */
	public static MotorAxis createNewIntTypeMotorAxis() {
		MotorAxis motorAxis = new MotorAxis();
		
		String name = "MotorAxis-" + Calendar.getInstance().getTime().getTime();
		motorAxis.setId(name);
		motorAxis.setName(name);
		motorAxis.setClassName("MotorAxisClass");
		
		motorAxis.setPosition(FunctionMother.createNewIntTypeFunction());
		motorAxis.setStatus(FunctionMother.createNewFunction());
		motorAxis.setMoveDone(FunctionMother.createNewFunction());
		motorAxis.setGoto(FunctionMother.createNewIntTypeFunction());
		motorAxis.setStop(FunctionMother.createNewFunction());
		motorAxis.setDeadband(FunctionMother.createNewFunction());
		motorAxis.setOffset(FunctionMother.createNewFunction());
		motorAxis.setTweakValue(FunctionMother.createNewFunction());
		motorAxis.setTweakForward(FunctionMother.createNewFunction());
		motorAxis.setTweakReverse(FunctionMother.createNewFunction());
		motorAxis.setSoftHighLimit(FunctionMother.createNewFunction());
		motorAxis.setSoftLowLimit(FunctionMother.createNewFunction());
		motorAxis.setLimitViolation(FunctionMother.createNewFunction());
		
		return motorAxis;
	}
	
	/**
	 * @since 1.34
	 */
	public static MotorAxis createNewDoubleTypeMotorAxis() {
		MotorAxis motorAxis = new MotorAxis();
		
		String name = "MotorAxis-" + Calendar.getInstance().getTime().getTime();
		motorAxis.setId(name);
		motorAxis.setName(name);
		motorAxis.setClassName("MotorAxisClass");
		
		motorAxis.setPosition(FunctionMother.createNewDoubleTypeFunction());
		motorAxis.setStatus(FunctionMother.createNewFunction());
		motorAxis.setMoveDone(FunctionMother.createNewFunction());
		motorAxis.setGoto(FunctionMother.createNewDoubleTypeFunction());
		motorAxis.setStop(FunctionMother.createNewFunction());
		motorAxis.setDeadband(FunctionMother.createNewFunction());
		motorAxis.setOffset(FunctionMother.createNewFunction());
		motorAxis.setTweakValue(FunctionMother.createNewFunction());
		motorAxis.setTweakForward(FunctionMother.createNewFunction());
		motorAxis.setTweakReverse(FunctionMother.createNewFunction());
		motorAxis.setSoftHighLimit(FunctionMother.createNewFunction());
		motorAxis.setSoftLowLimit(FunctionMother.createNewFunction());
		motorAxis.setLimitViolation(FunctionMother.createNewFunction());
		
		return motorAxis;
	}
	
	/**
	 * Adds an option to the given motor axis.
	 * 
	 * @param motorAxis the motorAxis the option should be added to
	 * @return the given motor axis with a new option
	 */
	public static MotorAxis addOption(MotorAxis motorAxis) {
		motorAxis.add(OptionMother.createNewOption());
		return motorAxis;
	}
	
	private MotorAxisMother() {
		// private
	}
}
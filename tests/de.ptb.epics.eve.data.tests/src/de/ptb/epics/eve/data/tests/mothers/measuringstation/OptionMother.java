package de.ptb.epics.eve.data.tests.mothers.measuringstation;

import java.util.Calendar;

import de.ptb.epics.eve.data.AutoAcquireTypes;
import de.ptb.epics.eve.data.measuringstation.Option;

/**
 * Fabricates Option test fixtures and tailors them.
 * 
 * @author Marcus Michalsky
 * @since 1.26
 */
public class OptionMother {

	/**
	 * 
	 * @return
	 */
	public static Option createNewOption() {
		return OptionMother.createNewOption(false);
	}
	
	/**
	 * 
	 * @param monitor
	 * @return
	 * @since 1.30.5
	 */
	public static Option createNewOption(boolean monitor) {
		Option option = new Option();
		String name = "Option-" + Calendar.getInstance().getTime().getTime();
		option.setId(name);
		option.setName(name);
		option.setClassName("OptionClass");
		option.setMonitor(monitor);
		option.setValue(FunctionMother.createNewDoubleTypeFunction());
		return option;
	}
	
	/**
	 * @since 1.36
	 */
	public static Option createNewIntegerOption() {
		Option option = new Option();
		String name = "Option-" + Calendar.getInstance().getTime().getTime();
		option.setId(name);
		option.setName(name);
		option.setClassName("OptionClass");
		option.setMonitor(false);
		option.setValue(FunctionMother.createNewIntTypeFunction());
		return option;
	}
	
	/**
	 * @since 1.36
	 */
	public static Option createNewDoubleOption() {
		Option option = new Option();
		String name = "Option-" + Calendar.getInstance().getTime().getTime();
		option.setId(name);
		option.setName(name);
		option.setClassName("OptionClass");
		option.setMonitor(false);
		option.setValue(FunctionMother.createNewDoubleTypeFunction());
		return option;
	}
	
	/**
	 * @since 1.36
	 */
	public static Option createNewStringOption() {
		Option option = new Option();
		String name = "Option-" + Calendar.getInstance().getTime().getTime();
		option.setId(name);
		option.setName(name);
		option.setClassName("OptionClass");
		option.setMonitor(false);
		option.setValue(FunctionMother.createNewStringTypeFunction());
		return option;
	}
	
	/**
	 * @since 1.36
	 */
	public static Option createNewDiscreteStringOption() {
		Option option = new Option();
		String name = "Option-" + Calendar.getInstance().getTime().getTime();
		option.setId(name);
		option.setName(name);
		option.setClassName("OptionClass");
		option.setMonitor(false);
		option.setValue(FunctionMother.createNewDiscreteStringTypeFunction());
		return option;
	}
	
	/**
	 * @since 1.37
	 */
	public static Option createNewSnapshotAcquireOption() {
		Option option = new Option(AutoAcquireTypes.SNAPSHOT);
		String name = "Option-" + Calendar.getInstance().getTime().getTime();
		option.setId(name);
		option.setName(name);
		option.setClassName("OptionClass");
		option.setMonitor(false);
		option.setValue(FunctionMother.createNewDiscreteStringTypeFunction());
		return option;
	}
	
	/**
	 * 
	 * @since 1.37
	 */
	public static Option createNewMeasurementAcquireOption() {
		Option option = new Option(AutoAcquireTypes.MEASUREMENT);
		String name = "Option-" + Calendar.getInstance().getTime().getTime();
		option.setId(name);
		option.setName(name);
		option.setClassName("OptionClass");
		option.setMonitor(false);
		option.setValue(FunctionMother.createNewDiscreteStringTypeFunction());
		return option;
	}
}
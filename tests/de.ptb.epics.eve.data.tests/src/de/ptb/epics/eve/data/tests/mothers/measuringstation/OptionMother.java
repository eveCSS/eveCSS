package de.ptb.epics.eve.data.tests.mothers.measuringstation;

import java.util.Calendar;

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
		return option;
	}
}
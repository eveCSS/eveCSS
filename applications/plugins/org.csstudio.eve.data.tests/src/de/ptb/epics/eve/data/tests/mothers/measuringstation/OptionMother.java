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
		Option option = new Option();
		String name = "Option-" + Calendar.getInstance().getTime().getTime();
		option.setId(name);
		option.setName(name);
		option.setClassName("OptionClass");
		
		return option;
	}
}
package de.ptb.epics.eve.data.scandescription;

import javax.xml.bind.annotation.XmlEnumValue;

/**
 * <code>MonitorOption</code> represents the available types of monitor options in 
 * the scan.
 * 
 * @author Hartmut Scherr
 */
public enum MonitorOption {
	
	/**
	 * All Options of the available devices will be monitored. 
	 */
	@XmlEnumValue("all")
	ALL,
	
	/**
	 * All Options of the devices which are involved in the scan.
	 */
	@XmlEnumValue("involved")
	INVOLVED,

	/**
	 * No Options are monitored.
	 */
	@XmlEnumValue("none")
	NONE,

	/**
	 * All Options of the measurement.xml File will be monitored.
	 */
	@XmlEnumValue("measuringstation")
	MEASURINGSTATION;

	/**
	 * Converts a <code>MonitorOption</code> to a {@link java.lang.String}.
	 * 
	 * @param monitorOption the monitor Option that should be converted
	 * @return the <code>String</code> corresponding to the monitor Option
	 */
	public static String typeToString(final MonitorOption monitorOption) {
		switch(monitorOption) {
			case ALL:
				return "all";
			case INVOLVED:
				return "involved";
			case NONE:
				return "none";
			case MEASURINGSTATION:
				return "measuringstation";
		}
		return null;
	}
	
	/**
	 * Converts a {@link java.lang.String} to its corresponding 
	 * {@link de.ptb.epics.eve.data.scandescription.MonitorOption}
	 * 
	 * @param name the {@link java.lang.String} that should be converted
	 * @return The corresponding
	 * 		   {@link de.ptb.epics.eve.data.scandescription.MonitorOption}
	 * @throws IllegalArgumentException if the argument is <code>null</code> 
	 */
	public static MonitorOption stringToType(final String name) {
		if(name == null) {
			throw new IllegalArgumentException(
					"The parameter 'name' must not be null!");
		}
		
		if( name.equals("all")) {
			return MonitorOption.ALL;
		}
		if( name.equals("involved")) {
			return MonitorOption.INVOLVED;
		}
		if( name.equals("none")) {
			return MonitorOption.NONE;
		}
		if( name.equals("measuringstation")) {
			return MonitorOption.MEASURINGSTATION;
		}
		return null;
	}
	
	/**
	 * Returns all available monitor options.
	 * 
	 * @return all available monitor options
	 */
	public static String[] getPossibleMonitorOptions() {
		final String[] values = {"all", "involved", "none", "measuringstation"}; 
		return values;
	}
}
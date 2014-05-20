package de.ptb.epics.eve.data.scandescription;

/**
 * Defines which options are monitored in a scan description. 
 * 
 * @author Hartmut Scherr
 * @author Marcus Michalsky
 */
public enum MonitorOption {
	
	/**
	 * All Options of the devices which are used in the scan.
	 * with the setting monitor="true"
	 */
	USED_IN_SCAN {
		@Override
		public String toString() {
			return "used in scan";
		}},

	/**
	 * No Options are monitored.
	 */
	NONE {
		@Override
		public String toString() {
			return "none";
		}},

	/**
	 * The Options are editable.
	 */
	CUSTOM {
		@Override
		public String toString() {
			return "custom";
		}
	},

	/**
	 * All Options of the measurement.xml File will be monitored
	 * with the setting monitor="true"
	 */
	AS_IN_DEVICE_DEFINITION {
		@Override
		public String toString() {
			return "as in device definition";
		}
	};
	
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
		
		if( name.equals("as in device definition")) {
			return MonitorOption.AS_IN_DEVICE_DEFINITION;
		}
		if( name.equals("custom")) {
			return MonitorOption.CUSTOM;
		}
		if( name.equals("none")) {
			return MonitorOption.NONE;
		}
		if( name.equals("used in scan")) {
			return MonitorOption.USED_IN_SCAN;
		}
		return null;
	}
}
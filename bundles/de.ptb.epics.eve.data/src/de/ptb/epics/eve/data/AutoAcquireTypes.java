package de.ptb.epics.eve.data;

/**
 * Defines whether and how options should be acquired automatically when their 
 * corresponding device is used. Consideration is distinguished between
 * snapshots and measurements.
 * 
 * @author Marcus Michalsky
 * @since 1.37
 */
public enum AutoAcquireTypes {
	
	/**
	 * Option should be considered only during snapshots
	 */
	SNAPSHOT {
		@Override
		public String toString() {
			return "snapshot";
		}
	},
	
	/**
	 * Option should be considered during each measurement
	 */
	MEASUREMENT {
		@Override
		public String toString() {
			return "measurement";
		}
	},
	
	/**
	 * Option should not be considered
	 */
	NO {
		@Override
		public String toString() {
			return "no";
		}
	};
	
	/**
	 * @param text the value (pass <code>null</code> to get the default value)
	 * @return the enum
	 */
	public static AutoAcquireTypes getEnum(String text) {
		if (text == null) {
			return AutoAcquireTypes.NO;
		}
		for (AutoAcquireTypes aat : AutoAcquireTypes.values()) {
			if (aat.toString().equals(text)) {
				return aat;
			}
		}
		return null;
	}
}

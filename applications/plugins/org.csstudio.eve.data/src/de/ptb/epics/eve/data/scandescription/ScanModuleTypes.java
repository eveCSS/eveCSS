package de.ptb.epics.eve.data.scandescription;

/**
 * @author Marcus Michalsky
 * @since 1.8
 */
public enum ScanModuleTypes {

	/** a classic scan module */
	CLASSIC {
		@Override
		public String toString() {
			return "classic";
		}
	},

	/** scan module to save all axis positions */
	SAVE_AXIS_POSITIONS {
		@Override
		public String toString() {
			return "save_axis_positions";
		}
	},

	/** scan module to save all channel values */
	SAVE_CHANNEL_VALUES {
		@Override
		public String toString() {
			return "save_channel_values";
		}
	};
	
	/**
	 * @param value the value
	 * @return the enum
	 */
	public static ScanModuleTypes getEnum(String value) {
		if (value.equals("classic")) {
			return ScanModuleTypes.CLASSIC;
		} else if (value.equals("save_axis_positions")) {
			return ScanModuleTypes.SAVE_AXIS_POSITIONS;
		} else if (value.equals("save_channel_values")) {
			return ScanModuleTypes.SAVE_CHANNEL_VALUES;
		}
		return null;
	}
}
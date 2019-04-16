package de.ptb.epics.eve.data.scandescription;

/**
 * @author Marcus Michalsky
 * @since 1.8
 */
public enum ScanModuleTypes {

	/**
	 * classic scan module
	 **/
	CLASSIC {
		@Override
		public String toString() {
			return "classic";
		}
	},

	/** 
	 * scan module to save axis positions 
	 **/
	SAVE_AXIS_POSITIONS {
		@Override
		public String toString() {
			return "save_axis_positions";
		}
	},

	/** 
	 * scan module to save channel values 
	 **/
	SAVE_CHANNEL_VALUES {
		@Override
		public String toString() {
			return "save_channel_values";
		}
	},
	
	/** 
	 * top up aware scan module 
	 * 
	 * @since 1.20
	 */
	TOP_UP {
		@Override
		public String toString() {
			return "top_up";
		}
	},
	
	/**
	 * scan module to save all axis positions of axes in the current device 
	 * definition (determined by the engine).
	 * 
	 * @since 1.31
	 */
	DYNAMIC_AXIS_POSITIONS {
		@Override
		public String toString() {
			return "dynamic_axis_positions";
		}
	},
	
	/**
	 * scan module to save all channel values of channels in the current device 
	 * definition (determined by the engine).
	 * 
	 * @since 1.31
	 */
	DYNAMIC_CHANNEL_VALUES {
		@Override
		public String toString() {
			return "dynamic_channel_values";
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
		} else if (value.equals("top_up")) {
			return ScanModuleTypes.TOP_UP;
		} else if (value.equals("dynamic_axis_positions")) {
			return ScanModuleTypes.DYNAMIC_AXIS_POSITIONS;
		} else if (value.equals("dynamic_channel_values")) {
			return ScanModuleTypes.DYNAMIC_CHANNEL_VALUES;
		}
		return null;
	}
}
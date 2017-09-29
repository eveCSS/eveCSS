package de.ptb.epics.eve.data.scandescription.channelmode;

/**
 * @author Marcus Michalsky
 * @since 1.27
 */
public enum ChannelModes {
	
	/** */
	STANDARD {
		@Override
		public String toString() {
			return "Standard";
		}
	},
	
	/** */
	INTERVAL {
		@Override
		public String toString() {
			return "Interval";
		}
	};
	
	/**
	 * 
	 * @param value
	 * @return
	 * @throws IllegalArgumentException if there is no mode that matches the given string
	 */
	public static ChannelModes getEnum(String value) {
		switch (value) {
		case "Standard":
			return ChannelModes.STANDARD;
		case "Interval":
			return ChannelModes.INTERVAL;
		}
		throw new IllegalArgumentException("no match");
	}
}
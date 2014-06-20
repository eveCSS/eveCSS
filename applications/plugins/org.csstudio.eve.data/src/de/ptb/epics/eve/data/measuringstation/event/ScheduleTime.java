package de.ptb.epics.eve.data.measuringstation.event;

/**
 * @author Marcus Michalsky
 * @since 1.19
 */
public enum ScheduleTime {
	
	/**
	 * The ScheduleEvent is triggered at the start of the scan module
	 */
	START {
		@Override 
		public String toString() {
			return "Start";
		}
	},
	
	/**
	 * The Schedule Event is triggered at the end of the scan module
	 */
	END {
		@Override 
		public String toString() {
			return "End";
		}
	};
	
	public static ScheduleTime stringToEnum(String s) {
		switch (s) {
		case "Start":
			return ScheduleTime.START;
		case "End":
			return ScheduleTime.END;
		}	
		return ScheduleTime.END;
	}
}
package de.ptb.epics.eve.data.measuringstation.event;

import javax.xml.bind.annotation.XmlEnumValue;

/**
 * @author Marcus Michalsky
 * @since 1.19
 */
public enum ScheduleTime {
	
	/**
	 * The ScheduleEvent is triggered at the start of the scan module
	 */
	@XmlEnumValue(value = "Start")
	START("Start") {
		@Override 
		public String toString() {
			return "Start";
		}
	},
	
	/**
	 * The Schedule Event is triggered at the end of the scan module
	 */
	@XmlEnumValue(value = "End")
	END("End") {
		@Override 
		public String toString() {
			return "End";
		}
	};
	
	private String value;
	
	private ScheduleTime(String s) {
		this.value = s;
	}
	
	/**
	 * Returns the String representation (used in XML)
	 * @return the String representation (used in XML)
	 * @since 1.23
	 */
	public String getXmlValue() {
		return this.value;
	}
	
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
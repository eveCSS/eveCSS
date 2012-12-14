package de.ptb.epics.eve.data.scandescription;

import javax.xml.bind.annotation.XmlEnumValue;

/**
 * @author Marcus Michalsky
 * @since 1.7
 */
public enum Stepfunctions {

	/**
	 * adds a value to the current position until the end
	 * value has been reached.
	 */
	@XmlEnumValue("Add")
	ADD {@Override public String toString() {return "Add";}},

	/**
	 * multiplies the current position with a given value
	 * until the end position has been reached.
	 */
	@XmlEnumValue("Multiply")
	MULTIPLY {@Override public String toString() {return "Multiply";}},

	/**
	 * reads positions from a file.
	 */
	@XmlEnumValue("File")
	FILE {@Override public String toString() {return "File";}},

	/**
	 * axis movement is controlled by a plug in.
	 */
	@XmlEnumValue("Plugin")
	PLUGIN {@Override public String toString() {return "Plugin";}},

	/**
	 * axis positions are defined as a list of positions.
	 */
	@XmlEnumValue("Positionlist")
	POSITIONLIST {@Override public String toString() {return "Positionlist";}};
	
	/**
	 * @param value the value
	 * @return the enum
	 */
	public static Stepfunctions getEnum(String value) {
		if (value.equals("Add")) {
			return Stepfunctions.ADD;
		} else if (value.equals("Multiply")) {
			return Stepfunctions.MULTIPLY;
		} else if (value.equals("File")) {
			return Stepfunctions.FILE;
		} else if (value.equals("Plugin")) {
			return Stepfunctions.PLUGIN;
		} else if (value.equals("Positionlist")) {
			return Stepfunctions.POSITIONLIST;
		}
		throw new IllegalArgumentException("no match");
	}
}
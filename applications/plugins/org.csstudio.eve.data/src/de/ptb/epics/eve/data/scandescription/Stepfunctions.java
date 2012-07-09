package de.ptb.epics.eve.data.scandescription;

/**
 * The step function that is used to move the axis.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (-at-) ptb.de>
 */
public enum Stepfunctions {

	/**
	 * This step function adds a value to the current position until the end
	 * value has been reached.
	 */
	ADD,

	/**
	 * This step function multiplies the current position with a given value
	 * until the end position has been reached..
	 */
	MULTIPLY,

	/**
	 * This step function reads the positions from a file.
	 */
	FILE,

	/**
	 * This step function indicates that the axis is controlled by a plug in.
	 */
	PLUGIN,

	/**
	 * This step function indicates that the axis is controlled by a list of
	 * values.
	 */
	POSITIONLIST;

	/**
	 * This method converts a string to a value of this enum.
	 * 
	 * The strings are the values of the enum, beginning with upper case and
	 * continues with lower case letters.
	 * 
	 * @param stepfunction
	 *            The string of the step function. Must not be null!
	 * @return The corresponding enum value. The value ADD is returned it the
	 *         string is unknown.
	 */
	public static Stepfunctions stepfunctionToEnum(final String stepfunction) {
		if (stepfunction == null) {
			throw new IllegalArgumentException(
					"The parameter 'stepfunction' must not be null!");
		}
		if (stepfunction.equals("File"))
			return Stepfunctions.FILE;
		else if (stepfunction.equals("Multiply"))
			return Stepfunctions.MULTIPLY;
		else if (stepfunction.equals("Plugin"))
			return Stepfunctions.PLUGIN;
		else if (stepfunction.equals("Positionlist"))
			return Stepfunctions.POSITIONLIST;
		else
			return Stepfunctions.ADD;
	}

	/**
	 * This method converts a value of the step function enum to a string,
	 * 
	 * @param stepenum
	 *            The value of the step function enum.
	 * @return The string.
	 */
	public static String stepfunctionToString(final Stepfunctions stepenum) {
		switch (stepenum) {
		case FILE:
			return "File";
		case MULTIPLY:
			return "Multiply";
		case PLUGIN:
			return "Plugin";
		case POSITIONLIST:
			return "Positionlist";
		default:
			return "Add";
		}
	}
}
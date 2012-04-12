package de.ptb.epics.eve.data;

/**
 * The event types used in the Application.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @see de.ptb.epics.eve.data.measuringstation.Event
 */
public enum EventTypes {

	/**
	 * Used for monitor events. These are events which are defined in the scan
	 * description and represents a state at the measuring station.
	 */
	MONITOR,

	/**
	 * Used for schedule events. These are events which are defined in a
	 * relation to a Scan Module and occur when the Scan Module has finished.
	 */
	SCHEDULE,

	/**
	 * Used for detector ready event, which may be sent if a detector is ready
	 * to take data.
	 */
	DETECTOR;

	/**
	 * Converts a <code>String</code> of a method type (<code>MethodTypes</code>
	 * ) into its corresponding event type (<code>EventTypes</code>).
	 * 
	 * @param name
	 *            the <code>String</code that should be converted<br>
	 *            <b>Precondition:<b> name is element of {"monitor", "schedule",
	 *            "detector"}
	 * @return the corresponding event type
	 * @throws IllegalArgumentException
	 *             if the argument is <code>null</code>
	 */
	public static EventTypes stringToType(final String name) {
		if (name == null) {
			throw new IllegalArgumentException(
					"The parameter 'name' must not be null!");
		}
		if (name.equals("monitor")) {
			return EventTypes.MONITOR;
		} else if (name.equals("schedule")) {
			return EventTypes.SCHEDULE;
		} else if (name.equals("detector")) {
			return EventTypes.DETECTOR;
		}
		return null;
	}

	/**
	 * Converts an event type (<code>EventTypes</code>) into a
	 * <code>String</code>,
	 * 
	 * @param type
	 *            the type, that should be converted.
	 * @return the corresponding <code>String</code>.
	 * @throws IllegalArgumentException
	 *             if the argument is <code>null</code>
	 */
	public static String typeToString(final EventTypes type) {
		if (type == null) {
			throw new IllegalArgumentException(
					"The parameter 'type' must not be null!");
		}
		switch (type) {
		case MONITOR:
			return "monitor";
		case SCHEDULE:
			return "schedule";
		case DETECTOR:
			return "detector";
		}
		return null;
	}
}
package de.ptb.epics.eve.data.scandescription.updater.patches.patch8o0t9o0;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
enum EventAction {
	ON, 
	OFF, 
	ONOFF;
	
	public static EventAction stringToType(String s) {
		switch (s) {
		case "ON": return EventAction.ON;
		case "OFF": return EventAction.OFF;
		case "ONOFF": return EventAction.ONOFF;
		default: return null;
		}
	}
}

package de.ptb.epics.eve.data;

/**
 * <code>EventImpacts</code> describes the Consequences of an event.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public enum EventImpacts {
	/** Repeats the current scan point, when redo event occurs. */
	REDO,
	
	/** Ends the current scan module and continues with the next. */
	BREAK, 
	
	/** Stops the scan. */
	STOP, 
	
	/** Wait for trigger event before moving to next position. */
	TRIGGER
}
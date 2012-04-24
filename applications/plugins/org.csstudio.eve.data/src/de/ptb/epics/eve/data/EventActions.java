package de.ptb.epics.eve.data;

/**
 * <code>EventActions</code> defines whether an event enables or disables the 
 * pause of a scan if the corresponding event triggers.
 * 
 * @author Marcus Michalsky
 * @since 1.2
 */
public enum EventActions {
	
	/**
	 * the corresponding event enables pause
	 */
	ON,
	
	/**
	 * the corresponding event disables pause
	 */
	OFF,
	
	/**
	 * the corresponding event both enables and disables pause
	 */
	ONOFF
}
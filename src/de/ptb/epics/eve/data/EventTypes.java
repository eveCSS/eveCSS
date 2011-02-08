/*
 * Copyright (c) 2001, 2007 Physikalisch-Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 */
package de.ptb.epics.eve.data;

/**
 * the event types existing in the Application. 
 * 
 * @author   Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version   1.3
 * @see  de.ptb.epics.eve.data.measuringstation.Event
 */
public enum EventTypes {

	/**
	 * Used for monitor events, this are events which are defined in the scan
	 * description and represents a state at the measuring station.
	 *
	 * @uml.property  name="mONITOR"
	 * @uml.associationEnd  
	 */
	MONITOR,
	
	/**
	 * Used for schedule events, this are events which are defined in a
	 * relation to a Scan Module and occur when the Scan Module has finished.
	 *
	 * @uml.property  name="sCHEDULE"
	 * @uml.associationEnd  
	 */
	SCHEDULE,
	
	/**
	 * Used for detector ready event, which may be sent if a detector is ready 
	 * to take data.
	 * 
	 * @uml.property  name="dETECTOR"
	 * @uml.associationEnd  
	 */
	DETECTOR;
	
	/**
	 * translates a String of a MethodType, into its corresponding
	 * enum value.
	 * 
	 * @param name one of {"monitor", "schedule", "detector"}
	 * @return the corresponding EventType
	 * @exception IllegalArgumentException if name == 'null'
	 */
	public static EventTypes stringToType(final String name) {
		if(name == null) {
			throw new IllegalArgumentException(
					"The parameter 'name' must not be null!");
		}
		
		if( name.equals( "monitor" ) ) {
			return EventTypes.MONITOR;
		} else if( name.equals( "schedule" ) ) {
			return EventTypes.SCHEDULE;
		} else if( name.equals( "detector" ) ) {
			return EventTypes.DETECTOR;
		}
		return null;
	}
	
	/**
	 * translates an EventType into a String, 
	 * 
	 * @param type the type, that should be translated.
	 * @return the corresponding string.
	 * @exception IllegalArgumentException if type == 'null' 
	 */
	public static String typeToString(final EventTypes type) {
		if(type == null) {
			throw new IllegalArgumentException(
					"The parameter 'type' must not be null!");
		}
		
		switch(type) {
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
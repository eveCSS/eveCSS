/*
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 */
package de.ptb.epics.eve.data;


/**
 * This enum represents differen Tranfport layer systems for accessing a variable.
 * @author   Stephan Rehfeld  <stephan.rehfeld( -at -) ptb.de>
 */
public enum TransportTypes {
	
	/**
	 * Channel Access
	 *
	 * @uml.property  name="cA"
	 * @uml.associationEnd  
	 */
	CA,
	
	/**
	 * A local variable
	 *
	 * @uml.property  name="lOCAL"
	 * @uml.associationEnd  
	 */
	LOCAL;
	
	/**
	 * converts a string into a value of type TransportType (enum).
	 * 
	 * @param name the string that should be converted.
	 * @return The corrosponding TransportTypes value.
	 * @exception IllegalArgumentException if name == 'null'
	 */
	public static TransportTypes stringToType(final String name) {
		if(name == null) {
			throw new IllegalArgumentException(
					"The parameter 'name' must not be null!");
		}
		if( name.equals( "ca" ) ) {
			return TransportTypes.CA;
		} else if( name.equals( "local" ) ) {
			return TransportTypes.LOCAL;
		}
		return null;
	}
	
	/**
	 * converts a value of type TransportTypes (enum) into a String
	 *
	 * @param type the value that should be converted.
	 * @return the corresponding value as a String.
	 */
	public static String typeToString(final TransportTypes type) {
		if(type == null) {
			throw new IllegalArgumentException(
					"The parameter 'type' must not be null");
		}	
		switch( type ) {
			case CA:
				return "ca";
			case LOCAL:
				return "local";
		}
		return null;
	}
}
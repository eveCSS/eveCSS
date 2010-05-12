/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data;


/**
 * This enum represents differen Tranfport layer systems for accessing a variable.
 * 
 * @author Stephan Rehfeld  <stephan.rehfeld( -at -) ptb.de>
 *
 */
public enum TransportTypes {
	
	/**
	 * Channel Access
	 */
	CA,
	
	/**
	 * A local variable
	 */
	LOCAL;
	
	/**
	 * This static method converts a string like it's used in the measuring station description to a value in the
	 * TransportType Enum.
	 * 
	 * @param name The string that should be converted. Must not be null.
	 * @return The corrosponding TransportTypes value.
	 */
	public static TransportTypes stringToType( final String name ) {
		if( name == null ) {
			throw new IllegalArgumentException( "The parameter 'name' must not be null!" );
		}
		
		if( name.equals( "ca" ) ) {
			return TransportTypes.CA;
		} else if( name.equals( "local" ) ) {
			return TransportTypes.LOCAL;
		}
		return null;
	}
	/**
	 * This static method converts a value of the TransportTypes enum to a String like it's used in the measuring
	 * station description. 
	 *
	 * @param type The value that should be converted.
	 * @return The corresponding value as a String.
	 */
	public static String typeToString( final TransportTypes type ) {
		if( type == null ) {
			throw new IllegalArgumentException( "The parameter 'type' must not be null" );
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

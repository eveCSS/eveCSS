/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data;

/**
 * 
 * This enum defines the possible methods of a access description.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.3
 * 
 * @see de.ptb.epics.eve.data.measuringstation.Access
 *
 */
public enum MethodTypes {
	
	/**
	 * Indicates that a value is be putted in this access.
	 */
	PUT,
	
	/**
	 * Indicates that a value is be putted in this access and
	 * that a callback will occure after the value hat been putted.
	 */
	PUTCB,
	
	/**
	 * Indicates that a value is be read in this access.
	 */
	GET,
	
	/**
	 * Indicates that a value is be read in this access and
	 * that a callback will occure after the value hat been read.
	 */
	GETCB,
	
	/**
	 * Indicates that a value ins putted an read in this access and
	 * that a callback will ocure after the process. 
	 */
	GETPUTCB,
	
	/**
	 * Indicates that the access if for noticing changes.
	 */
	MONITOR;
	
	/**
	 * This static Method is translating a name for the method type like it's used in the
	 * measuring station description or the scan description into the correpondenting MethodType.
	 * Possible values are: PUT, PUTCB, GET, GETCB, GETPUTCB and monitor
	 * 
	 * @param name The String that should be translated. Must not be null!
	 * @return The correspondenting EventType.
	 */
	public static MethodTypes stringToType( final String name ) {
		if( name == null ) {
			throw new IllegalArgumentException( "The parameter 'name' must not be null!" );
		}
		
		if( name.equals( "PUT" ) ) {
			return MethodTypes.PUT;
		} else if( name.equals( "PUTCB" ) ) {
			return MethodTypes.PUTCB;
		} else if( name.equals( "GET" ) ) {
			return MethodTypes.GET;
		} else if( name.equals( "GETCB" ) ) {
			return MethodTypes.GETCB;
		} else if( name.equals( "GETPUTCB" ) ) {
			return MethodTypes.GETPUTCB;
		} else if( name.equals( "monitor" ) ) {
			return MethodTypes.MONITOR;
		}
		return null;
	}
	
	/**
	 * This static method translates a MethodType into a String, like it's used in the measuring
	 * station description or the scan description. 
	 * 
	 * @param type The type, that should be translated. Must not be null!
	 * @return The correspondentin string. Null if the Type was invalid.
	 */
	public static String typeToString( final MethodTypes type ) {
		if( type == null ) {
			throw new IllegalArgumentException( "The parameter 'type' must not be null!" );
		}
		
		switch( type ) {
			case PUT:
				return "PUT";
			case PUTCB:
				return "PUTCB";
			case GET:
				return "GET";
			case GETCB:
				return "GETCB";
			case GETPUTCB:
				return "GETPUTCB";
			case MONITOR:
				return "monitor";
		}
		
		return null;
	}
}

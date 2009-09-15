/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data;

/**
 * 
 * This enum defines the possible plotmodes of a line in a plot window.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.3
 * 
 * @see de.ptb.epics.eve.data.measuringstation.PlugIn
 *
 */
public enum PluginTypes {

	/**
	 * Says, that the Plugin is a position plugin, that could be used to calculate the position of motor axis.
	 */
	POSITION,
	/**
	 * Says, that the Plugin is a save plugin, that could be used in the definition of a Scan-Chain.
	 */
	SAVE,
	/**
	 * Says, that the Plugin is a display plugin, that is doing some precalculations for a plotWindow. 
	 */
	DISPLAY,
	
	POSTSCANPOSITIONING;
	
	/**
	 * This static Method is translating a name for plugin type like it's used in the
	 * measuring station description or the scan description into the correpondenting PluginType.
	 * Possible values are: position, save and display
	
	 * @param type The type, that should be translated. Must not be null!
	 * @return The correspondentin string. Null if the Type was invalid.
	 */
	public static String typeToString( final PluginTypes type ) {
		if( type == null ) {
			throw new IllegalArgumentException( "The parameter 'type' must not be null" );
		}
		
		switch( type ) {
			case POSITION:
				return "position";
			case SAVE:
				return "save";
			case DISPLAY:
				return "display";
			case POSTSCANPOSITIONING:
				return "postscanpositioning";
		}
		return null;
		
	}
	
}

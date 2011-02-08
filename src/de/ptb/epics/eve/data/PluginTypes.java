/*
 * Copyright (c) 2001, 2008 Physikalisch-Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 */
package de.ptb.epics.eve.data;

/**
 * This enum defines the possible plotmodes of a line in a plot window.
 * @author   Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version   1.3
 * @see  de.ptb.epics.eve.data.measuringstation.PlugIn
 */
public enum PluginTypes {

	/**
	 * Says, that the plug in is a position plug in, that could be used to 
	 * calculate the position of motor axis.
	 *
	 * @uml.property  name="pOSITION"
	 * @uml.associationEnd  
	 */
	POSITION,
	/**
	 * Says, that the plug in is a save plug in, that could be used in the 
	 * definition of a Scan-Chain.
	 *
	 * @uml.property  name="sAVE"
	 * @uml.associationEnd  
	 */
	SAVE,
	/**
	 * Says, that the plug in is a display plug in, that is doing some 
	 * pre-calculations for a plotWindow. 
	 *
	 * @uml.property  name="dISPLAY"
	 * @uml.associationEnd  
	 */
	DISPLAY,
	
	/**
	 * Says, that the plug in is a postscanpositions plug in, that moves an axis
	 * to a position after
	 *
	 * @uml.property  name="pOSTSCANPOSITIONING"
	 * @uml.associationEnd  
	 */
	POSTSCANPOSITIONING;
	
	/**
	 * This static Method is translating a name for plug in type into its
	 * corresponding PluginType.
	 * Possible values are: position, save and display
	
	 * @param type the type, that should be translated.
	 * @return (type valid) ? corresponding string : 'null'
	 * @exception IllegalArgumentException if type == 'null'
	 */
	public static String typeToString(final PluginTypes type) {
		if(type == null) {
			throw new IllegalArgumentException(
					"The parameter 'type' must not be null");
		}
		
		switch(type) {
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
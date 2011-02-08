/*
 * Copyright (c) 2001, 2008 Physikalisch-Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 */
package de.ptb.epics.eve.data;

/**
 * This enum holds possible values that describe at which point all motor axis
 * of a measuring station should be saved to the output file.
 * @author   srehfeld
 */
public enum SaveAxisPositionsTypes {

	/**
	 * Never save all axis positions.
	 *
	 * @uml.property  name="nEVER"
	 * @uml.associationEnd  
	 */
	NEVER,
	
	/**
	 * Save all axis positions before the start of a Scan Module.
	 *
	 * @uml.property  name="bEFORE"
	 * @uml.associationEnd  
	 */
	BEFORE,
	
	/**
	 * Save all axis positions after the end of a Scan Module.
	 *
	 * @uml.property  name="aFTER"
	 * @uml.associationEnd  
	 */
	AFTER,
	
	/**
	 * Save all axis positions before and after the end of a Scan Module.
	 *
	 * @uml.property  name="bOTH"
	 * @uml.associationEnd  
	 */
	BOTH;
	
	/**
	 * converts a value of the enum to a string.
	 * 
	 * @param saveAxisPositionsTypes the value that should be converted
	 * @return The string value of the passes enum value.
	 * @exception UnsupportedOperationException 
	 */
	public static String typeToString(
					final SaveAxisPositionsTypes saveAxisPositionsTypes) {
		switch(saveAxisPositionsTypes) {
			case NEVER:
				return "never";
			case BEFORE:
				return "before";
			case AFTER:
				return "after";
			case BOTH:
				return "both";
		}
		throw new UnsupportedOperationException(
				"The value " + saveAxisPositionsTypes + 
				" was passed, but no translation to a String was found. " +
				"Please check your implementation of this method!");
	}
	
	/**
	 * 
	 * converts a string to a value of the SaveAxisPositionsTypes enum.
	 * 
	 * @param saveAxisPositionsTypes one of {"never", "before", "after", "both"}
	 * @return the corresponding type of the given string
	 * @exception IllegalArgumentException if saveAxisPositionsTypes == 'null'
	 */
	public static SaveAxisPositionsTypes stringToType(
							final String saveAxisPositionsTypes) {
		if(saveAxisPositionsTypes == null) {
			throw new IllegalArgumentException(
					"The parameter 'saveAxisPositionsTypes' must not be null!");
		}
		if( saveAxisPositionsTypes.equals( "never" ) ) {
			return SaveAxisPositionsTypes.NEVER;
		} else if( saveAxisPositionsTypes.equals( "before" ) ) {
			return SaveAxisPositionsTypes.BEFORE;
		} else if( saveAxisPositionsTypes.equals( "after" ) ) {
			return SaveAxisPositionsTypes.AFTER;
		}  else if( saveAxisPositionsTypes.equals( "both" ) ) {
			return SaveAxisPositionsTypes.BOTH;
		}   
		return null;
	}
}
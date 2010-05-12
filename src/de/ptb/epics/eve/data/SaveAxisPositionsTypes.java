/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data;

/**
 * This enum hold possible values that describes at which point all motor axis of
 * a measuring station should be saved to the outputfile.
 * 
 * @author srehfeld
 *
 */
public enum SaveAxisPositionsTypes {

	/**
	 * Never save all axis positions.
	 */
	NEVER,
	
	/**
	 * Save all axis positions before the start of a Scan Module.
	 */
	BEFORE,
	
	/**
	 * Save all axis positions after the end of a Scan Module.
	 */
	AFTER,
	
	/**
	 * Save all axis positoins before and after the end of a Scan Module.
	 */
	BOTH;
	
	/**
	 * This static method convert a value of the enum to a string like it's used in the scan description.
	 * 
	 * @param saveAxisPositionsTypes The value that should be converted to a string.
	 * @return The string value of the passes enum value.
	 */
	public static String typeToString( final SaveAxisPositionsTypes saveAxisPositionsTypes ) {
		switch( saveAxisPositionsTypes ) {
			case NEVER:
				return "never";
			case BEFORE:
				return "before";
			case AFTER:
				return "after";
			case BOTH:
				return "both";
		}
		throw new UnsupportedOperationException( "The value " + saveAxisPositionsTypes + " was passed, but no translation to a String was found. Please check your implementation of this method!" );
	}
	
	/**
	 * 
	 * This methods converts a string like it's used in the scan description to a value of the SaveAxisPositionsTypes enum.
	 * 
	 * @param saveAxisPositionsTypes The String that should be converted. Possible values are never, before, after and both. Must not be null.
	 * @return Gives back the correponding type of the given string or gives back null if the string is unknown.
	 */
	public static SaveAxisPositionsTypes stringToType( final String saveAxisPositionsTypes ) {
		if( saveAxisPositionsTypes == null ) {
			throw new IllegalArgumentException( "The parameter 'saveAxisPositionsTypes' must not be null!");
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

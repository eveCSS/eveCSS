/*
 * Copyright (c) 2001, 2008 Physikalisch-Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 */
package de.ptb.epics.eve.data;

/**
 * Defines available values that describe at which point all motor axis
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
	
	// TODO define when exception is thrown
	// TODO correct bad style with saveAxisPositionsTypes and SaveAxisPositionsTypes
	/**
	 * Converts a save axis positions type (<code>SaveAxisPositionsTypes</code>)
	 * into its corresponding <code>String</code>.
	 * 
	 * @param saveAxisPositionsTypes the position type that should be converted
	 * @return the corresponding <code>String</code>
	 * @throws UnsupportedOperationException ...
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
	 * Converts a <code>String</code> into its corresponding save axis positions 
	 * type (<code>SaveAxisPositionsTypes</code>).
	 * 
	 * @param saveAxisPositionsTypes the <code>String</code> that should be 
	 * 								  converted<br>
	 * 								  <b>Precondition:</b> 
	 * 								  saveAxisPositionsTypes is element of  
	 * 								  {"never", "before", "after", "both"}
	 * @return the corresponding axis position
	 * @throws IllegalArgumentException if the argument is <code>null</code>
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
/* 
 * Copyright (c) 2001, 2008 Physikalisch-Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 */
package de.ptb.epics.eve.viewer;

import de.ptb.epics.eve.ecp1.intern.DataModifier;

/**
 * Defines all available mathematical functions.
 * Used to represent different statistical measures.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public enum MathFunction {

	/** 
	 * the plain (unmodified) value 
	 */
	UNMODIFIED, 
	
	/** 
	 *  
	 */
	MINIMUM, 
	
	/** 
	 *  
	 */
	MAXIMUM, 
	
	/** 
	 *  
	 */
	CENTER, 
	
	/** 
	 *  
	 */
	EDGE, 
	
	/** 
	 *  
	 */
	AVERAGE, 
	
	/** 
	 *  
	 */
	DEVIATION, 
	
	/** 
	 *  
	 */
	FWHM, 
	
	/** 
	 *  
	 */
	SUM, 
	
	/** 
	 *  
	 */
	UNKNOWN;
	
	/**
	 * Converts the enum type to a <code>String</code>.
	 * 
	 * @return a <code>String</code> containing the corresponding enum type. 
	 */
	public String toString() {
		
		switch (this) {
			case UNMODIFIED:	return "Raw";
			case MINIMUM:		return "Minimum";
			case MAXIMUM:		return "Maximum";
			case CENTER:		return "Center";
			case EDGE:			return "Edge";
			case AVERAGE:		return "Average";
			case DEVIATION:		return "Deviation";
			case FWHM:			return "FWHM";
			case SUM:			return "Sum";
			
			default:	return "unknown";
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public DataModifier toDataModifier() {
		switch (this) {
		case UNMODIFIED:
			return DataModifier.UNMODIFIED;
		case MINIMUM:
			return DataModifier.MIN;
		case MAXIMUM:
			return DataModifier.MAX;
		case CENTER:
			return DataModifier.MEAN_VALUE;
		case EDGE:
			return DataModifier.EDGE;
		case AVERAGE:
			return DataModifier.MEAN_VALUE;
		case DEVIATION:
			return DataModifier.STANDARD_DEVIATION;
		case FWHM:
			return DataModifier.FWHM;
		case SUM:
			return DataModifier.SUM;
		default:
			return DataModifier.UNMODIFIED;
		}	
	}
}
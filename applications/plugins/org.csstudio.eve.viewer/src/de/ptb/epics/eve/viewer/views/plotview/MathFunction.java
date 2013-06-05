package de.ptb.epics.eve.viewer.plot;

import de.ptb.epics.eve.ecp1.types.DataModifier;

/**
 * Defines all available mathematical functions.
 * Used to represent several statistical measures.
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
	 * the mathematical minimum
	 */
	MINIMUM, 
	
	/** 
	 * the mathematical maximum
	 */
	MAXIMUM, 
	
	/** 
	 * the middle point of the range of measures 
	 */
	CENTER, 
	
	/** 
	 * ...
	 */
	EDGE, 
	
	/** 
	 * the arithmetic average
	 */
	AVERAGE, 
	
	/** 
	 * the standard deviation
	 */
	DEVIATION, 
	
	/** 
	 * full width, half maximum  
	 */
	FWHM, 
	
	/** 
	 * the sum
	 */
	SUM, 
	
	/**
	 * normalized data
	 */
	NORMALIZED,
	
	PEAK,
	/** 
	 * ??? 
	 */
	UNKNOWN;
	
	/**
	 * Converts the enum type to a <code>String</code>.
	 * 
	 * @return a <code>String</code> containing the corresponding enum type. 
	 */
	public String toString() {
		
		switch (this) {
			case UNMODIFIED:	return "Recent";
			case MINIMUM:		return "Minimum";
			case MAXIMUM:		return "Maximum";
			case CENTER:		return "Center";
			case EDGE:			return "Edge";
			case AVERAGE:		return "Average";
			case DEVIATION:		return "Deviation";
			case FWHM:			return "FWHM";
			case SUM:			return "Sum";
			case NORMALIZED:	return "Normalized";
			case PEAK:			return "Peak";
			
			default:	return "unknown";
		}
	}
	
	/**
	 * Converts the enum type to its corresponding <code>DataModifier</code>.
	 * 
	 * @return the corresponding <code>DataModifier</code>
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
			return DataModifier.CENTER;
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
		case NORMALIZED:
			return DataModifier.NORMALIZED;
		case PEAK:
			return DataModifier.PEAK;
		default:
			return DataModifier.UNKNOWN;
		}	
	}
}
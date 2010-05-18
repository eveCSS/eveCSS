package de.ptb.epics.eve.viewer;

import de.ptb.epics.eve.data.ComparisonTypes;
import de.ptb.epics.eve.ecp1.intern.DataModifier;

public enum MathFunction {

	UNMODIFIED, MINIMUM, MAXIMUM, CENTER, EDGE, AVERAGE, DEVIATION, FWHM, SUM, UNKNOWN;
	
	public String toString(){
		switch (this) {
		case UNMODIFIED:
			return "Raw";
		case MINIMUM:
			return "Minimum";
		case MAXIMUM:
			return "Maximum";
		case CENTER:
			return "Center";
		case EDGE:
			return "Edge";
		case AVERAGE:
			return "Average";
		case DEVIATION:
			return "Deviation";
		case FWHM:
			return "FWHM";
		case SUM:
			return "Sum";
		default:
			return "unknown";
		}
	}
	
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

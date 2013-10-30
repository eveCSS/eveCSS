package de.ptb.epics.eve.data.scandescription.processors;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.scandescription.ScanDescription;

/**
 * Defines some general methods that must be implemented by any
 * implementation, that is saving a scan module.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @author Marcus Michalsky
 */
public interface IScanDescriptionSaver {

	/**
	 * Sets the measuring station the scan description originated from. 
	 * 
	 * @param measuringStation the measuring station.
	 * <dt><b>Precondition:</b><dd> the argument is not <code>null</code>
	 */
	void setMeasuringStationDescription(final IMeasuringStation measuringStation);
	
	/**
	 * Returns the measuring station the scan description resulted from.
	 * 
	 * @return the measuring station the scan description resulted from
	 * <dt><b>Postcondition:</b><dd> the return value is not <code>null</code>
	 */
	IMeasuringStation getMeasuringStationDescription();
	
	/**
	 * Sets the scan description that should be saved.
	 * 
	 * @param scanDescription the scan description that should be saved. 
	 * <dt><b>Precondition:</b><dd> the argument is not <code>null</code>
	 */
	void setScanDescription(final ScanDescription scanDescription);
	
	/**
	 * Returns the scan description that should be saved.
	 * 
	 * @return the scan description that should be saved
	 * <dt><b>Postcondition:</b><dd> the return value is not <code>null</code> 
	 */
	ScanDescription getScanDescription();
	
	/**
	 * Saves the scan description.
	 * 
	 * @return <code>true</code> if the scan description has been saved, 
	 * 			<code>false</code> otherwise
	 */
	boolean save();
}
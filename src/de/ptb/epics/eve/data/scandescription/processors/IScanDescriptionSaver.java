/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data.scandescription.processors;

import java.util.List;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.scandescription.ScanDescription;

/**
 * This interface defines some general methods that must be implemented by any
 * implementation, that is saving the model of the Scan Modul Editor.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.3
 *
 */
public interface IScanDescriptionSaver {

	/**
	 * Sets the description of the measuring station for which the scan description, that should
	 * be saved was made for.
	 * 
	 * @param measuringStation The measuring station. The implementation must not accept null here.
	 */
	public void setMeasuringStationDescription( final IMeasuringStation measuringStation );
	
	/**
	 * Gives back the measuring station, for that the scan description is for.
	 * 
	 * @return The measuring station. The implementation must not return null!
	 */
	public IMeasuringStation getMeasuringStationDescription();
	
	/**
	 * Sets the scan description that should be saved.
	 * 
	 * @param scanDescription The scan descrption that should be saved. The implementation must not accept null here.
	 */
	public void setScanDescription( final ScanDescription scanDescription );
	
	/**
	 * Gives back the scan description that should be saved.
	 * 
	 * @return The scan description that should be saved. The implemenation must not return null here!
	 */
	public ScanDescription getScanDescription();
	
	/**
	 * The call of save will save the scan description.
	 * 
	 * @return Returns true if the scan description has been saved correctly and false if not.
	 */
	public boolean save();
	
}

package de.ptb.epics.eve.ecp1.client.interfaces;

import de.ptb.epics.eve.ecp1.client.model.MeasurementData;

/**
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public interface IMeasurementDataListener {
	
	/**
	 * Gets called if new data has arrived.
	 * 
	 * @param measurementData the 
	 * 		  {@link de.ptb.epics.eve.ecp1.client.model.MeasurementData}
	 */
	void measurementDataTransmitted(final MeasurementData measurementData);
}
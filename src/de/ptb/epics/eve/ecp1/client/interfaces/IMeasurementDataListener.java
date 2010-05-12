package de.ptb.epics.eve.ecp1.client.interfaces;

import de.ptb.epics.eve.ecp1.client.model.MeasurementData;

public interface IMeasurementDataListener {
	public void measurementDataTransmitted( final MeasurementData measurementData );
}

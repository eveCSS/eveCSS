package de.ptb.epics.eve.viewer.views.plotview.table;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.ecp1.client.model.MeasurementData;

/**
 * @author Marcus Michalsky
 * @since 1.22
 */
public abstract class Single extends Data {
	private static final Logger LOGGER = Logger.getLogger(Single.class
			.getName());

	/**
	 * @param plotWindow
	 * @param yAxis defines which y axis of the plot is used
	 */
	public Single(PlotWindow plotWindow, int yAxis) {
		super(plotWindow, yAxis);
	}
	
	protected boolean isAssociated(MeasurementData measurementData) {
		if (!super.isAssociated(measurementData)) {
			return false;
		}
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void measurementDataTransmitted(MeasurementData measurementData) {
		if (!this.isAssociated(measurementData)) {
			LOGGER.debug("Single not associated");
			return;
		}
		LOGGER.debug("Single data match");
		
		if (measurementData.getName().equals(super.getPlotWindow().getXAxis().getID())) {
			super.setMotorPosition(Data.convert(measurementData, 0));
			super.setMotorRawValue(measurementData.getValues().get(0));
			return;
		}
		if (measurementData.getName().equals(this.getDetectorId())) {
			super.setDetectorValue(Data.convert(measurementData, 0));
		}
	}
}
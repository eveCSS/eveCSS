package de.ptb.epics.eve.viewer.views.plotview.table;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.ecp1.client.model.MeasurementData;

/**
 * @author Marcus Michalsky
 * @since 1.22
 */
public abstract class Aggregate extends Data {
	private static final Logger LOGGER = Logger.getLogger(Aggregate.class
			.getName());
	
	/**
	 * Constructor.
	 * 
	 * @param plotWindow the plot window
	 * @param yAxis defines which y axis of the plot is used
	 */
	public Aggregate(PlotWindow plotWindow, int yAxis) {
		super(plotWindow, yAxis);
	}
	
	/**
	 *{@inheritDoc} 
	 */
	@Override
	protected boolean isAssociated(MeasurementData measurementData) {
		if (!super.isAssociated(measurementData)) {
			return false;
		}
		if (!measurementData.getName().equals(this.getDetectorId())) {
			return false;
		}
		if (measurementData.getPositionCounter() != super.getPlotWindow()
				.getId()) {
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
			LOGGER.debug("Aggregate not associated");
			return;
		}
		LOGGER.debug("Aggregate data match");
		super.setMotorPosition(Data.convert(measurementData, 0));
		super.setMotorRawValue(measurementData.getValues().get(0));
		super.setDetectorValue(Data.convert(measurementData, 1));
	}
}
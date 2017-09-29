package de.ptb.epics.eve.viewer.views.plotview.table;

import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.ecp1.types.DataModifier;

/**
 * @author Marcus Michalsky
 * @since 1.22
 */
public class Normalized extends Single {

	/**
	 * Constructor.
	 * 
	 * @param plotWindow the plot window
	 * @param defines which y axis of the plot is used
	 */
	public Normalized(PlotWindow plotWindow, int yAxis) {
		super(plotWindow, yAxis);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DataModifier getDataModifier() {
		return DataModifier.NORMALIZED;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDetectorId() {
		return super.getPlotWindow().getYAxes().get(super.getyAxis())
				.getDetectorChannel().getID()
				+ "__"
				+ super.getPlotWindow().getYAxes().get(super.getyAxis())
						.getNormalizeChannel().getID();
	}
}
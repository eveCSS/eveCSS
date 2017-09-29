package de.ptb.epics.eve.viewer.views.plotview.table;

import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.ecp1.types.DataModifier;

/**
 * @author Marcus Michalsky
 * @since 1.22
 */
public class Deviation extends Aggregate {

	/**
	 * @param plotWindow
	 * @param yAxis
	 */
	public Deviation(PlotWindow plotWindow, int yAxis) {
		super(plotWindow, yAxis);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DataModifier getDataModifier() {
		return DataModifier.STANDARD_DEVIATION;
	}
}
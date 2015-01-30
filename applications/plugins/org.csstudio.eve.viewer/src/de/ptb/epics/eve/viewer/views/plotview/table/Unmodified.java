package de.ptb.epics.eve.viewer.views.plotview.table;

import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.ecp1.types.DataModifier;

/**
 * @author Marcus Michalsky
 * @since 1.22
 */
public class Unmodified extends Single {

	/**
	 * Constructor.
	 * 
	 * @param plotWindow the plot window
	 * @param yAxis defines which y axis of the plot is used
	 */
	public Unmodified(PlotWindow plotWindow, int yAxis) {
		super(plotWindow, yAxis);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DataModifier getDataModifier() {
		return DataModifier.UNMODIFIED;
	}
}
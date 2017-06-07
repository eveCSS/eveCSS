package de.ptb.epics.eve.viewer.views.plotview.table;

import de.ptb.epics.eve.data.TransportTypes;
import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.ecp1.types.DataModifier;

/**
 * @author Marcus Michalsky
 * @since 1.22
 */
public class Edge extends Aggregate {

	/**
	 * Constructor.
	 * 
	 * @param plotWindow the plot window
	 * @param yAxis defines which y axis of the plot is used
	 */
	public Edge(PlotWindow plotWindow, int yAxis) {
		super(plotWindow, yAxis);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DataModifier getDataModifier() {
		return DataModifier.EDGE;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isGoToEnabled() {
		if (!this.getPlotWindow().getXAxis().getGoto().getAccess()
				.getTransport().equals(TransportTypes.LOCAL)
				&& !this.getMotorPosition().equals(Data.NO_VALUE)) {
			return true;
		}
		return false;
	}
}
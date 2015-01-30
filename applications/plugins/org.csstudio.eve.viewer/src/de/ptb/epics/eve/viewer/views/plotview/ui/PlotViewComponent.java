package de.ptb.epics.eve.viewer.views.plotview.ui;

import de.ptb.epics.eve.data.scandescription.PlotWindow;

/**
 * Common functionality of PlotView components.
 * 
 * @author Marcus Michalsky
 * @since 1.22
 */
public interface PlotViewComponent {
	
	/**
	 * Sets the plot window whose elements should be shown.
	 * 
	 * @param plotWindow the plot window whose elements should be shown
	 */
	public void setPlotWindow(PlotWindow plotWindow);
}
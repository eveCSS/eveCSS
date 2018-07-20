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
	void setPlotWindow(PlotWindow plotWindow);
	
	/**
	 * Resets all elements of the plot view component such as listeners such 
	 * that it is safe to dispose it.
	 * 
	 * @since 1.29.6
	 */
	void clear();
}
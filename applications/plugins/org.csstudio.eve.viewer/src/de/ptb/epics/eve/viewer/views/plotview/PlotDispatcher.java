package de.ptb.epics.eve.viewer.views.plotview;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.viewer.XMLDispatcher;

/**
 * PlotDispatcher controls the creation and removal of plot windows.
 * 
 * @author Marcus Michalsky
 * @since 1.13
 */
public class PlotDispatcher implements PropertyChangeListener {
	private static final Logger LOGGER = Logger.getLogger(PlotDispatcher.class
			.getName());

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals(XMLDispatcher.SCAN_DESCRIPTION_PROP)) {
			if (e.getNewValue() instanceof ScanDescription) {
				ScanDescription sd = (ScanDescription)e.getNewValue();
				LOGGER.debug("new scan description received");
				// TODO
			}
		}
	}
}
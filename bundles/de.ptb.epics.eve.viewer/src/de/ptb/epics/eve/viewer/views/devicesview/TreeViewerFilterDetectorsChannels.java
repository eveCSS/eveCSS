package de.ptb.epics.eve.viewer.views.devicesview;

import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;

/**
 * <code>TreeViewerFilterDetectorsChannels</code>.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class TreeViewerFilterDetectorsChannels extends ViewerFilter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if(element instanceof Detector || element instanceof DetectorChannel) {
			return false;
		} else if(element instanceof List<?>) {
			// the element is a category
			if(!((List<?>) element).isEmpty()) {
				// the element contains items (just to ensure get(0) resolves)
				if (((List<?>) element).get(0) instanceof Detector ||
					((List<?>) element).get(0) instanceof DetectorChannel) {
					// category is "Detectors & Channels" -> filter
					return false;
				}
			}
		}
		return true;
	}
}
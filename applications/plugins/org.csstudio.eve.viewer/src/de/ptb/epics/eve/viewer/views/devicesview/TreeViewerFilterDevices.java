package de.ptb.epics.eve.viewer.views.devicesview;

import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import de.ptb.epics.eve.data.measuringstation.Device;

/**
 * <code>TreeViewerFilterDevices</code>.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class TreeViewerFilterDevices extends ViewerFilter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if(element instanceof Device) {
			return false;
		} else if(element instanceof List<?>) {
			// the element is a category
			if(!((List<?>) element).isEmpty()) {
				// the element contains items (just to ensure get(0) resolves)
				if (((List<?>) element).get(0) instanceof Device) {
					// category is "Devices" -> filter
					return false;
				}
			}
		}
		return true;
	}
}
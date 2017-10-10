package de.ptb.epics.eve.viewer.views.devicesview;

import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;

/**
 * <code>TreeViewerFilterMotorsAxes</code>.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class TreeViewerFilterMotorsAxes extends ViewerFilter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if(element instanceof Motor || element instanceof MotorAxis) {
			// the element is a motor or a motor axis -> filter
			return false;
		} else if(element instanceof List<?>) {
			// the element is a category
			if(!((List<?>) element).isEmpty()) {
				// the element contains items (just to ensure get(0) resolves)
				if (((List<?>) element).get(0) instanceof Motor ||
					((List<?>) element).get(0) instanceof MotorAxis) {
					// category is "Motors & Axes" -> filter
					return false;
				}
			}
		}
		return true;
	}
}
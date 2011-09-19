package de.ptb.epics.eve.viewer.views.devicesview;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;

/**
 * @author mmichals
 *
 */
public class TreeViewerFilter extends ViewerFilter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if(element instanceof IMeasuringStation) return true;
		if(element instanceof String) return true;
		if(element instanceof Motor || element instanceof MotorAxis) {
			return true;
		}
		return false;
	}

}

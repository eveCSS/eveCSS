package de.ptb.epics.eve.editor.views.scanmoduleview.motoraxiscomposite;

import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.util.jface.ViewerComparator;

/**
 * @author Marcus Michalsky
 * @since 1.5
 */
public class TableViewerComparator extends ViewerComparator {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		return direction * ((((Axis) e1).getMotorAxis().getName()).
				compareTo(((Axis) e2).getMotorAxis().getName()));
	}
}
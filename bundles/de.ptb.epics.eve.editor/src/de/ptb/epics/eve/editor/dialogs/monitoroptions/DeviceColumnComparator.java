package de.ptb.epics.eve.editor.dialogs.monitoroptions;

import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.util.ui.jface.ViewerComparator;

/**
 * @author Hartmut Scherr
 * @since 1.16
 */
public class DeviceColumnComparator extends ViewerComparator {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		return direction * (((Option)e1).getParent().getName()).compareTo
				(((Option)e2).getParent().getName());
	}
}
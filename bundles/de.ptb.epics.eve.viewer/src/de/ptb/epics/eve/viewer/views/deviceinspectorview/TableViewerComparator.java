package de.ptb.epics.eve.viewer.views.deviceinspectorview;

import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.util.ui.jface.ViewerComparator;

/**
 * <code>TableViewerComparator</code> is the comparator used by the table 
 * viewers of the 
 * {@link de.ptb.epics.eve.viewer.views.deviceinspectorview.ui.DeviceInspectorView}.
 * It allows sorting by name in ascending and descending order.
 * 
 * @author Marcus Michalsky
 * @since 0.4.2
 */
public class TableViewerComparator extends ViewerComparator {
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * It compares with respect to the direction set by 
	 * {@link #setDirection(int)}. If a {@link #DESCENDING} direction is set, 
	 * the behavior is vice versa than described in 
	 * {@link org.eclipse.jface.viewers.ViewerComparator}. 
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		AbstractDevice dev1 = ((CommonTableElement)e1).getAbstractDevice();
		AbstractDevice dev2 = ((CommonTableElement)e2).getAbstractDevice();
		return direction * dev1.getName().compareTo(dev2.getName());
	}
}
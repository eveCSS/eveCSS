package de.ptb.epics.eve.viewer.views.deviceoptionsview;

import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.util.jface.ViewerComparator;

/**
 * <code>TableViewerComparator</code> is the comparator used by the table 
 * viewer of the 
 * {@link de.ptb.epics.eve.viewer.views.deviceoptionsview.DeviceOptionsView}.
 * It allows sorting by name in ascending and descending order.
 * 
 * @author Marcus Michalsky
 * @since 1.1
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
		OptionPV o1 = (OptionPV) e1;
		OptionPV o2 = (OptionPV) e2;
		return direction* o1.getOption().getName().compareTo(
				o2.getOption().getName());
	}
}
package de.ptb.epics.eve.viewer.views.deviceinspectorview;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;

/**
 * <code>TableViewerComparator</code> is the comparator used by the table 
 * viewers of the 
 * {@link de.ptb.epics.eve.viewer.views.deviceinspectorview.DeviceInspectorView}.
 * It allows sorting by name in ascending and descending order.
 * 
 * @author Marcus Michalsky
 * @since 0.4.2
 */
public class TableViewerComparator extends ViewerComparator {
	/*
	 * see http://www.vogella.de/articles/EclipseJFaceTableAdvanced/article.html
	 * for more details, especially when adding multiple column support...
	 */
	
	/** ascending sort order (e.g. A to Z, 0 to 9) */
	public static final int ASCENDING = 1;
	
	/** descending sort order (e.g. Z to A, 9 to 0) */
	public static final int DESCENDING = -1;
	
	// sort order (ascending or descending)
	private int direction;

	/**
	 * Constructs a <code>TableViewerComparator</code> (with {@value #ASCENDING} 
	 * sort direction).
	 */
	public TableViewerComparator() {
		direction = ASCENDING;
	}
	
	/**
	 * Sets the direction of the sort (Use the static field {@link #ASCENDING} 
	 * to sort from A to Z or {@link #DESCENDING} to sort from Z to A.)
	 * 
	 * @param direction the order of sorting (must be one of 
	 * {{@link #ASCENDING}, {@link #DESCENDING}})
	 * @throws IllegalArgumentException if <code>direction</code> is neither 
	 * 		{@link #ASCENDING} nor {@link #DESCENDING}
	 */
	public void setDirection(int direction) {
		if (direction != TableViewerComparator.ASCENDING && 
			direction != TableViewerComparator.DESCENDING) {
			throw new IllegalArgumentException("No arguments other than the " + 
				"static fields (ASCENDING, DESCENDING) declared are allowed!");
		}
		this.direction = direction;
	}
	
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
package de.ptb.epics.eve.util.ui.jface;

import org.eclipse.jface.viewers.Viewer;

/**
 * Can be used as a {@link org.eclipse.jface.viewers.ViewerComparator} by 
 * expanding this class and implementing 
 * {@link #compare(Viewer, Object, Object)} as needed.
 * 
 * @author Marcus Michalsky
 * @since 1.5
 * @see "http://www.vogella.de/articles/EclipseJFaceTableAdvanced/article.html"
 * @deprecated use {@link de.ptb.epics.eve.util.ui.AlphabeticalTableViewerSorter} instead.
 */
public abstract class ViewerComparator extends
		org.eclipse.jface.viewers.ViewerComparator {
	
	/** ascending sort order (e.g. A to Z, 0 to 9) */
	public static final int ASCENDING = 1;
	
	/** state for no active sorting */
	public static final int NONE = 0;
	
	/** descending sort order (e.g. Z to A, 9 to 0) */
	public static final int DESCENDING = -1;
	
	// sort order (ascending or descending)
	protected int direction;

	/**
	 * Constructor. (Initial sort direction is {@value #ASCENDING}).
	 */
	public ViewerComparator() {
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
		if (direction != ViewerComparator.ASCENDING && 
			direction != ViewerComparator.DESCENDING) {
			throw new IllegalArgumentException("No arguments other than the " + 
				"static fields (ASCENDING, DESCENDING) declared are allowed!");
		}
		this.direction = direction;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract int compare(Viewer viewer, Object e1, Object e2);
}

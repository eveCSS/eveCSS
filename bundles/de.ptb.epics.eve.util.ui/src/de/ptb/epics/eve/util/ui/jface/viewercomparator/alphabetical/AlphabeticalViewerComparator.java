package de.ptb.epics.eve.util.ui.jface.viewercomparator.alphabetical;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

/**
 * Used as ViewerComparator for a StructuredViewer. Its compare method has to 
 * be implemented according to the used object type.
 * 
 * @author Marcus Michalsky
 * @since 1.34
 */
public abstract class AlphabeticalViewerComparator extends ViewerComparator {
	private SortOrder sortOrder;
	
	public AlphabeticalViewerComparator() {
		this.sortOrder = SortOrder.NONE;
	}
	
	public SortOrder getSortOrder() {
		return this.sortOrder;
	}
	
	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract int compare(Viewer viewer, Object e1, Object e2);
}

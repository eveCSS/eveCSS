package de.ptb.epics.eve.util.ui.jface.viewercomparator.alphabetical;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TableColumn;

import de.ptb.epics.eve.util.ui.Activator;

/**
 * Used to sort a table (viewer) alphabetically in ascending or descending order.
 * Just call the constructor with a given table viewer, the column to sort and 
 * an {@link AlphabeticalViewerComparator} with implemented compare method.
 * The sort state can be read or set via getter/setter (e.g. for memento purposes).
 * 
 * @author Marcus Michalsky
 * @since 1.34
 */
public class AlphabeticalTableViewerSorter {
	private TableViewer viewer;
	private TableColumn column;
	private AlphabeticalViewerComparator comparator;
	
	private Image ascendingIcon;
	private Image descendingIcon;
	
	public AlphabeticalTableViewerSorter(TableViewer viewer, TableColumn column, 
			AlphabeticalViewerComparator comparator) {
		this.viewer = viewer;
		this.column = column;
		this.comparator = comparator;
		
		this.ascendingIcon = Activator.getDefault().getImageRegistry().
				get("SORT_ASCENDING");
		this.descendingIcon = Activator.getDefault().getImageRegistry().
				get("SORT_DESCENDING");
		
		this.column.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				toggle();
			}
		});
		// TODO finalize: remove Listener to Column ? For now it should exist as
		// long as the application is running (table of non-closable view)
	}
	
	private void toggle() {
		switch (this.comparator.getSortOrder()) {
		case ASCENDING:
			this.comparator.setSortOrder(SortOrder.DESCENDING);
			this.viewer.setComparator(comparator);
			this.column.setImage(descendingIcon);
			break;
		case DESCENDING:
			this.comparator.setSortOrder(SortOrder.NONE);
			this.viewer.setComparator(null);
			this.column.setImage(null);
			break;
		case NONE:
			this.comparator.setSortOrder(SortOrder.ASCENDING);
			this.viewer.setComparator(comparator);
			this.column.setImage(ascendingIcon);
			break;
		default:
			break;
		}
		this.viewer.refresh();
	}
	
	/**
	 * Returns the sort order
	 * @return the sort order
	 */
	public SortOrder getSortOrder() {
		return this.comparator.getSortOrder();
	}
	
	/**
	 * Sets the sort order
	 * @param sortOrder the sort order to set
	 */
	public void setSortOrder(SortOrder sortOrder) {
		// a little hack/trick: just set the "previous" Sort Order and call toggle:
		switch (sortOrder) {
		case ASCENDING:
			this.comparator.setSortOrder(SortOrder.NONE);
			break;
		case DESCENDING:
			this.comparator.setSortOrder(SortOrder.ASCENDING);
			break;
		case NONE:
			this.comparator.setSortOrder(SortOrder.DESCENDING);
			break;
		default:
			break;
		}
		this.toggle();
	}
}

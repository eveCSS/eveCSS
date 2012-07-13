package de.ptb.epics.eve.editor.views.scanmoduleview;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.util.Activator;
import de.ptb.epics.eve.util.jface.ViewerComparator;


/**
 * Prototype for Composites used in the actions tab.
 * <p>
 * The composite is notified of scan module changes of its parent view via 
 * {@link #setScanModule(ScanModule)}. Composites extending this class 
 * should add the contained {@link TableViewerFocusListener} to the viewer 
 * after initialization.
 * <p>
 * For sorting {@link de.ptb.epics.eve.util.jface.ViewerComparator} should be 
 * extended and a newly created instance assigned to 
 * {@link #tableViewerComparator}. Afterwards an instance of 
 * {@link ColumnSelectionListener} can be set as 
 * {@link org.eclipse.swt.events.SelectionListener} to the column of interest.
 * 
 * @author Marcus Michalsky
 * @since 1.2
 */
public abstract class ActionComposite extends Composite {

	private static Logger logger = 
			Logger.getLogger(ActionComposite.class.getName());
	
	protected TableViewer tableViewer;
	protected ScanModuleView parentView;

	protected ViewerComparator tableViewerComparator;
	protected int tableViewerSortState;
	
	private Image ascending;
	private Image descending;
	
	/**
	 * Constructor.
	 * 
	 * @param parentView the view the composite is contained in
	 * @param parent the parent
	 * @param style the style
	 */
	public ActionComposite(final ScanModuleView parentView, Composite parent, 
			int style) {
		super(parent, style);
		this.parentView = parentView;
		
		this.tableViewerSortState = ViewerComparator.NONE;
		
		ascending = Activator.getDefault().getImageRegistry()
				.get("SORT_ASCENDING");
		descending = Activator.getDefault().getImageRegistry()
				.get("SORT_DESCENDING");
	}

	/**
	 * Sets the {@link de.ptb.epics.eve.data.scandescription.ScanModule}.
	 * 
	 * @param scanModule the 
	 * 		  {@link de.ptb.epics.eve.data.scandescription.ScanModule} that 
	 * 		  should be set
	 */
	public void setScanModule(final ScanModule scanModule) {
		logger.debug(this.getClass().getName());
		this.tableViewer.setInput(scanModule);
	}

	// ************************************************************************
	// **************************** Listener **********************************
	// ************************************************************************
	
	/**
	 * {@link org.eclipse.swt.events.FocusListener} of <code>tableViewer</code>.
	 */
	protected class TableViewerFocusListener implements FocusListener {
		
		/**
		 * Constructor.
		 */
		public TableViewerFocusListener() {
			super();
		}
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusGained(FocusEvent e) {
			logger.debug("focusGained");
			parentView.setSelectionProvider(tableViewer);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusLost(FocusEvent e) {
		}
	}
	
	/**
	 * @author Marcus Michalsky
	 * @since 1.5
	 */
	protected class ColumnSelectionListener implements SelectionListener {
		
		private TableViewerColumn sortColumn;
		
		public ColumnSelectionListener(TableViewerColumn sortColumn) {
			this.sortColumn = sortColumn;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			switch(tableViewerSortState) {
			case ViewerComparator.NONE: // was no sorting -> now ascending
				tableViewerComparator.setDirection(
						ViewerComparator.ASCENDING);
				tableViewer.setComparator(tableViewerComparator);
				sortColumn.getColumn().setImage(ascending);
				break;
			case 1: // was ascending -> now descending
				tableViewerComparator.setDirection(
						ViewerComparator.DESCENDING);
				tableViewer.setComparator(tableViewerComparator);
				tableViewer.refresh();
				sortColumn.getColumn().setImage(descending);
				break;
			case 2: // was descending -> now no sorting
				tableViewer.setComparator(null);
				sortColumn.getColumn().setImage(null);
				break;
			}
			// set is {0,1,2}
			// if it becomes 3 it has to be 0 again
			// but before the state has to be increased to the new state
			tableViewerSortState = ++tableViewerSortState % 3;
			logger.debug("new table sort state: "
					+ tableViewerSortState);
		}
	}
}
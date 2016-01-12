package de.ptb.epics.eve.editor.views.scanmoduleview;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import de.ptb.epics.eve.data.scandescription.AbstractBehavior;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
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
 * Remember to initialize the <code>sortColumn</code> property in the subclass.
 * 
 * @author Marcus Michalsky
 * @since 1.2
 */
public abstract class ActionComposite extends Composite implements
		IModelUpdateListener {

	private static Logger logger = 
			Logger.getLogger(ActionComposite.class.getName());
	
	private ScanModule currentScanModule;
	
	protected TableViewer tableViewer;
	protected TableViewerColumn sortColumn;
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
		this.currentScanModule = null;
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
		if (this.currentScanModule != null) {
			this.currentScanModule.removeModelUpdateListener(this);
		}
		this.currentScanModule = scanModule;
		this.tableViewer.setInput(scanModule);
		if (this.currentScanModule != null) {
			this.currentScanModule.addModelUpdateListener(this);
		}
	}

	
	/**
	 * Returns the scan module
	 * @return the scan module
	 */
	public ScanModule getCurrentScanModule() {
		return currentScanModule;
	}

	/**
	 * Returns the model (collection) the composite represents.
	 * 
	 * Used for a generic DnD implementation.
	 * @return the model (collection) the composite represents
	 * @since 1.25
	 */
	public abstract List<? extends AbstractBehavior> getModel();
	
	/**
	 * Returns the current sort state of the contained table viewer.
	 * 
	 * @return the current sort state of the contained table viewer
	 */
	public int getSortState() {
		return this.tableViewerSortState;
	}
	
	/**
	 * Sets the sort state of the contained table viewer.
	 * 
	 * @param sortState the sort state that should be set.
	 * @throws IllegalArgumentException if <code>sortState</code> not in {0,1,2}
	 */
	public void setSortState(int sortState) {
		switch(sortState) {
		case 0:
			this.tableViewer.setComparator(null);
			this.sortColumn.getColumn().setImage(null);
			this.tableViewerSortState = 0;
			break;
		case 1:
			this.tableViewerComparator.setDirection(
					ViewerComparator.ASCENDING);
			this.tableViewer.setComparator(tableViewerComparator);
			this.sortColumn.getColumn().setImage(ascending);
			this.tableViewerSortState = 1;
			break;
		case 2:
			this.tableViewerComparator.setDirection(
					ViewerComparator.DESCENDING);
			this.tableViewer.setComparator(tableViewerComparator);
			this.tableViewer.refresh();
			this.sortColumn.getColumn().setImage(descending);
			this.tableViewerSortState = 2;
			break;
		default: throw new IllegalArgumentException("Illegal sort state.");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
		this.tableViewer.refresh();
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
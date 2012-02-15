package de.ptb.epics.eve.editor.views.scanmoduleview.plotcomposite;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchActionConstants;

import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;

/**
 * <code>PlotComposite</code> is part of the 
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView}.
 * 
 * @author Hartmut Scherr
 * @author Marcus Michalsky
 */
public class PlotComposite extends Composite {

	// logging
	private static Logger logger = 
			Logger.getLogger(PlotComposite.class.getName());

	private TableViewer tableViewer;
	private ScanModuleView parentView;

	/**
	 * Constructor.
	 * 
	 * @param parent the parent
	 * @param style the style
	 */
	public PlotComposite(final ScanModuleView parentView, final Composite parent, 
						 final int style) {
		super(parent, style);
		this.parentView = parentView;
		
		this.setLayout(new GridLayout());
		
		createViewer();
		
		this.tableViewer.getTable().addFocusListener(new 
										TableViewerFocusListener());
	}
	
	/*
	 * 
	 */
	private void createViewer() {
		this.tableViewer = new TableViewer(this, SWT.NONE);
		GridData gridData = new GridData();
		gridData.minimumHeight = 120;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		this.tableViewer.getTable().setLayoutData(gridData);
		createColumns(this.tableViewer);
		this.tableViewer.getTable().setHeaderVisible(true);
		this.tableViewer.getTable().setLinesVisible(true);
		this.tableViewer.setContentProvider(new ContentProvider());
		this.tableViewer.setLabelProvider(new LabelProvider());
		
		// create context menu
		MenuManager menuManager = new MenuManager();
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menuManager.setRemoveAllWhenShown(true);
		this.tableViewer.getTable().setMenu(
				menuManager.createContextMenu(this.tableViewer.getTable()));
		// register menu
		parentView.getSite().registerContextMenu(
			"de.ptb.epics.eve.editor.views.scanmoduleview.plotcomposite.popup", 
			menuManager, this.tableViewer);
	}
	
	/*
	 * 
	 */
	private void createColumns(TableViewer viewer) {
		TableViewerColumn idColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		idColumn.getColumn().setText("Id");
		idColumn.getColumn().setWidth(50);

		TableViewerColumn xColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		xColumn.getColumn().setText("x Axis");
		xColumn.getColumn().setWidth(100);

		TableViewerColumn y1Column = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		y1Column.getColumn().setText("y Axis1");
		y1Column.getColumn().setWidth(100);

		TableViewerColumn y2Column = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		y2Column.getColumn().setText("y Axis2");
		y2Column.getColumn().setWidth(100);
	}
	
	/**
	 * Sets the {@link de.ptb.epics.eve.data.scandescription.ScanModule} the 
	 * composite is based on.
	 * 
	 * @param scanModule the 
	 * 		{@link de.ptb.epics.eve.data.scandescription.ScanModule} that 
	 * 		should be set
	 */
	public void setScanModule(final ScanModule scanModule) {
		logger.debug("setScanModule");
		
		this.tableViewer.setInput(scanModule);
		
		if(scanModule == null) {
			return;
		}
		
		// if there are plots present... 
		if(tableViewer.getTable().getItems().length > 0) {
			// ... and none is selected ...
			if(tableViewer.getTable().getSelectionCount() == 0) {
				// ... select the first one
				tableViewer.getTable().select(0);
			}
		}
		parentView.setSelectionProvider(this.tableViewer);
	}

	// ************************************************************************
	// **************************** Listeners *********************************
	// ************************************************************************

	/**
	 * {@link org.eclipse.swt.events.FocusListener} of <code>tableViewer</code>.
	 */
	private class TableViewerFocusListener implements FocusListener {
		
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
}
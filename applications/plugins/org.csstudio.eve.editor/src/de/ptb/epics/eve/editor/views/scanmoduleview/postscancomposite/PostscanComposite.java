package de.ptb.epics.eve.editor.views.scanmoduleview.postscancomposite;

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
 * <code>PostscanComposite</code> is part of the 
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView}.
 * 
 * @author ?
 * @author Marcus Michalsky
 * @author Hartmut Scherr
 */
public class PostscanComposite extends Composite {

	private static Logger logger = 
			Logger.getLogger(PostscanComposite.class.getName());
	
	private TableViewer tableViewer;
	private ScanModuleView parentView;
	
	/**
	 * Constructs a <code>PostscanComposite</code>.
	 * 
	 * @param parentView 
	 * @param parent the parent
	 * @param style the style
	 */
	public PostscanComposite(final ScanModuleView parentView, 
							final Composite parent, final int style) {
		super(parent, style);
		this.parentView = parentView;
		this.setLayout(new GridLayout());
		createViewer();
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
		createColumns();
		this.tableViewer.getTable().setHeaderVisible(true);
		this.tableViewer.getTable().setLinesVisible(true);
		this.tableViewer.setContentProvider(new ContentProvider());
		this.tableViewer.setLabelProvider(new LabelProvider());
		this.tableViewer.getTable().addFocusListener(new 
				TableViewerFocusListener());
		
		// create context menu
		MenuManager menuManager = new MenuManager();
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menuManager.setRemoveAllWhenShown(true);
		this.tableViewer.getTable().setMenu(
				menuManager.createContextMenu(this.tableViewer.getTable()));
		// register menu
		parentView.getSite().registerContextMenu(
			"de.ptb.epics.eve.editor.views.scanmoduleview.postscancomposite.popup", 
			menuManager, this.tableViewer);
	}

	/*
	 * 
	 */
	private void createColumns() {
		TableViewerColumn deviceColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		deviceColumn.getColumn().setText("Device");
		deviceColumn.getColumn().setWidth(400);
		
		TableViewerColumn valueColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		valueColumn.getColumn().setText("Value");
		valueColumn.getColumn().setWidth(100);
		valueColumn.setEditingSupport(new ValueEditingSupport(this.tableViewer));
		
		TableViewerColumn resetColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		resetColumn.getColumn().setText("Reset Original");
		resetColumn.getColumn().setWidth(100);
		resetColumn.setEditingSupport(new ResetEditingSupport(this.tableViewer));
		
		TableViewerColumn emptyColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		emptyColumn.getColumn().setText("");
		emptyColumn.getColumn().setWidth(10);
	}
	
	/**
	 * Sets the {@link de.ptb.epics.eve.data.scandescription.ScanModule} this 
	 * composite is based on.
	 * 
	 * @param scanModule the 
	 * {@link de.ptb.epics.eve.data.scandescription.ScanModule} this composite 
	 * 		is based on
	 */
	public void setScanModule(final ScanModule scanModule) {
		
		this.tableViewer.setInput(scanModule);
		
		// if there are postcans present... 
		if(tableViewer.getTable().getItems().length > 0) {
			// ... and none is selected ...
			if(tableViewer.getTable().getSelectionCount() == 0) {
				// ... select the first one
				tableViewer.getTable().select(0);
			}
		}
	}
	
	// ***********************************************************************
	// ****************************** Listener *******************************
	// ***********************************************************************
	
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
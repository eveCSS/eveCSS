package de.ptb.epics.eve.editor.views.scanmoduleview.positioningcomposite;

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
 * <code>PositioningComposite</code> is part of the 
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView}.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class PositioningComposite extends Composite {

	private static Logger logger = 
			Logger.getLogger(PositioningComposite.class.getName());
	
	private TableViewer tableViewer;
	private ScanModuleView parentView;
	
	/**
	 * Constructs a <code>PositioningComposite</code>.
	 * 
	 * @param parent the parent
	 * @param style the style
	 */
	public PositioningComposite(final ScanModuleView parentView, 
								final Composite parent, final int style) {
		super(parent, style);
		this.parentView = parentView;
		
		this.setLayout(new GridLayout());
		
		createViewer();
		
		this.tableViewer.getTable().addFocusListener(
				new TableViewerFocusListener());
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
		
		// create context menu
		MenuManager menuManager = new MenuManager();
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menuManager.setRemoveAllWhenShown(true);
		this.tableViewer.getTable().setMenu(
				menuManager.createContextMenu(this.tableViewer.getTable()));
		// register menu
		parentView.getSite().registerContextMenu(
			"de.ptb.epics.eve.editor.views.scanmoduleview.positioningcomposite.popup", 
			menuManager, this.tableViewer);
	}
	
	/*
	 * 
	 */
	private void createColumns() {
		TableViewerColumn axisColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		axisColumn.getColumn().setText("Motor Axis");
		axisColumn.getColumn().setWidth(140);
		
		TableViewerColumn pluginColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		pluginColumn.getColumn().setText("Plugin");
		pluginColumn.getColumn().setWidth(60);
		pluginColumn.setEditingSupport(
				new PluginEditingSupport(this.tableViewer));
		
		TableViewerColumn channelColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		channelColumn.getColumn().setText("Detector Channel");
		channelColumn.getColumn().setWidth(140);
		channelColumn.setEditingSupport(
				new ChannelEditingSupport(this.tableViewer));
		
		TableViewerColumn normColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		normColumn.getColumn().setText("Normalize Channel");
		normColumn.getColumn().setWidth(140);
		normColumn.setEditingSupport(
				new NormalizeEditingSupport(this.tableViewer));
		
		TableViewerColumn paramColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		paramColumn.getColumn().setText("Parameters");
		paramColumn.getColumn().setWidth(100);
		paramColumn.setEditingSupport(
				new ParamEditingSupport(this.tableViewer));
		
		TableViewerColumn emptyColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		emptyColumn.getColumn().setText("");
		emptyColumn.getColumn().setWidth(1);
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
		
		this.tableViewer.setInput(scanModule);
		
		// if there are positionings present... 
		if(tableViewer.getTable().getItems().length > 0) {
			// ... and none is selected ...
			if(tableViewer.getTable().getSelectionCount() == 0) {
				// ... select the first one
				tableViewer.getTable().select(0);
			}
		}
	}
	
	// ***********************************************************************
	// *************************** Listeners *********************************
	// ***********************************************************************
	
	/**
	 * {@link org.eclipse.swt.events.FocusListener} on the table.
	 * <p>
	 * Sets the <code>viewer</code> as the selection provider of the 
	 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView}.
	 * 
	 * @author Marcus Michalsky
	 * @since 1.1
	 */
	private class TableViewerFocusListener implements FocusListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusGained(FocusEvent e) {
			logger.debug("focus gained");
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
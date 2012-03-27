package de.ptb.epics.eve.editor.views.scanmoduleview.detectorchannelcomposite;

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
 * <code>DetectorChannelComposite</code>. is part of the
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView}.
 * 
 * @author ?
 * @author Marcus Michalsky
 * @author Hartmut Scherr
 */
public class DetectorChannelComposite extends Composite {

	// logging
	private static Logger logger = Logger.getLogger(
			DetectorChannelComposite.class);
	
	private TableViewer tableViewer;
	private ScanModuleView parentView;
	
	/**
	 * Constructs a <code>DetectorChannelComposite</code>.
	 * 
	 * @param parent the parent composite
	 * @param style the style
	 * @param measuringStation the measuring station (containing available 
	 * 		  detector channels)
	 */
	public DetectorChannelComposite(final ScanModuleView parentView, 
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
		this.tableViewer.getTable().addFocusListener(
				new TableViewerFocusListener());
		
		// create context menu
		MenuManager menuManager = new MenuManager();
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menuManager.setRemoveAllWhenShown(true);
		this.tableViewer.getTable().setMenu(
				menuManager.createContextMenu(this.tableViewer.getTable()));
		// register menu
		parentView.getSite().registerContextMenu(
			"de.ptb.epics.eve.editor.views.scanmoduleview.detectorchannelcomposite.popup", 
			menuManager, this.tableViewer);
	}
	
	/*
	 * 
	 */
	private void createColumns() {
		TableViewerColumn channelColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		channelColumn.getColumn().setText("Detector Channel");
		channelColumn.getColumn().setWidth(250);
		
		TableViewerColumn avgColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		avgColumn.getColumn().setText("Average");
		avgColumn.getColumn().setWidth(80);
	}
	
	/**
	 * Sets the {@link de.ptb.epics.eve.data.scandescription.ScanModule}.
	 * 
	 * @param scanModule the 
	 * 		  {@link de.ptb.epics.eve.data.scandescription.ScanModule} that 
	 * 		  should be set
	 */
	public void setScanModule(final ScanModule scanModule) {
		logger.debug("setScanModule");
		
		this.tableViewer.setInput(scanModule);
		
		// if there are detector channels present... 
		if(tableViewer.getTable().getItems().length > 0) {
			// ... and none is selected ...
			if(tableViewer.getTable().getSelectionCount() == 0) {
				// ... select the first one and set the detector channel view
				tableViewer.getTable().select(0);
			}
		}
		this.parentView.setSelectionProvider(this.tableViewer);
	}

	// ************************************************************************
	// **************************** Listeners *********************************
	// ************************************************************************

	/**
	 * 
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
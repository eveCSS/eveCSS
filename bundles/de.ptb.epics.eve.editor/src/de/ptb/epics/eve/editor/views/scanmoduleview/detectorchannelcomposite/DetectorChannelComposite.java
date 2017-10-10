package de.ptb.epics.eve.editor.views.scanmoduleview.detectorchannelcomposite;

import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchActionConstants;

import de.ptb.epics.eve.data.scandescription.AbstractBehavior;
import de.ptb.epics.eve.editor.views.DelColumnEditingSupport;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;
import de.ptb.epics.eve.editor.views.scanmoduleview.ActionComposite;

/**
 * <code>DetectorChannelComposite</code>. is part of the
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView}.
 * 
 * @author ?
 * @author Marcus Michalsky
 * @author Hartmut Scherr
 */
public class DetectorChannelComposite extends ActionComposite {

	/**
	 * Constructs a <code>DetectorChannelComposite</code>.
	 * 
	 * @param parentView the parent view
	 * @param parent the parent composite
	 * @param style the style
	 */
	public DetectorChannelComposite(final ScanModuleView parentView, 
									final Composite parent, final int style) {
		super(parentView, parent, style);
		this.setLayout(new GridLayout());
		this.tableViewerComparator = new TableViewerComparator();
		createViewer();
		this.initDnD();
	}

	/*
	 * 
	 */
	private void createViewer() {
		this.tableViewer = new TableViewer(this, SWT.FULL_SELECTION);
		GridData gridData = new GridData();
		gridData.minimumHeight = 50;
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
		TableViewerColumn delColumn = new TableViewerColumn(
				this.tableViewer, SWT.CENTER);
		delColumn.getColumn().setText("");
		delColumn.getColumn().setWidth(22);
		delColumn.setEditingSupport(new DelColumnEditingSupport(
				this.tableViewer, 
				"de.ptb.epics.eve.editor.command.removechannel"));
		
		TableViewerColumn channelColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		channelColumn.getColumn().setText("Name");
		channelColumn.getColumn().setWidth(220);
		channelColumn.getColumn().addSelectionListener(
				new ColumnSelectionListener(channelColumn));
		this.sortColumn = channelColumn;
		
		TableViewerColumn avgColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		avgColumn.getColumn().setText("Average");
		avgColumn.getColumn().setWidth(75);
		
		TableViewerColumn intervalColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		intervalColumn.getColumn().setText("Interval");
		intervalColumn.getColumn().setWidth(75);
		
		TableViewerColumn deferredColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		deferredColumn.getColumn().setText("Deferred");
		deferredColumn.getColumn().setWidth(75);
		
		TableViewerColumn normalizedColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		normalizedColumn.getColumn().setText("Normalized By");
		normalizedColumn.getColumn().setWidth(220);
		
		TableViewerColumn stoppedByColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		stoppedByColumn.getColumn().setText("Stopped By");
		stoppedByColumn.getColumn().setWidth(220);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<? extends AbstractBehavior> getModel() {
		return this.getCurrentScanModule().getChannelList();
	}
}
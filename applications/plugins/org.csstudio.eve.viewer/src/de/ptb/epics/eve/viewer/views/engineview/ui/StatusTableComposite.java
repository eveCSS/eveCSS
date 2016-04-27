package de.ptb.epics.eve.viewer.views.engineview.ui;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import de.ptb.epics.eve.ecp1.client.ECP1Client;
import de.ptb.epics.eve.ecp1.client.interfaces.IConnectionStateListener;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.views.engineview.statustable.StatusTracker;
import de.ptb.epics.eve.viewer.views.engineview.statustable.StatusTableElement;
import de.ptb.epics.eve.util.datetime.*;

/**
 * @author Marcus Michalsky
 * @since 1.26
 */
public class StatusTableComposite extends Composite implements IConnectionStateListener {
	private TableViewer tableViewer;
	
	public StatusTableComposite(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 3;
		this.setLayout(gridLayout);
		
		this.createViewer();
		
		ECP1Client engine = Activator.getDefault().getEcp1Client();
		engine.addConnectionStateListener(this);
		
		StatusTracker statusTracker = new StatusTracker();
		engine.addConnectionStateListener(statusTracker);
		engine.addEngineStatusListener(statusTracker);
		engine.addChainProgressListener(statusTracker);
		engine.addChainStatusListener(statusTracker);
		
		this.tableViewer.setInput(statusTracker);
		
		StatusTableSelectionListener selectionListener = 
				new StatusTableSelectionListener(this.tableViewer);
		engine.addEngineStatusListener(selectionListener);
		this.tableViewer.getTable().addSelectionListener(
				selectionListener);
	}
	
	private void createViewer() {
		tableViewer = new TableViewer(this, SWT.FULL_SELECTION | SWT.BORDER);
		createColumns(tableViewer);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);
		
		tableViewer.setContentProvider(new ContentProvider());
		tableViewer.setSorter(new TableViewerSorter());
		
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.minimumHeight = 150;
		tableViewer.getTable().setLayoutData(gridData);
	}
	
	private void createColumns(TableViewer viewer) {
		TableViewerColumn chainColumn = new TableViewerColumn(viewer, SWT.NONE);
		chainColumn.getColumn().setWidth(50);
		chainColumn.getColumn().setText("Chain");
		chainColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((StatusTableElement)element).getChainId().toString();
			}
		});
		
		TableViewerColumn idColumn = new TableViewerColumn(viewer, SWT.NONE);
		idColumn.getColumn().setWidth(50);
		idColumn.getColumn().setText("SM ID");
		idColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				StatusTableElement statusElement = (StatusTableElement)element;
				return statusElement.getScanModuleId() == null 
						? ""
						: statusElement.getScanModuleId().toString();
			}
		});
		
		TableViewerColumn nameColumn = new TableViewerColumn(viewer, SWT.NONE);
		nameColumn.getColumn().setWidth(100);
		nameColumn.getColumn().setText("SM Name");
		nameColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((StatusTableElement)element).getScanModuleName();
			}
		});
		
		TableViewerColumn statusColumn = new TableViewerColumn(viewer, SWT.NONE);
		statusColumn.getColumn().setWidth(80);
		statusColumn.getColumn().setText("Status");
		statusColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((StatusTableElement)element).getStatus();
			}
		});
		
		TableViewerColumn reasonColumn = new TableViewerColumn(viewer, SWT.NONE);
		reasonColumn.getColumn().setWidth(80);
		reasonColumn.getColumn().setText("Reason");
		reasonColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((StatusTableElement)element).getReason();
			}
		});
		
		TableViewerColumn timeColumn = new TableViewerColumn(viewer, SWT.NONE);
		timeColumn.getColumn().setWidth(20);
		timeColumn.getColumn().setText("Remaining Time");
		timeColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				StatusTableElement statusElement = (StatusTableElement)element;
				if (statusElement.getRemainingTime() == null) {
					return "";
				}
				return DateTime.humanReadable(statusElement.getRemainingTime());
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stackConnected() {
		Display.getDefault().syncExec(new Runnable() {
			@Override public void run() {
				tableViewer.getTable().setEnabled(true);
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stackDisconnected() {
		Display.getDefault().syncExec(new Runnable() {
			@Override public void run() {
				tableViewer.getTable().setEnabled(false);
			}
		});
	}
}
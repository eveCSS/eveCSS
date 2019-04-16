package de.ptb.epics.eve.editor.views.scanmoduleview.dynamicchannelvaluescomposite;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IMemento;

import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleViewComposite;

/**
 * Shows a table containing all channels that would be contained in a snapshot 
 * based on the currently loaded device definition. Content is sorted 
 * lexicographically (case insensitive).
 * 
 * @author Marcus Michalsky
 * @since 1.31
 */
public class DynamicChannelValuesComposite extends ScanModuleViewComposite {
	private static final Logger LOGGER = Logger.getLogger(
			DynamicChannelValuesComposite.class.getName());
	private TableViewer tableViewer;
	
	public DynamicChannelValuesComposite(ScanModuleView parentView, Composite parent, 
			int style) {
		super(parentView, parent, style);
		
		this.setLayout(new GridLayout());
		Label label = new Label(this, SWT.WRAP);
		label.setText("Dynamic channels are determined when the scan is executed. "
				+ "Based on the current device definition a snapshot of the " 
				+ "following channels would be made:");
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		label.setLayoutData(gridData);
		
		tableViewer = new TableViewer(this, SWT.HIDE_SELECTION | SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		tableViewer.getTable().setLayoutData(gridData);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);
		TableViewerColumn nameColumn = new TableViewerColumn(tableViewer, 
				SWT.NONE);
		nameColumn.getColumn().setText("Name");
		nameColumn.getColumn().setWidth(200);
		nameColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((DetectorChannel)element).getName();
			}
		});
		tableViewer.getTable().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.getTable().deselectAll();
			}
		});
		tableViewer.setContentProvider(new ArrayContentProvider());
		this.tableViewer.setSorter(new ViewerSorter() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				DetectorChannel ch1 = (DetectorChannel)e1;
				DetectorChannel ch2 = (DetectorChannel)e2;
				return ch1.getName().toLowerCase().compareTo(
						ch2.getName().toLowerCase());
			}
		});
		this.tableViewer.setFilters(
				new ViewerFilter[] {new SnapshotChannelsSaveValueFilter()});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ScanModuleTypes getType() {
		return ScanModuleTypes.DYNAMIC_CHANNEL_VALUES;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setScanModule(ScanModule scanModule) {
		if (scanModule == null) {
			this.tableViewer.setInput(null);
			return;
		}
		this.tableViewer.setInput(Activator.getDefault().getDeviceDefinition().
				getDetectorChannels().values());
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveState(IMemento memento) {
		// nothing to save for now
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void restoreState(IMemento memento) {
		// nothing to restore for now
	}
	
	private class SnapshotChannelsSaveValueFilter extends ViewerFilter {
		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			DetectorChannel ch = (DetectorChannel) element;
			if (!ch.isSaveValue()) {
				LOGGER.debug("Detector Channel '" + ch.getName() 
					+ "' excluded from Snapshot (saveValue == true)");
			}
			return ch.isSaveValue();
		}
	}
}

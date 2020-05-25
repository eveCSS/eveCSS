package de.ptb.epics.eve.editor.views.axeschannelsview.ui;

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

import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.Activator;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class DynamicAxisPositionsComposite extends AxesChannelsViewComposite {
	private static final Logger LOGGER = Logger.getLogger(
			DynamicAxisPositionsComposite.class.getName());
	private TableViewer tableViewer;
	
	public DynamicAxisPositionsComposite(AxesChannelsView parentView, Composite parent, int style) {
		super(parentView, parent, style);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		this.setLayout(gridLayout);
		
		Label label = new Label(this, SWT.WRAP);
		label.setText("Dynamic axes are determined when the scan is executed. "
				+ "Based on the current device definition a snapshot of the " 
				+ "following axes would be made:");
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
				return ((MotorAxis)element).getName();
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
				MotorAxis m1 = (MotorAxis) e1;
				MotorAxis m2 = (MotorAxis) e2;
				return m1.getName().toLowerCase().compareTo(
						m2.getName().toLowerCase());
			}
		});
		this.tableViewer.setFilters(new ViewerFilter[] { new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				MotorAxis ma = (MotorAxis) element;
				if (LOGGER.isDebugEnabled() && !ma.isSaveValue()) {
					LOGGER.debug("Motor Axis '" + ma.getName() 
					+ "' excluded from Snapshot (saveValue == true)");
				}
				return ma.isSaveValue();
			}
		}});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ScanModuleTypes getType() {
		return ScanModuleTypes.DYNAMIC_AXIS_POSITIONS;
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
				getMotorAxes().values());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
		// nothing to do
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
}

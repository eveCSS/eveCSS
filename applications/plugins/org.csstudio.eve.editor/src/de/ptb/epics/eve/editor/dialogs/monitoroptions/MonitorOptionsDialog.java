package de.ptb.epics.eve.editor.dialogs.monitoroptions;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;

import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.editor.Activator;

/**
 * 
 * @author Hartmut Scherr
 * @since 1.14
 */
public class MonitorOptionsDialog extends Dialog {
	private static Logger LOGGER = 
			Logger.getLogger(MonitorOptionsDialog.class.getName());

	private ScanDescription scanDescription;
	private IMeasuringStation measuringStation;
	
	// tree viewer containing the device definition
	private TreeViewer treeViewer;
	private TreeViewerSelectionChangedListener treeViewerSelectionChangedListener;
	private static final int TREEVIEWER_SASH_WEIGHT = 1;
	
	// table viewer showing options of selected devices
	private TableViewer optionsTable;
	private TableViewerContentProvider optionsTableContentProvider;
	private static final int TABLEVIEWER_SASH_WEIGHT = 2;
	
	// List of abstract devices currently selected in tree viewer
	// (where options are extracted from and shown in table)
	private List<AbstractDevice> tableDevices;
	
	private SelectState selectState;
	
	private Image ascending;
	private Image descending;

	// sorting
	private OptionsTableOptionNameColumnSelectionListener 
			optionsTableOptionNameColumnSelectionListener;
	private OptionsTableDeviceNameColumnSelectionListener 
			optionsTableDeviceNameColumnSelectionListener;
	private OptionColumnComparator optionsTableViewerComparator;
	private DeviceColumnComparator deviceTableViewerComparator;
	private int optionsTableSortState; // 0 no sort, 1 asc, 2 desc
	private int deviceTableSortState; // 0 no sort, 1 asc, 2 desc

	/**
	 * Constructor.
	 * 
	 * @param shell the shell
	 * @param scanDescription the scan description
	 */
	public MonitorOptionsDialog(final Shell shell,
			final ScanDescription scanDescription) {
		super(shell);
		this.scanDescription = scanDescription;
		this.selectState = SelectState.NONE;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Control createDialogArea(final Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);

		SashForm sashForm = new SashForm(area, SWT.HORIZONTAL | SWT.SMOOTH);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true,
				true);
		sashForm.setLayoutData(gridData);
		
		measuringStation = Activator.getDefault().getDeviceDefinition();
		this.tableDevices = new ArrayList<AbstractDevice>();
		
		Composite treeComposite = new Composite(sashForm, SWT.NONE);
		treeComposite.setLayout(new FillLayout());
		this.createTreeViewer(treeComposite);
		
		Composite tableComposite = new Composite(sashForm, SWT.NONE);
		tableComposite.setLayout(new FillLayout());
		this.createTableViewer(tableComposite);
		
		sashForm.setWeights(new int[] {
				MonitorOptionsDialog.TREEVIEWER_SASH_WEIGHT,
				MonitorOptionsDialog.TABLEVIEWER_SASH_WEIGHT });
		
		return area;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(800, 600);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button closeButton = createButton(parent, IDialogConstants.CLOSE_ID,
					IDialogConstants.CLOSE_LABEL, true);
			closeButton.addSelectionListener(new SelectionAdapter() {
				@Override public void widgetSelected(SelectionEvent e) {
					setReturnCode(Window.OK);
					close();
				}
			}
		);
	}
	
	private void createTreeViewer(Composite parent) {
		treeViewer = new TreeViewer(parent);
		treeViewer.setContentProvider(new TreeViewerContentProvider());
		treeViewer.setLabelProvider(new TreeViewerLabelProvider());
		treeViewer.setAutoExpandLevel(1);
		treeViewer.setInput(measuringStation);
	}
	
	private void createTableViewer(Composite parent) {
		this.optionsTable = new TableViewer(parent);
		this.optionsTable.getTable().setHeaderVisible(true);
		this.optionsTable.getTable().setLinesVisible(true);
		this.optionsTableContentProvider = new TableViewerContentProvider();
		this.optionsTable.setContentProvider(optionsTableContentProvider);
		
		createColumns(parent, optionsTable);
		
		this.optionsTable.setInput(this.tableDevices);
		
		// for sorting
		ascending = de.ptb.epics.eve.util.Activator.getDefault()
				.getImageRegistry().get("SORT_ASCENDING");
		descending = de.ptb.epics.eve.util.Activator.getDefault()
				.getImageRegistry().get("SORT_DESCENDING");
		optionsTableSortState = 0;
		deviceTableSortState = 0;
		optionsTableViewerComparator = new OptionColumnComparator();
		deviceTableViewerComparator = new DeviceColumnComparator();

		this.treeViewerSelectionChangedListener =
				new TreeViewerSelectionChangedListener();
		treeViewer.addSelectionChangedListener(
				treeViewerSelectionChangedListener);
	}
	
	/*
	 * helper for createPartControl
	 */
	private void createColumns(final Composite parent, final TableViewer viewer) {
		ColumnViewerToolTipSupport.enableFor(optionsTable, ToolTip.NO_RECREATE);

		final TableViewerColumn selColumn =
				new TableViewerColumn(viewer, SWT.NONE);
		selColumn.getColumn().setWidth(25);
		selColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return "";
			}
			@Override
			public Image getImage(Object element) {
				Option o = (Option) element;
				if (scanDescription.getMonitors().contains(o)) {
					return Activator.getDefault().getImageRegistry()
							.get("CHECKED");
				} else {
					return Activator.getDefault().getImageRegistry()
							.get("UNCHECKED");
				}
			}
		});
		selColumn.getColumn().setAlignment(SWT.CENTER);
		selColumn.setEditingSupport(new SelColumnEditingSupport(
				viewer, scanDescription));
		selColumn.getColumn().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (AbstractDevice device : tableDevices) {
					for (Option o : device.getOptions()) {
						switch (selectState) {
						case NONE:
							if (!scanDescription.getMonitors().contains(o)) {
								scanDescription.addMonitor(o);
							}
							break;
						case ALL:
							if (scanDescription.getMonitors().contains(o)) {
								scanDescription.removeMonitor(o);
							}
							break;
						}
					}
				}
				selectState = SelectState.values()[(selectState.ordinal() + 1) % 2];
				LOGGER.debug("Select State: " + selectState.toString());
				viewer.refresh();
			}
		});
		
		final TableViewerColumn optionViewerColumn = 
				new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn optionColumn = optionViewerColumn.getColumn();
		optionColumn.setText("Option");
		optionColumn.setWidth(120);
		optionColumn.setResizable(true);
		optionViewerColumn.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Option)element).getName();
			}
		});
		optionsTableOptionNameColumnSelectionListener = 
				new OptionsTableOptionNameColumnSelectionListener();
		optionColumn.addSelectionListener(
				optionsTableOptionNameColumnSelectionListener);

		final TableViewerColumn deviceViewerColumn = 
				new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn deviceColumn = deviceViewerColumn.getColumn();
		deviceColumn.setText("Device");
		deviceColumn.setWidth(120);
		deviceColumn.setResizable(true);
		deviceViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Option)element).getParent().getName();
			}
		});
		optionsTableDeviceNameColumnSelectionListener = 
				new OptionsTableDeviceNameColumnSelectionListener();
		deviceColumn.addSelectionListener(
				optionsTableDeviceNameColumnSelectionListener);
	}
	
	/**
	 * <code>TreeViewerSelectionListener</code>.
	 * 
	 * @author Hartmut Scherr
	 * @since 1.14
	 */
	private class TreeViewerSelectionChangedListener implements
			ISelectionChangedListener {
		
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			// clear previous selected devices
			tableDevices.clear();

			ISelection selection = event.getSelection();
			if (!(selection instanceof IStructuredSelection)) {
				return;
			}
			for (Object o : ((IStructuredSelection)selection).toList()) {
				// selections can be classes or abstract devices
				if (o instanceof AbstractDevice) {
					AbstractDevice device = (AbstractDevice)o;
					if (device instanceof Motor) {
						for (MotorAxis axis : ((Motor)device).getAxes()) {
							if (axis.getClassName().isEmpty() && 
									!tableDevices.contains(axis)) {
								tableDevices.add(axis);
							}
						}
					} else if (device instanceof Detector) {
						for (DetectorChannel channel : ((Detector) device)
								.getChannels()) {
							if (channel.getClassName().isEmpty() && 
									!tableDevices.contains(channel)) {
								tableDevices.add(channel);
							}
						}
					}
					if (!tableDevices.contains(device)) {
						tableDevices.add(device);
					}
				} else if (o instanceof String) {
					for (AbstractDevice device : measuringStation
							.getDeviceList((String) o)) {
						if (device instanceof Motor) {
							for (MotorAxis axis : ((Motor)device).getAxes()) {
								if (axis.getClassName().isEmpty() && 
										!tableDevices.contains(axis)) {
									tableDevices.add(axis);
								}
							}
						} else if (device instanceof Detector) {
							for (DetectorChannel channel : ((Detector) device)
									.getChannels()) {
								if (channel.getClassName().isEmpty() && 
										!tableDevices.contains(channel)) {
									tableDevices.add(channel);
								}
							}
						}
						if (!tableDevices.contains(device)) {
							tableDevices.add(device);
						}
					}
				}
			}
			optionsTable.setInput(tableDevices);
			selectState = SelectState.NONE;
		}
	}
	
	/**
	 * 
	 * @author Hartmut Scherr
	 * @since 1.16
	 */
	class OptionsTableOptionNameColumnSelectionListener implements SelectionListener {
		// TODO: extract common functionality to remove redundant code, see #1795
		
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
			LOGGER.debug("option name column clicked");
			LOGGER.debug("old option table sort state: " + optionsTableSortState);
			switch(optionsTableSortState) {
				case 0: // was no sorting -> now ascending
						optionsTableViewerComparator.setDirection(
								OptionColumnComparator.ASCENDING);
						optionsTable.setComparator(optionsTableViewerComparator);
						optionsTable.getTable().getColumn(1).
								setImage(ascending);
						break;
				case 1: // was ascending -> now descending
						optionsTableViewerComparator.setDirection(
								OptionColumnComparator.DESCENDING);
						optionsTable.setComparator(optionsTableViewerComparator);
						optionsTable.refresh();
						optionsTable.getTable().getColumn(1).
								setImage(descending);
						break;
				case 2: // was descending -> now no sorting
						optionsTable.setComparator(null);
						optionsTable.getTable().getColumn(1).setImage(null);
						break;
			}
			// no sorting of device name column
			optionsTable.getTable().getColumn(2).setImage(null);
			deviceTableSortState = 0;
			// set is {0,1,2}
			// if it becomes 3 it has to be 0 again
			// but before the state has to be increased to the new state
			optionsTableSortState = ++optionsTableSortState % 3;
			LOGGER.debug("new options table sort state: " + optionsTableSortState);
		}
	}

	/**
	 * 
	 * @author Hartmut Scherr
	 * @since 1.16
	 */
	class OptionsTableDeviceNameColumnSelectionListener implements SelectionListener {
		// TODO: extract common functionality to remove redundant code, see #1795
		
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
			LOGGER.debug("device name column clicked");
			LOGGER.debug("old device table sort state: " + deviceTableSortState);
			switch(deviceTableSortState) {
				case 0: // was no sorting -> now ascending
						deviceTableViewerComparator.setDirection(
								OptionColumnComparator.ASCENDING);
						optionsTable.setComparator(deviceTableViewerComparator);
						optionsTable.getTable().getColumn(2).setImage(ascending);
						break;
				case 1: // was ascending -> now descending
						deviceTableViewerComparator.setDirection(
								OptionColumnComparator.DESCENDING);
						optionsTable.setComparator(deviceTableViewerComparator);
						optionsTable.refresh();
						optionsTable.getTable().getColumn(2).setImage(descending);
						break;
				case 2: // was descending -> now no sorting
						optionsTable.setComparator(null);
						optionsTable.getTable().getColumn(2).setImage(null);
						break;
			}
			// no sorting of option name column
			optionsTable.getTable().getColumn(1).setImage(null);
			optionsTableSortState = 0;
			// set is {0,1,2}
			// if it becomes 3 it has to be 0 again
			// but before the state has to be increased to the new state
			deviceTableSortState = ++deviceTableSortState % 3;
			LOGGER.debug("new device table sort state: " + deviceTableSortState);
		}
	}
}
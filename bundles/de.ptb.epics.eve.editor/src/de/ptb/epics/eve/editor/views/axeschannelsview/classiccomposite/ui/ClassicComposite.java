package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.ui;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.PositionMode;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.handler.axeschannelsview.RemoveAxesDefaultHandler;
import de.ptb.epics.eve.editor.handler.axeschannelsview.RemoveChannelsDefaultHandler;
import de.ptb.epics.eve.editor.views.DelColumnEditingSupport;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.AxesContentProvider;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.MainAxisColumnLabelProvider;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.MainAxisEditingSupport;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.PositionModeEditingSupport;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.StepfunctionEditingSupport;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.ValuesColumnLabelProvider;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.ValuesEditingSupport;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.AcquisitionTypeEditingSupport;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.ChannelsContentProvider;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.DeferredColumnLabelProvider;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.DeferredEditingSupport;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.NormalizeEditingSupport;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.ParametersColumnLabelProvider;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.ParametersEditingSupport;
import de.ptb.epics.eve.editor.views.axeschannelsview.ui.AxesChannelsView;
import de.ptb.epics.eve.editor.views.axeschannelsview.ui.AxesChannelsViewComposite;
import de.ptb.epics.eve.util.ui.jface.viewercomparator.alphabetical.AlphabeticalTableViewerSorter;
import de.ptb.epics.eve.util.ui.jface.viewercomparator.alphabetical.AlphabeticalViewerComparator;
import de.ptb.epics.eve.util.ui.jface.viewercomparator.alphabetical.SortOrder;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class ClassicComposite extends AxesChannelsViewComposite {
	private static final int TABLE_MIN_HEIGHT = 150;
	
	private static final String MEMENTO_AXES_SASH_WEIGHT = "axesSashWeight";
	private static final String MEMENTO_CHANNELS_SASH_WEIGHT = "channelsSashWeight";
	private static final String MEMENTO_AXES_SORT_ORDER = "axesSortState";
	private static final String MEMENTO_CHANNELS_SORT_ORDER = "channelsSortState";
	
	private ScanModule scanModule;
	private TableViewer axesTable;
	private TableViewer channelsTable;
	
	private AlphabeticalTableViewerSorter axesTableSorter;
	private AlphabeticalTableViewerSorter channelsTableSorter;

	private SashForm sashForm;
	
	public ClassicComposite(AxesChannelsView parentView, Composite parent, int style) {
		super(parentView, parent, style);
		
		this.setLayout(new FillLayout());
		
		ScrolledComposite sc = new ScrolledComposite(this, SWT.V_SCROLL);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		
		sashForm = new SashForm(sc, SWT.VERTICAL);
		sashForm.setLayout(new FillLayout());
		sc.setContent(sashForm);
		
		Composite axesComposite = new Composite(sashForm, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		axesComposite.setLayout(gridLayout);
		Label axesLabel = new Label(axesComposite, SWT.NONE);
		axesLabel.setText("Motor Axes:");
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.LEFT;
		axesLabel.setLayoutData(gridData);
		createAxesTable(axesComposite);
		
		Composite channelsComposite = new Composite(sashForm, SWT.NONE);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		channelsComposite.setLayout(gridLayout);
		Label channelsLabel = new Label(channelsComposite, SWT.NONE);
		channelsLabel.setText("Detector Channels:");
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.LEFT;
		channelsLabel.setLayoutData(gridData);
		createChannelsTable(channelsComposite);
		
		sashForm.setWeights(new int[] {50, 50});
		
		sc.setMinSize(sashForm.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	private void createAxesTable(Composite parent) {
		this.axesTable = new TableViewer(parent, SWT.BORDER | SWT.MULTI);
		axesTable.getTable().setHeaderVisible(true);
		axesTable.getTable().setLinesVisible(true);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.minimumHeight = TABLE_MIN_HEIGHT;
		axesTable.getTable().setLayoutData(gridData);
		
		createAxesTableColumns(axesTable);
		
		axesTable.setContentProvider(new AxesContentProvider());

		axesTable.getTable().addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				getParentView().setSelectionProvider(axesTable);
				super.focusGained(e);
			}
		});
		
		ColumnViewerToolTipSupport.enableFor(axesTable);
		
		this.axesTableSorter = new AlphabeticalTableViewerSorter(axesTable, 
				axesTable.getTable().getColumn(1), // sorry for the "hack"
				new AlphabeticalViewerComparator() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				String axis1Name = ((Axis)e1).getAbstractDevice().getName().
						toLowerCase();
				String axis2Name = ((Axis)e2).getAbstractDevice().getName().
						toLowerCase();
				int result = axis1Name.compareTo(axis2Name);
				if (SortOrder.ASCENDING.equals(getSortOrder())) {
					return result;
				} else if (SortOrder.DESCENDING.equals(getSortOrder())) {
					return result * -1;
				}
				return 0;
			}
		});
		
		// create context menu
		MenuManager menuManager = new MenuManager();
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menuManager.setRemoveAllWhenShown(true);
		axesTable.getTable().setMenu(
				menuManager.createContextMenu(axesTable.getTable()));
		// register menu
		getParentView().getSite().registerContextMenu(
			"de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axestable.popup", 
			menuManager, axesTable);
	}
	
	private void createAxesTableColumns(TableViewer viewer) {
		TableViewerColumn delColumn = new TableViewerColumn(viewer, SWT.NONE);
		delColumn.getColumn().setText("");
		delColumn.getColumn().setWidth(22);
		delColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public Image getImage(Object element) {
				return PlatformUI.getWorkbench().getSharedImages().getImage(
						ISharedImages.IMG_TOOL_DELETE);
			}
		});
		delColumn.setEditingSupport(new DelColumnEditingSupport(viewer, 
				RemoveAxesDefaultHandler.ID));
		
		TableViewerColumn nameColumn = new TableViewerColumn(viewer, SWT.NONE);
		nameColumn.getColumn().setText("Name");
		nameColumn.getColumn().setWidth(220);
		nameColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Axis)element).getAbstractDevice().getName();
			}
		});
		
		TableViewerColumn stepfunctionColumn = new TableViewerColumn(viewer, SWT.NONE);
		stepfunctionColumn.getColumn().setText("Stepfunction");
		stepfunctionColumn.getColumn().setWidth(110);
		stepfunctionColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Axis)element).getStepfunction().toString();
			}
		});
		stepfunctionColumn.setEditingSupport(new StepfunctionEditingSupport(viewer));
		
		TableViewerColumn mainAxisColumn = new TableViewerColumn(viewer, SWT.CENTER);
		mainAxisColumn.getColumn().setText("Main");
		mainAxisColumn.getColumn().setToolTipText("Main Axis");
		mainAxisColumn.getColumn().setWidth(50);
		mainAxisColumn.setLabelProvider(new MainAxisColumnLabelProvider());
		mainAxisColumn.setEditingSupport(new MainAxisEditingSupport(viewer));
		
		TableViewerColumn positionModeColumn = new TableViewerColumn(viewer, SWT.NONE);
		positionModeColumn.getColumn().setText("Mode");
		positionModeColumn.getColumn().setWidth(50);
		positionModeColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (PositionMode.ABSOLUTE.equals(((Axis)element).getPositionMode())) {
					return "abs";
				} else {
					return "rel";
				}
			}
		});
		positionModeColumn.setEditingSupport(new PositionModeEditingSupport(viewer));
		
		TableViewerColumn valuesColumn = new TableViewerColumn(viewer, SWT.NONE);
		valuesColumn.getColumn().setText("Values");
		valuesColumn.getColumn().setWidth(250);
		valuesColumn.setLabelProvider(new ValuesColumnLabelProvider());
		valuesColumn.setEditingSupport(new ValuesEditingSupport(viewer));
		
		TableViewerColumn posCntColumn = new TableViewerColumn(viewer, SWT.NONE);
		posCntColumn.getColumn().setText("# points");
		posCntColumn.getColumn().setWidth(80);
		posCntColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (((Axis)element).getMode().getPositionCount() == null) {
					return "n/a";
				} else {
					return ((Axis)element).getMode().getPositionCount().toString();
				}
			}
		});
	}
	
	private void createChannelsTable(Composite parent) {
		this.channelsTable = new TableViewer(parent, SWT.BORDER | SWT.MULTI);
		channelsTable.getTable().setHeaderVisible(true);
		channelsTable.getTable().setLinesVisible(true);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.minimumHeight = TABLE_MIN_HEIGHT;
		channelsTable.getTable().setLayoutData(gridData);
		
		createChannelsTableColumns(channelsTable);
		
		channelsTable.setContentProvider(new ChannelsContentProvider());
		
		channelsTable.getTable().addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				getParentView().setSelectionProvider(channelsTable);
				super.focusGained(e);
			}
		});
		
		ColumnViewerToolTipSupport.enableFor(channelsTable);
		
		this.channelsTableSorter = new AlphabeticalTableViewerSorter(
				channelsTable,
				channelsTable.getTable().getColumn(1), // sorry for the "hack"
				new AlphabeticalViewerComparator() {
					@Override
					public int compare(Viewer viewer, Object e1, Object e2) {
						String channel1Name = ((Channel)e1).getAbstractDevice().
								getName().toLowerCase();
						String channel2Name = ((Channel)e2).getAbstractDevice().
								getName().toLowerCase();
						int result = channel1Name.compareTo(channel2Name);
						if (SortOrder.ASCENDING.equals(getSortOrder())) {
							return result;
						} else if (SortOrder.DESCENDING.equals(getSortOrder())) {
							return result * -1;
						}
						return 0;
					}
				});
		
		// create context menu
		MenuManager menuManager = new MenuManager();
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menuManager.setRemoveAllWhenShown(true);
		channelsTable.getTable().setMenu(
				menuManager.createContextMenu(channelsTable.getTable()));
		// register menu
		getParentView().getSite().registerContextMenu(
			"de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channelstable.popup", 
			menuManager, channelsTable);
	}
	
	private void createChannelsTableColumns(TableViewer viewer) {
		TableViewerColumn delColumn = new TableViewerColumn(viewer, SWT.NONE);
		delColumn.getColumn().setText("");
		delColumn.getColumn().setWidth(22);
		delColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public Image getImage(Object element) {
				return PlatformUI.getWorkbench().getSharedImages().getImage(
						ISharedImages.IMG_TOOL_DELETE);
			}
		});
		delColumn.setEditingSupport(new DelColumnEditingSupport(viewer, 
				RemoveChannelsDefaultHandler.ID));
		
		TableViewerColumn nameColumn = new TableViewerColumn(viewer, SWT.NONE);
		nameColumn.getColumn().setText("Name");
		nameColumn.getColumn().setWidth(220);
		nameColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public Image getImage(Object element) {
				if (!((Channel)element).getModelErrors().isEmpty()) {
					return PlatformUI.getWorkbench().getSharedImages().
							getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
				}
				return null;
			}
			
			@Override
			public String getText(Object element) {
				return ((Channel)element).getAbstractDevice().getName();
			}
		});
		
		TableViewerColumn acquisitionTypeColumn = new TableViewerColumn(viewer, 
				SWT.NONE);
		acquisitionTypeColumn.getColumn().setText("Acq. Type");
		acquisitionTypeColumn.getColumn().setWidth(80);
		acquisitionTypeColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Channel channel = (Channel)element;
				if (channel.getScanModule().isUsedAsNormalizeChannel(channel)) {
					return "Normalize";
				}
				return ((Channel)element).getChannelMode().toString();
			}
			
			@Override
			public String getToolTipText(Object element) {
				Channel channel = (Channel)element;
				if (channel.getScanModule().isUsedAsNormalizeChannel(channel)) {
					return "used as normalize channel";
				}
				return super.getToolTipText(element);
			}
		});
		acquisitionTypeColumn.setEditingSupport(
				new AcquisitionTypeEditingSupport(viewer));
		
		TableViewerColumn parametersColumn = new TableViewerColumn(viewer, 
				SWT.NONE);
		parametersColumn.getColumn().setText("Parameters");
		parametersColumn.getColumn().setWidth(260);
		ColumnLabelProvider parametersLabelProvider = new ParametersColumnLabelProvider();
		parametersColumn.setLabelProvider(parametersLabelProvider);
		parametersColumn.setEditingSupport(new ParametersEditingSupport(viewer, 
				parametersLabelProvider));
		
		TableViewerColumn deferredColumn = new TableViewerColumn(viewer, 
				SWT.CENTER);
		deferredColumn.getColumn().setText("Deferred");
		deferredColumn.getColumn().setWidth(75);
		deferredColumn.setLabelProvider(new DeferredColumnLabelProvider());
		deferredColumn.setEditingSupport(new DeferredEditingSupport(viewer));
		
		TableViewerColumn normalizeColumn = new TableViewerColumn(viewer, 
				SWT.NONE);
		normalizeColumn.getColumn().setText("Normalized By");
		normalizeColumn.getColumn().setWidth(220);
		normalizeColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Channel channel = (Channel)element;
				if (channel.getNormalizeChannel() != null) {
					return channel.getNormalizeChannel().getName();
				} else {
					return Character.toString('\u2014');
				}
			}
		});
		normalizeColumn.setEditingSupport(new NormalizeEditingSupport(viewer));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ScanModuleTypes getType() {
		return ScanModuleTypes.CLASSIC;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setScanModule(ScanModule scanModule) {
		if (this.scanModule != null) {
			this.scanModule.removeModelUpdateListener(this);
		}
		this.scanModule = scanModule;
		this.axesTable.setInput(scanModule);
		this.channelsTable.setInput(scanModule);
		if (this.scanModule != null) {
			this.scanModule.addModelUpdateListener(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
		this.axesTable.refresh();
		this.channelsTable.refresh();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveState(IMemento memento) {
		// sash weights
		memento.putInteger(MEMENTO_AXES_SASH_WEIGHT, sashForm.getWeights()[0]);
		memento.putInteger(MEMENTO_CHANNELS_SASH_WEIGHT, sashForm.getWeights()[1]);
		
		// table sort states
		memento.putInteger(MEMENTO_AXES_SORT_ORDER, 
				axesTableSorter.getSortOrder().ordinal());
		memento.putInteger(MEMENTO_CHANNELS_SORT_ORDER, 
				channelsTableSorter.getSortOrder().ordinal());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void restoreState(IMemento memento) {
		// restore sash weights
		int[] weights = new int[2];
		weights[0] = (memento.getInteger(MEMENTO_AXES_SASH_WEIGHT) == null) 
				? 1 : memento.getInteger(MEMENTO_AXES_SASH_WEIGHT);
		weights[1] = (memento.getInteger(MEMENTO_CHANNELS_SASH_WEIGHT) == null)
				? 1 : memento.getInteger(MEMENTO_CHANNELS_SASH_WEIGHT);
		this.sashForm.setWeights(weights);
		
		// restore table sort states
		if (memento.getInteger(MEMENTO_AXES_SORT_ORDER) != null) {
			this.axesTableSorter.setSortOrder(SortOrder.values()
					[memento.getInteger(MEMENTO_AXES_SORT_ORDER)]);
		}
		if (memento.getInteger(MEMENTO_CHANNELS_SORT_ORDER) != null) {
			this.channelsTableSorter.setSortOrder(SortOrder.values()
					[memento.getInteger(MEMENTO_CHANNELS_SORT_ORDER)]);
		}
	}
}

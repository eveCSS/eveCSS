package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.ui;

import java.util.List;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.scandescription.AbstractBehavior;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.PositionMode;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.handler.axeschannelsview.RemoveAxesDefaultHandler;
import de.ptb.epics.eve.editor.handler.axeschannelsview.RemoveChannelsDefaultHandler;
import de.ptb.epics.eve.editor.views.DelColumnEditingSupport;
import de.ptb.epics.eve.editor.views.ScanModuleTableDragSourceListener;
import de.ptb.epics.eve.editor.views.ScanModuleTableDropTargetListener;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.ScanModuleSelectionProvider;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.TriggerDelaySettleTimeModelToTargetConverter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.TriggerDelaySettleTimeTargetToModelConverter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.TriggerDelaySettleTimeTargetToModelValidator;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.ValueCountTargetToModelConverter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.ValueCountTargetToModelValidator;
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

	private AxesCAComposite axesCAComposite;
	
	private Text valueCountText;
	private Text triggerDelayText;
	private Text settleTimeText;
	private Button triggerConfirmAxisCheckBox;
	private Button triggerConfirmChannelCheckBox;
	
	public ClassicComposite(AxesChannelsView parentView, Composite parent, int style) {
		super(parentView, parent, style);
		
		this.setLayout(new FillLayout());
		ScrolledComposite sc = new ScrolledComposite(this, SWT.V_SCROLL);
		
		Composite topComposite = new Composite(sc, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		topComposite.setLayout(gridLayout);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setContent(topComposite);
		
		this.createGeneralComposite(topComposite);
		
		sashForm = new SashForm(topComposite, SWT.VERTICAL);
		sashForm.SASH_WIDTH = 4;
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		sashForm.setLayoutData(gridData);
		
		Composite axesComposite = new Composite(sashForm, SWT.BORDER);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		axesComposite.setLayout(gridLayout);
		
		axesCAComposite = new AxesCAComposite(axesComposite, SWT.NONE);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		axesCAComposite.setLayoutData(gridData);
		
		Label axesLabel = new Label(axesComposite, SWT.NONE);
		axesLabel.setText("Motor Axes:");
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.LEFT;
		axesLabel.setLayoutData(gridData);
		this.createAxesTable(axesComposite);
		
		Composite channelsComposite = new Composite(sashForm, SWT.BORDER);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		channelsComposite.setLayout(gridLayout);
		Label channelsLabel = new Label(channelsComposite, SWT.NONE);
		channelsLabel.setText("Detector Channels:");
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.LEFT;
		channelsLabel.setLayoutData(gridData);
		this.createChannelsTable(channelsComposite);
		
		this.initDragAndDrop();
		
		sashForm.setWeights(new int[] {50, 50});
		
		sc.setMinSize(sashForm.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		this.bindValues();
	}
	
	private void createGeneralComposite(Composite parent) {
		Composite generalComposite = new Composite(parent, SWT.BORDER);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		generalComposite.setLayoutData(gridData);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.horizontalSpacing = 15;
		generalComposite.setLayout(gridLayout);
		
		Composite valueCountComposite = new Composite(generalComposite, SWT.NONE);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		valueCountComposite.setLayout(gridLayout);
		Label valueCountLabel = new Label(valueCountComposite, SWT.NONE);
		valueCountLabel.setText("No of Measurements:");
		valueCountLabel.setToolTipText(
				"Number of Measurements taken for each motor position");
		this.valueCountText = new Text(valueCountComposite, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = 7;
		this.valueCountText.setLayoutData(gridData);
		// TODO add focus listener mouse listener focus listener
		
		Composite triggerDelayComposite = new Composite(generalComposite, SWT.NONE);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		triggerDelayComposite.setLayout(gridLayout);
		Label triggerDelayLabel = new Label(triggerDelayComposite, SWT.NONE);
		triggerDelayLabel.setText("Trigger Delay (in s):");
		triggerDelayLabel.setToolTipText("delay in s before detectors are triggered");
		this.triggerDelayText = new Text(triggerDelayComposite, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = 7;
		this.triggerDelayText.setLayoutData(gridData);
		// TODO add focus listener mouse listener focus listener
		
		Composite settleTimeComposite = new Composite(generalComposite, SWT.NONE);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		settleTimeComposite.setLayout(gridLayout);
		Label settleTimeLabel = new Label(settleTimeComposite, SWT.NONE);
		settleTimeLabel.setText("Settle Time (in s):");
		settleTimeLabel.setToolTipText(
				"delay time after first positioning in the scan module");
		this.settleTimeText = new Text(settleTimeComposite, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = 7;
		this.settleTimeText.setLayoutData(gridData);
		// TODO add focus listener mouse listener focus listener
		
		Composite triggerComposite = new Composite(generalComposite, SWT.NONE);
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		triggerComposite.setLayoutData(gridData);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		triggerComposite.setLayout(gridLayout);
		Label triggerLabel = new Label(triggerComposite, SWT.NONE);
		triggerLabel.setText("Manual Trigger:");
		this.triggerConfirmAxisCheckBox = new Button(triggerComposite, 
				SWT.CHECK);
		this.triggerConfirmAxisCheckBox.setText("Motors");
		this.triggerConfirmChannelCheckBox = new Button(triggerComposite, 
				SWT.CHECK);
		this.triggerConfirmChannelCheckBox.setText("Detectors");
		// TODO add Focus listener
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
		
		axesTable.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if (!(selection instanceof IStructuredSelection) || 
						((IStructuredSelection)selection).size() != 1) {
					return;
				}
				Object o = ((IStructuredSelection)selection).getFirstElement();
				if (o instanceof Axis) {
					axesCAComposite.setAxis((Axis)o);
				}
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
			public String getToolTipText(Object element) {
				if  (!((Channel)element).getModelErrors().isEmpty()) {
					StringBuilder sb = new StringBuilder();
					for (IModelError error : ((Channel)element).getModelErrors()) {
						sb.append(error.getErrorMessage() + "\n");
					}
					// cut last line break
					return sb.toString().substring(0, sb.toString().length()-1);
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

	private void bindValues() {
		DataBindingContext context = new DataBindingContext();
		ISelectionProvider selectionProvider = new ScanModuleSelectionProvider(
				ScanModuleTypes.CLASSIC);
		IObservableValue selectionObservable = ViewersObservables.
				observeSingleSelection(selectionProvider);
		
		IObservableValue valueCountTargetObservable = SWTObservables.
				observeText(this.valueCountText, SWT.Modify);
		IObservableValue valueCountModelObservable = BeansObservables.
				observeDetailValue(selectionObservable, ScanModule.class, 
						ScanModule.VALUE_COUNT_PROP, Integer.class);
		UpdateValueStrategy valueCountTargetToModelStrategy = 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).
				setAfterGetValidator(new ValueCountTargetToModelValidator()).
				setConverter(new ValueCountTargetToModelConverter());
		Binding valueCountBinding = context.bindValue(
				valueCountTargetObservable, valueCountModelObservable, 
				valueCountTargetToModelStrategy, 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));
		ControlDecorationSupport.create(valueCountBinding, SWT.LEFT);
		
		IObservableValue triggerDelayTargetObservable = SWTObservables.
				observeText(this.triggerDelayText, SWT.Modify);
		IObservableValue triggerDelayModelObservable = BeansObservables.
				observeDetailValue(selectionObservable, ScanModule.class, 
						ScanModule.TRIGGER_DELAY_PROP, Double.class);
		UpdateValueStrategy triggerDelayTargetToModelStrategy = 
			new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).
			setAfterGetValidator(
				new TriggerDelaySettleTimeTargetToModelValidator("Trigger Delay")).
			setConverter(new TriggerDelaySettleTimeTargetToModelConverter());
		UpdateValueStrategy triggerDelayModelToTargetStrategy = 
			new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).
			setConverter(new TriggerDelaySettleTimeModelToTargetConverter());
		Binding triggerDelayBinding = context.bindValue(
				triggerDelayTargetObservable, triggerDelayModelObservable, 
				triggerDelayTargetToModelStrategy, 
				triggerDelayModelToTargetStrategy);
		ControlDecorationSupport.create(triggerDelayBinding, SWT.LEFT);
		
		IObservableValue settleTimeTargetObservable = SWTObservables.
				observeText(this.settleTimeText, SWT.Modify);
		IObservableValue settleTimeModelObservable = BeansObservables.
				observeDetailValue(selectionObservable, ScanModule.class, 
						ScanModule.SETTLE_TIME_PROP, Double.class);
		UpdateValueStrategy settleTimeTargetToModelStrategy = 
			new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).
			setAfterGetValidator(
				new TriggerDelaySettleTimeTargetToModelValidator("Settle Time")).
			setConverter(new TriggerDelaySettleTimeTargetToModelConverter());
		UpdateValueStrategy settleTimeModelToTargetStrategy = 
			new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).
			setConverter(new TriggerDelaySettleTimeModelToTargetConverter());
		Binding settleTimeBinding = context.bindValue(settleTimeTargetObservable, 
				settleTimeModelObservable, settleTimeTargetToModelStrategy, 
				settleTimeModelToTargetStrategy);
		ControlDecorationSupport.create(settleTimeBinding, SWT.LEFT);
		
		IObservableValue axisTriggerTargetObservable = SWTObservables.
				observeSelection(this.triggerConfirmAxisCheckBox);
		IObservableValue axisTriggerModelObservable = BeansObservables.
				observeDetailValue(selectionObservable, ScanModule.class, 
						ScanModule.TRIGGER_CONFIRM_AXIS_PROP, Boolean.class);
		context.bindValue(axisTriggerTargetObservable, 
				axisTriggerModelObservable, 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE), 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));
		
		IObservableValue channelTriggerTargetObservable = SWTObservables.
				observeSelection(this.triggerConfirmChannelCheckBox);
		IObservableValue channelTriggerModelObservable = BeansObservables.
				observeDetailValue(selectionObservable, ScanModule.class, 
						ScanModule.TRIGGER_CONFIRM_CHANNEL_PROP, Boolean.class);
		context.bindValue(channelTriggerTargetObservable, 
				channelTriggerModelObservable, 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE), 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));
	}
	
	private void initDragAndDrop() {
		Transfer[] axesTransfers = new Transfer[] {TextTransfer.getInstance()};
		this.axesTable.addDragSupport(DND.DROP_MOVE, axesTransfers, 
				new ScanModuleTableDragSourceListener(this.axesTable));
		this.axesTable.addDropSupport(DND.DROP_MOVE, axesTransfers, 
				new ScanModuleTableDropTargetListener(this.axesTable) {
			@Override
			public List<? extends AbstractBehavior> getModel() {
				return scanModule.getAxesList();
			}
		});
		Transfer[] channelsTransfers = new Transfer[] {TextTransfer.getInstance()};
		this.channelsTable.addDragSupport(DND.DROP_MOVE, channelsTransfers, 
				new ScanModuleTableDragSourceListener(this.channelsTable));
		this.channelsTable.addDropSupport(DND.DROP_MOVE, channelsTransfers, 
				new ScanModuleTableDropTargetListener(this.channelsTable) {
			@Override
			public List<? extends AbstractBehavior> getModel() {
				return scanModule.getChannelList();
			}
		});
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
			this.axesCAComposite.setAxis(null);
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

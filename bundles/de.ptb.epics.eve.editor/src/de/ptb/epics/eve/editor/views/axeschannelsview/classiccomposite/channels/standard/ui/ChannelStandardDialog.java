package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.ui;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.IMenuService;

import de.ptb.epics.eve.data.ComparisonTypes;
import de.ptb.epics.eve.data.measuringstation.event.MonitorEvent;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.data.scandescription.channelmode.StandardMode;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.views.DialogCellEditorDialog;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.AverageTargetToModelConverter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.AverageTargetToModelValidator;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.ChannelRedoEventMenuContributionMonitor;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.DeleteColumnEditingSupport;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.DoubleModelToTargetConverter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.DoubleTargetToModelConverter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.LimitEditingSupport;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.MaxAttemptsModelToTargetConverter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.MaxAttemptsTargetToModelConverter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.MaxAttemptsTargetToModelValidator;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.MaxDeviationTargetToModelValidator;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.MinimumTargetToModelValidator;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.OperatorEditingSupport;
import de.ptb.epics.eve.util.ui.swt.TextSelectAllFocusListener;
import de.ptb.epics.eve.util.ui.swt.TextSelectAllMouseListener;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class ChannelStandardDialog extends DialogCellEditorDialog 
		implements IModelUpdateListener {
	
	private static final String TOOLTIP_AVERAGE = "Determines how many " +
		"channel readings are taken to calculate the average. ";
	private static final String TOOLTIP_MAX_DEVIATION = "The deviation of " +
		"the first two measured values must be less or equal then the given " +
		"value in percent to be valid. If that constraint ist not fulfilled, " +
		"the first measured value is discarded and a new value is taken " +
		"until the condition is met.";
	private static final String TOOLTIP_MINIMUM = "If the first measured " +
		"value (absolute) is greater than or equal the given value a " +
		"tolerance check is conducted. ";
	private static final String TOOLTIP_MAX_ATTEMPTS = "If the tolerance " +
		"check failed the given value amount of times, the check is " +
		"stopped an the next values are used for the average " +
		"calculation. If Max. Attempts = 0, no tolerance check is conducted.";
	private static final String TOOLTIP_REDO_EVENTS = "Events which when " +
		"occurring restart the channel calculation are defined here.";
	
	/*
	 * indentation used for text input layout to leave space for decorators
	 */
	private static final int TEXT_INDENTATION = 7;
	private static final String LONG_DASH = Character.toString('\u2014');
	
	private Channel channel;

	private Text averageText;
	private Text maxDeviationText;
	private Text minimumText;
	private Text maxAttemptsText;

	private DataBindingContext context;
	private Binding averageBinding;
	private Binding maxDeviationBinding;
	private Binding minimumBinding;
	private Binding maxAttemptsBinding;
	private TableViewer eventsTable;
	
	private MenuManager menuManager;
	
	public ChannelStandardDialog(Shell shell, Control control, Channel channel) {
		super(shell, control);
		this.channel = channel;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 5;
		gridLayout.marginHeight = 5;
		gridLayout.numColumns = 2;
		composite.setLayout(gridLayout);
		GridData gridData = new GridData();
		gridData.minimumWidth = 250;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		composite.setLayoutData(gridData);

		Label averageLabel = new Label(composite, SWT.NONE);
		averageLabel.setText("Average:");
		averageLabel.setToolTipText(TOOLTIP_AVERAGE);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		averageLabel.setLayoutData(gridData);

		averageText = new Text(composite, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = TEXT_INDENTATION;
		gridData.grabExcessHorizontalSpace = true;
		averageText.setLayoutData(gridData);
		averageText.addFocusListener(new TextSelectAllFocusListener(averageText));
		averageText.addMouseListener(new TextSelectAllMouseListener(averageText));
		averageText.addFocusListener(new TextFocusListener(averageText));

		Label maxDeviationLabel = new Label(composite, SWT.NONE);
		maxDeviationLabel.setText("Max. Deviation (%):");
		maxDeviationLabel.setToolTipText(TOOLTIP_MAX_DEVIATION);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		maxDeviationLabel.setLayoutData(gridData);

		maxDeviationText = new Text(composite, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = TEXT_INDENTATION;
		gridData.grabExcessHorizontalSpace = true;
		maxDeviationText.setLayoutData(gridData);
		maxDeviationText.addFocusListener(
				new TextSelectAllFocusListener(maxDeviationText));
		maxDeviationText.addMouseListener(
				new TextSelectAllMouseListener(maxDeviationText));
		maxDeviationText.addFocusListener(
				new TextFocusListener(maxDeviationText));

		Label minimumLabel = new Label(composite, SWT.NONE);
		minimumLabel.setText("Minimum:");
		minimumLabel.setToolTipText(TOOLTIP_MINIMUM);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		minimumLabel.setLayoutData(gridData);

		minimumText = new Text(composite, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = TEXT_INDENTATION;
		gridData.grabExcessHorizontalSpace = true;
		minimumText.setLayoutData(gridData);
		minimumText.addFocusListener(new TextSelectAllFocusListener(minimumText));
		minimumText.addMouseListener(new TextSelectAllMouseListener(minimumText));
		minimumText.addFocusListener(new TextFocusListener(minimumText));

		Label maxAttemptsLabel = new Label(composite, SWT.NONE);
		maxAttemptsLabel.setText("Max. Attempts:");
		maxAttemptsLabel.setToolTipText(TOOLTIP_MAX_ATTEMPTS);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		maxAttemptsLabel.setLayoutData(gridData);

		maxAttemptsText = new Text(composite, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = TEXT_INDENTATION;
		gridData.grabExcessHorizontalSpace = true;
		maxAttemptsText.setLayoutData(gridData);
		maxAttemptsText.addFocusListener(
				new TextSelectAllFocusListener(maxAttemptsText));
		maxAttemptsText.addMouseListener(
				new TextSelectAllMouseListener(maxAttemptsText));
		maxAttemptsText.addFocusListener(new TextFocusListener(maxAttemptsText));

		Label redoEventsLabel = new Label(composite, SWT.NONE);
		redoEventsLabel.setText("Redo Events:");
		redoEventsLabel.setToolTipText(TOOLTIP_REDO_EVENTS);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = SWT.LEFT;
		redoEventsLabel.setLayoutData(gridData);

		this.createRedoEventsTable(composite);
		
		this.createBinding();
		return composite;
	}

	private void createRedoEventsTable(Composite parent) {
		eventsTable = new TableViewer(parent, SWT.BORDER);
		eventsTable.getTable().setHeaderVisible(true);
		eventsTable.getTable().setLinesVisible(true);
		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.minimumHeight = 120;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		eventsTable.getTable().setLayoutData(gridData);
		this.createRedoEventsTableColumns(eventsTable);
		eventsTable.setContentProvider(new ArrayContentProvider());
		eventsTable.setInput(this.channel.getRedoEvents());
		this.channel.getRedoControlEventManager().addModelUpdateListener(this);
		
		this.createMenu(eventsTable);
	}
	
	private void createRedoEventsTableColumns(TableViewer viewer) {
		TableViewerColumn deleteColumn = new TableViewerColumn(viewer, SWT.CENTER);
		deleteColumn.getColumn().setText("");
		deleteColumn.getColumn().setWidth(22);
		deleteColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public Image getImage(Object element) {
				return PlatformUI.getWorkbench().getSharedImages().
						getImage(ISharedImages.IMG_TOOL_DELETE);
			}
			
			@Override
			public String getText(Object element) {
				return "";
			}
		});
		deleteColumn.setEditingSupport(new DeleteColumnEditingSupport(viewer, 
				this.channel));
		
		TableViewerColumn sourceColumn = new TableViewerColumn(viewer, SWT.LEFT);
		sourceColumn.getColumn().setText("Source");
		sourceColumn.getColumn().setWidth(280);
		sourceColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((ControlEvent)element).getEvent().getName();
			}
		});
		
		TableViewerColumn operatorColumn = new TableViewerColumn(viewer, SWT.LEFT);
		operatorColumn.getColumn().setText("Operator");
		operatorColumn.getColumn().setWidth(80);
		operatorColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ControlEvent controlEvent = (ControlEvent)element;
				if (controlEvent.getEvent() instanceof MonitorEvent) {
					// TODO return Unicode Symbols ?
					return ComparisonTypes.typeToString(
							controlEvent.getLimit().getComparison());
				} else {
					return LONG_DASH;
				}
			}
		});
		operatorColumn.setEditingSupport(new OperatorEditingSupport(viewer));
		
		TableViewerColumn limitColumn = new TableViewerColumn(viewer, SWT.LEFT);
		limitColumn.getColumn().setText("Limit");
		limitColumn.getColumn().setWidth(60);
		limitColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public Image getImage(Object element) {
				if (!((ControlEvent)element).getModelErrors().isEmpty()) {
					return PlatformUI.getWorkbench().getSharedImages().
							getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
				}
				return null;
			}
			
			@Override
			public String getText(Object element) {
				ControlEvent controlEvent = (ControlEvent)element;
				if (controlEvent.getEvent() instanceof MonitorEvent) {
					return controlEvent.getLimit().getValue();
				} else {
					return LONG_DASH;
				}
			}
		});
		limitColumn.setEditingSupport(new LimitEditingSupport(viewer));
	}
	
	private void createMenu(TableViewer viewer) {
		menuManager = new MenuManager();
		
		MenuManager monitorEventsMenu = new MenuManager("Monitor Events", 
				Activator.getDefault().getImageRegistry().getDescriptor(
						"MONITOREVENTS"), "monitorEventsMenu");
		for (IContributionItem item : new ChannelRedoEventMenuContributionMonitor(channel).getContributionItems()) {
			monitorEventsMenu.add(item);
		}
		menuManager.add(monitorEventsMenu);
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		//menuManager.setRemoveAllWhenShown(true);
		
		IMenuService menuService = (IMenuService) PlatformUI.getWorkbench().
				getService(IMenuService.class);
		menuService.populateContributionManager(menuManager, 
				"popup:de.ptb.epics.eve.editor.dialog.channelstandarddialog.redoevents.popup");
		menuManager.update();
		
		viewer.getControl().setMenu(menuManager.createContextMenu(viewer.getControl()));
	}
	
	private void createBinding() {
		context = new DataBindingContext();

		IObservableValue averageTargetObservable = 
				WidgetProperties.text(SWT.Modify).observe(this.averageText);
		IObservableValue averageModelObservable = 
				BeanProperties.value(StandardMode.AVERAGE_COUNT_PROP, 
						Integer.class).observe(this.channel);
		averageBinding = context.bindValue(
				averageTargetObservable, averageModelObservable, 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).
					setConverter(new AverageTargetToModelConverter()).
					setAfterGetValidator(new AverageTargetToModelValidator()),
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));
		ControlDecorationSupport.create(averageBinding, SWT.LEFT);

		IObservableValue maxDeviationTargetObservable =
				WidgetProperties.text(SWT.Modify).observe(this.maxDeviationText);
		IObservableValue maxDeviationModelObservable = 
				BeanProperties.value(StandardMode.MAX_DEVIATION_PROP, 
						Double.class).observe(this.channel);
		maxDeviationBinding = context.bindValue(
				maxDeviationTargetObservable, maxDeviationModelObservable, 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).
					setConverter(new DoubleTargetToModelConverter()).
					setAfterGetValidator(new MaxDeviationTargetToModelValidator()),
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).
					setConverter(new DoubleModelToTargetConverter()));
		ControlDecorationSupport.create(maxDeviationBinding, SWT.LEFT);
		
		IObservableValue minimumTargetObservable = 
				WidgetProperties.text(SWT.Modify).observe(this.minimumText);
		IObservableValue minimumModelObservable = BeanProperties.value(
				StandardMode.MINIMUM_PROP, Double.class).observe(this.channel);
		minimumBinding = context.bindValue(
				minimumTargetObservable, minimumModelObservable, 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).
					setConverter(new DoubleTargetToModelConverter()).
					setAfterGetValidator(new MinimumTargetToModelValidator()),
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).
					setConverter(new DoubleModelToTargetConverter()));
		ControlDecorationSupport.create(minimumBinding, SWT.LEFT);
		
		IObservableValue maxAttemptsTargetObservable = 
				WidgetProperties.text(SWT.Modify).observe(this.maxAttemptsText);
		IObservableValue maxAttemptsModelObservable = BeanProperties.value(
				StandardMode.MAX_ATTEMPTS_PROP, Integer.class).observe(this.channel);
		maxAttemptsBinding = context.bindValue(
				maxAttemptsTargetObservable, maxAttemptsModelObservable, 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).
					setConverter(new MaxAttemptsTargetToModelConverter()).
					setAfterGetValidator(new MaxAttemptsTargetToModelValidator()),
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).
				setConverter(new MaxAttemptsModelToTargetConverter()));
		ControlDecorationSupport.create(maxAttemptsBinding, SWT.LEFT);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean close() {
		((IMenuService) PlatformUI.getWorkbench().
			getService(IMenuService.class)).releaseContributions(menuManager);
		this.channel.getRedoControlEventManager().removeModelUpdateListener(this);
		this.context.dispose();
		return super.close();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
		this.eventsTable.refresh();
	}
	
	private class TextFocusListener extends FocusAdapter {
		private Text widget;
		
		public TextFocusListener(Text widget) {
			this.widget = widget;
		}
		
		@Override
		public void focusLost(FocusEvent e) {
			if (widget == averageText) {
				averageText.setSelection(0, 0);
				averageBinding.updateModelToTarget();
			} else if (widget == maxDeviationText) {
				maxDeviationText.setSelection(0, 0);
				maxDeviationBinding.updateModelToTarget();
			} else if (widget == minimumText) {
				minimumText.setSelection(0, 0);
				minimumBinding.updateModelToTarget();
			} else if (widget == maxAttemptsText) {
				maxAttemptsText.setSelection(0, 0);
				maxAttemptsBinding.updateModelToTarget();
			}
		}
	}
}

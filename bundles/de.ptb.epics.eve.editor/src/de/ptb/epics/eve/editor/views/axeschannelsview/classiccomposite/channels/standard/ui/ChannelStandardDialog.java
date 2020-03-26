package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.ui;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
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
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.ComparisonTypes;
import de.ptb.epics.eve.data.measuringstation.event.MonitorEvent;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.data.scandescription.channelmode.StandardMode;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.AverageTargetToModelConverter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.AverageTargetToModelValidator;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.DoubleModelToTargetConverter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.DoubleTargetToModelConverter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.LimitEditingSupport;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.MaxAttemptsModelToTargetConverter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.MaxAttemptsTargetToModelConverter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.MaxAttemptsTargetToModelValidator;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.MaxDeviationTargetToModelValidator;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.MinimumTargetToModelValidator;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.OperatorEditingSupport;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.ui.DialogCellEditorDialog;
import de.ptb.epics.eve.util.ui.swt.TextSelectAllFocusListener;
import de.ptb.epics.eve.util.ui.swt.TextSelectAllMouseListener;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class ChannelStandardDialog extends DialogCellEditorDialog 
		implements IModelUpdateListener {
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

	private Binding averageBinding;
	private Binding maxDeviationBinding;
	private Binding minimumBinding;
	private Binding maxAttemptsBinding;
	private TableViewer eventsTable;
	
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
		minimumLabel.setToolTipText("for values < minimum no deviation check");
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
		maxAttemptsLabel.setToolTipText("Maximum attempts to calculate deviation");
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
	
	private void createBinding() {
		DataBindingContext context = new DataBindingContext();

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
		this.channel.getRedoControlEventManager().removeModelUpdateListener(this);
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

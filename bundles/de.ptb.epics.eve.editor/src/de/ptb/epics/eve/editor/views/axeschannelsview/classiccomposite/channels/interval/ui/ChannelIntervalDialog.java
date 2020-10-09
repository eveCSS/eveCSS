package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.interval.ui;

import java.util.List;
import java.util.Locale;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.conversion.NumberToStringConverter;
import org.eclipse.core.databinding.conversion.StringToNumberConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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

import com.ibm.icu.text.NumberFormat;

import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.channelmode.IntervalMode;
import de.ptb.epics.eve.editor.views.DialogCellEditorDialog;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.interval.StoppedByModelToTargetConverter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.interval.StoppedByTargetToModelConverter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.interval.TriggerIntervalTargetToModelValidator;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class ChannelIntervalDialog extends DialogCellEditorDialog {
	
	private static final String TOOLTIP_TRIGGER_INTERVAL = 
			"The interval/rate measurements are taken in seconds.";
	private static final String TOOLTIP_STOPPED_BY = 
			"The channel which when finished stops the mean calculation.";
	
	/*
	 * indentation used for text input layout to leave space for decorators
	 */
	private static final int TEXT_INDENTATION = 7;
	
	private Channel channel;

	private Text triggerIntervalText;
	private ComboViewer stoppedByComboViewer;
	
	private final Image errorImage;

	private Binding triggerIntervalBinding;
	
	public ChannelIntervalDialog(Shell shell, Control control, Channel channel) {
		super(shell, control);
		this.channel = channel;
		this.errorImage = FieldDecorationRegistry.getDefault().
			getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage();
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

		Label triggerIntervalLabel = new Label(composite, SWT.NONE);
		triggerIntervalLabel.setText("Trigger Interval:");
		triggerIntervalLabel.setToolTipText(TOOLTIP_TRIGGER_INTERVAL);
		
		triggerIntervalText = new Text(composite, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = TEXT_INDENTATION;
		gridData.grabExcessHorizontalSpace = true;
		triggerIntervalText.setLayoutData(gridData);
		triggerIntervalText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				triggerIntervalBinding.updateModelToTarget();
			}
		});
		
		Label stoppedByLabel = new Label(composite, SWT.NONE);
		stoppedByLabel.setText("Stopped By:");
		stoppedByLabel.setToolTipText(TOOLTIP_STOPPED_BY);
		
		stoppedByComboViewer = new ComboViewer(composite, 
				SWT.BORDER | SWT.READ_ONLY);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = TEXT_INDENTATION;
		gridData.grabExcessHorizontalSpace = true;
		stoppedByComboViewer.getCombo().setLayoutData(gridData);
		stoppedByComboViewer.setContentProvider(
				ArrayContentProvider.getInstance());
		stoppedByComboViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element == null) {
					return "";
				}
				return ((Channel)element).getAbstractDevice().getName();
			}
		});
		List<Channel> entries = this.channel.getScanModule().
				getValidStoppedByChannels(channel);
		stoppedByComboViewer.setInput(entries);
		if (!entries.isEmpty()) {
			this.stoppedByComboViewer.insert(null, 0);
		}
		final ControlDecoration stoppedByDecoration = new ControlDecoration(
				stoppedByComboViewer.getCombo(), SWT.LEFT);
		stoppedByDecoration.setDescriptionText("Stopped By is mandatory!");
		stoppedByDecoration.setImage(errorImage);
		//initial state
		if (channel.getStoppedBy() == null) {
			stoppedByDecoration.show();
		} else {
			stoppedByDecoration.hide();
		}
		// changed state if user selects entry
		stoppedByComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (channel.getStoppedBy() == null) {
					stoppedByDecoration.show();
				} else {
					stoppedByDecoration.hide();
				}
			}
		});
		
		this.createBinding();
		return composite;
	}
	
	private void createBinding() {
		DataBindingContext context = new DataBindingContext();
		
		IObservableValue triggerIntervalTargetObservable = 
				WidgetProperties.text(SWT.Modify).observe(this.triggerIntervalText);
		IObservableValue triggerIntervalModelObservable = 
				BeanProperties.value(IntervalMode.TRIGGER_INTERVAL_PROP).
					observe(this.channel);
		triggerIntervalBinding = context.bindValue(
				triggerIntervalTargetObservable, 
				triggerIntervalModelObservable, 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).
					setConverter(StringToNumberConverter.toDouble(
							NumberFormat.getInstance(Locale.US), false)).
					setAfterGetValidator(new TriggerIntervalTargetToModelValidator()),
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).
					setConverter(NumberToStringConverter.fromDouble(
							NumberFormat.getInstance(Locale.US), false)));
		ControlDecorationSupport.create(triggerIntervalBinding, SWT.LEFT);
		
		IObservableValue stoppedByTargetObservable = WidgetProperties.
				selection().observe(this.stoppedByComboViewer.getCombo());
		IObservableValue stoppedByModelObservable = 
				BeanProperties.value(IntervalMode.STOPPED_BY_PROP).
					observe(this.channel);
		context.bindValue(stoppedByTargetObservable, stoppedByModelObservable,
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).
					setConverter(new StoppedByTargetToModelConverter(channel)),
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).
					setConverter(new StoppedByModelToTargetConverter()));
	}
}

package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.interval.ui;

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
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
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
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.ui.DialogCellEditorDialog;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class ChannelIntervalDialog extends DialogCellEditorDialog {
	/*
	 * indentation used for text input layout to leave space for decorators
	 */
	private static final int TEXT_INDENTATION = 7;
	
	private Channel channel;

	private Text triggerIntervalText;

	private ComboViewer stoppedByComboViewer;
	
	public ChannelIntervalDialog(Shell shell, Control control, Channel channel) {
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

		Label triggerIntervalLabel = new Label(composite, SWT.NONE);
		triggerIntervalLabel.setText("Trigger Interval:");
		
		triggerIntervalText = new Text(composite, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = TEXT_INDENTATION;
		gridData.grabExcessHorizontalSpace = true;
		triggerIntervalText.setLayoutData(gridData);
		
		Label stoppedByLabel = new Label(composite, SWT.NONE);
		stoppedByLabel.setText("Stopped By:");
		
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
		// TODO add SelectionListener
		
		this.createBinding();
		
		// TODO Auto-generated method stub
		return composite;
	}
	
	private void createBinding() {
		DataBindingContext context = new DataBindingContext();
		
		IObservableValue triggerIntervalTargetObservable = 
				WidgetProperties.text(SWT.Modify).observe(this.triggerIntervalText);
		IObservableValue triggerIntervalModelObservable = 
				BeanProperties.value(IntervalMode.TRIGGER_INTERVAL_PROP).
					observe(this.channel);
		Binding triggerIntervalBinding = context.bindValue(
				triggerIntervalTargetObservable, 
				triggerIntervalModelObservable, 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).
					setConverter(StringToNumberConverter.toDouble(
							NumberFormat.getInstance(Locale.US), false)),
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).
					setConverter(NumberToStringConverter.fromDouble(
							NumberFormat.getInstance(Locale.US), false)));
		ControlDecorationSupport.create(triggerIntervalBinding, SWT.LEFT);
	}
}

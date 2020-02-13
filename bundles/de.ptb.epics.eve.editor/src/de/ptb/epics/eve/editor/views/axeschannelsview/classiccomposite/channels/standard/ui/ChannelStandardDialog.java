package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.ui;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.channelmode.StandardMode;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.AverageTargetToModelConverter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.AverageTargetToModelValidator;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.DoubleModelToTargetConverter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.DoubleTargetToModelConverter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.MaxAttemptsModelToTargetConverter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.MaxAttemptsTargetToModelConverter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.MaxAttemptsTargetToModelValidator;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.MaxDeviationTargetToModelValidator;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.MinimumTargetToModelValidator;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.ui.DialogCellEditorDialog;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class ChannelStandardDialog extends DialogCellEditorDialog {
	/*
	 * indentation used for text input layout to leave space for decorators
	 */
	private static final int TEXT_INDENTATION = 7;
	
	private Channel channel;

	private Text averageText;

	private Text maxDeviationText;

	private Text minimumText;

	private Text maxAttemptsText;
	
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
		// TODO add Focus, Mouse and Focus Listener ?

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
		// TODO Focus, Mouse, Focus Listener

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
		// TODO Focus, Mouse, Focus

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
		// TODO Focus, Mouse, Focus

		Label redoEventsLabel = new Label(composite, SWT.NONE);
		redoEventsLabel.setText("Redo Events:");
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = SWT.LEFT;
		redoEventsLabel.setLayoutData(gridData);

		// TODO Event composite benötigt Parent View ?!?
		
		this.createBinding();
		
		// TODO Auto-generated method stub
		return composite;
	}
	
	private void createBinding() {
		DataBindingContext context = new DataBindingContext();

		IObservableValue averageTargetObservable = 
				WidgetProperties.text(SWT.Modify).observe(this.averageText);
		IObservableValue averageModelObservable = 
				BeanProperties.value(StandardMode.AVERAGE_COUNT_PROP, 
						Integer.class).observe(this.channel);
		Binding averageBinding = context.bindValue(
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
		Binding maxDeviationBinding = context.bindValue(
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
		Binding minimumBinding = context.bindValue(
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
		Binding maxAttemptsBinding = context.bindValue(
				maxAttemptsTargetObservable, maxAttemptsModelObservable, 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).
					setConverter(new MaxAttemptsTargetToModelConverter()).
					setAfterGetValidator(new MaxAttemptsTargetToModelValidator()),
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).
				setConverter(new MaxAttemptsModelToTargetConverter()));
		ControlDecorationSupport.create(maxAttemptsBinding, SWT.LEFT);
	}
}

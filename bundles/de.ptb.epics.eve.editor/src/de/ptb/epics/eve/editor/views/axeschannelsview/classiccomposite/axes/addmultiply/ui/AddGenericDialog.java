package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

import javax.xml.datatype.Duration;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.SelectObservableValue;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.PositionMode;
import de.ptb.epics.eve.data.scandescription.axismode.AddMultiplyMode;
import de.ptb.epics.eve.data.scandescription.axismode.AdjustParameter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.AddDoubleModelToTargetConverter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.AddDoubleModelToTargetValidator;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.AddDoubleTargetToModelConverter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.AddDoubleTargetToModelValidator;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.ui.DialogCellEditorDialog;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public abstract class AddGenericDialog extends DialogCellEditorDialog implements PropertyChangeListener {
	private Axis axis;
	private AddMultiplyMode<?> addMultiplyMode;
	
	private Button startRadioButton;
	private Text startText;
	private Button stopRadioButton;
	private Text stopText;
	private Button stepwidthRadioButton;
	private Text stepwidthText;
	private Button stepcountRadioButton;
	private Text stepcountText;
	
	DataBindingContext context;
	
	@SuppressWarnings("unchecked")
	public AddGenericDialog(Shell shell, Control control, Axis axis) {
		super(shell, control);
		this.axis = axis;
		
		switch(axis.getType()) {
		case DATETIME:
			if (PositionMode.ABSOLUTE.equals(axis.getPositionMode())) {
				this.addMultiplyMode = (AddMultiplyMode<Date>)axis.getMode();
			} else if (PositionMode.RELATIVE.equals(axis.getPositionMode())) {
				this.addMultiplyMode = (AddMultiplyMode<Duration>)axis.getMode();
			}
			break;
		case DOUBLE:
			this.addMultiplyMode = (AddMultiplyMode<Double>)axis.getMode();
			break;
		case INT:
			this.addMultiplyMode = (AddMultiplyMode<Integer>)axis.getMode();
			break;
		default:
			break;
		}
		this.addMultiplyMode.addPropertyChangeListener(
				AddMultiplyMode.ADJUST_PARAMETER_PROP, this);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		composite.setLayout(gridLayout);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.minimumWidth = 350;
		composite.setLayoutData(gridData);
		
		startRadioButton = new Button(composite, SWT.RADIO);
		startRadioButton.setText("Start:");
		startText = new Text(composite, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = 7;
		gridData.grabExcessHorizontalSpace = true;
		startText.setLayoutData(gridData);

		stopRadioButton = new Button(composite, SWT.RADIO);
		stopRadioButton.setText("Stop:");
		stopText = new Text(composite, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = 7;
		gridData.grabExcessHorizontalSpace = true;
		stopText.setLayoutData(gridData);

		stepwidthRadioButton = new Button(composite, SWT.RADIO);
		stepwidthRadioButton.setText("Stepwidth:");
		stepwidthText = new Text(composite, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = 7;
		gridData.grabExcessHorizontalSpace = true;
		stepwidthText.setLayoutData(gridData);

		stepcountRadioButton = new Button(composite, SWT.RADIO);
		stepcountRadioButton.setText("Stepcount:");
		stepcountText = new Text(composite, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = 7;
		gridData.grabExcessHorizontalSpace = true;
		stepcountText.setLayoutData(gridData);
		
		this.createBindings();
		this.createTypedBindings();
		this.setEnabled();

		return composite;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean close() {
		this.addMultiplyMode.removePropertyChangeListener(
				AddMultiplyMode.ADJUST_PARAMETER_PROP, this);
		return super.close();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		this.setEnabled();
	}
	
	private void createBindings() {
		this.context = new DataBindingContext();
		
		SelectObservableValue selectionTargetObservable = 
				new SelectObservableValue();
		selectionTargetObservable.addOption(AdjustParameter.START, 
				SWTObservables.observeSelection(this.startRadioButton));
		selectionTargetObservable.addOption(AdjustParameter.STOP, 
				SWTObservables.observeSelection(this.stopRadioButton));
		selectionTargetObservable.addOption(AdjustParameter.STEPWIDTH, 
				SWTObservables.observeSelection(this.stepwidthRadioButton));
		selectionTargetObservable.addOption(AdjustParameter.STEPCOUNT, 
				SWTObservables.observeSelection(this.stepcountRadioButton));
		IObservableValue selectionModelObservable = BeansObservables.observeValue(
				this.addMultiplyMode, AddMultiplyMode.ADJUST_PARAMETER_PROP);
		UpdateValueStrategy targetToModel = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		UpdateValueStrategy modelToTarget = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		context.bindValue(selectionTargetObservable, 
				selectionModelObservable, targetToModel, modelToTarget);
		
		IObservableValue stepcountTargetObservable = 
				SWTObservables.observeText(stepcountText, SWT.Modify);
		IObservableValue stepcountModelObservable = BeansObservables.observeValue(
						this.addMultiplyMode, AddMultiplyMode.STEPCOUNT_PROP);
		UpdateValueStrategy stepcountTargetToModel = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE).
				setConverter(new AddDoubleTargetToModelConverter()).
				setAfterGetValidator(new AddDoubleTargetToModelValidator());
		UpdateValueStrategy stepcountModelToTarget = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE).
				setAfterGetValidator(new AddDoubleModelToTargetValidator()).
				setConverter(new AddDoubleModelToTargetConverter());
		Binding stepcountBinding = context.bindValue(stepcountTargetObservable, 
				stepcountModelObservable, stepcountTargetToModel, stepcountModelToTarget);
		ControlDecorationSupport.create(stepcountBinding, SWT.LEFT);
	}
	
	/**
	 * creates bindings for widgets according to axis type
	 */
	protected abstract void createTypedBindings();
	
	private void setEnabled() {
		if (this.addMultiplyMode == null) {
			return;
		}
		
		this.startText.setEnabled(true);
		this.stopText.setEnabled(true);
		this.stepwidthText.setEnabled(true);
		this.stepcountText.setEnabled(true);
		this.stepcountRadioButton.setEnabled(true);
		
		switch (this.addMultiplyMode.getAdjustParameter()) {
		case START:
			this.startText.setEnabled(false);
			break;
		case STEPCOUNT:
			this.stepcountText.setEnabled(false);
			break;
		case STEPWIDTH:
			this.stepwidthText.setEnabled(false);
			break;
		case STOP:
			this.stopText.setEnabled(false);
			break;
		default:
			break;
		}
		
		// if an axis (other than this one) is set as main axis, 
		// its step count is used
		if (this.addMultiplyMode.getReferenceAxis() != null) {
			this.stepcountText.setEnabled(false);
			this.stepcountRadioButton.setEnabled(false);
		}
	}
	
	protected Axis getAxis() {
		return this.axis;
	}
	
	protected AddMultiplyMode<?> getAxisMode() {
		return this.addMultiplyMode;
	}
	
	protected DataBindingContext getContext() {
		return this.context;
	}
	
	protected Text getStartText() {
		return this.startText;
	}
	
	protected Text getStopText() {
		return this.stopText;
	}
	
	protected Text getStepwidthText() {
		return this.stepwidthText;
	}
}

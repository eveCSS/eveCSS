package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.datetime.ui;

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
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
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
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.datetime.AddDateTimeModelToTargetConverter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.datetime.AddDateTimeTargetToModelConverter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.datetime.AddDateTimeTargetToModelValidator;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.datetime.AddDoubleTargetToModelValidator;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.ui.DialogCellEditorDialog;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class AddDateTimeDialog extends DialogCellEditorDialog implements PropertyChangeListener {
	private Axis axis;
	private AddMultiplyMode<?> addMultiplyMode;
	
	private Button startRadioButton;
	private Text startText;
	private ControlDecoration startTextProposalDecoration;
	private Button stopRadioButton;
	private Text stopText;
	private ControlDecoration stopTextProposalDecoration;
	private Button stepwidthRadioButton;
	private Text stepwidthText;
	private ControlDecoration stepwidthTextProposalDecoration;
	private Button stepcountRadioButton;
	private Text stepcountText;
	
	private Image contentProposalImage;
	
	@SuppressWarnings("unchecked")
	public AddDateTimeDialog(Shell shell, Control control, Axis axis) {
		super(shell, control);
		this.axis = axis;
		
		if (PositionMode.ABSOLUTE.equals(axis.getPositionMode())) {
			this.addMultiplyMode = (AddMultiplyMode<Date>)axis.getMode();
		} else if (PositionMode.RELATIVE.equals(axis.getPositionMode())) {
			this.addMultiplyMode = (AddMultiplyMode<Duration>)axis.getMode();
		}
		
		this.addMultiplyMode.addPropertyChangeListener(
				AddMultiplyMode.ADJUST_PARAMETER_PROP, this);
		
		this.contentProposalImage = FieldDecorationRegistry.getDefault()
			.getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL)
			.getImage();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean close() {
		this.addMultiplyMode.removePropertyChangeListener(
				AddMultiplyMode.ADJUST_PARAMETER_PROP, this);
		// TODO dispose image ???
		return super.close();
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
		startTextProposalDecoration = new ControlDecoration(this.startText, 
				SWT.LEFT | SWT.BOTTOM);
		startTextProposalDecoration.setImage(contentProposalImage);
		startTextProposalDecoration.setShowOnlyOnFocus(true);

		stopRadioButton = new Button(composite, SWT.RADIO);
		stopRadioButton.setText("Stop:");
		stopText = new Text(composite, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = 7;
		gridData.grabExcessHorizontalSpace = true;
		stopText.setLayoutData(gridData);
		stopTextProposalDecoration = new ControlDecoration(this.stopText, 
				SWT.LEFT | SWT.BOTTOM);
		stopTextProposalDecoration.setImage(contentProposalImage);
		stopTextProposalDecoration.setShowOnlyOnFocus(true);

		stepwidthRadioButton = new Button(composite, SWT.RADIO);
		stepwidthRadioButton.setText("Stepwidth:");
		stepwidthText = new Text(composite, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = 7;
		gridData.grabExcessHorizontalSpace = true;
		stepwidthText.setLayoutData(gridData);
		stepwidthTextProposalDecoration = new ControlDecoration(
				this.stepwidthText, SWT.LEFT | SWT.BOTTOM);
		stepwidthTextProposalDecoration.setImage(contentProposalImage);
		stepwidthTextProposalDecoration.setShowOnlyOnFocus(true);

		stepcountRadioButton = new Button(composite, SWT.RADIO);
		stepcountRadioButton.setText("Stepcount:");
		stepcountText = new Text(composite, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = 7;
		gridData.grabExcessHorizontalSpace = true;
		stepcountText.setLayoutData(gridData);
		
		this.createBinding();
		this.setEnabled();

		return composite;
	}
	
	private void createBinding() {
		DataBindingContext context = new DataBindingContext();
		
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
		
		IObservableValue startTargetObservable = SWTObservables.observeText(
				startText, SWT.Modify);
		IObservableValue startModelObservable = BeansObservables.observeValue(
				this.addMultiplyMode, AddMultiplyMode.START_PROP);
		UpdateValueStrategy startTargetToModel = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		startTargetToModel.setConverter(
				new AddDateTimeTargetToModelConverter(axis));
		startTargetToModel.setAfterGetValidator(
				new AddDateTimeTargetToModelValidator(axis));
		UpdateValueStrategy startModelToTarget = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		startModelToTarget.setConverter(new AddDateTimeModelToTargetConverter(
				axis, false));
		Binding startBinding = context.bindValue(startTargetObservable, 
				startModelObservable, startTargetToModel, startModelToTarget);
		ControlDecorationSupport.create(startBinding, SWT.LEFT | SWT.TOP);
		
		IObservableValue stopTargetObservable = SWTObservables.observeText(
				stopText, SWT.Modify);
		IObservableValue stopModelObservable = BeansObservables.observeValue(
				this.addMultiplyMode, AddMultiplyMode.STOP_PROP);
		UpdateValueStrategy stopTargetToModel = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		stopTargetToModel.setConverter(
				new AddDateTimeTargetToModelConverter(axis));
		stopTargetToModel.setAfterGetValidator(
				new AddDateTimeTargetToModelValidator(axis));
		UpdateValueStrategy stopModelToTarget = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		stopModelToTarget.setConverter(
				new AddDateTimeModelToTargetConverter(axis, false));
		Binding stopBinding = context.bindValue(stopTargetObservable, 
				stopModelObservable, stopTargetToModel, stopModelToTarget);
		ControlDecorationSupport.create(stopBinding, SWT.LEFT | SWT.TOP);
		
		IObservableValue stepwidthTargetObservable = SWTObservables.observeText(
				stepwidthText, SWT.Modify);
		IObservableValue stepwidthModelObservable = BeansObservables.observeValue(
				this.addMultiplyMode, AddMultiplyMode.STEPWIDTH_PROP);
		UpdateValueStrategy stepwidthTargetToModel = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		stepwidthTargetToModel.setConverter(
				new AddDateTimeTargetToModelConverter(axis));
		stepwidthTargetToModel.setAfterGetValidator(
				new AddDateTimeTargetToModelValidator(axis));
		UpdateValueStrategy stepwidthModelToTarget = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		stepwidthModelToTarget.setConverter(
				new AddDateTimeModelToTargetConverter(axis, true));
		Binding stepwidthBinding = context.bindValue(stepwidthTargetObservable, 
				stepwidthModelObservable, stepwidthTargetToModel, 
				stepwidthModelToTarget);
		ControlDecorationSupport.create(stepwidthBinding, SWT.LEFT | SWT.TOP);
		
		IObservableValue stepcountTargetObservable = 
				SWTObservables.observeText(stepcountText, SWT.Modify);
		IObservableValue stepcountModelObservable = BeansObservables.observeValue(
						this.addMultiplyMode, AddMultiplyMode.STEPCOUNT_PROP);
		UpdateValueStrategy stepcountTargetToModel = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		// TODO target to model Converter
		stepcountTargetToModel.setAfterGetValidator(
				new AddDoubleTargetToModelValidator());
		UpdateValueStrategy stepcountModelToTarget = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		// TODO model to target converter
		Binding stepcountBinding = context.bindValue(stepcountTargetObservable, 
				stepcountModelObservable, stepcountTargetToModel, stepcountModelToTarget);
		ControlDecorationSupport.create(stepcountBinding, SWT.LEFT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		this.setEnabled();
	}
	
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
	
	// TODO destroy bindings when dialog is closed ?
}

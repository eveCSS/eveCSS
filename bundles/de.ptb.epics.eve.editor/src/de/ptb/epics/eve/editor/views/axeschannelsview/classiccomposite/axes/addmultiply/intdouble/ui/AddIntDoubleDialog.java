package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.intdouble.ui;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.axismode.AddMultiplyMode;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.intdouble.AddIntDoubleModelToTargetConverter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.intdouble.AddIntDoubleModelToTargetValidator;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.intdouble.AddIntDoubleTargetToModelConverter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.intdouble.AddIntDoubleTargetToModelValidator;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.ui.AddGenericDialog;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class AddIntDoubleDialog extends AddGenericDialog {

	public AddIntDoubleDialog(Shell shell, Control control, Axis axis) {
		super(shell, control, axis);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createTypedBindings() {
		IObservableValue startTargetObservable = WidgetProperties.text(
				SWT.Modify).observe(getStartText());
		IObservableValue startModelObservable = BeanProperties.value(
				AddMultiplyMode.START_PROP).observe(getAxisMode());
		UpdateValueStrategy startTargetToModel = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE).
				setAfterGetValidator(new AddIntDoubleTargetToModelValidator(getAxis())).
				setConverter(new AddIntDoubleTargetToModelConverter(getAxis()));
		UpdateValueStrategy startModelToTarget = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE).
				setAfterGetValidator(new AddIntDoubleModelToTargetValidator(getAxis())).
				setConverter(new AddIntDoubleModelToTargetConverter(getAxis()));
		Binding startBinding = getContext().bindValue(startTargetObservable, 
				startModelObservable, startTargetToModel, startModelToTarget);
		ControlDecorationSupport.create(startBinding, SWT.LEFT);
		
		IObservableValue stopTargetObservable = WidgetProperties.text(
				SWT.Modify).observe(getStopText());
		IObservableValue stopModelObservable = BeanProperties.value(
				AddMultiplyMode.STOP_PROP).observe(getAxisMode());
		UpdateValueStrategy stopTargetToModel = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE).
				setAfterGetValidator(new AddIntDoubleTargetToModelValidator(getAxis())).
				setConverter(new AddIntDoubleTargetToModelConverter(getAxis()));
		UpdateValueStrategy stopModelToTarget = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE).
				setAfterGetValidator(new AddIntDoubleModelToTargetValidator(getAxis())).
				setConverter(new AddIntDoubleModelToTargetConverter(getAxis()));
		Binding stopBinding = getContext().bindValue(stopTargetObservable, 
				stopModelObservable, stopTargetToModel, stopModelToTarget);
		ControlDecorationSupport.create(stopBinding, SWT.LEFT);
		
		IObservableValue stepwidthTargetObservable = WidgetProperties.text(
				SWT.Modify).observe(getStepwidthText());
		IObservableValue stepwidthModelObservable = BeanProperties.value(
				AddMultiplyMode.STEPWIDTH_PROP).observe(getAxisMode());
		UpdateValueStrategy stepwidthTargetToModel = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE).
				setAfterGetValidator(new AddIntDoubleTargetToModelValidator(getAxis())).
				setConverter(new AddIntDoubleTargetToModelConverter(getAxis()));
		UpdateValueStrategy stepwidthModelToTarget = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE).
				setAfterGetValidator(new AddIntDoubleModelToTargetValidator(getAxis())).
				setConverter(new AddIntDoubleModelToTargetConverter(getAxis()));
		Binding stepwidthBinding = getContext().bindValue(stepwidthTargetObservable, 
				stepwidthModelObservable, stepwidthTargetToModel, stepwidthModelToTarget);
		ControlDecorationSupport.create(stepwidthBinding, SWT.LEFT);
	}
}

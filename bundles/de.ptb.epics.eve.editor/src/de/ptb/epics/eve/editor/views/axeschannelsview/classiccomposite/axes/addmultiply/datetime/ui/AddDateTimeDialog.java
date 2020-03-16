package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.datetime.ui;

import java.util.Date;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.axismode.AddMultiplyMode;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.datetime.AddDateTimeModelToTargetConverter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.datetime.AddDateTimeTargetToModelConverter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.datetime.AddDateTimeTargetToModelValidator;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.ui.AddGenericDialog;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class AddDateTimeDialog extends AddGenericDialog {
	
	private ControlDecoration startTextProposalDecoration;
	private ControlDecoration stopTextProposalDecoration;
	private ControlDecoration stepwidthTextProposalDecoration;
	
	private Image contentProposalImage;
	
	public AddDateTimeDialog(Shell shell, Control control, Axis axis) {
		super(shell, control, axis);

		this.contentProposalImage = FieldDecorationRegistry.getDefault()
			.getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL)
			.getImage();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean close() {
		// TODO dispose image ???
		return super.close();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Control myControl = super.createDialogArea(parent);

		startTextProposalDecoration = new ControlDecoration(getStartText(), 
				SWT.LEFT | SWT.BOTTOM);
		startTextProposalDecoration.setImage(contentProposalImage);
		startTextProposalDecoration.setShowOnlyOnFocus(true);
		startTextProposalDecoration.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				DateSelectorDialog dialog = new DateSelectorDialog(getShell(), 
						((AddMultiplyMode<Date>)getAxis().getMode()).getStart());
				dialog.open();
				if (dialog.getReturnCode() == Window.OK) {
					((AddMultiplyMode<Date>)getAxis().getMode()).setStart(dialog.getDate());
				}
			}
		});

		stopTextProposalDecoration = new ControlDecoration(getStopText(), 
				SWT.LEFT | SWT.BOTTOM);
		stopTextProposalDecoration.setImage(contentProposalImage);
		stopTextProposalDecoration.setShowOnlyOnFocus(true);
		stopTextProposalDecoration.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				DateSelectorDialog dialog = new DateSelectorDialog(getShell(), 
						((AddMultiplyMode<Date>)getAxis().getMode()).getStop());
				dialog.open();
				if (dialog.getReturnCode() == Window.OK) {
					((AddMultiplyMode<Date>)getAxis().getMode()).setStop(dialog.getDate());
				}
			}
		});

		stepwidthTextProposalDecoration = new ControlDecoration(
				getStepwidthText(), SWT.LEFT | SWT.BOTTOM);
		stepwidthTextProposalDecoration.setImage(contentProposalImage);
		stepwidthTextProposalDecoration.setShowOnlyOnFocus(true);
		
		return myControl;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createTypedBindings() {
		IObservableValue startTargetObservable = SWTObservables.observeText(
				getStartText(), SWT.Modify);
		IObservableValue startModelObservable = BeansObservables.observeValue(
				getAxisMode(), AddMultiplyMode.START_PROP);
		UpdateValueStrategy startTargetToModel = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		startTargetToModel.setConverter(
				new AddDateTimeTargetToModelConverter(getAxis()));
		startTargetToModel.setAfterGetValidator(
				new AddDateTimeTargetToModelValidator(getAxis()));
		UpdateValueStrategy startModelToTarget = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		startModelToTarget.setConverter(new AddDateTimeModelToTargetConverter(
				getAxis(), false));
		Binding startBinding = getContext().bindValue(startTargetObservable, 
				startModelObservable, startTargetToModel, startModelToTarget);
		ControlDecorationSupport.create(startBinding, SWT.LEFT | SWT.TOP);
		
		IObservableValue stopTargetObservable = SWTObservables.observeText(
				getStopText(), SWT.Modify);
		IObservableValue stopModelObservable = BeansObservables.observeValue(
				getAxisMode(), AddMultiplyMode.STOP_PROP);
		UpdateValueStrategy stopTargetToModel = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		stopTargetToModel.setConverter(
				new AddDateTimeTargetToModelConverter(getAxis()));
		stopTargetToModel.setAfterGetValidator(
				new AddDateTimeTargetToModelValidator(getAxis()));
		UpdateValueStrategy stopModelToTarget = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		stopModelToTarget.setConverter(
				new AddDateTimeModelToTargetConverter(getAxis(), false));
		Binding stopBinding = getContext().bindValue(stopTargetObservable, 
				stopModelObservable, stopTargetToModel, stopModelToTarget);
		ControlDecorationSupport.create(stopBinding, SWT.LEFT | SWT.TOP);
		
		IObservableValue stepwidthTargetObservable = SWTObservables.observeText(
				getStepwidthText(), SWT.Modify);
		IObservableValue stepwidthModelObservable = BeansObservables.observeValue(
				getAxisMode(), AddMultiplyMode.STEPWIDTH_PROP);
		UpdateValueStrategy stepwidthTargetToModel = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		stepwidthTargetToModel.setConverter(
				new AddDateTimeTargetToModelConverter(getAxis()));
		stepwidthTargetToModel.setAfterGetValidator(
				new AddDateTimeTargetToModelValidator(getAxis()));
		UpdateValueStrategy stepwidthModelToTarget = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		stepwidthModelToTarget.setConverter(
				new AddDateTimeModelToTargetConverter(getAxis(), true));
		Binding stepwidthBinding = getContext().bindValue(stepwidthTargetObservable, 
				stepwidthModelObservable, stepwidthTargetToModel, 
				stepwidthModelToTarget);
		ControlDecorationSupport.create(stepwidthBinding, SWT.LEFT | SWT.TOP);
	}
	
	// TODO destroy bindings when dialog is closed ?
}

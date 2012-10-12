package de.ptb.epics.eve.editor.views.motoraxisview.addmultiplycomposite;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.conversion.StringToNumberConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.SelectObservableValue;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.axismode.AddMultiplyMode;
import de.ptb.epics.eve.data.scandescription.axismode.AdjustParameter;
import de.ptb.epics.eve.editor.views.motoraxisview.MotorAxisViewComposite;
import de.ptb.epics.eve.util.pv.PVNumberFormat;
import de.ptb.epics.eve.util.swt.DoubleVerifyListener;
import de.ptb.epics.eve.util.swt.IntegerVerifyListener;

/**
 * @author Marcus Michalsky
 * @since 1.7
 */
public class AddMultiplyComposite extends MotorAxisViewComposite implements
		PropertyChangeListener {

	private static final Logger LOGGER = Logger
			.getLogger(AddMultiplyComposite.class.getName());
	
	private AddMultiplyMode<?> addMultiplyMode;
	
	private Button startRadioButton;
	private Text startText;
	private VerifyListener startTextVerifyListener;
	private Button stopRadioButton;
	private Text stopText;
	private VerifyListener stopTextVerifyListener;
	private Button stepwidthRadioButton;
	private Text stepwidthText;
	private VerifyListener stepwidthTextVerifyListener;
	private Button stepcountRadioButton;
	private Text stepcountText;
	private VerifyListener stepcountTextVerifyListener;

	private Button mainAxisCheckBox;
	
	private Binding selectBinding;
	private SelectObservableValue selectionTargetObservable;
	private IObservableValue selectionModelObservable;
	
	private Binding startBinding;
	private IObservableValue startTargetObservable;
	private IObservableValue startModelObservable;
	
	private Binding stopBinding;
	private IObservableValue stopTargetObservable;
	private IObservableValue stopModelObservable;
	
	private Binding stepwidthBinding;
	private IObservableValue stepwidthTargetObservable;
	private IObservableValue stepwidthModelObservable;
	
	private Binding stepcountBinding;
	private IObservableValue stepcountTargetObservable;
	private IObservableValue stepcountModelObservable;
	
	private Binding mainAxisBinding;
	private IObservableValue mainAxisTargetObservable;
	private IObservableValue mainAxisModelObservable;
	
	/**
	 * Constructor.
	 * 
	 * @param parent the parent
	 * @param style the style
	 */
	public AddMultiplyComposite(final Composite parent, final int style) {
		super(parent, style);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		this.setLayout(gridLayout);
		
		// initialize start elements
		this.startRadioButton = new Button(this, SWT.RADIO);
		this.startRadioButton.setText("Start:");
		
		this.startText = new Text(this, SWT.BORDER);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = 7;
		gridData.grabExcessHorizontalSpace = true;
		this.startText.setLayoutData(gridData);
		// end of: initialize start elements
		
		// initialize stop elements
		this.stopRadioButton = new Button(this, SWT.RADIO);
		this.stopRadioButton.setText("Stop:");
		
		this.stopText = new Text(this, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = 7;
		gridData.grabExcessHorizontalSpace = true;
		this.stopText.setLayoutData(gridData);
		// end of: initialize stop elements
		
		// initialize step width elements
		this.stepwidthRadioButton = new Button(this, SWT.RADIO);
		this.stepwidthRadioButton.setText("Stepwidth:");
		
		this.stepwidthText = new Text(this, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = 7;
		gridData.grabExcessHorizontalSpace = true;
		this.stepwidthText.setLayoutData(gridData);
		// end of: initialize step width elements 
		
		// initialize step count elements
		this.stepcountRadioButton = new Button(this, SWT.RADIO);
		this.stepcountRadioButton.setText("Stepcount:");
		
		this.stepcountText = new Text(this, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = 7;
		gridData.grabExcessHorizontalSpace = true;
		this.stepcountText.setLayoutData(gridData);
		// end of: initialize step count elements
		
		this.mainAxisCheckBox = new Button(this, SWT.CHECK);
		this.mainAxisCheckBox.setText("main axis");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 2;
		this.mainAxisCheckBox.setLayoutData(gridData);
		
		this.addMultiplyMode = null;
		
		LOGGER.debug("composite created");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		this.reset();
		super.dispose();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setAxis(Axis axis) {
		this.reset();
		if (axis == null) {
			return;
		}
		if (!(axis.getMode() instanceof AddMultiplyMode)) {
			LOGGER.warn("invalid axis mode");
			return;
		}
		switch(axis.getType()) {
		case DOUBLE:
			this.addMultiplyMode = (AddMultiplyMode<Double>)axis.getMode();
			break;
		case INT:
			this.addMultiplyMode = (AddMultiplyMode<Integer>)axis.getMode();
			break;
		default:
			LOGGER.warn("wrong axis type");
			return;
		}
		this.addMultiplyMode.addPropertyChangeListener(
				AddMultiplyMode.ADJUST_PARAMETER_PROP, this);
		this.createBinding();
		this.setEnabled();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createBinding() {
		LOGGER.debug("create bindings");
	/*	switch (this.addMultiplyMode.getType()) {
		case DOUBLE:
			this.startTextVerifyListener = new DoubleVerifyListener(
					this.startText);
			this.stopTextVerifyListener = new DoubleVerifyListener(
					this.stopText);
			this.stepwidthTextVerifyListener = new DoubleVerifyListener(
					this.stepwidthText);
			this.stepcountTextVerifyListener = new DoubleVerifyListener(
					this.stepcountText);
			break;
		case INT:
			this.startTextVerifyListener = new IntegerVerifyListener(
					this.startText);
			this.stopTextVerifyListener = new IntegerVerifyListener(
					this.stopText);
			this.stepwidthTextVerifyListener = new IntegerVerifyListener(
					this.stepwidthText);
			this.stepcountTextVerifyListener = new IntegerVerifyListener(
					this.stepcountText);
			break;
		default:
			break;
		}
		this.startText.addVerifyListener(startTextVerifyListener);
		this.stopText.addVerifyListener(stopTextVerifyListener);
		this.stepwidthText.addVerifyListener(stepwidthTextVerifyListener);
		this.stepcountText.addVerifyListener(stepcountTextVerifyListener);*/
		this.selectionTargetObservable = new SelectObservableValue();
		this.selectionTargetObservable.addOption(AdjustParameter.START,
				SWTObservables.observeSelection(this.startRadioButton));
		this.selectionTargetObservable.addOption(AdjustParameter.STOP,
				SWTObservables.observeSelection(this.stopRadioButton));
		this.selectionTargetObservable.addOption(AdjustParameter.STEPWIDTH,
				SWTObservables.observeSelection(this.stepwidthRadioButton));
		this.selectionTargetObservable.addOption(AdjustParameter.STEPCOUNT,
				SWTObservables.observeSelection(this.stepcountRadioButton));
		this.selectionModelObservable = BeansObservables.observeValue(
				this.addMultiplyMode, AddMultiplyMode.ADJUST_PARAMETER_PROP);
		UpdateValueStrategy targetToModel = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		UpdateValueStrategy modelToTarget = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		this.selectBinding = context.bindValue(selectionTargetObservable,
				selectionModelObservable, targetToModel, modelToTarget);
		
		this.startTargetObservable = SWTObservables.observeText(startText, 
				SWT.Modify);
		this.startModelObservable = BeansObservables.observeValue(
				this.addMultiplyMode, AddMultiplyMode.START_PROP);
		UpdateValueStrategy startTargetToModel = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		/*startTargetToModel.setConverter(new TargetToModelConverter(
				this.addMultiplyMode.getType()));
		startTargetToModel.setAfterConvertValidator(new TargetToModelValidator(
				this.addMultiplyMode.getType()));*/
		UpdateValueStrategy startModelToTarget = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		/*startModelToTarget.setConverter(new ModelToTargetConverter(
				this.addMultiplyMode.getType()));
		startModelToTarget.setAfterGetValidator(new ModelToTargetValidator(
				this.addMultiplyMode.getType()));*/
		this.startBinding = context.bindValue(startTargetObservable,
				startModelObservable, startTargetToModel, startModelToTarget);
		ControlDecorationSupport.create(this.startBinding, SWT.LEFT);
		
		this.stopTargetObservable = SWTObservables.observeText(stopText, 
				SWT.Modify);
		this.stopModelObservable = BeansObservables.observeValue(
				this.addMultiplyMode, AddMultiplyMode.STOP_PROP);
		UpdateValueStrategy stopTargetToModel = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		stopTargetToModel.setConverter(new TargetToModelConverter(
				this.addMultiplyMode.getType()));
		/*stopTargetToModel.setAfterGetValidator(new TargetToModelValidator(
				this.addMultiplyMode.getType()));*/
		UpdateValueStrategy stopModelToTarget = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		stopModelToTarget.setConverter(new ModelToTargetConverter(
				this.addMultiplyMode.getType()));
		/*stopModelToTarget.setAfterGetValidator(new ModelToTargetValidator(
				this.addMultiplyMode.getType()));*/
		this.stopBinding = context.bindValue(stopTargetObservable,
				stopModelObservable, stopTargetToModel, stopModelToTarget);
		ControlDecorationSupport.create(this.stopBinding, SWT.LEFT);
		
		this.stepwidthTargetObservable = SWTObservables.observeText(
				stepwidthText, SWT.Modify);
		this.stepwidthModelObservable = BeansObservables.observeValue(
				this.addMultiplyMode, AddMultiplyMode.STEPWIDTH_PROP);
		UpdateValueStrategy stepwidthTargetToModel = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		stepwidthTargetToModel.setConverter(new TargetToModelConverter(
				this.addMultiplyMode.getType()));
				//StringToNumberConverter.toDouble(new PVNumberFormat("##0.00000E00"), false));
		stepwidthTargetToModel.setAfterGetValidator(new TargetToModelValidator(
				this.addMultiplyMode.getType()));
		UpdateValueStrategy stepwidthModelToTarget = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		stepwidthModelToTarget.setConverter(new ModelToTargetConverter(
				this.addMultiplyMode.getType()));
		stepwidthModelToTarget.setAfterGetValidator(new ModelToTargetValidator(
				this.addMultiplyMode.getType()));
		this.stepwidthBinding = context.bindValue(stepwidthTargetObservable,
				stepwidthModelObservable, stepwidthTargetToModel,
				stepwidthModelToTarget);
		ControlDecorationSupport.create(this.stepwidthBinding, SWT.LEFT);
		
		this.stepcountTargetObservable = SWTObservables.observeText(
				stepcountText, SWT.Modify);
		this.stepcountModelObservable = BeansObservables.observeValue(
				this.addMultiplyMode, AddMultiplyMode.STEPCOUNT_PROP);
		UpdateValueStrategy stepcountTargetToModel = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		stepcountTargetToModel.setConverter(new TargetToModelConverter(
				DataTypes.DOUBLE));
		stepcountTargetToModel.setAfterGetValidator(new TargetToModelValidator(
				DataTypes.DOUBLE));
		UpdateValueStrategy stepcountModelToTarget = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		stepcountModelToTarget.setConverter(new ModelToTargetConverter(
				DataTypes.DOUBLE));
		stepcountModelToTarget.setAfterGetValidator(new ModelToTargetValidator(
				DataTypes.DOUBLE));
		this.stepcountBinding = context.bindValue(stepcountTargetObservable,
				stepcountModelObservable, stepcountTargetToModel,
				stepcountModelToTarget);
		ControlDecorationSupport.create(this.stepcountBinding, SWT.LEFT);
		
		this.mainAxisTargetObservable = SWTObservables
				.observeSelection(mainAxisCheckBox);
		this.mainAxisModelObservable = BeansObservables.observeValue(
				this.addMultiplyMode, AddMultiplyMode.MAIN_AXIS_PROP);
		UpdateValueStrategy mainAxisTargetToModel = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		UpdateValueStrategy mainAxisModelToTarget = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		this.mainAxisBinding = context.bindValue(mainAxisTargetObservable,
				mainAxisModelObservable, mainAxisTargetToModel,
				mainAxisModelToTarget);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
		if (this.addMultiplyMode != null) {
			LOGGER.debug("remove bindings");
			/*this.startText.removeVerifyListener(startTextVerifyListener);
			this.stopText.removeVerifyListener(stopTextVerifyListener);
			this.stepwidthText.removeVerifyListener(stepwidthTextVerifyListener);
			this.stepcountText.removeVerifyListener(stepcountTextVerifyListener);*/
			this.addMultiplyMode.removePropertyChangeListener(
					AddMultiplyMode.ADJUST_PARAMETER_PROP, this);
			this.selectBinding.dispose();
			this.selectionTargetObservable.dispose();
			this.selectionModelObservable.dispose();
			this.startBinding.dispose();
			this.startTargetObservable.dispose();
			this.startModelObservable.dispose();
			this.stopBinding.dispose();
			this.stopTargetObservable.dispose();
			this.stopModelObservable.dispose();
			this.stepwidthBinding.dispose();
			this.stepwidthTargetObservable.dispose();
			this.stepwidthModelObservable.dispose();
			this.stepcountBinding.dispose();
			this.stepcountTargetObservable.dispose();
			this.stepcountModelObservable.dispose();
			this.mainAxisBinding.dispose();
			this.mainAxisTargetObservable.dispose();
			this.mainAxisModelObservable.dispose();
		}
		this.addMultiplyMode = null;
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
		this.mainAxisCheckBox.setEnabled(true);
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
		}
		// if an axis is set as main axis, its step count is used
		if (this.addMultiplyMode.getReferenceAxis() != null) {
			this.stepcountText.setEnabled(false);
			this.stepcountRadioButton.setEnabled(false);
			this.mainAxisCheckBox.setEnabled(false);
		}
	}
	
	/**
	 * calculate the height to see all entries of this composite
	 * 
	 * @return the needed height of Composite to see all entries
	 */
	public int getTargetHeight() {
		return (mainAxisCheckBox.getBounds().y + 
				mainAxisCheckBox.getBounds().height + 5);
	}

	/**
	 * calculate the width to see all entries of this composite
	 * 
	 * @return the needed width of Composite to see all entries
	 */
	public int getTargetWidth() {
		return (mainAxisCheckBox.getBounds().x + 
				mainAxisCheckBox.getBounds().width + 5);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		LOGGER.debug(e.getPropertyName());
		if (e.getPropertyName().equals(AddMultiplyMode.ADJUST_PARAMETER_PROP)) {
			this.setEnabled();
		}
	}
}
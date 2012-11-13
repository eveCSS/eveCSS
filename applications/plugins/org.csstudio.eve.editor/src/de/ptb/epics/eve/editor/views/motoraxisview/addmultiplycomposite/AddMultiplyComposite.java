package de.ptb.epics.eve.editor.views.motoraxisview.addmultiplycomposite;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

import javax.xml.datatype.Duration;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.SelectObservableValue;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.PositionMode;
import de.ptb.epics.eve.data.scandescription.axismode.AddMultiplyMode;
import de.ptb.epics.eve.data.scandescription.axismode.AdjustParameter;
import de.ptb.epics.eve.editor.views.motoraxisview.MotorAxisViewComposite;

/**
 * @author Marcus Michalsky
 * @since 1.7
 */
public class AddMultiplyComposite extends MotorAxisViewComposite implements
		PropertyChangeListener {

	private static final Logger LOGGER = Logger
			.getLogger(AddMultiplyComposite.class.getName());
	
	private Axis axis;
	private AddMultiplyMode<?> addMultiplyMode;
	
	private Button startRadioButton;
	private Text startText;
	private ControlDecoration startTextControlDecoration;
	private SelectionListener startTextControlDecorationSelectionListener;
	private FocusListener startTextFocusListener;
	private MouseListener startTextMouseListener;
	private Button stopRadioButton;
	private Text stopText;
	private ControlDecoration stopTextControlDecoration;
	private SelectionListener stopTextControlDecorationSelectionListener;
	private FocusListener stopTextFocusListener;
	private MouseListener stopTextMouseListener;
	private Button stepwidthRadioButton;
	private Text stepwidthText;
	private ControlDecoration stepwidthTextControlDecoration;
	private SelectionListener stepwidhtTextControlDecorationSelectionListener;
	private FocusListener stepwidthTextFocusListener;
	private MouseListener stepwidthTextMouseListener;
	private Button stepcountRadioButton;
	private Text stepcountText;
	private FocusListener stepcountTextFocusListener;
	private MouseListener stepcountTextMouseListener;

	private Button mainAxisCheckBox;
	
	private Binding selectBinding;
	private SelectObservableValue selectionTargetObservable;
	private IObservableValue selectionModelObservable;
	
	private Binding startBinding;
	private IObservableValue startTargetObservable;
	private IObservableValue startModelObservable;
	private ControlDecorationSupport startDecoration;
	
	private Binding stopBinding;
	private IObservableValue stopTargetObservable;
	private IObservableValue stopModelObservable;
	private ControlDecorationSupport stopDecoration;
	
	private Binding stepwidthBinding;
	private IObservableValue stepwidthTargetObservable;
	private IObservableValue stepwidthModelObservable;
	private ControlDecorationSupport stepwidthDecoration;
	
	private Binding stepcountBinding;
	private IObservableValue stepcountTargetObservable;
	private IObservableValue stepcountModelObservable;
	private ControlDecorationSupport stepcountDecoration;
	
	private Binding mainAxisBinding;
	private IObservableValue mainAxisTargetObservable;
	private IObservableValue mainAxisModelObservable;
	
	private Image contentProposalImage;
	
	private final String descriptionText = "Click to open Date Selector";
	
	/**
	 * Constructor.
	 * 
	 * @param parent the parent
	 * @param style the style
	 */
	public AddMultiplyComposite(final Composite parent, final int style) {
		super(parent, style);
		
		this.contentProposalImage = FieldDecorationRegistry.getDefault()
				.getFieldDecoration(
						FieldDecorationRegistry.DEC_CONTENT_PROPOSAL)
								.getImage();
		
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
		this.startTextControlDecoration = new ControlDecoration(this.startText,
				SWT.LEFT | SWT.BOTTOM);
		this.startTextControlDecoration.setImage(contentProposalImage);
		this.startTextControlDecoration.setDescriptionText(this.descriptionText);
		this.startTextControlDecoration.setShowOnlyOnFocus(true);
		this.startTextControlDecorationSelectionListener = 
				new DateTimeProposalSelectionListener(this.startText);
		this.startTextControlDecoration.addSelectionListener(
				startTextControlDecorationSelectionListener);
		this.startTextFocusListener = new TextFocusListener(this.startText);
		this.startTextMouseListener = new TextMouseListener(this.startText);
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
		this.stopTextControlDecoration = new ControlDecoration(this.stopText,
				SWT.LEFT | SWT.BOTTOM);
		this.stopTextControlDecoration.setImage(contentProposalImage);
		this.stopTextControlDecoration.setDescriptionText(this.descriptionText);
		this.stopTextControlDecoration.setShowOnlyOnFocus(true);
		this.stopTextControlDecorationSelectionListener = 
				new DateTimeProposalSelectionListener(this.stopText);
		this.stopTextControlDecoration.addSelectionListener(
				stopTextControlDecorationSelectionListener);
		this.stopTextFocusListener = new TextFocusListener(stopText);
		this.stopTextMouseListener = new TextMouseListener(stopText);
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
		this.stepwidthTextControlDecoration = new ControlDecoration(
				this.stepwidthText, SWT.LEFT | SWT.BOTTOM);
		this.stepwidthTextControlDecoration.setImage(contentProposalImage);
		this.stepwidthTextControlDecoration
				.setDescriptionText(this.descriptionText);
		this.stepwidthTextControlDecoration.setShowOnlyOnFocus(true);
		this.stepwidhtTextControlDecorationSelectionListener = 
				new DateTimeProposalSelectionListener(this.stepwidthText);
		this.stepwidthTextControlDecoration.addSelectionListener(
				stepwidhtTextControlDecorationSelectionListener);
		this.stepwidthTextFocusListener = new TextFocusListener(stepwidthText);
		this.stepwidthTextMouseListener = new TextMouseListener(stepwidthText);
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
		this.stepcountTextFocusListener = new TextFocusListener(stepcountText);
		this.stepcountTextMouseListener = new TextMouseListener(stepcountText);
		// end of: initialize step count elements
		
		this.mainAxisCheckBox = new Button(this, SWT.CHECK);
		this.mainAxisCheckBox.setText("Main Axis");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 2;
		this.mainAxisCheckBox.setLayoutData(gridData);
		
		this.addMultiplyMode = null;
		this.axis = null;
		
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
		this.axis = axis;
		switch(axis.getType()) {
		case DOUBLE:
			this.addMultiplyMode = (AddMultiplyMode<Double>)axis.getMode();
			break;
		case INT:
			this.addMultiplyMode = (AddMultiplyMode<Integer>)axis.getMode();
			break;
		case DATETIME:
			if (axis.getPositionMode().equals(PositionMode.ABSOLUTE)) {
				this.addMultiplyMode = (AddMultiplyMode<Date>)axis.getMode();
				this.startTextControlDecoration.show();
				this.stopTextControlDecoration.show();
			} else if (axis.getPositionMode().equals(PositionMode.RELATIVE)) {
				this.addMultiplyMode = (AddMultiplyMode<Duration>)axis.getMode();
				this.startTextControlDecoration.show();
				this.stopTextControlDecoration.show();
				this.stepwidthTextControlDecoration.show();
			}
			break;
		default:
			LOGGER.warn("wrong axis type");
			return;
		}
		this.addMultiplyMode.addPropertyChangeListener(
				AddMultiplyMode.ADJUST_PARAMETER_PROP, this);
		this.axis.getMotorAxis().addPropertyChangeListener("highlimit", this);
		this.axis.getMotorAxis().addPropertyChangeListener("lowlimit", this);
		this.axis.addPropertyChangeListener("positionMode", this);
		
		this.createBinding();
		this.setEnabled();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createBinding() {
		LOGGER.debug("create bindings");
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
		startTargetToModel.setConverter(new TargetToModelConverter(
				this.addMultiplyMode.getType(), this.addMultiplyMode.getAxis()));
		startTargetToModel.setAfterGetValidator(
				new TargetToModelAfterGetValidator(
						this.addMultiplyMode.getType(), this.addMultiplyMode
								.getAxis()));
		startTargetToModel.setAfterConvertValidator(
				new TargetToModelAfterConvertValidator(
						this.addMultiplyMode.getAxis()));
		UpdateValueStrategy startModelToTarget = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		startModelToTarget.setConverter(new ModelToTargetConverter(
				this.addMultiplyMode.getType(), this.addMultiplyMode.getAxis(),
				false));
		startModelToTarget.setAfterGetValidator(new ModelToTargetValidator(
				this.addMultiplyMode.getType()));
		this.startBinding = context.bindValue(startTargetObservable,
				startModelObservable, startTargetToModel, startModelToTarget);
		this.startDecoration = ControlDecorationSupport.create(
				this.startBinding, SWT.LEFT | SWT.TOP);
		
		this.stopTargetObservable = SWTObservables.observeText(stopText, 
				SWT.Modify);
		this.stopModelObservable = BeansObservables.observeValue(
				this.addMultiplyMode, AddMultiplyMode.STOP_PROP);
		UpdateValueStrategy stopTargetToModel = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		stopTargetToModel.setConverter(new TargetToModelConverter(
				this.addMultiplyMode.getType(), this.addMultiplyMode.getAxis()));
		stopTargetToModel.setAfterGetValidator(
				new TargetToModelAfterGetValidator(
						this.addMultiplyMode.getType(), this.addMultiplyMode
								.getAxis()));
		stopTargetToModel.setAfterConvertValidator(
				new TargetToModelAfterConvertValidator(
						this.addMultiplyMode.getAxis()));
		UpdateValueStrategy stopModelToTarget = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		stopModelToTarget.setConverter(new ModelToTargetConverter(
				this.addMultiplyMode.getType(), this.addMultiplyMode.getAxis(),
				false));
		stopModelToTarget.setAfterGetValidator(new ModelToTargetValidator(
				this.addMultiplyMode.getType()));
		this.stopBinding = context.bindValue(stopTargetObservable,
				stopModelObservable, stopTargetToModel, stopModelToTarget);
		this.stopDecoration = ControlDecorationSupport.create(this.stopBinding,
				SWT.LEFT | SWT.TOP);
		
		this.stepwidthTargetObservable = SWTObservables.observeText(
				stepwidthText, SWT.Modify);
		this.stepwidthModelObservable = BeansObservables.observeValue(
				this.addMultiplyMode, AddMultiplyMode.STEPWIDTH_PROP);
		UpdateValueStrategy stepwidthTargetToModel = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		stepwidthTargetToModel.setConverter(new TargetToModelConverter(
				this.addMultiplyMode.getType(), this.addMultiplyMode.getAxis()));
		stepwidthTargetToModel.setAfterGetValidator(new TargetToModelAfterGetValidator(
				this.addMultiplyMode.getType(), this.addMultiplyMode.getAxis()));
		UpdateValueStrategy stepwidthModelToTarget = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		stepwidthModelToTarget.setConverter(new ModelToTargetConverter(
				this.addMultiplyMode.getType(), this.addMultiplyMode.getAxis(),
				true));
		stepwidthModelToTarget.setAfterGetValidator(new ModelToTargetValidator(
				this.addMultiplyMode.getType()));
		this.stepwidthBinding = context.bindValue(stepwidthTargetObservable,
				stepwidthModelObservable, stepwidthTargetToModel,
				stepwidthModelToTarget);
		this.stepwidthDecoration = ControlDecorationSupport.create(
				this.stepwidthBinding, SWT.LEFT | SWT.TOP);
		
		this.stepcountTargetObservable = SWTObservables.observeText(
				stepcountText, SWT.Modify);
		this.stepcountModelObservable = BeansObservables.observeValue(
				this.addMultiplyMode, AddMultiplyMode.STEPCOUNT_PROP);
		UpdateValueStrategy stepcountTargetToModel = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		stepcountTargetToModel.setConverter(new TargetToModelConverter(
				DataTypes.DOUBLE, this.addMultiplyMode.getAxis()));
		stepcountTargetToModel.setAfterGetValidator(new TargetToModelAfterGetValidator(
				DataTypes.DOUBLE, this.addMultiplyMode.getAxis()));
		UpdateValueStrategy stepcountModelToTarget = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		stepcountModelToTarget.setConverter(new ModelToTargetConverter(
				DataTypes.DOUBLE, this.addMultiplyMode.getAxis(), false));
		stepcountModelToTarget.setAfterGetValidator(new ModelToTargetValidator(
				DataTypes.DOUBLE));
		this.stepcountBinding = context.bindValue(stepcountTargetObservable,
				stepcountModelObservable, stepcountTargetToModel,
				stepcountModelToTarget);
		this.stepcountDecoration = ControlDecorationSupport.create(
				this.stepcountBinding, SWT.LEFT);
		
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
		
		this.startText.addFocusListener(startTextFocusListener);
		this.stopText.addFocusListener(stopTextFocusListener);
		this.stepwidthText.addFocusListener(stepwidthTextFocusListener);
		this.stepcountText.addFocusListener(stepcountTextFocusListener);
		
		this.startText.addMouseListener(startTextMouseListener);
		this.stopText.addMouseListener(stopTextMouseListener);
		this.stepwidthText.addMouseListener(stepwidthTextMouseListener);
		this.stepcountText.addMouseListener(stepcountTextMouseListener);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
		if (this.addMultiplyMode != null) {
			LOGGER.debug("remove bindings");
			
			this.startText.removeMouseListener(startTextMouseListener);
			this.stopText.removeMouseListener(stopTextMouseListener);
			this.stepwidthText.removeMouseListener(stepwidthTextMouseListener);
			this.stepcountText.removeMouseListener(stepcountTextMouseListener);
			
			this.startText.removeFocusListener(startTextFocusListener);
			this.stopText.removeFocusListener(stopTextFocusListener);
			this.stepwidthText.removeFocusListener(stepwidthTextFocusListener);
			this.stepcountText.removeFocusListener(stepcountTextFocusListener);
			
			this.addMultiplyMode.removePropertyChangeListener(
					AddMultiplyMode.ADJUST_PARAMETER_PROP, this);
			this.selectBinding.dispose();
			this.selectionTargetObservable.dispose();
			this.selectionModelObservable.dispose();
			this.startBinding.dispose();
			this.startTargetObservable.dispose();
			this.startModelObservable.dispose();
			if (this.startDecoration != null) {
				this.startDecoration.dispose();
			}
			this.stopBinding.dispose();
			this.stopTargetObservable.dispose();
			this.stopModelObservable.dispose();
			if (this.stopDecoration != null) {
				this.stopDecoration.dispose();
			}
			this.stepwidthBinding.dispose();
			this.stepwidthTargetObservable.dispose();
			this.stepwidthModelObservable.dispose();
			if (this.stepwidthDecoration != null) {
				this.stepwidthDecoration.dispose();
			}
			this.stepcountBinding.dispose();
			this.stepcountTargetObservable.dispose();
			this.stepcountModelObservable.dispose();
			if (this.stepcountDecoration != null) {
				this.stepcountDecoration.dispose();
			}
			this.mainAxisBinding.dispose();
			this.mainAxisTargetObservable.dispose();
			this.mainAxisModelObservable.dispose();
			
			this.startTextControlDecoration.hide();
			this.stopTextControlDecoration.hide();
			this.stepwidthTextControlDecoration.hide();
		}
		if (this.axis != null) {
			this.axis.getMotorAxis().removePropertyChangeListener("highlimit",
					this);
			this.axis.getMotorAxis().removePropertyChangeListener("lowlimit",
					this);
			this.axis.removePropertyChangeListener("positionMode", this);
		}
		this.addMultiplyMode = null;
		this.axis = null;
		this.redraw();
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
		// if an axis (other than this one) is set as main axis, 
		// its step count is used
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
		} else if (e.getPropertyName().equals("highlimit") || 
				e.getPropertyName().equals("lowlimit")) {
			this.startBinding.updateTargetToModel();
			this.stopBinding.updateTargetToModel();
		} else if (e.getPropertyName().equals("positionMode")) {
			this.setAxis(this.axis);
		}
	}
	
	/* ********************************************************************** */
	/* **************************** Listeners ******************************* */
	/* ********************************************************************** */
	
	/**
	 * @author Marcus Michalsky
	 * @since 1.7
	 */
	private class TextFocusListener implements FocusListener {

		private Text widget;
		
		/**
		 * @param widget the widget to observe
		 */
		public TextFocusListener(Text widget) {
			this.widget = widget;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override 
		public void focusGained(FocusEvent e) {
			if (!axis.getType().equals(DataTypes.DATETIME)) {
				this.widget.selectAll();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusLost(FocusEvent e) {
			LOGGER.debug("focus lost");
			if (this.widget == startText) {
			 	startBinding.updateModelToTarget();
			 	startBinding.validateTargetToModel();
			} else if (this.widget == stopText) {
				stopBinding.updateModelToTarget();
				stopBinding.validateTargetToModel();
			} else if (this.widget == stepwidthText) {
				stepwidthBinding.updateModelToTarget();
			} else if (this.widget == stepcountText) {
				stepcountBinding.updateModelToTarget();
			}
		}
	}
	
	/**
	 * 
	 * @author Marcus Michalsky
	 * @since 1.8
	 */
	private class TextMouseListener extends MouseAdapter {
		
		private Text widget;
		
		/**
		 * @param widget the widget the listener is attached to
		 */
		public TextMouseListener(Text widget) {
			this.widget = widget;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseDown(MouseEvent e) {
			if (e.button == 1) {
				if (!axis.getType().equals(DataTypes.DATETIME)) {
					this.widget.selectAll();
				}
			}
			super.mouseDown(e);
		}
	}
	
	/**
	 * @author Marcus Michalsky
	 * @since 1.7
	 */
	private class DateTimeProposalSelectionListener implements SelectionListener {

		private Text text;
		
		/**
		 * @param text the text field to fill
		 */
		public DateTimeProposalSelectionListener(Text text) {
			this.text = text;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("unchecked")
		@Override
		public void widgetSelected(SelectionEvent e) {
			switch (axis.getPositionMode()) {
			case ABSOLUTE:
				DateSelectorDialog dialog = new DateSelectorDialog(getShell());
				dialog.open();
				if (dialog.getReturnCode() == Dialog.OK) {
					LOGGER.debug("OK");
					LOGGER.debug(dialog.getDate());
					if (this.text == startText) {
						((AddMultiplyMode<Date>) axis.getMode()).setStart(dialog
								.getDate());
					} else if (this.text == stopText) {
						((AddMultiplyMode<Date>) axis.getMode()).setStop(dialog
								.getDate());
					}
				} else {
					LOGGER.debug("cancel");
				}
				break;
			case RELATIVE:
				DurationSelectorDialog durationDialog = 
						new DurationSelectorDialog(getShell());
				durationDialog.open();
				if (durationDialog.getReturnCode() == Dialog.OK) {
					LOGGER.debug("OK");
					LOGGER.debug(durationDialog.getDuration());
					if (this.text == startText) {
						((AddMultiplyMode<Duration>) axis.getMode())
								.setStart(durationDialog.getDuration());
					} else if (this.text == stopText) {
						((AddMultiplyMode<Duration>) axis.getMode())
						.setStop(durationDialog.getDuration());
					} else if (this.text == stepwidthText) {
						((AddMultiplyMode<Duration>) axis.getMode())
						.setStepwidth(durationDialog.getDuration());
					}
				} else {
					LOGGER.debug("cancel");
				}
				break;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
	}
}
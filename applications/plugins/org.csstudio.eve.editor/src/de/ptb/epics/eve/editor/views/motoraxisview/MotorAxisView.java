package de.ptb.epics.eve.editor.views.motoraxisview;

import org.apache.log4j.Logger;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.PositionMode;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.Activator;

/**
 * <code>MotorAxisView</code> shows the attributes of a 
 * {@link de.ptb.epics.eve.data.measuringstation.MotorAxis} and allows 
 * modifications.
 * 
 * @author Hartmut Scherr
 * @author Marcus Michalsky
 */
public class MotorAxisView extends ViewPart implements IModelUpdateListener {

	/**
	 * the unique identifier of the view.
	 */
	public static final String ID = 
		"de.ptb.epics.eve.editor.views.MotorAxisView";

	// logging
	private static Logger logger = 
		Logger.getLogger(MotorAxisView.class.getName());
	
	// *******************************************************************
	// ********************** underlying model ***************************
	// *******************************************************************
	
	// the axis that should be presented
	private Axis currentAxis;
	
	private ScanModule scanModule;
	
	// *******************************************************************
	// ****************** end of: underlying model ***********************
	// *******************************************************************

	private boolean modelUpdateListenerSuspended;
	
	// the utmost composite
	private Composite top = null;
	// Scrolled Composite wrapping top to enable scrolling
	private ScrolledComposite sc = null;

	// GUI: "Step function: <combo> x"
	private Label stepfunctionLabel;
	private Combo stepFunctionCombo;
	private Label stepfunctionErrorLabel;
	
	private StepFunctionComboSelectionListener stepFunctionComboSelectionListener;
	
	// GUI: "Position mode: <combo> x"
	private Label positionModeLabel;
	private Combo positionModeCombo;
	private Label positionModeErrorLabel;
	
	private PositionModeComboSelectionListener positionModeComboSelectionListener;
	
	private Composite emptyComposite;
	private MotorAxisStartStopStepwidthComposite motorAxisStartStopStepwidthComposite;
	private MotorAxisFileComposite motorAxisFileComposite;
	private MotorAxisPluginComposite motorAxisPluginComposite;
	private MotorAxisPositionlistComposite motorAxisPositionlistComposite;
	
	private SashForm sashForm;
	
	private double stepcount;
	private String[] stepfunctions;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite parent) {
		
		modelUpdateListenerSuspended = false;
		
		// simple fill layout
		parent.setLayout(new FillLayout());
		
		// if no measuring station loaded -> show error, do nothing
		if(Activator.getDefault().getMeasuringStation() == null) {
			final Label errorLabel = new Label(parent, SWT.NONE);
			errorLabel.setText("No Measuring Station has been loaded. " +
							   "Please check Preferences!");
			return;
		}
		
		// layout, scrolling and top composite
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		GridData gridData;
		
		this.sc = new ScrolledComposite(parent, SWT.H_SCROLL | 
										SWT.V_SCROLL | SWT.BORDER);
		
		this.top = new Composite(sc, SWT.NONE);
		this.top.setLayout(gridLayout);
		
		sc.setContent(this.top);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		
		// step function elements
		this.stepfunctions = Activator.getDefault().getMeasuringStation().
				getSelections().getStepfunctions();
		
		this.stepfunctionLabel = new Label(this.top, SWT.NONE);
		this.stepfunctionLabel.setText("Step function: ");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		this.stepfunctionLabel.setLayoutData(gridData);
		
		this.stepFunctionCombo = new Combo(this.top, SWT.READ_ONLY);
		this.stepFunctionCombo.setItems(this.stepfunctions);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.stepFunctionCombo.setLayoutData(gridData);
		this.stepFunctionCombo.setEnabled(false);
		this.stepFunctionComboSelectionListener = 
				new StepFunctionComboSelectionListener();
		this.stepFunctionCombo.addSelectionListener(
				stepFunctionComboSelectionListener);
		
		this.stepfunctionErrorLabel = new Label(this.top, SWT.NONE);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		this.stepfunctionErrorLabel.setLayoutData(gridData);
		// end of: step function elements
		
		// position mode elements
		this.positionModeLabel = new Label(this.top, SWT.NONE);
		this.positionModeLabel.setText("Position mode: ");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		this.positionModeLabel.setLayoutData(gridData);
		
		this.positionModeCombo = new Combo(this.top, SWT.READ_ONLY);
		this.positionModeCombo.setItems(PositionMode.getPossiblePositionModes());
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.positionModeCombo.setLayoutData(gridData);
		this.positionModeCombo.setEnabled(false);
		this.positionModeComboSelectionListener = 
				new PositionModeComboSelectionListener();
		this.positionModeCombo.addSelectionListener(
				positionModeComboSelectionListener);
		
		this.positionModeErrorLabel = new Label(this.top, SWT.NONE);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		this.positionModeErrorLabel.setLayoutData(gridData);
		// end of: position mode elements
		
		sashForm = new SashForm(top, SWT.VERTICAL);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 3;
		sashForm.setLayoutData(gridData);
		
		this.emptyComposite = new Composite(sashForm, SWT.NONE);
		this.motorAxisStartStopStepwidthComposite = 
				new MotorAxisStartStopStepwidthComposite(sashForm, SWT.NONE, this);
		this.motorAxisFileComposite = 
				new MotorAxisFileComposite(sashForm, SWT.NONE, this);
		this.motorAxisPluginComposite = 
				new MotorAxisPluginComposite(sashForm, SWT.NONE, this);
		this.motorAxisPositionlistComposite = 
				new MotorAxisPositionlistComposite(sashForm, SWT.NONE, this);
		
		sashForm.setMaximizedControl(this.emptyComposite);
		
		// content not visible after creation of the view
		top.setVisible(false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
	}

	/**
	 * Sets the {@link de.ptb.epics.eve.data.scandescription.Axis}
	 * (the underlying model whose contents is presented by this view).
	 *  
	 * @param axis the {@link de.ptb.epics.eve.data.scandescription.Axis} that 
	 * 		  should be set
	 * @param stepcount the step count (generally the step count of the axis, 
	 * 		  except when a main axis is set which is then used instead)
	 */
	public void setCurrentAxis(final Axis axis, final double stepcount) {
		
		if(axis != null) {
			logger.debug("axis set to: " + axis.getMotorAxis().getID());
		} else {
			logger.debug("axis set to: null");
		}
		
		// if a current axis is set, stop listening to it
		if(this.currentAxis != null) {
			this.currentAxis.removeModelUpdateListener(this);
		}
		// set the new axis as current axis
		this.currentAxis = axis;
		this.stepcount = stepcount;
		
		if(this.currentAxis != null) {
			// current axis not null -> listen to updates on it
			this.currentAxis.addModelUpdateListener(this);
		} 
		
		// update elements (calls the inherited method from the UpdateListener 
		// with argument null indicating an internal call)
		updateEvent(null);
	}
	
	/**
	 * Sets the {@link de.ptb.epics.eve.data.scandescription.Axis}. Has to be 
	 * used instead of {@link #setCurrentAxis(Axis, double)} if ...
	 * 
	 * @param axis the {@link de.ptb.epics.eve.data.scandescription.Axis} that 
	 * 	      should be set
	 * @param stepamount the step amount 
	 * @param scanModule the 
	 * 		  {@link de.ptb.epics.eve.data.scandescription.ScanModule} (to get 
	 * 		  available plug ins)
	 */
	public void setCurrentAxis(final Axis axis, final double stepamount, 
							   final ScanModule scanModule) {
		this.scanModule = scanModule;
		setCurrentAxis(axis, stepamount);
	}
	
	/*
	 * Sets the maximized control in the sashForm according to the selection 
	 * in the stepFunctionCombo.
	 */
	private void setComposite() {
		int targetWidth = 100;
		int targetHeight = 100;
		int sashX = sashForm.getBounds().x;
		int sashY = sashForm.getBounds().y;
		
		if (currentAxis.getStepfunctionString().equals("Add") ||
			currentAxis.getStepfunctionString().equals("Multiply")) {
				this.sashForm.setMaximizedControl(
						motorAxisStartStopStepwidthComposite);
				this.motorAxisStartStopStepwidthComposite.setCurrentAxis(
						currentAxis, stepcount);
				targetWidth = motorAxisStartStopStepwidthComposite.
						getTargetWidth() + sashX;
				targetHeight = motorAxisStartStopStepwidthComposite.
						getTargetHeight() + sashY;
		} else if(currentAxis.getStepfunctionString().equals("File")) {
			this.sashForm.setMaximizedControl(motorAxisFileComposite);
			this.motorAxisFileComposite.setAxis(currentAxis);
			targetWidth = motorAxisFileComposite.getTargetWidth() + sashX;
			targetHeight = motorAxisFileComposite.getTargetHeight() + sashY;
		} else if(currentAxis.getStepfunctionString().equals("Plugin")) {
			this.sashForm.setMaximizedControl(motorAxisPluginComposite);
			this.motorAxisPluginComposite.setAxis(currentAxis, scanModule);
			targetWidth = motorAxisPluginComposite.getTargetWidth() + sashX;
			targetHeight = motorAxisPluginComposite.getTargetHeight() + sashY;
		} else if(currentAxis.getStepfunctionString().equals("Positionlist")) {
			this.sashForm.setMaximizedControl(motorAxisPositionlistComposite);
			this.motorAxisPositionlistComposite.setAxis(currentAxis);
			targetWidth = motorAxisPositionlistComposite.getTargetWidth() 
							+ sashX;
			targetHeight = motorAxisPositionlistComposite.getTargetHeight() 
							+ sashY;
		} else {
			this.sashForm.setMaximizedControl(emptyComposite);
		}
		sc.setMinSize(targetWidth, targetHeight);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEvent(ModelUpdateEvent modelUpdateEvent) {

		if(modelUpdateEvent != null) {
			logger.debug(modelUpdateEvent.getSender());
			logger.debug(modelUpdateEvent.getMessage());
		}
		
		if(modelUpdateListenerSuspended) return;
		
		removeListeners();
		
		if(this.currentAxis != null) {
			this.setPartName(this.currentAxis.getMotorAxis().getFullIdentifyer());
			this.stepFunctionCombo.setText(this.currentAxis.getStepfunctionString());
			this.positionModeCombo.setText(PositionMode.typeToString(
					this.currentAxis.getPositionMode()));
			
			this.stepFunctionCombo.setEnabled(true);
			this.positionModeCombo.setEnabled(true);
			
			/* this.motorAxisStartStopStepwidthComposite.setCurrentAxis(null, 0);
			this.motorAxisFileComposite.setAxis(null);
			this.motorAxisPluginComposite.setAxis(null, null);
			this.motorAxisPositionlistComposite.setAxis(null); */
			
			setComposite();
			
			top.layout();
			top.setVisible(true);
		} else {
			this.setPartName("No Motor Axis selected");
			this.stepFunctionCombo.setText("");
			this.positionModeCombo.setText("");
			this.stepFunctionCombo.setEnabled(false);
			this.positionModeCombo.setEnabled(false);
			top.setVisible(false);
		}
		addListeners();
	}
	
	/**
	 * 
	 */
	protected void suspendModelUpdateListener() {
		currentAxis.removeModelUpdateListener(this);
		modelUpdateListenerSuspended = true;
	}
	
	/**
	 * 
	 */
	protected void resumeModelUpdateListener() {
		currentAxis.addModelUpdateListener(this);
		modelUpdateListenerSuspended = false;
	}
	
	/*
	 * 
	 */
	private void addListeners() {
		this.stepFunctionCombo.
				addSelectionListener(stepFunctionComboSelectionListener);
		this.positionModeCombo.
				addSelectionListener(positionModeComboSelectionListener);
	}
	
	/*
	 * 
	 */
	private void removeListeners() {
		this.stepFunctionCombo.
				removeSelectionListener(stepFunctionComboSelectionListener);
		this.positionModeCombo.
				removeSelectionListener(positionModeComboSelectionListener);
	}
	
	// ************************************************************************
	// **************************** Listeners *********************************
	// ************************************************************************

	/**
	 * {@link org.eclipse.swt.events.SelectionListener} of StepFunctionCombo.
	 */
	private class StepFunctionComboSelectionListener implements SelectionListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(currentAxis != null) {
				if(currentAxis.getStepfunctionString().equals(
						stepFunctionCombo.getText())) {
					return;
				}
				currentAxis.setStepfunction(stepFunctionCombo.getText());
				setComposite();
			}
		}
	}
	
	/**
	 * {@link org.eclipse.swt.events.SelectionListener} of StepFunctionCombo.
	 */
	private class PositionModeComboSelectionListener implements SelectionListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(currentAxis != null) {
				currentAxis.setPositionMode(
						PositionMode.stringToType(positionModeCombo.getText()));
			}
		}
	}
}
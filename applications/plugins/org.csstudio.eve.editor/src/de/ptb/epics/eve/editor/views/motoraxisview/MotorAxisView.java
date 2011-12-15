package de.ptb.epics.eve.editor.views.motoraxisview;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
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
public class MotorAxisView extends ViewPart implements IModelUpdateListener, ISelectionListener {

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
	private StartStopStepwidthComposite startStopStepwidthComposite;
	private DateTimeComposite dateTimeComposite;
	private FileComposite fileComposite;
	private PluginComposite pluginComposite;
	private PositionlistComposite positionlistComposite;
	
	private SashForm sashForm;
	
	private String[] stepfunctions;
	private List<String> discreteStepfunctions;
	
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
		
		this.discreteStepfunctions = new LinkedList<String>();
		for(String s : this.stepfunctions) {
			if(!(s.equals("Add") || s.equals("Multiply"))) {
				this.discreteStepfunctions.add(s);
			}
		}
		
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
		this.startStopStepwidthComposite = 
				new StartStopStepwidthComposite(sashForm, SWT.NONE, this);
		this.dateTimeComposite = new DateTimeComposite(sashForm, SWT.NONE, this);
		this.fileComposite = 
				new FileComposite(sashForm, SWT.NONE, this);
		this.pluginComposite = 
				new PluginComposite(sashForm, SWT.NONE, this);
		this.positionlistComposite = 
				new PositionlistComposite(sashForm, SWT.NONE, this);
		
		sashForm.setMaximizedControl(this.emptyComposite);
		
		// content not visible after creation of the view
		top.setVisible(false);
		
		// listen to selection changes (the selected device's options are 
		// displayed)
		getSite().getWorkbenchWindow().getSelectionService().
				addSelectionListener(this);

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
	 */
	private void setAxis(final Axis axis) {
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
		this.scanModule = null;
		
		if(this.currentAxis != null) {
			this.scanModule = axis.getScanModule();
			// current axis not null -> listen to updates on it
			this.currentAxis.addModelUpdateListener(this);
		}
		// update elements (calls the inherited method from the UpdateListener 
		// with argument null indicating an internal call)
		updateEvent(null);
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
				// use code block in future implementations that use the 
				// DateTimeComposite...
				/*if(currentAxis.getMotorAxis().getPosition().getType().equals(
						de.ptb.epics.eve.data.DataTypes.DATETIME)) {
					this.sashForm.setMaximizedControl(dateTimeComposite);
				} else {*/
				this.sashForm.setMaximizedControl(
						startStopStepwidthComposite);
				this.startStopStepwidthComposite.setCurrentAxis(currentAxis);
				targetWidth = startStopStepwidthComposite.
						getTargetWidth() + sashX;
				targetHeight = startStopStepwidthComposite.
						getTargetHeight() + sashY;
				/*}*/
		} else if(currentAxis.getStepfunctionString().equals("File")) {
			this.sashForm.setMaximizedControl(fileComposite);
			this.fileComposite.setAxis(currentAxis);
			targetWidth = fileComposite.getTargetWidth() + sashX;
			targetHeight = fileComposite.getTargetHeight() + sashY;
		} else if(currentAxis.getStepfunctionString().equals("Plugin")) {
			this.sashForm.setMaximizedControl(pluginComposite);
			this.pluginComposite.setAxis(currentAxis, scanModule);
			targetWidth = pluginComposite.getTargetWidth() + sashX;
			targetHeight = pluginComposite.getTargetHeight() + sashY;
		} else if(currentAxis.getStepfunctionString().equals("Positionlist")) {
			this.sashForm.setMaximizedControl(positionlistComposite);
			this.positionlistComposite.setAxis(currentAxis);
			targetWidth = positionlistComposite.getTargetWidth() 
							+ sashX;
			targetHeight = positionlistComposite.getTargetHeight() 
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
			this.setPartName(
					this.currentAxis.getMotorAxis().getFullIdentifyer());
			this.positionModeCombo.setText(PositionMode.typeToString(
					this.currentAxis.getPositionMode()));
			if(this.currentAxis.getMotorAxis().getGoto().isDiscrete()) {
				this.stepFunctionCombo.setItems(
						this.discreteStepfunctions.toArray(new String[0]));
				this.positionModeLabel.setVisible(false);
				this.positionModeCombo.setVisible(false);
			} else {
				this.stepFunctionCombo.setItems(stepfunctions);
				this.positionModeLabel.setVisible(true);
				this.positionModeCombo.setVisible(true);
			}
			this.stepFunctionCombo.setText(
					this.currentAxis.getStepfunctionString());
			this.stepFunctionCombo.setEnabled(true);
			this.positionModeCombo.setEnabled(true);
			
			this.startStopStepwidthComposite.setCurrentAxis(null);
			this.fileComposite.setAxis(null);
			this.pluginComposite.setAxis(null, null);
			this.positionlistComposite.setAxis(null);
			
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
	
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {

		System.out.println("selectionChanged in MotorAxisView: " + selection);
		if(selection instanceof IStructuredSelection) {
			System.out.println("   IStructuredSelection");
			if(((IStructuredSelection) selection).size() == 0) {
				System.out.println("      Size = null");
// Nur wenn keine Achsen mehr da sind, soll die View wirklich gelöscht werden!
//				if (scanModule.getChannels())
				System.out.println("   this.scanModule: " + this.scanModule);
				System.out.println("   scanModule: " + this.scanModule);
				System.out.println("   currentAxis: " + currentAxis);
				if (this.scanModule != null) {
					System.out.println("   Axes: " + scanModule.getAxes().length);
					if (scanModule.getAxes().length == 0)
						clearView();
				}
				return;
			}
			// since at any given time this view can only display options of 
			// one device we take the first element of the selection
			Object o = ((IStructuredSelection) selection).toList().get(0);
			if (o instanceof Axis) {
				System.out.println("   Achse: " + ((Axis)o).getMotorAxis().getFullIdentifyer());
				// Display Channel Settings in DetectorChannelView
				// Hier müssen jetzt die ganzen Detektoreinstellungen angezeigt werden
				// Das soll nicht mehr über einen anderen Modus erfolgen
				// Änderungen in der ScanModulView sollen keinen direkten
				// Aufruf mehr in die DetectorChannelView haben.
				// 1. Aufruf wegnehmen
				// 2. Hier neue Aktualisierung einfügen
				// Dann gibt es zwischendurch keine Aktualisierung!
				setAxis((Axis)o);
			}
			else {
				System.out.println(   "Instance: " + o);
//				setChannel(null);
//				updateEvent(null);
			}
		}
		
	}
	/**
	 *  @author scherr
	 *  
	 */
	private void clearView() {

		// currentAxis is null
		this.setPartName("No Motor Axis selected");
		this.stepFunctionCombo.setText("");
		this.positionModeCombo.setText("");
		this.stepFunctionCombo.setEnabled(false);
		this.positionModeCombo.setEnabled(false);
		top.setVisible(false);
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
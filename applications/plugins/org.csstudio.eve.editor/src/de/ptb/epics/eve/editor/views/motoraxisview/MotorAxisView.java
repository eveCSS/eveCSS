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
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.graphical.editparts.ScanDescriptionEditPart;
import de.ptb.epics.eve.editor.graphical.editparts.ScanModuleEditPart;

/**
 * <code>MotorAxisView</code> shows the attributes of a 
 * {@link de.ptb.epics.eve.data.measuringstation.MotorAxis} and allows 
 * modifications.
 * 
 * @author Hartmut Scherr
 * @author Marcus Michalsky
 */
public class MotorAxisView extends ViewPart implements ISelectionListener {

	/** the unique identifier of the view. */
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
	
	// the utmost composite
	private Composite top = null;
	// Scrolled Composite wrapping top to enable scrolling
	private ScrolledComposite sc = null;

	// GUI: "Step function: <combo> x"
	private Label stepfunctionLabel;
	private Combo stepFunctionCombo;
	private Label stepfunctionErrorLabel;
	
	private StepFunctionComboSelectionListener 
			stepFunctionComboSelectionListener;
	
	// GUI: "Position mode: <combo> x"
	private Label positionModeLabel;
	private Combo positionModeCombo;
	private Label positionModeErrorLabel;
	
	private PositionModeComboSelectionListener 
			positionModeComboSelectionListener;
	
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
				new StartStopStepwidthComposite(sashForm, SWT.NONE);
		this.dateTimeComposite = new DateTimeComposite(sashForm, SWT.NONE);
		this.fileComposite = 
				new FileComposite(sashForm, SWT.NONE);
		this.pluginComposite = 
				new PluginComposite(sashForm, SWT.NONE);
		this.positionlistComposite = 
				new PositionlistComposite(sashForm, SWT.NONE);
		
		sashForm.setMaximizedControl(this.emptyComposite);
		
		// content not visible after creation of the view
		top.setVisible(false);
		
		// listen to selection changes (the selected axis' properties are 
		// displayed for editing)
		getSite().getWorkbenchWindow().getSelectionService().
				addSelectionListener(this);
	}
	// ************************************************************************
	// ********************** end of createPartControl ************************
	// ************************************************************************
	
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
			logger.debug("set axis (" + axis.getAbstractDevice().
					getFullIdentifyer() + ")");
		} else {
			logger.debug("set axis (null)");
		}
		
		// set the new axis as current axis
		this.currentAxis = axis;
		this.scanModule = null;
		
		if(this.currentAxis != null) {
			this.scanModule = axis.getScanModule();
		}
		
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
			this.startStopStepwidthComposite.setCurrentAxis(null);
			this.fileComposite.setAxis(null);
			this.pluginComposite.setAxis(null, null);
			this.positionlistComposite.setAxis(null);
			
			setComposite();
			
			top.layout();
			top.setVisible(true);
		} else {
			this.setPartName("No Motor Axis selected");
			top.setVisible(false);
		}
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
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		logger.debug("selection changed");
		
		if(selection instanceof IStructuredSelection) {
			if(((IStructuredSelection) selection).size() == 0) {
/*				if (this.scanModule != null) {
					if (scanModule.getAxes().length == 0) {
						if(logger.isDebugEnabled()) {
							logger.debug("selection is empty, scanModule: " + 
									this.scanModule.getId() + "-> ignore"); 
						}
						setAxis(null);
					}
				} else {
					logger.debug(
					  "selection ist empty, no scanModule available -> ignore");
				}*/
				return;
			}
			// since at any given time this view can only display options of 
			// one motor axis, we take the first element of the selection
			Object o = ((IStructuredSelection) selection).toList().get(0);
			if (o instanceof Axis) {
				// set new Axis
				if(logger.isDebugEnabled()) {
					logger.debug("Axis: " + ((Axis)o).getMotorAxis().
							getFullIdentifyer() + " selected.");
				}
				setAxis((Axis)o);
			} else if (o instanceof ScanModuleEditPart) {
				// ScanModule was selected
				if(logger.isDebugEnabled()) {
					logger.debug("selection is ScanModuleEditPart: " + o);
					logger.debug("ScanModule: " + ((ScanModule)
							((ScanModuleEditPart)o).getModel()).getId() + 
							" selected."); 
				}
				if (this.scanModule != null && !(this.scanModule.equals(
					(ScanModule)((ScanModuleEditPart)o).getModel()))) {
						setAxis(null);
				}
			} else if (o instanceof ScanDescriptionEditPart) {
				logger.debug("selection is ScanDescriptionEditPart: " + o);
				setAxis(null);
			} else {
				logger.debug("unknown selection -> ignore: " + o);
			}
		}
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
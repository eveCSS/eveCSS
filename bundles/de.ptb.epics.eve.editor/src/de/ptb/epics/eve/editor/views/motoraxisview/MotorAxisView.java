package de.ptb.epics.eve.editor.views.motoraxisview;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.PositionMode;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;
import de.ptb.epics.eve.data.scandescription.Stepfunctions;
import de.ptb.epics.eve.data.scandescription.defaults.DefaultsManager;
import de.ptb.epics.eve.data.scandescription.defaults.axis.DefaultsAxis;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.gef.editparts.ChainEditPart;
import de.ptb.epics.eve.editor.gef.editparts.ScanDescriptionEditPart;
import de.ptb.epics.eve.editor.gef.editparts.ScanModuleEditPart;
import de.ptb.epics.eve.editor.views.EditorViewPerspectiveListener;
import de.ptb.epics.eve.editor.views.IEditorView;
import de.ptb.epics.eve.editor.views.motoraxisview.addmultiplycomposite.AddMultiplyComposite;
import de.ptb.epics.eve.editor.views.motoraxisview.filecomposite.FileComposite;
import de.ptb.epics.eve.editor.views.motoraxisview.plugincomposite.PluginComposite;
import de.ptb.epics.eve.editor.views.motoraxisview.positionlistcomposite.PositionlistComposite;
import de.ptb.epics.eve.editor.views.motoraxisview.rangecomposite.RangeComposite;
import de.ptb.epics.eve.util.io.StringUtil;

/**
 * <code>MotorAxisView</code> shows the attributes of a 
 * {@link de.ptb.epics.eve.data.measuringstation.MotorAxis} and allows 
 * modifications.
 * 
 * @author Hartmut Scherr
 * @author Marcus Michalsky
 */
public class MotorAxisView extends ViewPart implements IEditorView,
		ISelectionListener, PropertyChangeListener {

	/** the unique identifier of the view. */
	public static final String ID = 
		"de.ptb.epics.eve.editor.views.MotorAxisView";

	// logging
	private static final Logger LOGGER = 
		Logger.getLogger(MotorAxisView.class.getName());

	// the axis that should be presented
	private Axis currentAxis;
	
	private ScanModule scanModule;

	// the utmost composite
	private Composite top;
	// Scrolled Composite wrapping top to enable scrolling
	private ScrolledComposite sc;

	private CAComposite caComposite;
	
	// GUI: "Step function: <combo> x"
	private Label stepfunctionLabel;
	private Combo stepFunctionCombo;
	
	private StepFunctionComboSelectionListener 
			stepFunctionComboSelectionListener;
	
	// GUI: "Position mode: <combo> x"
	private Label positionModeLabel;
	private Combo positionModeCombo;
	
	private PositionModeComboSelectionListener 
			positionModeComboSelectionListener;
	
	private Composite emptyComposite;
	private AddMultiplyComposite addMultipyComposite;
	private FileComposite fileComposite;
	private PluginComposite pluginComposite;
	private PositionlistComposite positionlistComposite;
	private RangeComposite rangeComposite;
	
	private SashForm sashForm;
	
	private EditorViewPerspectiveListener editorViewPerspectiveListener;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite parent) {
		parent.setLayout(new FillLayout());
		
		// if no measuring station loaded -> show error, do nothing
		if(Activator.getDefault().getMeasuringStation() == null) {
			final Label errorLabel = new Label(parent, SWT.NONE);
			errorLabel.setText("No Measuring Station has been loaded. " +
							   "Please check Preferences!");
			return;
		}
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		GridData gridData;
		
		this.sc = new ScrolledComposite(parent, SWT.H_SCROLL | 
										SWT.V_SCROLL | SWT.BORDER);
		
		this.top = new Composite(sc, SWT.NONE);
		this.top.setLayout(gridLayout);
		
		sc.setContent(this.top);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		
		this.caComposite = new CAComposite(this.top, SWT.NONE);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		this.caComposite.setLayoutData(gridData);
		
		Label separator = new Label(this.top, SWT.SEPARATOR | SWT.SHADOW_OUT
				| SWT.HORIZONTAL);
		separator.setBounds(50,80,100,50);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		separator.setLayoutData(gridData);
		
		this.stepfunctionLabel = new Label(this.top, SWT.NONE);
		this.stepfunctionLabel.setText("Step Function: ");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalIndent = 5;
		this.stepfunctionLabel.setLayoutData(gridData);
		
		this.stepFunctionCombo = new Combo(this.top, SWT.READ_ONLY);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalIndent = 5;
		this.stepFunctionCombo.setLayoutData(gridData);
		this.stepFunctionComboSelectionListener = 
				new StepFunctionComboSelectionListener();
		this.stepFunctionCombo.addSelectionListener(
				stepFunctionComboSelectionListener);
		
		this.positionModeLabel = new Label(this.top, SWT.NONE);
		this.positionModeLabel.setText("Position Mode: ");
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
		
		sashForm = new SashForm(top, SWT.VERTICAL);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 2;
		sashForm.setLayoutData(gridData);
		
		this.emptyComposite = new Composite(sashForm, SWT.NONE);
		this.addMultipyComposite = 
				new AddMultiplyComposite(sashForm, SWT.NONE);
		this.fileComposite = 
				new FileComposite(sashForm, SWT.NONE);
		this.pluginComposite = 
				new PluginComposite(sashForm, SWT.NONE);
		this.positionlistComposite = 
				new PositionlistComposite(sashForm, SWT.NONE);
		this.rangeComposite = 
				new RangeComposite(sashForm, SWT.NONE);
		
		sashForm.setMaximizedControl(this.emptyComposite);
		
		top.setVisible(false);
		
		// listen to selection changes (the selected axis' properties are 
		// displayed for editing)
		getSite().getWorkbenchWindow().getSelectionService().
				addSelectionListener(this);
		
		// reset view if last editor was closed
		this.editorViewPerspectiveListener = new EditorViewPerspectiveListener(
				this);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.addPerspectiveListener(this.editorViewPerspectiveListener);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		LOGGER.debug("got focus -> forward to top composite");
		this.top.setFocus();
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
			LOGGER.debug("set axis (" + axis.getAbstractDevice().
					getFullIdentifyer() + ")");
		} else {
			LOGGER.debug("set axis (null)");
		}
		
		if (this.currentAxis != null) {
			this.scanModule.removePropertyChangeListener("removeAxis", this);
			this.currentAxis.removePropertyChangeListener(
					Axis.STEPFUNCTION_PROP, this);
			this.currentAxis.removePropertyChangeListener(
					Axis.POSITON_MODE_PROP, this);
			this.currentAxis.getMotorAxis().disconnect();
		}
		
		// set the new axis as current axis
		this.currentAxis = axis;
		this.scanModule = null;
		
		if(this.currentAxis != null) {
			top.setVisible(true);
			removeListeners();
			
			this.scanModule = this.currentAxis.getScanModule();
			this.scanModule.addPropertyChangeListener("removeAxis", this);
			this.currentAxis.addPropertyChangeListener(
					Axis.STEPFUNCTION_PROP, this);
			this.currentAxis.addPropertyChangeListener(
					Axis.POSITON_MODE_PROP, this);
			
			this.setPartName(
					this.currentAxis.getMotorAxis().getName());
			
			this.positionModeCombo.setText(PositionMode.typeToString(
					this.currentAxis.getPositionMode()));
			if(this.currentAxis.getMotorAxis().getGoto().isDiscrete()) {
				this.positionModeLabel.setVisible(false);
				this.positionModeCombo.setVisible(false);
			} else {
				this.positionModeLabel.setVisible(true);
				this.positionModeCombo.setVisible(true);
			}
			this.stepFunctionCombo.setItems(StringUtil.getStringList(
					this.currentAxis.getStepfunctions()).toArray(new String[0]));
			this.stepFunctionCombo.setText(
					this.currentAxis.getStepfunction().toString());
			this.addMultipyComposite.setAxis(null);
			this.fileComposite.setAxis(null);
			this.pluginComposite.setAxis(null);
			this.positionlistComposite.setAxis(null);
			this.rangeComposite.setAxis(null);
			
			this.currentAxis.getMotorAxis().connect();
			
			setComposite();
			
			top.layout();
			addListeners();
		} else {
			this.setPartName("No Motor Axis selected");
			top.setVisible(false);
		}
		this.caComposite.setAxis(this.currentAxis);
	}
	
	/*
	 * Sets the maximized control in the sashForm according to the selection 
	 * in the stepFunctionCombo.
	 */
	private void setComposite() {
		switch (this.currentAxis.getStepfunction()) {
		case ADD:
		case MULTIPLY:
			this.sashForm.setMaximizedControl(
					addMultipyComposite);
			this.addMultipyComposite.setAxis(currentAxis);
			break;
		case FILE:
			this.sashForm.setMaximizedControl(fileComposite);
			this.fileComposite.setAxis(currentAxis);
			break;
		case PLUGIN:
			this.sashForm.setMaximizedControl(pluginComposite);
			this.pluginComposite.setAxis(currentAxis);
			break;
		case POSITIONLIST:
			this.sashForm.setMaximizedControl(positionlistComposite);
			this.positionlistComposite.setAxis(currentAxis);
			break;
		case RANGE:
			this.sashForm.setMaximizedControl(rangeComposite);
			this.rangeComposite.setAxis(currentAxis);
			break;
		default:
			this.sashForm.setMaximizedControl(emptyComposite);
			break;
		}
		sc.setMinSize(this.sashForm.getMaximizedControl().getBounds().width + 
				sashForm.getBounds().x, 
				this.sashForm.getMaximizedControl().getBounds().height + 
				sashForm.getBounds().y);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset() {
		this.setAxis(null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			LOGGER.debug("selection changed");
			
		if(selection instanceof IStructuredSelection) {
			if(((IStructuredSelection) selection).size() == 0) {
				return;
			}

			// since at any given time this view can only display options of 
			// one motor axis, we take the first element of the selection
			Object o = ((IStructuredSelection) selection).toList().get(0);
			if (o instanceof Axis && ((Axis)o).getScanModule().getType().equals(ScanModuleTypes.CLASSIC)) {
				// set new Axis
				if(LOGGER.isDebugEnabled()) {
					LOGGER.debug("Axis: " + ((Axis)o).getMotorAxis().
							getFullIdentifyer() + " selected.");
				}
				setAxis((Axis)o);
			} else if (o instanceof ScanModuleEditPart) {
				// ScanModule was selected
				if(LOGGER.isDebugEnabled()) {
					LOGGER.debug("selection is ScanModuleEditPart: " + o);
					LOGGER.debug("ScanModule: " + 
							((ScanModuleEditPart)o).getModel().getId() + 
							" selected."); 
				}
				if (this.scanModule != null && !this.scanModule.equals(
					((ScanModuleEditPart)o).getModel())) {
						setAxis(null);
				}
			} else if (o instanceof ChainEditPart) {
				LOGGER.debug("selection is ChainEditPart: " + o);
				setAxis(null);
			} else if (o instanceof ScanDescriptionEditPart) {
				LOGGER.debug("selection is ScanDescriptionEditPart: " + o);
				setAxis(null);
			} else {
				LOGGER.debug("unknown selection -> ignore: " + o);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getOldValue().equals(currentAxis)) {
			// current Axis will be removed
			setAxis(null);
		} else if (e.getPropertyName().equals(Axis.STEPFUNCTION_PROP)) {
			this.stepFunctionCombo.setItems(StringUtil.getStringList(
					this.currentAxis.getStepfunctions()).toArray(new String[0]));
			this.stepFunctionCombo.setText(
					this.currentAxis.getStepfunction().toString());
			this.setComposite();
		} else if (e.getPropertyName().equals(Axis.POSITON_MODE_PROP)) {
			this.positionModeCombo.setText(
				PositionMode.typeToString(this.currentAxis.getPositionMode()));
		}
	}
	
	/*
	 * 
	 */
	private void addListeners() {
		stepFunctionCombo.addSelectionListener(
				stepFunctionComboSelectionListener);
		positionModeCombo.addSelectionListener(
				positionModeComboSelectionListener);
	}

	/*
	 * 
	 */
	private void removeListeners() {
		stepFunctionCombo.removeSelectionListener(
				stepFunctionComboSelectionListener);
		positionModeCombo.removeSelectionListener(
				positionModeComboSelectionListener);
	}	
	
	private class StepFunctionComboSelectionListener extends 
					SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			LOGGER.debug("step function modified");
			if(currentAxis != null) {
				if (currentAxis.getStepfunction().equals(
						Stepfunctions.getEnum(stepFunctionCombo.getText()))) {
					return;
				}
				currentAxis.removePropertyChangeListener(
						Axis.STEPFUNCTION_PROP, MotorAxisView.this);
				currentAxis.setStepfunction(Stepfunctions.getEnum(
						stepFunctionCombo.getText()));
				currentAxis.addPropertyChangeListener(
						Axis.STEPFUNCTION_PROP, MotorAxisView.this);
				DefaultsAxis defMa = Activator.getDefault().getDefaults()
						.getAxis(currentAxis.getMotorAxis().getID());
				if (defMa != null && defMa.getStepfunction().equals(
						currentAxis.getStepfunction())) {
					DefaultsManager.transferDefaults(defMa, currentAxis);
				}
				setComposite();
			}
		}
	}
	
	private class PositionModeComboSelectionListener extends 
					SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			LOGGER.debug("position mode modified");
			if(currentAxis != null) {
				currentAxis.setPositionMode(
						PositionMode.stringToType(positionModeCombo.getText()));
			}
		}
	}
}
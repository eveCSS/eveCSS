package de.ptb.epics.eve.editor.views.plotwindowview;

import java.beans.PropertyChangeListener;

import org.apache.log4j.Logger;
import org.csstudio.swt.xygraph.figures.Trace.TraceType;
import org.csstudio.swt.xygraph.figures.Trace.PointStyle;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.YAxis;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.data.PlotModes;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.graphical.editparts.ScanDescriptionEditPart;
import de.ptb.epics.eve.editor.graphical.editparts.ScanModuleEditPart;

/**
 * <code>PlotWindowView</code> contains all configuration parts, corresponding 
 * to plots. It shows the current configuration of a 
 * {@link de.ptb.epics.eve.data.scandescription.PlotWindow} set by 
 * {@link #setPlotWindow(PlotWindow, ScanModule)}.
 * 
 * @author ?
 * @author Marcus Michalsky
 * @author Hartmut Scherr
 */
public class PlotWindowView extends ViewPart implements ISelectionListener, 
					IModelUpdateListener, PropertyChangeListener  {

	/**
	 * The unique identifier of <code>PlotWindowView</code>.
	 */
	public static final String ID = 
			"de.ptb.epics.eve.editor.views.PlotWindowView";
	
	// logging 
	private static final Logger logger = 
		Logger.getLogger(PlotWindowView.class.getName());
	
	// *******************************************************************
	// ********************** underlying model ***************************
	// *******************************************************************
	
	// the plot window containing all data related to the plot
	private PlotWindow plotWindow;
	
	// a direct reference to the two available axis
	// (to avoid writing plotWindow.getAxes.get(0/1) all the time)
	private YAxis yAxis1;
	private YAxis yAxis2;

	// the scan module which contains available motors and detector channels
	// that could be selected via the select boxes
	private ScanModule scanModule;
	
	// the available motor axes and detector channels
	// (to avoid iterating over plotWindow.getAxes / getChannels all the time)
	private Axis[] availableMotorAxes;
	private Channel[] availableDetectorChannels;
	
	// *******************************************************************
	// ******************* end of: underlying model **********************
	// *******************************************************************
	
	// the contents of this view
	private Composite top;
	
	// the configurations for the three axis will be expandable
	private ExpandBar bar;
	
	// "General" configurations, concerning the x axis
	private Composite xAxisComposite;
	
	// configurations for the first y axis are in here
	private Composite yAxis1Composite;
	
	// configurations for the second y axis are in here
	private Composite yAxis2Composite;
	
	// *********************************************
	// elements for the "general" composite (x axis)
	// *********************************************
	
	private ExpandItem itemGeneral;
	
	// GUI: Motor Axis: "Select-Box":<motor-name>
	private Label motorAxisLabel;
	private Combo motorAxisComboBox;
	private ControlDecoration motorAxisComboControlDecoration;
	private MotorAxisComboBoxSelectionListener 
			motorAxisComboBoxSelectionListener;
	
	// check box indicating whether the plot should be cleared before
	private Button preInitWindowCheckBox;
	private PreInitWindowCheckBoxSelectionListener 
			preInitWindowCheckBoxSelectionListener;
	
	// GUI: Scale Type: "Select-Box":{linear,log} x
	private Label scaleTypeLabel;
	private Combo scaleTypeComboBox;
	private ScaleTypeComboBoxSelectionListener 
			scaleTypeComboBoxSelectionListener;
	// *********************************************
	// end of: elements for the "general composite (x axis)
	// *********************************************
	
	// *********************************************
	// elements for the first y axis composite
	// *********************************************
	
	private ExpandItem itemYAxis1;
	
	// GUI: Detector Channel: "Select-Box":<detector-channels> x
	private Label yAxis1DetectorChannelLabel;
	private Combo yAxis1DetectorChannelComboBox;
	private ControlDecoration yAxis1DetectorChannelComboControlDecoration;
	private YAxis1DetectorChannelComboBoxSelectionListener
			yAxis1DetectorChannelComboBoxSelectionListener;
	
	// GUI: Normalize Channel: "Select-Box":<detector-channels> x
	private Label yAxis1NormalizeChannelLabel;
	private Combo yAxis1NormalizeChannelComboBox;
	private YAxis1NormalizeChannelComboBoxSelectionListener
			yAxis1NormalizeChannelComboBoxSelectionListener;
	
	// GUI: Scale Type: "Select-Box":{linear,log} x
	private Label yAxis1ScaletypeLabel;
	private Combo yAxis1ScaletypeComboBox;
	private YAxis1ScaleTypeComboBoxSelectionListener
			yAxis1ScaleTypeComboBoxSelectionListener;
	
	private ColorFieldEditor yAxis1ColorFieldEditor;
	private YAxis1ColorFieldEditorPropertyChangeListener
			yAxis1ColorFieldEditorPropertyChangeListener;
	private Combo yAxis1ColorComboBox; 
	private YAxis1ColorComboBoxSelectionListener
			yAxis1ColorComboBoxSelectionListener;
	
	private Label yAxis1LinestyleLabel;
	private Combo yAxis1LinestyleComboBox;
	private YAxis1LineStyleComboBoxSelectionListener
			yAxis1LineStyleComboBoxSelectionListener;
	
	private Label yAxis1MarkstyleLabel;
	private Combo yAxis1MarkstyleComboBox;
	private YAxis1MarkStyleComboBoxSelectionListener
			yAxis1MarkStyleComboBoxSelectionListener;
	// *********************************************
	// end of: elements for the first y axis composite
	// *********************************************
	
	// *********************************************
	// elements for the second y axis composite
	// *********************************************
	private ExpandItem itemYAxis2;
	
	// GUI: Detector Channel: "Select-Box":<detector-channels> x
	private Label yAxis2DetectorChannelLabel;
	private Combo yAxis2DetectorChannelComboBox;
	private ControlDecoration yAxis2DetectorChannelComboControlDecoration;
	private YAxis2DetectorChannelComboBoxSelectionListener
			yAxis2DetectorChannelComboBoxSelectionListener;
	
	// GUI: Normalize Channel: "Select-Box":<detector-channels> x
	private Label yAxis2NormalizeChannelLabel;
	private Combo yAxis2NormalizeChannelComboBox;
	private YAxis2NormalizeChannelComboBoxSelectionListener
			yAxis2NormalizeChannelComboBoxSelectionListener;
	
	// GUI: Scale Type: "Select-Box":{linear,log} x
	private Label yAxis2ScaletypeLabel;
	private Combo yAxis2ScaletypeComboBox;
	private YAxis2ScaleTypeComboBoxSelectionListener
			yAxis2ScaleTypeComboBoxSelectionListener;
	
	private ColorFieldEditor yAxis2ColorFieldEditor;
	private YAxis2ColorFieldEditorPropertyChangeListener
			yAxis2ColorFieldEditorPropertyChangeListener;
	private Combo yAxis2ColorComboBox;
	private YAxis2ColorComboBoxSelectionListener
			yAxis2ColorComboBoxSelectionListener;
	
	private Label yAxis2LinestyleLabel;
	private Combo yAxis2LinestyleComboBox;
	private YAxis2LineStyleComboBoxSelectionListener
			yAxis2LineStyleComboBoxSelectionListener;
	
	private Label yAxis2MarkstyleLabel;
	private Combo yAxis2MarkstyleComboBox;
	private YAxis2MarkStyleComboBoxSelectionListener
			yAxis2MarkStyleComboBoxSelectionListener;
	
	// *********************************************
	// end of: elements for the second y axis composite	
	// *********************************************
	
	private Image errorImage;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite parent) {
		parent.setLayout(new FillLayout());
		
		// if there is no measuring station loaded, show error message
		if(Activator.getDefault().getMeasuringStation() == null) {
			final Label errorLabel = new Label(parent, SWT.NONE);
			errorLabel.setText("No Measuring Station has been loaded. " +
					"Please check Preferences!");
			return;
		}
		
		this.errorImage = FieldDecorationRegistry.getDefault().
			getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage();
		
		// initialize the contents composite with a grid layout
		top = new Composite(parent, SWT.NONE);
		top.setLayout(new GridLayout());
		
		// initialize the expand bar with a grid layout
		this.bar = new ExpandBar(this.top, SWT.V_SCROLL);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		this.bar.setLayoutData(gridData);
		
		initXAxisComposite();
		initYAxis1Composite();
		initYAxis2Composite();
		
		
		
		this.itemGeneral = new ExpandItem(this.bar, SWT.NONE, 0);
		itemGeneral.setText("General");
		itemGeneral.setHeight(
				this.xAxisComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		itemGeneral.setControl(this.xAxisComposite);
		itemGeneral.setExpanded(true);
		
		this.itemYAxis1 = new ExpandItem(this.bar, SWT.NONE, 0);
		itemYAxis1.setText("Y-Axis 1");
		itemYAxis1.setHeight(
				this.yAxis1Composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		itemYAxis1.setControl(this.yAxis1Composite);
		itemYAxis1.setExpanded(true);
		
		this.itemYAxis2 = new ExpandItem(this.bar, SWT.NONE, 0);
		itemYAxis2.setText("Y-Axis 2");
		itemYAxis2.setHeight(
				this.yAxis2Composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		itemYAxis2.setControl(this.yAxis2Composite);
		
		top.setVisible(false);

		// listen to selection changes (the selected device's options are 
		// displayed)
		getSite().getWorkbenchWindow().getSelectionService().
				addSelectionListener(this);

	} 

	/*
	 * initialize contents of the x axis composite
	 */
	private void initXAxisComposite() {
		this.xAxisComposite = new Composite(this.bar, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		this.xAxisComposite.setLayout(gridLayout);
		
		// GUI: Motor Axis: <Combo>
		this.motorAxisLabel = new Label(this.xAxisComposite, SWT.NONE);
		this.motorAxisLabel.setText("Motor Axis:");
		this.motorAxisComboBox = new Combo(this.xAxisComposite, SWT.READ_ONLY);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = 7;
		this.motorAxisComboBox.setLayoutData(gridData);
		this.motorAxisComboControlDecoration = new ControlDecoration(
				motorAxisComboBox, SWT.LEFT);
		this.motorAxisComboControlDecoration.setImage(errorImage);
		this.motorAxisComboControlDecoration.setDescriptionText(
				"xAxis must not be empty!");
		this.motorAxisComboControlDecoration.hide();
		this.motorAxisComboBoxSelectionListener = 
				new MotorAxisComboBoxSelectionListener();
		this.motorAxisComboBox.addSelectionListener(
				motorAxisComboBoxSelectionListener);
		
		// check box for pre initialization
		this.preInitWindowCheckBox = new Button(this.xAxisComposite, SWT.CHECK);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		this.preInitWindowCheckBox.setLayoutData(gridData);
		this.preInitWindowCheckBox.setText("Preinit Window");
		this.preInitWindowCheckBoxSelectionListener = 
				new PreInitWindowCheckBoxSelectionListener();
		this.preInitWindowCheckBox.addSelectionListener(
				preInitWindowCheckBoxSelectionListener);
		
		// GUI: Scale Type: <Combo>
		this.scaleTypeLabel = new Label(this.xAxisComposite, SWT.NONE);
		this.scaleTypeLabel.setText("Scale Type:");
		this.scaleTypeComboBox = new Combo(this.xAxisComposite, SWT.READ_ONLY);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.scaleTypeComboBox.setLayoutData(gridData);
		this.scaleTypeComboBox.setItems(PlotModes.valuesAsString());
		this.scaleTypeComboBoxSelectionListener = 
				new ScaleTypeComboBoxSelectionListener();
		this.scaleTypeComboBox.addSelectionListener(
				scaleTypeComboBoxSelectionListener);
	}
	
	/*
	 * initialize contents of the first y axis composite
	 */
	private void initYAxis1Composite() {
		this.yAxis1Composite = new Composite(this.bar, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		this.yAxis1Composite.setLayout(gridLayout);
		
		// GUI: Detector Channel: <Combo>
		this.yAxis1DetectorChannelLabel = 
				new Label(this.yAxis1Composite, SWT.NONE);
		this.yAxis1DetectorChannelLabel.setText("Detector Channel:");
		this.yAxis1DetectorChannelComboBox = 
				new Combo(this.yAxis1Composite, SWT.READ_ONLY);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.yAxis1DetectorChannelComboBox.setLayoutData(gridData);
		this.yAxis1DetectorChannelComboControlDecoration = 
				new ControlDecoration(this.yAxis1DetectorChannelComboBox, 
						SWT.LEFT);
		this.yAxis1DetectorChannelComboControlDecoration.setImage(errorImage);
		this.yAxis1DetectorChannelComboControlDecoration.setDescriptionText(
				"At least one y axis has to be set!");
		this.yAxis1DetectorChannelComboControlDecoration.hide();
		this.yAxis1DetectorChannelComboBoxSelectionListener = 
				new YAxis1DetectorChannelComboBoxSelectionListener();
		this.yAxis1DetectorChannelComboBox.addSelectionListener(
				yAxis1DetectorChannelComboBoxSelectionListener);
		
		// GUI: Normalize Channel:
		this.yAxis1NormalizeChannelLabel = 
				new Label(this.yAxis1Composite, SWT.NONE);
		this.yAxis1NormalizeChannelLabel.setText("Normalize Channel:");
		
		// select box for the normalize channel
		this.yAxis1NormalizeChannelComboBox = 
				new Combo(this.yAxis1Composite, SWT.READ_ONLY);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.yAxis1NormalizeChannelComboBox.setLayoutData(gridData);
		
		this.yAxis1NormalizeChannelComboBoxSelectionListener = 
				new YAxis1NormalizeChannelComboBoxSelectionListener();
		this.yAxis1NormalizeChannelComboBox.addSelectionListener(
				yAxis1NormalizeChannelComboBoxSelectionListener);
		
		// GUI: Color:
		Label yAxis1ColorLabel = new Label(yAxis1Composite, SWT.NONE);
		yAxis1ColorLabel.setText("Color:");
		
		// wrapper to fix positioning problems with the grid and field editor
		Composite yAxis1ColorBoxesWrapper = 
				new Composite(this.yAxis1Composite, SWT.NONE);
		
		GridLayout yAxis1ColorBoxesWrapperGridLayout = new GridLayout();
		yAxis1ColorBoxesWrapperGridLayout.numColumns = 2;
		yAxis1ColorBoxesWrapper.setLayout(yAxis1ColorBoxesWrapperGridLayout);
		
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		yAxis1ColorBoxesWrapper.setLayoutData(gridData);
		
		// the first element of the color composite (the select box)
		this.yAxis1ColorComboBox = 
			new Combo(yAxis1ColorBoxesWrapper, SWT.READ_ONLY);
		gridData.grabExcessHorizontalSpace = true;
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		this.yAxis1ColorComboBox.setLayoutData(gridData);
		
		String[] yAxis1PredefinedColors = 
			{"black", "red", "green", "blue", "pink", "purple", "custom..."};
		this.yAxis1ColorComboBox.setItems(yAxis1PredefinedColors);

		// SelectionListener for Color Combo Box
		this.yAxis1ColorComboBoxSelectionListener = 
				new YAxis1ColorComboBoxSelectionListener();
		this.yAxis1ColorComboBox.addSelectionListener( 
				yAxis1ColorComboBoxSelectionListener);
		
		Composite yAxis1ColorFieldWrapper = 
				new Composite(yAxis1ColorBoxesWrapper, SWT.NONE);
		
		GridLayout yAxis1ColorFieldWrapperGridLayout = new GridLayout();
		yAxis1ColorFieldWrapperGridLayout.numColumns = 2;
		yAxis1ColorFieldWrapper.setLayout(yAxis1ColorFieldWrapperGridLayout);
		
		yAxis1ColorFieldEditor = new ColorFieldEditor(
				"y axis 1 color selector", "", yAxis1ColorFieldWrapper);
		yAxis1ColorFieldEditorPropertyChangeListener = 
				new YAxis1ColorFieldEditorPropertyChangeListener();
		yAxis1ColorFieldEditor.setPropertyChangeListener(
				yAxis1ColorFieldEditorPropertyChangeListener);
		// end of wrapper composites for color
		
		// GUI: Linestyle:
		this.yAxis1LinestyleLabel = new Label(this.yAxis1Composite, SWT.NONE);
		this.yAxis1LinestyleLabel.setText("Linestyle:");
		
		// select box for the line style
		this.yAxis1LinestyleComboBox = 
				new Combo(this.yAxis1Composite, SWT.READ_ONLY);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.yAxis1LinestyleComboBox.setLayoutData(gridData);
		// use the items available in the plot from css
		this.yAxis1LinestyleComboBox.setItems(TraceType.stringValues()); 
		this.yAxis1LineStyleComboBoxSelectionListener = 
				new YAxis1LineStyleComboBoxSelectionListener();
		this.yAxis1LinestyleComboBox.addSelectionListener(
				yAxis1LineStyleComboBoxSelectionListener);
		
		// GUI: Markstyle:
		this.yAxis1MarkstyleLabel = new Label(this.yAxis1Composite, SWT.NONE);
		this.yAxis1MarkstyleLabel.setText("Markstyle:");
		
		// select box for the mark style
		this.yAxis1MarkstyleComboBox = 
				new Combo(this.yAxis1Composite, SWT.READ_ONLY);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.yAxis1MarkstyleComboBox.setLayoutData(gridData);
		// set items from the plot of CSS
		this.yAxis1MarkstyleComboBox.setItems(PointStyle.stringValues()); 
		// selection listener for the combo box
		this.yAxis1MarkStyleComboBoxSelectionListener = 
				new YAxis1MarkStyleComboBoxSelectionListener();
		this.yAxis1MarkstyleComboBox.addSelectionListener( 
				yAxis1MarkStyleComboBoxSelectionListener);
		
		// GUI: Scaletype:
		this.yAxis1ScaletypeLabel = new Label(this.yAxis1Composite, SWT.NONE);
		this.yAxis1ScaletypeLabel.setText("Scaletype:");
		
		// select box for the scale type
		this.yAxis1ScaletypeComboBox = 
				new Combo(this.yAxis1Composite, SWT.READ_ONLY);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.yAxis1ScaletypeComboBox.setLayoutData(gridData);
		this.yAxis1ScaletypeComboBox.setItems(PlotModes.valuesAsString());
		this.yAxis1ScaleTypeComboBoxSelectionListener = 
				new YAxis1ScaleTypeComboBoxSelectionListener();
		this.yAxis1ScaletypeComboBox.addSelectionListener( 
				yAxis1ScaleTypeComboBoxSelectionListener);
	}
	
	/*
	 * initialize contents of the second y axis composite
	 */
	private void initYAxis2Composite() {
		this.yAxis2Composite = new Composite(this.bar, SWT.NONE);
		GridLayout yAxis2GridLayout = new GridLayout();
		yAxis2GridLayout.numColumns = 2;
		this.yAxis2Composite.setLayout(yAxis2GridLayout);
		
		// GUI: Detector Channel: <Combo>
		this.yAxis2DetectorChannelLabel = 
				new Label(this.yAxis2Composite, SWT.NONE);
		this.yAxis2DetectorChannelLabel.setText("Detector Channel:");
		this.yAxis2DetectorChannelComboBox = 
				new Combo(this.yAxis2Composite, SWT.READ_ONLY);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.yAxis2DetectorChannelComboBox.setLayoutData(gridData);
		this.yAxis2DetectorChannelComboControlDecoration = 
				new ControlDecoration(this.yAxis2DetectorChannelComboBox, 
						SWT.LEFT);
		this.yAxis2DetectorChannelComboControlDecoration.setImage(errorImage);
		this.yAxis2DetectorChannelComboControlDecoration.setDescriptionText(
				"At least one y axis has to be set!");
		this.yAxis2DetectorChannelComboControlDecoration.hide();
		this.yAxis2DetectorChannelComboBoxSelectionListener = 
				new YAxis2DetectorChannelComboBoxSelectionListener();
		this.yAxis2DetectorChannelComboBox.addSelectionListener(
				yAxis2DetectorChannelComboBoxSelectionListener);
		
		// GUI: Normalize Channel: <Combo>
		this.yAxis2NormalizeChannelLabel = 
				new Label(this.yAxis2Composite, SWT.NONE);
		this.yAxis2NormalizeChannelLabel.setText("Normalize Channel:");
		this.yAxis2NormalizeChannelComboBox = 
				new Combo(this.yAxis2Composite, SWT.READ_ONLY);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.yAxis2NormalizeChannelComboBox.setLayoutData(gridData);
		this.yAxis2NormalizeChannelComboBoxSelectionListener = 
				new YAxis2NormalizeChannelComboBoxSelectionListener();
		this.yAxis2NormalizeChannelComboBox.addSelectionListener( 
				yAxis2NormalizeChannelComboBoxSelectionListener);
		
		// GUI: Color:
		Label colorLabel = new Label(yAxis2Composite, SWT.NONE);
		colorLabel.setText("Color:");
		
		// color selection stuff in a wrapper to repair layout problems
		Composite yAxis2ColorBoxesWrapper = new Composite(yAxis2Composite, 
														  SWT.NONE);
		
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		yAxis2ColorBoxesWrapper.setLayoutData(gridData);
		
		GridLayout yAxis2ColorBoxesWrapperGridLayout = new GridLayout();
		yAxis2ColorBoxesWrapperGridLayout.numColumns = 2;
		yAxis2ColorBoxesWrapper.setLayout(yAxis2ColorBoxesWrapperGridLayout);
		
		this.yAxis2ColorComboBox = new Combo(yAxis2ColorBoxesWrapper, 
											 SWT.READ_ONLY);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.yAxis2ColorComboBox.setLayoutData(gridData);
		
		String[] yAxis2PredefinedColors = 
			{"black", "red", "green", "blue", "pink", "purple", "custom..."};
		this.yAxis2ColorComboBox.setItems(yAxis2PredefinedColors);
		this.yAxis2ColorComboBoxSelectionListener = 
				new YAxis2ColorComboBoxSelectionListener();
		this.yAxis2ColorComboBox.addSelectionListener( 
				yAxis2ColorComboBoxSelectionListener);

		// wrapper to get rid of the grid
		Composite colorFieldWrapper = new Composite(yAxis2ColorBoxesWrapper, 
													SWT.NONE);
			
		GridLayout colorFieldWrapperGridLayout = new GridLayout();
		colorFieldWrapperGridLayout.numColumns = 2;
		colorFieldWrapper.setLayout(colorFieldWrapperGridLayout);
		
		yAxis2ColorFieldEditor = new ColorFieldEditor(
				"color selector 2", "", colorFieldWrapper);		
		yAxis2ColorFieldEditorPropertyChangeListener = 
				new YAxis2ColorFieldEditorPropertyChangeListener();
		yAxis2ColorFieldEditor.setPropertyChangeListener(
				yAxis2ColorFieldEditorPropertyChangeListener);
		
		// GUI: Linestyle:
		this.yAxis2LinestyleLabel = new Label( this.yAxis2Composite, SWT.NONE);
		this.yAxis2LinestyleLabel.setText("Linestyle:");
		
		this.yAxis2LinestyleComboBox = 
				new Combo(this.yAxis2Composite, SWT.READ_ONLY);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.yAxis2LinestyleComboBox.setLayoutData(gridData);
		// set line styles to css trace types for the plot
		this.yAxis2LinestyleComboBox.setItems(TraceType.stringValues()); 
			
		this.yAxis2LineStyleComboBoxSelectionListener = 
				new YAxis2LineStyleComboBoxSelectionListener();
		this.yAxis2LinestyleComboBox.addSelectionListener( 
				yAxis2LineStyleComboBoxSelectionListener);
		
		// GUI: Markstyle:
		this.yAxis2MarkstyleLabel = new Label(this.yAxis2Composite, SWT.NONE);
		this.yAxis2MarkstyleLabel.setText("Markstyle:");
		
		// select box for the mark styles
		this.yAxis2MarkstyleComboBox = 
				new Combo(this.yAxis2Composite, SWT.READ_ONLY);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.yAxis2MarkstyleComboBox.setLayoutData(gridData);
		// use css plot point styles as mark styles
		this.yAxis2MarkstyleComboBox.setItems(PointStyle.stringValues()); 
		this.yAxis2MarkStyleComboBoxSelectionListener = 
				new YAxis2MarkStyleComboBoxSelectionListener();
		this.yAxis2MarkstyleComboBox.addSelectionListener( 
				yAxis2MarkStyleComboBoxSelectionListener);
		
		// GUI: Scalestype:
		this.yAxis2ScaletypeLabel = new Label(this.yAxis2Composite, SWT.NONE);
		this.yAxis2ScaletypeLabel.setText("Scaletype:");
	
		// select box for the scale type
		this.yAxis2ScaletypeComboBox = 
				new Combo(this.yAxis2Composite, SWT.READ_ONLY);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.yAxis2ScaletypeComboBox.setLayoutData(gridData);
		this.yAxis2ScaletypeComboBox.setItems(PlotModes.valuesAsString());
		this.yAxis2ScaleTypeComboBoxSelectionListener = 
				new YAxis2ScaleTypeComboBoxSelectionListener();
		this.yAxis2ScaletypeComboBox.addSelectionListener( 
				yAxis2ScaleTypeComboBoxSelectionListener);
	}
	
	// ************************************************************************
	// ********************** end of createPartControl ************************
	// ************************************************************************

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		logger.debug("focus gained -> forward to top composite");
		this.top.setFocus();
	}
	
	/**
	 * Sets the underlying model of the view 
	 * ({@link de.ptb.epics.eve.data.scandescription.PlotWindow} and 
	 * {@link de.ptb.epics.eve.data.scandescription.ScanModule}).
	 * The contents is shown and can be edited.<br>
	 * Pass (<code>null</code>,<code>null</code>) to reset the view 
	 * (show nothing).
	 * 
	 * @param plotWindow the <code>PlotWindow</code> that should be set
	 * @param scanModule the scanModule containing the given plot window
	 * @throws IllegalArgumentException if neither both arguments are 
	 * 		   <code>null</code> nor both aren't
	 */
	private void setPlotWindow(final PlotWindow plotWindow) {
		
		if(plotWindow != null) {
			logger.debug("set plot window (" + plotWindow.getId() + ")");
		} else {
			logger.debug("set plot window (null)");
		}
		
		if (this.plotWindow != null) {
			this.plotWindow.removeModelUpdateListener(this);
			this.scanModule.removeModelUpdateListener(this);
			this.scanModule.removePropertyChangeListener("removePlot", this);
		}
		
		// update the underlying model to the new one
		this.plotWindow = plotWindow;
		this.scanModule = (plotWindow==null)?null:plotWindow.getScanModule();
		
		if(this.plotWindow != null) {
			// plot Window is set ->
			// update contents of the view according to new plot window
			this.plotWindow.addModelUpdateListener(this);
			this.scanModule.addModelUpdateListener(this);
			this.scanModule.addPropertyChangeListener("removePlot", this);
		}
		updateEvent(null);
	}
	
	/*
	 * 
	 */
	private void checkForErrors() {
		this.motorAxisComboControlDecoration.hide();
		this.yAxis1DetectorChannelComboControlDecoration.hide();
		this.yAxis2DetectorChannelComboControlDecoration.hide();
		
		if (plotWindow.getXAxis() == null) {
			this.motorAxisComboControlDecoration.show();
		}
		// at least one y axis must be set -> otherwise show error
		if ((yAxis1 == null) && (yAxis2 == null)) {
			this.yAxis1DetectorChannelComboControlDecoration.show();
			this.yAxis2DetectorChannelComboControlDecoration.show();
		}
	}
	
	/*
	 * called by setPlotWindow() and listeners to update the color specific 
	 * widgets of given axis.
	 */
	private void updateColorsAxis(int axis) {
		RGB model_rgb = new RGB(0,0,0);
		
		if(axis == 1) {
			model_rgb = yAxis1.getColor();
		}
		if(axis == 2) {
			model_rgb = yAxis2.getColor();
		}
		
		// our predefined colors
		String[] items = {"black", "red", "green", "blue", 
						  "pink", "purple", "custom..."};
		
		if(axis == 1) yAxis1ColorComboBox.setItems(items); 
		if(axis == 2) yAxis2ColorComboBox.setItems(items); 
		
		// our predefined colors as RGB values...
		RGB black = new RGB(0,0,0);
		RGB red = new RGB(255,0,0);
		RGB green = new RGB(0,128,0);
		RGB blue = new RGB(0,0,255);
		RGB pink = new RGB(255,0,255);
		RGB purple = new RGB(128,0,128);
		
		if(model_rgb == null) model_rgb = black;
		
		// if the current RGB value equals one of the predefined colors, update 
		// select box accordingly
		if(model_rgb.equals(black)) {
			if(axis == 1) yAxis1ColorComboBox.select(0);
			if(axis == 2) yAxis2ColorComboBox.select(0);
		} else if(model_rgb.equals(red)) {
			if(axis == 1) yAxis1ColorComboBox.select(1);
			if(axis == 2) yAxis2ColorComboBox.select(1);
		} else if(model_rgb.equals(green)) {
			if(axis == 1) yAxis1ColorComboBox.select(2);
			if(axis == 2) yAxis2ColorComboBox.select(2);
		} else if(model_rgb.equals(blue)) {
			if(axis == 1) yAxis1ColorComboBox.select(3);
			if(axis == 2) yAxis2ColorComboBox.select(3);
		} else if(model_rgb.equals(pink)) {
			if(axis == 1) yAxis1ColorComboBox.select(4);
			if(axis == 2) yAxis2ColorComboBox.select(4);
		} else if(model_rgb.equals(purple)) {
			if(axis == 1) yAxis1ColorComboBox.select(5);
			if(axis == 2) yAxis2ColorComboBox.select(5);
		} else { // custom...
			if(axis == 1) yAxis1ColorComboBox.select(6);
			if(axis == 2) yAxis2ColorComboBox.select(6);
		}	
		
		// change color of color field editor to current RGB value
		if(axis == 1) 
			yAxis1ColorFieldEditor.getColorSelector().setColorValue(model_rgb);
		if(axis == 2)
			yAxis2ColorFieldEditor.getColorSelector().setColorValue(model_rgb);
	}
	
	/*
	 * called by listeners to update the color field of given axis to match 
	 * the color selected in the select box.
	 */
	private void updateColorField(int axis) {
		String selected_color_as_text = "";
		if(axis == 1) selected_color_as_text = yAxis1ColorComboBox.getText();
		if(axis == 2) selected_color_as_text = yAxis2ColorComboBox.getText();
		
		RGB selected_color = null;

		if(selected_color_as_text == "black") selected_color = new RGB(0,0,0);
		if(selected_color_as_text == "red" )  selected_color = new RGB(255,0,0);
		if(selected_color_as_text == "green") selected_color = new RGB(0,128,0);
		if(selected_color_as_text == "blue")  selected_color = new RGB(0,0,255);
		if(selected_color_as_text == "pink")  selected_color = new RGB(255,0,255);
		if(selected_color_as_text == "purple")selected_color = new RGB(128,0,128);
		if(selected_color_as_text == "custom...")
		{
			if(axis == 1) selected_color = 
				yAxis1ColorFieldEditor.getColorSelector().getColorValue();
			if(axis == 2) selected_color = 
				yAxis2ColorFieldEditor.getColorSelector().getColorValue();
		}
		// just in case...
		if(selected_color == null) selected_color = new RGB(0,0,0);
		
		if(axis == 1) yAxis1ColorFieldEditor.getColorSelector().
											setColorValue(selected_color);
		if(axis == 2) yAxis2ColorFieldEditor.getColorSelector().
											setColorValue(selected_color);
	}

	/*
	 * 
	 */
	private void addListeners() {
		motorAxisComboBox.addSelectionListener(
				motorAxisComboBoxSelectionListener);
		preInitWindowCheckBox.addSelectionListener(
				preInitWindowCheckBoxSelectionListener);
		scaleTypeComboBox.addSelectionListener(
				scaleTypeComboBoxSelectionListener);
		
		yAxis1DetectorChannelComboBox.addSelectionListener(
				yAxis1DetectorChannelComboBoxSelectionListener);
		yAxis1NormalizeChannelComboBox.addSelectionListener(
				yAxis1NormalizeChannelComboBoxSelectionListener);
		yAxis1ColorComboBox.addSelectionListener(
				yAxis1ColorComboBoxSelectionListener);
		yAxis1ColorFieldEditor.setPropertyChangeListener(
				yAxis1ColorFieldEditorPropertyChangeListener);
		yAxis1LinestyleComboBox.addSelectionListener(
				yAxis1LineStyleComboBoxSelectionListener);
		yAxis1MarkstyleComboBox.addSelectionListener(
				yAxis1MarkStyleComboBoxSelectionListener);
		yAxis1ScaletypeComboBox.addSelectionListener(
				yAxis1ScaleTypeComboBoxSelectionListener);
		
		yAxis2DetectorChannelComboBox.addSelectionListener(
				yAxis2DetectorChannelComboBoxSelectionListener);
		yAxis2NormalizeChannelComboBox.addSelectionListener(
				yAxis2NormalizeChannelComboBoxSelectionListener);
		yAxis2ColorComboBox.addSelectionListener(
				yAxis2ColorComboBoxSelectionListener);
		yAxis2ColorFieldEditor.setPropertyChangeListener(
				yAxis2ColorFieldEditorPropertyChangeListener);
		yAxis2LinestyleComboBox.addSelectionListener(
				yAxis2LineStyleComboBoxSelectionListener);
		yAxis2MarkstyleComboBox.addSelectionListener(
				yAxis2MarkStyleComboBoxSelectionListener);
		yAxis2ScaletypeComboBox.addSelectionListener(
				yAxis2ScaleTypeComboBoxSelectionListener);
	}
	
	/*
	 * 
	 */
	private void removeListeners() {
		motorAxisComboBox.removeSelectionListener(
				motorAxisComboBoxSelectionListener);
		preInitWindowCheckBox.removeSelectionListener(
				preInitWindowCheckBoxSelectionListener);
		scaleTypeComboBox.removeSelectionListener(
				scaleTypeComboBoxSelectionListener);
		
		yAxis1DetectorChannelComboBox.removeSelectionListener(
				yAxis1DetectorChannelComboBoxSelectionListener);
		yAxis1NormalizeChannelComboBox.removeSelectionListener(
				yAxis1NormalizeChannelComboBoxSelectionListener);
		yAxis1ColorComboBox.removeSelectionListener(
				yAxis1ColorComboBoxSelectionListener);
		yAxis1ColorFieldEditor.setPropertyChangeListener(null);
		yAxis1LinestyleComboBox.removeSelectionListener(
				yAxis1LineStyleComboBoxSelectionListener);
		yAxis1MarkstyleComboBox.removeSelectionListener(
				yAxis1MarkStyleComboBoxSelectionListener);
		yAxis1ScaletypeComboBox.removeSelectionListener(
				yAxis1ScaleTypeComboBoxSelectionListener);
		
		yAxis2DetectorChannelComboBox.removeSelectionListener(
				yAxis2DetectorChannelComboBoxSelectionListener);
		yAxis2NormalizeChannelComboBox.removeSelectionListener(
				yAxis2NormalizeChannelComboBoxSelectionListener);
		yAxis2ColorComboBox.removeSelectionListener(
				yAxis2ColorComboBoxSelectionListener);
		yAxis2ColorFieldEditor.setPropertyChangeListener(null);
		yAxis2LinestyleComboBox.removeSelectionListener(
				yAxis2LineStyleComboBoxSelectionListener);
		yAxis2MarkstyleComboBox.removeSelectionListener(
				yAxis2MarkStyleComboBoxSelectionListener);
		yAxis2ScaletypeComboBox.removeSelectionListener(
				yAxis2ScaleTypeComboBoxSelectionListener);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		logger.debug("selection changed");
		
		if(selection instanceof IStructuredSelection) {
			if(((IStructuredSelection) selection).isEmpty()) {
				return;
			}
			// since at any given time this view can only display options of 
			// one device we take the first element of the selection
			Object o = ((IStructuredSelection) selection).toList().get(0);
			if (o instanceof PlotWindow) {
				// set new PlotWindow
				if(logger.isDebugEnabled()) {
					logger.debug("PlotWindow: " + ((PlotWindow)o).
								getId() + " selected."); 
				}
				setPlotWindow((PlotWindow)o);
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
							setPlotWindow(null);
				}
			} else if (o instanceof ScanDescriptionEditPart) {
				logger.debug("selection is ScanDescriptionEditPart: " + o);
				setPlotWindow(null);
			} else {
				logger.debug("unknown selection -> ignore");
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		logger.debug("PropertyChange: " + evt.getPropertyName());
		if (evt.getPropertyName().equals("removePlot")) {
			if (evt.getOldValue().equals(plotWindow)) {
				// Plot Window will be removed
				setPlotWindow(null);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
		// widgets will be updated according to set / changed model
		// -> temporarily disable listeners to prevent Event Loops
		// (and unnecessary duplicate calls)
		removeListeners();
		
		if (this.plotWindow != null) {
			// show controls
			top.setVisible(true);
			// set view title
			this.setPartName("Plot Window: " + this.plotWindow.getId());
			// determine the number of yAxis of the plot
			int axes_count = plotWindow.getYAxisAmount();
			
			// General
			// depending on the axes count -> set reference(s) to the axis/axes
			switch(axes_count) {
				case 0: yAxis1 = null;
						yAxis2 = null;
						break;
				case 1: yAxis1 = plotWindow.getYAxes().get(0);
						yAxis2 = null;
						break;
				case 2: yAxis1 = plotWindow.getYAxes().get(0);
						yAxis2 = plotWindow.getYAxes().get(1);
						break;
			}	
			// determine available motor axes (as choices in select box)
			setAvailableMotorAxes();
			// set the content of the select box according to the given model
			if (this.plotWindow.getXAxis() != null) {
				this.motorAxisComboBox.setText(
							plotWindow.getXAxis().getFullIdentifyer());
			}
			
			// set the selection of the check box according to the given model
			this.preInitWindowCheckBox.setSelection(this.plotWindow.isInit());
			
			// set the plot mode according to the given model
			this.scaleTypeComboBox.setText(
					PlotModes.modeToString(this.plotWindow.getMode()));
			
			// determine available channels (as choices in detector & normalize
			// select boxes), add "none" as additional choice
			availableDetectorChannels = scanModule.getChannels();
			String[] detectorItems = new String[availableDetectorChannels.length];
			for (int i = 0; i < availableDetectorChannels.length; ++i) {
				detectorItems[i] = availableDetectorChannels[i].
									getDetectorChannel().getFullIdentifyer();
			}
			this.yAxis1DetectorChannelComboBox.setItems(detectorItems);
			this.yAxis1DetectorChannelComboBox.add("none", 0);	
			this.yAxis1NormalizeChannelComboBox.setItems(detectorItems);
			this.yAxis1NormalizeChannelComboBox.add("none", 0);	
			this.yAxis2DetectorChannelComboBox.setItems(detectorItems);
			this.yAxis2DetectorChannelComboBox.add("none", 0);	
			this.yAxis2NormalizeChannelComboBox.setItems(detectorItems);
			this.yAxis2NormalizeChannelComboBox.add("none", 0);	
			
			// ***************************************************************
			// ************************ yAxis1 *******************************
			// ***************************************************************
			if(this.yAxis1 != null) {
				// y axis 1 is set -> insert values and enable fields
				if(yAxis1.getDetectorChannel() != null) {
					this.yAxis1DetectorChannelComboBox.setText(
							yAxis1.getDetectorChannel().getFullIdentifyer());
				} else {
					yAxis1DetectorChannelComboBox.setText("none");
				}
				if (yAxis1.getNormalizeChannel() != null) {
					this.yAxis1NormalizeChannelComboBox.setText( 
							yAxis1.getNormalizeChannel().getFullIdentifyer());
				} else { 
					this.yAxis1NormalizeChannelComboBox.setText("none");
				}
				// set plot related properties according to the model
				updateColorsAxis(1);
				yAxis1LinestyleComboBox.setText(yAxis1.getLinestyle().toString());
				yAxis1MarkstyleComboBox.setText(yAxis1.getMarkstyle().toString());
				yAxis1ScaletypeComboBox.setText(
									PlotModes.modeToString(yAxis1.getMode()));
			} else {
				// no y axis 1->disable fields
				this.yAxis1DetectorChannelComboBox.setText("none");
			}
			// ***************************************************************
			// *********************** end of: yAxis1 ************************
			// ***************************************************************
			
			// ***************************************************************
			// *********************** yAxis2 ********************************
			// ***************************************************************
			if(this.yAxis2 != null) {
				// y axis 2 is present->insert values and enable fields
				itemYAxis2.setExpanded(true);
				if(yAxis2.getDetectorChannel() != null) {
					this.yAxis2DetectorChannelComboBox.setText(
							yAxis2.getDetectorChannel().getFullIdentifyer());
				} else {
					yAxis2DetectorChannelComboBox.setText("none");
				}
				if (yAxis2.getNormalizeChannel() != null) {
					this.yAxis2NormalizeChannelComboBox.setText(
							yAxis2.getNormalizeChannel().getFullIdentifyer());
				} else {
					this.yAxis2NormalizeChannelComboBox.setText("none");
				}
				// plot related fields...
				updateColorsAxis(2);
				yAxis2LinestyleComboBox.setText(yAxis2.getLinestyle().toString());
				yAxis2MarkstyleComboBox.setText(yAxis2.getMarkstyle().toString());
				yAxis2ScaletypeComboBox.setText(
								PlotModes.modeToString(yAxis2.getMode()));
			} else {
				// no y axis 2->disable fields
				itemYAxis2.setExpanded(false);
				this.yAxis2DetectorChannelComboBox.setText("none");
			}
			// ***************************************************************
			// *********************** end of: yAxis2 ************************
			// ***************************************************************
			
			checkForErrors();
			
			// for some reason without the next line of code the expanded items
			// are not shown
			top.layout();
		} else {
			// this.plotWindow == null (no plot selected)
			// hide controls
			top.setVisible(false);
			this.setPartName("No Plot Window selected");
			
			// plot window is null -> reset axes
			this.yAxis1 = null;
			this.yAxis2 = null;

		}
		addListeners();
	}

	/*
	 * 
	 */
	private void setAvailableMotorAxes() {
		availableMotorAxes = scanModule.getAxes();
		String[] axisItems = new String[availableMotorAxes.length];
		for (int i = 0; i < availableMotorAxes.length; ++i) {
			axisItems[i] = 
				availableMotorAxes[i].getMotorAxis().getFullIdentifyer();
		}
		// set available axes as choices in the select box
		this.motorAxisComboBox.setItems(axisItems);
	}

	// ************************************************************************
	// ******************************* listeners ******************************
	// ************************************************************************

	/**
	 * <code>ModifyListener</code> of <code>motorAxisComboBox</code>.
	 */
	class MotorAxisComboBoxSelectionListener implements SelectionListener {
		
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {	
		}
		
		/**
		 * {@inheritDoc}<br>
		 * Updates the data model according to the selected motor axis.
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			
			if(logger.isDebugEnabled())
				logger.debug("user selected motor: " + 
							 motorAxisComboBox.getText());
			
			if(motorAxisComboBox.getText().equals("none")) {
				// "none" is selected ->remove axis from the model
				if (plotWindow != null) 
				   plotWindow.setXAxis(null); 
			} else {
				// motor axis is selected (not "none") -> save it to the model
				plotWindow.setXAxis((MotorAxis)Activator.getDefault().
													getMeasuringStation().
										getAbstractDeviceByFullIdentifyer(
											motorAxisComboBox.getText()));
			}
			checkForErrors();
		}
	}
	
	/**
	 * <code>SelectionListener</code> of <code>preInitWindowCheckBox</code>.
	 */
	class PreInitWindowCheckBoxSelectionListener implements SelectionListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(final SelectionEvent e) {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(final SelectionEvent e) {
			logger.debug("pre init window modified");
			// set whether the plot should be initialized or not
			plotWindow.setInit(preInitWindowCheckBox.getSelection());
		}
	}
	
	/**
	 * <code>SelectionListener</code> of <code>scaleTypeComboBox</code>.
	 */
	class ScaleTypeComboBoxSelectionListener implements SelectionListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(final SelectionEvent e) {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(final SelectionEvent e) {
			logger.debug("scale type modified");
			// set the plot mode selected
			plotWindow.setMode(
					PlotModes.stringToMode(scaleTypeComboBox.getText()));
		}
	}
	
	// ************************************************************************
	// *************************** y axis 1 listener **************************
	// ************************************************************************	
	
	/**
	 * <code>SelectionListener</code> of 
	 * <code>yAxis1DetectorChannelComboBox</code>.
	 */
	class YAxis1DetectorChannelComboBoxSelectionListener 
										implements SelectionListener {
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
			logger.debug("yaxis 1 detector channel modified");
			// if there is "none" selected:
			if(yAxis1DetectorChannelComboBox.getText().equals("none")) {
				if(yAxis1 != null) {
					// remove the y axis from the plot (if there is any)
					plotWindow.removeYAxis(yAxis1);
					yAxis1 = null;
				}
				
				// reset/disable the normalize and scale type select boxes
				// and the plot related select boxes
				yAxis1NormalizeChannelComboBox.setText("none");
				yAxis1ColorComboBox.setText("black");
				yAxis1ColorFieldEditor.getColorSelector().
												setColorValue(new RGB(0,0,0));
				yAxis1LinestyleComboBox.setText("Solid Line");
				yAxis1MarkstyleComboBox.setText("None");
				yAxis1ScaletypeComboBox.setText("linear");
				
			} else {
				// first y axis is NOT "" or "none":
				// if y axis doesn't exist, create it
				if(yAxis1 == null) {
					yAxis1 = new YAxis();
				
					// default values for color, line style and mark style
					yAxis1.setColor(new RGB(0,0,255));
					yAxis1.setLinestyle(TraceType.SOLID_LINE);
					yAxis1.setMarkstyle(PointStyle.NONE);
					
					plotWindow.addYAxis(yAxis1);
				}
				
				// update/enable GUI elements
				if(yAxis1.getNormalizeChannel() == null) {
					yAxis1NormalizeChannelComboBox.setText("none");
				} else {
					yAxis1NormalizeChannelComboBox.setText(
							yAxis1.getNormalizeChannel().getFullIdentifyer());
				}
				
				updateColorsAxis(1);
				
				yAxis1LinestyleComboBox.setText(
						yAxis1.getLinestyle().toString());
				yAxis1MarkstyleComboBox.setText(
						yAxis1.getMarkstyle().toString());
				yAxis1ScaletypeComboBox.setText(
						PlotModes.modeToString(yAxis1.getMode()));
				yAxis1.setDetectorChannel((DetectorChannel)Activator.
						getDefault().getMeasuringStation().
						getAbstractDeviceByFullIdentifyer(
								yAxis1DetectorChannelComboBox.getText()));
			}
			checkForErrors();
		}
	}
	
	/**
	 * <code>SelectionListener</code> of 
	 * <code>yAxis1NormalizechannelComboBox</code>.
	 */
	class YAxis1NormalizeChannelComboBoxSelectionListener
										implements SelectionListener {

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {	
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			logger.debug("yaxis 1 normalize channel modified");
			
			if(yAxis1NormalizeChannelComboBox.getText().equals("none")) {
				// remove normalize channel
				if (yAxis1 != null) {
					yAxis1.setNormalizeChannel(null);
				}
			} else {
				// set normalize channel to the one selected
				yAxis1.setNormalizeChannel(
						(DetectorChannel)Activator.getDefault().
						                  getMeasuringStation().
					        getAbstractDeviceByFullIdentifyer(
					        		yAxis1NormalizeChannelComboBox.getText()));
			}
		}		
	}
	
	/**
	 * <code>SelectionListener</code> of <code>yAxis1ColorComboBox</code>.
	 */
	class YAxis1ColorComboBoxSelectionListener implements SelectionListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(final SelectionEvent e) {
		}

		/**
		 * {@inheritDoc}<br>
		 * Sets the selected color in the model and shows it in the 
		 * adjacent color field editor. 
		 */
		@Override
		public void widgetSelected(final SelectionEvent e) {
			// set the color in the color field to match the selection
			updateColorField(1);
			// save the color to the model
			yAxis1.setColor(yAxis1ColorFieldEditor.getColorSelector().
												 getColorValue());
			if(yAxis1ColorComboBox.getText().equals("custom...")) {
				yAxis1ColorFieldEditor.getColorSelector().open();
			}
			
		}
	}
	
	/**
	 * <code>PropertyChangeListener</code> of <code>colorFieldEditor</code>.
	 */
	class YAxis1ColorFieldEditorPropertyChangeListener 
							implements IPropertyChangeListener {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			
			yAxis1.setColor(
					yAxis1ColorFieldEditor.getColorSelector().getColorValue());
			
			updateColorsAxis(1);
		}	
	}
	
	/**
	 * <code>SelectionListener</code> of <code>yAxis1LineStyleComboBox</code>.
	 */
	class YAxis1LineStyleComboBoxSelectionListener 
											implements SelectionListener {	
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(final SelectionEvent e) {		
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(final SelectionEvent e) {
			if(yAxis1 != null) {
				TraceType[] tracetypes = TraceType.values();
				for(int i=0;i<tracetypes.length;i++)
				{
					if(tracetypes[i].toString().equals(
							yAxis1LinestyleComboBox.getText()))
					{
						yAxis1.setLinestyle(tracetypes[i]);
					}
				}
			}
			
		}
	}
	
	/**
	 * <code>SelectionListener</code> of <code>yAxis1MarkStyleComboBox</code>.
	 */
	class YAxis1MarkStyleComboBoxSelectionListener 
											implements SelectionListener {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(final SelectionEvent e) {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(final SelectionEvent e) {
			
			PointStyle[] markstyles = PointStyle.values();
			for(int i=0;i<markstyles.length;i++)
			{
				if(markstyles[i].toString().equals(
						yAxis1MarkstyleComboBox.getText()))
				{
					yAxis1.setMarkstyle(markstyles[i]);
				}
			}
			
		}
	}
	
	/**
	 * <code>SelectionListener</code> of <code>yAxis1ScaleTypeComboBox</code>.
	 */
	class YAxis1ScaleTypeComboBoxSelectionListener
											implements SelectionListener {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(final SelectionEvent e) {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(final SelectionEvent e) {
			yAxis1.setMode(
				PlotModes.stringToMode(yAxis1ScaletypeComboBox.getText()));
		}
	}
	
	// ************************************************************************
	// ************************************************************************
	// ********************* Listener of axis 2 *******************************
	// ************************************************************************
	// ************************************************************************
	
	/**
	 * <code>SelectionListener</code> of 
	 * <code>yAxis2DetectorChannelComboBox</code>.
	 */
	class YAxis2DetectorChannelComboBoxSelectionListener 
											implements SelectionListener {
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
			logger.debug("yaxis 2 detector channel modified");
			if(yAxis2DetectorChannelComboBox.getText().equals("") ||
			   yAxis2DetectorChannelComboBox.getText().equals("none")) {
				if(yAxis2 != null) {
					plotWindow.removeYAxis(yAxis2);
					yAxis2 = null;
				}
				
				// reset/disable the normalize and scale type select boxes
				// and the plot related select boxes
				yAxis2NormalizeChannelComboBox.setText("none");
				yAxis2ColorComboBox.setText("black");
				yAxis2ColorFieldEditor.getColorSelector().
									   setColorValue(new RGB(0,0,0));
				yAxis2LinestyleComboBox.setText("Solid Line");
				yAxis2MarkstyleComboBox.setText("None");
				yAxis2ScaletypeComboBox.setText("linear");
			
			} else {
				// select box has value other than "" or "none"...
				if(yAxis2 == null) {
					// a detector channel was selected, but none present yet
					yAxis2 = new YAxis();

					// default values for color, line style and mark style
					yAxis2.setColor(new RGB(0,128,0));
					yAxis2.setLinestyle(TraceType.DASH_LINE);
					yAxis2.setMarkstyle(PointStyle.NONE);
					
					plotWindow.addYAxis(yAxis2);
				}

				// update/enable GUI elements
				if(yAxis2.getNormalizeChannel() == null)
					yAxis2NormalizeChannelComboBox.setText("none");
				else 
					yAxis2NormalizeChannelComboBox.setText(
							yAxis2.getNormalizeChannel().getFullIdentifyer());
				
				updateColorsAxis(2);
				yAxis2LinestyleComboBox.setText(
						yAxis2.getLinestyle().toString());
				yAxis2MarkstyleComboBox.setText(
						yAxis2.getMarkstyle().toString());
				yAxis2ScaletypeComboBox.setText(
						PlotModes.modeToString(yAxis2.getMode()));
						
				yAxis2.setDetectorChannel((DetectorChannel)
						Activator.getDefault().getMeasuringStation().
							getAbstractDeviceByFullIdentifyer(
									yAxis2DetectorChannelComboBox.getText()));
				
			}
			checkForErrors();
		}
	}
	
	/**
	 * <code>ModifyListener</code> of 
	 * <code>yAxis2NormalizeChannelComboBox</code>.
	 */
	class YAxis2NormalizeChannelComboBoxSelectionListener 
												implements SelectionListener {
		
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
			logger.debug("yaxis 2 normalize channel modified");
			if(yAxis2NormalizeChannelComboBox.getText().equals("none")) {
				if(yAxis2 != null) {
					yAxis2.setNormalizeChannel(null);
				}
			} else {
				yAxis2.setNormalizeChannel((DetectorChannel)
						Activator.getDefault().getMeasuringStation().
							getAbstractDeviceByFullIdentifyer(
									yAxis2NormalizeChannelComboBox.getText()));
			}
		}
	}
	
	/**
	 * <code>SelectionListener</code> of <code>yAxis2ColorComboBox</code>.
	 */
	class YAxis2ColorComboBoxSelectionListener implements SelectionListener {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(final SelectionEvent e) {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(final SelectionEvent e) {
			// set the color in the color field to match the selection
			updateColorField(2);
			// save the color to the model
			yAxis2.setColor(yAxis2ColorFieldEditor.getColorSelector().
													 getColorValue());
			if(yAxis2ColorComboBox.getText().equals("custom...")) {
				yAxis2ColorFieldEditor.getColorSelector().open();
			}
		}
	}
	
	/**
	 * <code>PropertyChangeListener</code> of 
	 * <code>yAxis2ColorFieldEditor</code>.
	 */
	class YAxis2ColorFieldEditorPropertyChangeListener
										implements IPropertyChangeListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			
			yAxis2.setColor(
					yAxis2ColorFieldEditor.getColorSelector().getColorValue());
			
			updateColorsAxis(2);
			
		}
	}
	
	/**
	 * <code>SelectionListener</code> of <code>yAxis2LineStyleComboBox</code>. 
	 */
	class YAxis2LineStyleComboBoxSelectionListener 
											implements SelectionListener {	
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(final SelectionEvent e) {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(final SelectionEvent e) {
			
			TraceType[] tracetypes = TraceType.values();
			for(int i=0;i<tracetypes.length;i++)
			{
				if(tracetypes[i].toString().equals(
						yAxis2LinestyleComboBox.getText()))
				{
					yAxis2.setLinestyle(tracetypes[i]);
				}
			}
		}

	}
	
	/**
	 * <code>SelectionListener</code> of <code>yAxis2MarkStyleComboBox</code>.
	 */
	class YAxis2MarkStyleComboBoxSelectionListener 
											implements SelectionListener {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(final SelectionEvent e) {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(final SelectionEvent e) {
			PointStyle[] markstyles = PointStyle.values();
			for(int i=0;i<markstyles.length;i++)
			{
				if(markstyles[i].toString().equals(
						yAxis2MarkstyleComboBox.getText()))
				{
					yAxis2.setMarkstyle(markstyles[i]);
				}
			}
		}
	}

	/**
	 * <code>SelectionListener</code> of <code>yAxis2ScaleTypeComboBox</code>.
	 */
	class YAxis2ScaleTypeComboBoxSelectionListener
											implements SelectionListener {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(final SelectionEvent e) {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(final SelectionEvent e) {
			if(yAxis2 != null) {
				yAxis2.setMode(PlotModes.stringToMode(
						yAxis2ScaletypeComboBox.getText()));
			}
		}
	}
}
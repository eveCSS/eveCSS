package de.ptb.epics.eve.editor.views.scanmoduleview;

import org.apache.log4j.Logger;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;

import de.ptb.epics.eve.data.measuringstation.Event;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.errors.AxisError;
import de.ptb.epics.eve.data.scandescription.errors.ChannelError;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.PlotWindowError;
import de.ptb.epics.eve.data.scandescription.errors.PositioningError;
import de.ptb.epics.eve.data.scandescription.errors.PostscanError;
import de.ptb.epics.eve.data.scandescription.errors.PrescanError;
import de.ptb.epics.eve.data.scandescription.errors.ScanModuleError;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventTypes;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.gef.editparts.ChainEditPart;
import de.ptb.epics.eve.editor.gef.editparts.ScanModuleEditPart;
import de.ptb.epics.eve.editor.views.EditorViewPerspectiveListener;
import de.ptb.epics.eve.editor.views.IEditorView;
import de.ptb.epics.eve.editor.views.eventcomposite.EventComposite;
import de.ptb.epics.eve.editor.views.scanmoduleview.detectorchannelcomposite.DetectorChannelComposite;
import de.ptb.epics.eve.editor.views.scanmoduleview.motoraxiscomposite.MotorAxisComposite;
import de.ptb.epics.eve.editor.views.scanmoduleview.plotcomposite.PlotComposite;
import de.ptb.epics.eve.editor.views.scanmoduleview.positioningcomposite.PositioningComposite;
import de.ptb.epics.eve.editor.views.scanmoduleview.postscancomposite.PostscanComposite;
import de.ptb.epics.eve.editor.views.scanmoduleview.prescancomposite.PrescanComposite;
import de.ptb.epics.eve.util.jface.SelectionProviderWrapper;
import de.ptb.epics.eve.util.swt.PositiveIntegerVerifyListener;
import de.ptb.epics.eve.util.swt.TextSelectAllFocusListener;
import de.ptb.epics.eve.util.swt.TextSelectAllMouseListener;

/**
 * <code>ScanModulView</code> shows the currently selected scan module.
 * 
 * @author ?
 * @author Marcus Michalsky
 * @author Hartmut Scherr
 */
public class ScanModuleView extends ViewPart implements IEditorView,
		ISelectionListener, IModelUpdateListener {
	
	/**
	 * the id of the <code>ScanModulView</code>.
	 */
	public static final String ID = 
			"de.ptb.epics.eve.editor.views.ScanModulView"; 
	
	private static Logger logger = Logger.getLogger(ScanModuleView.class);

	// the scan module currently represented by this view
	private ScanModule currentScanModule;

	private Composite top;
	private Composite generalComposite;
	private Composite actionsComposite;
	private Composite eventsComposite;
	
	private boolean actionsCompositeMaximized;
	private boolean eventsCompositeMaximized;

	private Label valueCountLabel;
	private Text valueCountText;
	private PositiveIntegerVerifyListener valueCountTextVerifyListener;
	private ValueCountTextModifyListener valueCountTextModifyListener;
	
	private Label triggerDelayLabel;
	private Text triggerDelayText;
	private ControlDecoration triggerDelayTextControlDecoration;
	private TextDoubleVerifyListener triggerDelayTextVerifyListener;
	private TriggerDelayTextModifiedListener triggerDelayTextModifiedListener;
	
	private Label settleTimeLabel;
	private Text settleTimeText;
	private ControlDecoration settleTimeTextControlDecoration;
	private TextDoubleVerifyListener settleTimeTextVerifyListener;
	private SettleTimeTextModifiedListener settleTimeTextModifiedListener;
	
	private Button triggerConfirmAxisCheckBox;
	private TriggerConfirmAxisCheckBoxSelectionListener 
			triggerConfirmAxisCheckBoxSelectionListener;
	
	private Button triggerConfirmChannelCheckBox;
	private TriggerConfirmChannelCheckBoxSelectionListener
			triggerConfirmChannelCheckBoxSelectionListener;
	
	
	private CTabFolder actionsTabFolder;
	private CTabItem motorAxisTab;
	private CTabItem detectorChannelTab;
	private CTabItem prescanTab;
	private CTabItem postscanTab;
	private CTabItem positioningTab;
	private CTabItem plotTab;
	
	private Label actionMaxIcon;
	private ActionComposite motorAxisComposite;
	private ActionComposite detectorChannelComposite;
	private ActionComposite prescanComposite;
	private ActionComposite postscanComposite;
	private ActionComposite positioningComposite;
	private ActionComposite plotComposite;
	
	/** */
	public CTabFolder eventsTabFolder;
	private CTabItem pauseEventsTabItem;
	private CTabItem redoEventsTabItem;
	private CTabItem breakEventsTabItem;
	private CTabItem triggerEventsTabItem;
	
	private EventsTabFolderSelectionListener eventsTabFolderSelectionListener;
	
	private Label eventMaxIcon;
	private EventComposite pauseEventComposite;
	private EventComposite redoEventComposite;
	private EventComposite breakEventComposite;
	private EventComposite triggerEventComposite;
	
	private Button appendScheduleEventCheckBox;
	private AppendScheduleEventCheckBoxSelectionListener
			appendScheduleEventCheckBoxSelectionListener;
	
	private SashForm sashForm;
	
	private Image restoreIcon;
	private Image maximizeIcon;
	

	private Image errorImage;
	
	// the selection service only accepts one selection provider per view,
	// since we have multiple tabs with tables capable of providing selections, 
	// a wrapper handles them and registers the active one with the global 
	// selection service
	protected SelectionProviderWrapper selectionProviderWrapper;
	
	private EditorViewPerspectiveListener editorViewPerspectiveListener;
	
	private IMemento memento;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		init(site);
		this.memento = memento;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite parent) {
		logger.debug("createPartControl");
		
		parent.setLayout(new FillLayout());
		
		if(Activator.getDefault().getMeasuringStation() == null) {
			final Label errorLabel = new Label(parent, SWT.NONE);
			errorLabel.setText("No Measuring Station has been loaded. " +
					"Please check Preferences!");
			return;
		}
		
		this.restoreIcon = Activator.getDefault().getImageRegistry()
				.get("RESTORE");
		this.maximizeIcon = Activator.getDefault().getImageRegistry()
				.get("MAXIMIZE");
		
		this.errorImage = FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR)
				.getImage();
		
		this.top = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 4;
		this.top.setLayout(gridLayout);
		
		this.createGeneralComposite(this.top);
		this.sashForm = new SashForm(this.top, SWT.VERTICAL);
		this.sashForm.SASH_WIDTH = 4;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		this.sashForm.setLayoutData(gridData);
		this.createActionsComposite(this.sashForm);
		this.createEventComposite(this.sashForm);
		
		this.actionsCompositeMaximized = false;
		this.eventsCompositeMaximized = false;
		
		this.restoreState();
		
		top.setVisible(false);
		
		this.bindValues();
		
		// listen to selection changes (if a scan module is selected, its 
		// attributes are made available for editing)
		getSite().getWorkbenchWindow().getSelectionService().
				addSelectionListener(this);
		
		selectionProviderWrapper = new SelectionProviderWrapper();
		getSite().setSelectionProvider(selectionProviderWrapper);
		
		// reset view if last editor was closed
		this.editorViewPerspectiveListener = new EditorViewPerspectiveListener(
				this);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.addPerspectiveListener(this.editorViewPerspectiveListener);
	} // end of: createPartControl()

	/*
	 * 
	 */
	private void createGeneralComposite(Composite parent) {
		this.generalComposite = new Composite(parent, SWT.BORDER);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		this.generalComposite.setLayout(gridLayout);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.generalComposite.setLayoutData(gridData);
		
		// value count
		this.valueCountLabel = new Label(this.generalComposite, SWT.NONE);
		this.valueCountLabel.setText("No of Measurements:");
		this.valueCountLabel.setToolTipText("" +
				"Number of Measurements taken for each motor position");
		this.valueCountText = new Text(this.generalComposite, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.horizontalIndent = 7;
		gridData.grabExcessHorizontalSpace = true;
		this.valueCountText.setLayoutData(gridData);
		
		// Trigger Delay
		this.triggerDelayLabel = new Label(this.generalComposite, SWT.NONE);
		this.triggerDelayLabel.setText("Trigger Delay (in s):");
		this.triggerDelayLabel.setToolTipText("Delay time after positioning");
		this.triggerDelayText = new Text(this.generalComposite, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.horizontalIndent = 7;
		gridData.grabExcessHorizontalSpace = true;
		this.triggerDelayText.setLayoutData(gridData);
		
		// Settle Time
		this.settleTimeLabel = new Label(this.generalComposite, SWT.NONE);
		this.settleTimeLabel.setText("Settle Time (in s):");
		this.settleTimeLabel.setToolTipText(
				"Delay time after first positioning in the scan module");
		this.settleTimeText = new Text(this.generalComposite, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.horizontalIndent = 7;
		gridData.grabExcessHorizontalSpace = true;
		this.settleTimeText.setLayoutData(gridData);
		
		// Manual Trigger
		Label triggerLabel = new Label(this.generalComposite, SWT.NONE);
		triggerLabel.setText("Manual Trigger:");
		triggerLabel.setLayoutData(new GridData());
		Composite triggerCheckBoxes = new Composite(this.generalComposite,
				SWT.NONE);
		gridData = new GridData();
		gridData.horizontalIndent = 7;
		triggerCheckBoxes.setLayoutData(gridData);
		triggerCheckBoxes.setLayout(new FillLayout(SWT.HORIZONTAL));
		this.triggerConfirmAxisCheckBox = new Button(triggerCheckBoxes, 
												SWT.CHECK);
		this.triggerConfirmAxisCheckBox.setText("Motors");
		this.triggerConfirmChannelCheckBox = new Button(triggerCheckBoxes, 
												SWT.CHECK);
		this.triggerConfirmChannelCheckBox.setText("Detectors");
	}
	
	/*
	 * 
	 */
	private void createActionsComposite(Composite parent) {
		this.actionsComposite = new Composite(parent, SWT.BORDER);
		this.actionsComposite.setLayout(new GridLayout(2, false));
		
		this.actionMaxIcon = new Label(actionsComposite, SWT.NONE);
		this.actionMaxIcon.setImage(maximizeIcon);
		this.actionMaxIcon.addMouseListener(new ActionsMaxIconMouseListener());
		
		Label actionsLabel = new Label(actionsComposite, SWT.NONE);
		actionsLabel.setText("Actions:");
		
		this.actionsTabFolder = new CTabFolder(this.actionsComposite, SWT.FLAT);
		this.actionsTabFolder.setSimple(false);
		this.actionsTabFolder.setBorderVisible(true);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 2;
		this.actionsTabFolder.setLayoutData(gridData);
		
		motorAxisComposite = new MotorAxisComposite(this, 
				actionsTabFolder, SWT.NONE);
		detectorChannelComposite = new DetectorChannelComposite(this, 
				actionsTabFolder, SWT.NONE);
		prescanComposite = new PrescanComposite(this, actionsTabFolder, 
				SWT.NONE);
		postscanComposite = new PostscanComposite(this, actionsTabFolder, 
				SWT.NONE);
		positioningComposite = new PositioningComposite(this, actionsTabFolder, 
				SWT.NONE);
		plotComposite = new PlotComposite(this, actionsTabFolder, SWT.NONE);
		
		this.motorAxisTab = new CTabItem(this.actionsTabFolder, SWT.FLAT);
		this.motorAxisTab.setText(" Motor Axes ");
		this.motorAxisTab.setToolTipText(
				"Select motor axes to be used in this scan module");
		this.motorAxisTab.setControl(this.motorAxisComposite);
		
		this.detectorChannelTab = new CTabItem(this.actionsTabFolder, SWT.FLAT);
		this.detectorChannelTab.setText(" Detector Channels ");
		this.detectorChannelTab.setToolTipText(
				"Select detector channels to be used in this scan module");
		this.detectorChannelTab.setControl(this.detectorChannelComposite);
		
		this.prescanTab = new CTabItem(this.actionsTabFolder, SWT.FLAT);
		this.prescanTab.setText(" Prescan ");
		this.prescanTab.setToolTipText(
				"Action to do before scan module is started");
		this.prescanTab.setControl(this.prescanComposite);
		
		this.postscanTab = new CTabItem(this.actionsTabFolder, SWT.FLAT);
		this.postscanTab.setText(" Postscan ");
		this.postscanTab.setToolTipText("Action to do if scan module is done");
		this.postscanTab.setControl(this.postscanComposite);
		
		this.positioningTab = new CTabItem(this.actionsTabFolder, SWT.FLAT);
		this.positioningTab.setText(" Positioning ");
		this.positioningTab.setToolTipText(
				"Move motor to calculated position after scan module is done");
		this.positioningTab.setControl(this.positioningComposite);
		
		this.plotTab = new CTabItem(this.actionsTabFolder, SWT.FLAT);
		this.plotTab.setText(" Plot ");
		this.plotTab.setToolTipText("Plot settings for this scan module");
		this.plotTab.setControl(this.plotComposite);
	}
	
	/*
	 * 
	 */
	private void createEventComposite(Composite parent) {
		this.eventsComposite = new Composite(parent, SWT.BORDER);
		this.eventsComposite.setLayout(new GridLayout(2, false));
		
		this.eventMaxIcon = new Label(eventsComposite, SWT.NONE);
		this.eventMaxIcon.setImage(maximizeIcon);
		this.eventMaxIcon.addMouseListener(new EventMaxIconMouseListener());
		
		Label eventLabel = new Label(eventsComposite, SWT.NONE);
		eventLabel.setText("Events:");
		
		eventsTabFolder = new CTabFolder(this.eventsComposite, SWT.NONE);
		this.eventsTabFolder.setSimple(false);
		this.eventsTabFolder.setBorderVisible(true);
		this.eventsTabFolderSelectionListener = 
				new EventsTabFolderSelectionListener();	
		eventsTabFolder.addSelectionListener(eventsTabFolderSelectionListener);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 2;
		eventsTabFolder.setLayoutData(gridData);
		
		pauseEventComposite = new EventComposite(eventsTabFolder, SWT.NONE, 
				ControlEventTypes.PAUSE_EVENT, this);
		redoEventComposite = new EventComposite(eventsTabFolder, SWT.NONE, 
				ControlEventTypes.CONTROL_EVENT, this);
		breakEventComposite = new EventComposite(eventsTabFolder, SWT.NONE, 
				ControlEventTypes.CONTROL_EVENT, this);
		triggerEventComposite = new EventComposite(eventsTabFolder, SWT.NONE, 
				ControlEventTypes.CONTROL_EVENT, this);
		
		this.pauseEventsTabItem = new CTabItem(eventsTabFolder, SWT.NONE);
		this.pauseEventsTabItem.setText(" Pause ");
		this.pauseEventsTabItem.setToolTipText(
				"Configure event to pause and resume this scan module");
		this.pauseEventsTabItem.setControl(pauseEventComposite);
		this.redoEventsTabItem = new CTabItem(eventsTabFolder, SWT.NONE);
		this.redoEventsTabItem.setText(" Redo ");
		this.redoEventsTabItem.setToolTipText(
				"Repeat the last acquisition, if redo event occurs");
		this.redoEventsTabItem.setControl(redoEventComposite);
		this.breakEventsTabItem = new CTabItem(eventsTabFolder, SWT.NONE);
		this.breakEventsTabItem.setText(" Break ");
		this.breakEventsTabItem.setToolTipText(
				"Finish this scan module and continue with next");
		this.breakEventsTabItem.setControl(breakEventComposite);
		this.triggerEventsTabItem = new CTabItem(eventsTabFolder, SWT.NONE);
		this.triggerEventsTabItem.setText(" Trigger ");
		this.triggerEventsTabItem.setToolTipText(
				"Wait for trigger event before moving to next position");
		this.triggerEventsTabItem.setControl(triggerEventComposite);
		
		appendScheduleEventCheckBox = new Button(this.eventsComposite, 
												SWT.CHECK);
		appendScheduleEventCheckBox.setText("Append Schedule Event");
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		appendScheduleEventCheckBox.setLayoutData(gridData);
	}
	
	/*
	 * 
	 */
	private void bindValues() {
		
	}
	
	/*
	 * 
	 */
	private void restoreState() {
		if (memento == null) {
			return;
		}
		
		// restore sort state of action composite viewers
		if (memento.getInteger("AxesSortState") != null) {
			this.motorAxisComposite.setSortState(memento
					.getInteger("AxesSortState"));
		}
		if (memento.getInteger("ChannelSortState") != null) {
			this.detectorChannelComposite.setSortState(memento
					.getInteger("ChannelSortState"));
		}
	}
	
	/*
	 * called by CreatePartControl to create the contents of the first 
	 * expand item (General)
	 */
	private void createGeneralExpandItem() {
		/*
		this.valueCountTextVerifyListener = new PositiveIntegerVerifyListener();
		this.valueCountText.addVerifyListener(valueCountTextVerifyListener);
		this.valueCountTextModifyListener = new ValueCountTextModifyListener();
		this.valueCountText.addModifyListener(valueCountTextModifyListener);
		this.valueCountText.addFocusListener(
				new TextSelectAllFocusListener(this.valueCountText));
		this.valueCountText.addMouseListener(
				new TextSelectAllMouseListener(this.valueCountText));
		this.valueCountText.addFocusListener(new FocusAdapter() {
			@Override public void focusLost(FocusEvent e) {
				valueCountText.setSelection(0,0);
			}
		});
		
		this.triggerDelayTextVerifyListener = new TextDoubleVerifyListener();
		this.triggerDelayText.addVerifyListener(triggerDelayTextVerifyListener);
		this.triggerDelayTextModifiedListener = 
				new TriggerDelayTextModifiedListener();
		this.triggerDelayText.addModifyListener(
				triggerDelayTextModifiedListener);
		this.triggerDelayText.addFocusListener(
				new TextSelectAllFocusListener(this.triggerDelayText));
		this.triggerDelayText.addMouseListener(
				new TextSelectAllMouseListener(this.triggerDelayText));
		this.triggerDelayText.addFocusListener(new FocusAdapter() {
			@Override public void focusLost(FocusEvent e) {
				triggerDelayText.setSelection(0,0);
			}
		});
		this.triggerDelayTextControlDecoration = new ControlDecoration(
				triggerDelayText, SWT.LEFT);
		this.triggerDelayTextControlDecoration.setImage(errorImage);
		this.triggerDelayTextControlDecoration.setDescriptionText(
				"Trigger Delay value not possible");
		this.triggerDelayTextControlDecoration.hide();
		
		this.settleTimeTextVerifyListener = new TextDoubleVerifyListener();
		this.settleTimeTextModifiedListener = 
				new SettleTimeTextModifiedListener();
		this.settleTimeText.addModifyListener(settleTimeTextModifiedListener);
		this.settleTimeText.addFocusListener(
				new TextSelectAllFocusListener(this.settleTimeText));
		this.settleTimeText.addMouseListener(
				new TextSelectAllMouseListener(this.settleTimeText));
		this.settleTimeText.addFocusListener(new FocusAdapter() {
			@Override public void focusLost(FocusEvent e) {
				settleTimeText.setSelection(0,0);
			}
		});
		this.settleTimeTextControlDecoration = new ControlDecoration(
				settleTimeText, SWT.LEFT);
		this.settleTimeTextControlDecoration.setImage(errorImage);
		this.settleTimeTextControlDecoration.setDescriptionText(
				"Settle Time value not possible");
		this.settleTimeTextControlDecoration.hide();
		
		this.triggerConfirmAxisCheckBoxSelectionListener = 
				new TriggerConfirmAxisCheckBoxSelectionListener();
		this.triggerConfirmAxisCheckBox.addSelectionListener(
				triggerConfirmAxisCheckBoxSelectionListener);
		
		this.triggerConfirmChannelCheckBox = new Button(triggerCheckBoxes, 
												SWT.CHECK);
		this.triggerConfirmChannelCheckBox.setText("Detectors");
		this.triggerConfirmChannelCheckBoxSelectionListener = 
				new TriggerConfirmChannelCheckBoxSelectionListener();
		this.triggerConfirmChannelCheckBox.addSelectionListener(
				triggerConfirmChannelCheckBoxSelectionListener);
		*/
	}
	
	/*
	 * called by CreatePartControl to create the contents of the third 
	 * expand item (Events)
	 */
	private void createEventsExpandItem() {
		/*
		
		this.appendScheduleEventCheckBoxSelectionListener = 
				new AppendScheduleEventCheckBoxSelectionListener();
		this.appendScheduleEventCheckBox.addSelectionListener(
				appendScheduleEventCheckBoxSelectionListener);
		*/
	}
	
	// ***********************************************************************
	// ********************** end of create part control *********************
	// ***********************************************************************
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		logger.debug("focus gained -> forward to top composite");
		this.top.setFocus();
	}
	
	/**
	 * Returns the currently active scan module.
	 * 
	 * @return the scan module
	 */
	public ScanModule getCurrentScanModule() {
		return currentScanModule;
	}
	
	/*
	 * Sets the currently active scan module. Called by selectionChanged if 
	 * the current selection is of interest.
	 */
	private void setCurrentScanModule(ScanModule currentScanModule) {
		logger.debug("setCurrentScanModule");

		// if there was a scan module shown before, stop listening to changes
		if (this.currentScanModule != null) {
			this.currentScanModule.removeModelUpdateListener(this);
		}

		// set the new scan module as the current one
		this.currentScanModule = currentScanModule;

		// tell the action composites about the change
		this.motorAxisComposite.setScanModule(this.currentScanModule);
		this.detectorChannelComposite.setScanModule(this.currentScanModule);
		this.prescanComposite.setScanModule(this.currentScanModule);
		this.postscanComposite.setScanModule(this.currentScanModule);
		this.positioningComposite.setScanModule(this.currentScanModule);
		this.plotComposite.setScanModule(this.currentScanModule);

		// get the selected tab
		int selection_index = this.actionsTabFolder.getSelectionIndex();
		if (selection_index == -1) {
			this.actionsTabFolder.setSelection(0);
		} else {
			this.actionsTabFolder.setSelection(selection_index);
		}

		if (this.currentScanModule != null) {
			// new scan module
			this.currentScanModule.addModelUpdateListener(this);
			
			top.setVisible(true);
			this.setPartName(this.currentScanModule.getName() + ":"
							 + this.currentScanModule.getId());
			
			if (this.eventsTabFolder.getSelection() == null) {
				this.eventsTabFolder.setSelection(0);
			}
			
			this.triggerEventComposite.setControlEventManager(
					this.currentScanModule.getTriggerControlEventManager());
			this.breakEventComposite.setControlEventManager(
					this.currentScanModule.getBreakControlEventManager());
			this.redoEventComposite.setControlEventManager(
					this.currentScanModule.getRedoControlEventManager());
			this.pauseEventComposite.setControlEventManager(
					this.currentScanModule.getPauseControlEventManager());
		} else {
			// no scan module selected -> reset contents
			selectionProviderWrapper.setSelectionProvider(null);
			
			this.setPartName("No Scan Module selected");
			
			triggerEventComposite.setControlEventManager(null);
			breakEventComposite.setControlEventManager(null);
			redoEventComposite.setControlEventManager(null);
			pauseEventComposite.setControlEventManager(null);
			
			top.setVisible(false);
		}
		updateEvent(null);
	}
	
	/*
	 * 
	 */
	private void checkForErrors() {
		// check errors in General Tab
		//this.triggerDelayTextControlDecoration.hide();
		//this.settleTimeTextControlDecoration.hide();
		
		for(IModelError error : this.currentScanModule.getModelErrors()) {
			final IModelError modelError = error;
			if(modelError instanceof ScanModuleError) {
				final ScanModuleError scanModuleError = 
						(ScanModuleError) modelError;
				switch(scanModuleError.getErrorType()) {
					case TRIGGER_DELAY_NOT_POSSIBLE:
						this.triggerDelayTextControlDecoration.show();
						break;
					case SETTLE_TIME_NOT_POSSIBLE:
						this.settleTimeTextControlDecoration.show();
						break;
				}
			}
		}
		
		// check errors in Actions Tab
		this.motorAxisTab.setImage(null);
		this.detectorChannelTab.setImage(null);
		this.prescanTab.setImage(null);
		this.postscanTab.setImage(null);
		this.positioningTab.setImage(null);
		this.plotTab.setImage(null);
		boolean motorAxisErrors = false;
		boolean detectorChannelErrors = false;
		boolean prescanErrors = false;
		boolean postscanErrors = false;
		boolean positioningErrors = false;
		boolean plotWindowErrors = false;
		
		for(IModelError error : this.currentScanModule.getModelErrors()) {
			if(error instanceof AxisError) {
				motorAxisErrors = true;
			} else if(error instanceof ChannelError) {
				detectorChannelErrors = true;
			} else if(error instanceof PrescanError) {
				prescanErrors = true;
			} else if(error instanceof PostscanError) {
				postscanErrors = true;
			} else if(error instanceof PositioningError) {
				positioningErrors = true;
			} else if(error instanceof PlotWindowError) {
				plotWindowErrors = true;
			}
		}
		
		if(motorAxisErrors) {
			this.motorAxisTab.setImage(PlatformUI.getWorkbench().
									getSharedImages().getImage(
									ISharedImages.IMG_OBJS_ERROR_TSK));
		}
		if(detectorChannelErrors) {
			this.detectorChannelTab.setImage(PlatformUI.getWorkbench().
									getSharedImages().getImage( 
									ISharedImages.IMG_OBJS_ERROR_TSK));
		}
		if(prescanErrors) {
			this.prescanTab.setImage(PlatformUI.getWorkbench().
									getSharedImages().getImage(
									ISharedImages.IMG_OBJS_ERROR_TSK));
		}
		if(postscanErrors) {
			this.postscanTab.setImage(PlatformUI.getWorkbench().
									getSharedImages().getImage(
									ISharedImages.IMG_OBJS_ERROR_TSK));
		}
		if(positioningErrors) {
			this.positioningTab.setImage(PlatformUI.getWorkbench().
									getSharedImages().getImage(
									ISharedImages.IMG_OBJS_ERROR_TSK));
		}
		if(plotWindowErrors) {
			this.plotTab.setImage(PlatformUI.getWorkbench().
									getSharedImages().getImage(
									ISharedImages.IMG_OBJS_ERROR_TSK));
		}		
		
		// check errors in Events Tab
		this.pauseEventsTabItem.setImage(null);
		this.redoEventsTabItem.setImage(null);
		this.breakEventsTabItem.setImage(null);
		this.triggerEventsTabItem.setImage(null);
		
		if (this.currentScanModule.getPauseControlEventManager().
				getModelErrors().size() > 0) {
			this.pauseEventsTabItem.setImage(PlatformUI.getWorkbench().
									getSharedImages().getImage( 
									ISharedImages.IMG_OBJS_ERROR_TSK));
		}
		if (this.currentScanModule.getRedoControlEventManager().
				getModelErrors().size() > 0) {
			this.redoEventsTabItem.setImage(PlatformUI.getWorkbench().
									getSharedImages().getImage(
									ISharedImages.IMG_OBJS_ERROR_TSK));
		}
		if (this.currentScanModule.getBreakControlEventManager().
				getModelErrors().size() > 0) {
			this.breakEventsTabItem.setImage(PlatformUI.getWorkbench().
									getSharedImages().getImage(
									ISharedImages.IMG_OBJS_ERROR_TSK));
		}
		if (this.currentScanModule.getTriggerControlEventManager().
				getModelErrors().size() > 0) {
			this.triggerEventsTabItem.setImage(PlatformUI.getWorkbench().
									getSharedImages().getImage(
									ISharedImages.IMG_OBJS_ERROR_TSK));
		}
	}
	
	/**
	 * Sets the selection provider.
	 * 
	 * @param selectionProvider the selection provider that should be set
	 */
	public void setSelectionProvider(ISelectionProvider selectionProvider) {
		this.selectionProviderWrapper.setSelectionProvider(selectionProvider);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		logger.debug("selection changed");
		if (!(selection instanceof IStructuredSelection) ||
			((IStructuredSelection) selection).size() == 0) {
				return;
		}
		// at any given time this view can only display options of
		// one device we take the last (see org.eclipse.gef.SelectionMananger, 
		// primary selection)
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		Object o = structuredSelection.toList().get(structuredSelection.size()-1);
		if (o instanceof ScanModuleEditPart) {
			// set new ScanModule
			if (logger.isDebugEnabled()) {
				logger.debug("ScanModule: "
						+ ((ScanModuleEditPart) o).getModel().getId() 
						+ " selected.");
			}
			final ScanModuleEditPart smep = (ScanModuleEditPart) o;
			setCurrentScanModule(smep.getModel());
		} else if (o instanceof ChainEditPart) {
			// clicking empty space in the editor
			logger.debug("selection is ScanDescriptionEditPart: " + o);
			setCurrentScanModule(null);
		} else {
			logger.debug("selection other than ScanModule -> ignore: " + o);
		}
	}
	
	/*
	 * used by updateEvent() to re-enable listeners
	 */
	private void addListeners() {
		/*
		this.valueCountText.addModifyListener(valueCountTextModifyListener);
		 
		this.valueCountText.addVerifyListener(valueCountTextVerifyListener);
		this.triggerDelayText.addModifyListener(
				triggerDelayTextModifiedListener);
		this.triggerDelayText.addVerifyListener(triggerDelayTextVerifyListener);
		this.settleTimeText.addModifyListener(settleTimeTextModifiedListener);
		this.settleTimeText.addVerifyListener(settleTimeTextVerifyListener);
		this.triggerConfirmAxisCheckBox.addSelectionListener(
				triggerConfirmAxisCheckBoxSelectionListener);
		this.eventsTabFolder.addSelectionListener(
				eventsTabFolderSelectionListener);
		this.appendScheduleEventCheckBox.addSelectionListener(
				appendScheduleEventCheckBoxSelectionListener);
		*/
	}
	
	/*
	 * used by updateEvent() to temporarily disable listeners (preventing 
	 * event loops)
	 */
	private void removeListeners() {
		/*
		this.valueCountText.removeModifyListener(valueCountTextModifyListener);
		this.valueCountText.removeVerifyListener(valueCountTextVerifyListener);
		this.triggerDelayText.removeVerifyListener(
				triggerDelayTextVerifyListener);
		this.triggerDelayText.removeModifyListener(
				triggerDelayTextModifiedListener);
		this.settleTimeText.removeModifyListener(settleTimeTextModifiedListener);
		this.settleTimeText.removeVerifyListener(settleTimeTextVerifyListener);
		this.triggerConfirmAxisCheckBox.removeSelectionListener(
				triggerConfirmAxisCheckBoxSelectionListener);
		this.eventsTabFolder.removeSelectionListener(
				eventsTabFolderSelectionListener);
		this.appendScheduleEventCheckBox.removeSelectionListener(
				appendScheduleEventCheckBoxSelectionListener);
				*/
	}

	/*
	 * 
	 */
	private void suspendModelUpdateListener () {
		this.currentScanModule.removeModelUpdateListener(this);
	}
	
	/*
	 * 
	 */
	private void resumeModelUpdateListener () {
		this.currentScanModule.addModelUpdateListener(this);
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
		
		if(this.currentScanModule != null) {
			// set value count text
			this.valueCountText.setText(Integer.toString(this.currentScanModule
					.getValuecount()));
			
			// set trigger delay text
			this.triggerDelayText.setText((this.currentScanModule.
					getTriggerdelay() != Double.NEGATIVE_INFINITY) 
					? String.valueOf(this.currentScanModule.getTriggerdelay()) 
					: "");
			
			// set settle time text
			this.settleTimeText.setText((this.currentScanModule
					.getSettletime() != Double.NEGATIVE_INFINITY) 
					? String.valueOf(this.currentScanModule.getSettletime()) 
					: "");
					
			// set the check box for confirm trigger
			this.triggerConfirmAxisCheckBox.setSelection(
					this.currentScanModule.isTriggerconfirmaxis());
			this.triggerConfirmChannelCheckBox.setSelection(
					this.currentScanModule.isTriggerconfirmchannel());
			
			// TODO CheckBox for ScheduleIncident Start or End
			// TODO understand
			if (currentScanModule.getChain() != null) {
				Event testEvent = new Event(currentScanModule.getChain()
						.getId(), currentScanModule.getId(),
						Event.ScheduleIncident.END);
				this.appendScheduleEventCheckBox
						.setSelection(this.currentScanModule.getChain()
								.getScanDescription()
								.getEventById(testEvent.getID()) != null);
			} else {
				this.appendScheduleEventCheckBox.setSelection(false);
			}
			checkForErrors();
		}
		addListeners();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveState(IMemento memento) {
		// remember sort state of action composite viewers
		memento.putInteger("AxesSortState",
				this.motorAxisComposite.getSortState());
		memento.putInteger("ChannelSortState",
				this.detectorChannelComposite.getSortState());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset() {
		this.setCurrentScanModule(null);
	}
	
	// ************************************************************************
	// ******************** Listener ******************************************
	// ************************************************************************

	/**
	 * @author Marcus Michalsky
	 * @since 1.8
	 */
	private class ActionsMaxIconMouseListener extends MouseAdapter {
		
		/**
		 * {@inheritDoc}
		 */
		@Override 
		public void mouseUp(MouseEvent e) {
			if (actionsCompositeMaximized) {
				actionMaxIcon.setImage(maximizeIcon);
				actionMaxIcon.getParent().layout();
				sashForm.setMaximizedControl(null);
				actionsCompositeMaximized = false;
			} else {
				actionMaxIcon.setImage(restoreIcon);
				sashForm.setMaximizedControl(actionsComposite);
				actionsCompositeMaximized = true;
			}
		}
	}
	
	/**
	 * @author Marcus Michalsky
	 * @since 1.8
	 */
	private class EventMaxIconMouseListener extends MouseAdapter {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseUp(MouseEvent e) {
			if (eventsCompositeMaximized) {
				eventMaxIcon.setImage(maximizeIcon);
				eventMaxIcon.getParent().layout();
				sashForm.setMaximizedControl(null);
				eventsCompositeMaximized = false;
			} else {
				eventMaxIcon.setImage(restoreIcon);
				sashForm.setMaximizedControl(eventsComposite);
				eventsCompositeMaximized = true;
			}
		}
	}
	
	
	
	
	
	/**
	 * {@link org.eclipse.swt.events.SelectionListener} of 
	 * <code>confirmTriggerAxisCheckBox</code>.
	 */
	private class TriggerConfirmAxisCheckBoxSelectionListener implements 
					SelectionListener {
		
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
			currentScanModule.setTriggerconfirmaxis(
					triggerConfirmAxisCheckBox.getSelection());
		}
	}

	/**
	 * {@link org.eclipse.swt.events.SelectionListener} of
	 * <code>confirmTriggerDetectorCheckBox</code>.
	 * 
	 * @author Marcus Michalsky
	 * @since 1.5
	 */
	private class TriggerConfirmChannelCheckBoxSelectionListener implements 
				SelectionListener {

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
			currentScanModule.setTriggerconfirmchannel(
					triggerConfirmChannelCheckBox.getSelection());
		}
	}
	
	/**
	 * {@link org.eclipse.swt.events.SelectionListener} of
	 * <code>eventsTabFolder</code>.
	 */
	private class EventsTabFolderSelectionListener implements SelectionListener {

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
			switch (eventsTabFolder.getSelectionIndex()) {
			case 0:
				selectionProviderWrapper.setSelectionProvider(
						pauseEventComposite.getTableViewer());
				break;
			case 1:
				selectionProviderWrapper.setSelectionProvider(
						redoEventComposite.getTableViewer());
				break;
			case 2:
				selectionProviderWrapper.setSelectionProvider(
						breakEventComposite.getTableViewer());
				break;
			case 3:
				selectionProviderWrapper.setSelectionProvider(
						triggerEventComposite.getTableViewer());
				break;
			}
		}
	}
	
	/**
	 * {@link org.eclipse.swt.events.SelectionListener} of
	 * <code> appendScheduleEventCheckBox</code>.
	 */
	private class AppendScheduleEventCheckBoxSelectionListener implements
			SelectionListener {

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
			Event scheduleEvent = new Event(currentScanModule.getChain()
					.getId(), currentScanModule.getId(),
					Event.ScheduleIncident.END);

			if (appendScheduleEventCheckBox.getSelection()) {
				if (currentScanModule.getChain().getScanDescription()
						.getEventById(scheduleEvent.getID()) == null) {
					currentScanModule.getChain().getScanDescription()
							.add(scheduleEvent);
				}
			} else {
				Event event = currentScanModule.getChain().getScanDescription()
						.getEventById(scheduleEvent.getID());
				if (event != null) {
					// TODO
					// check if this event is used by any ControlEvents
					// and notify them, that we remove the event
					currentScanModule.getChain().getScanDescription()
							.remove(event);
				}
			}
		}
	}

	// ************************ Modify Listener ****************************
	
	/**
	 * {@link org.eclipse.swt.events.ModifyListener} of 
	 * <code>valueCountText</code>.
	 *
	 * @author Marcus Michalsky
	 * @since 1.5
	 */
	private class ValueCountTextModifyListener implements ModifyListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modifyText(ModifyEvent e) {
			suspendModelUpdateListener();
			if (valueCountText.getText().equals("")) {
				currentScanModule.setValuecount(1);
			} else {
				try {
					currentScanModule.setValuecount(Integer
							.parseInt(valueCountText.getText()));
				} catch (NumberFormatException e1) {
					logger.error(e1.getMessage(), e1);
				}
			}
			resumeModelUpdateListener();
		}
	}
	
	/**
	 * {@link org.eclipse.swt.events.ModifyListener} of
	 * <code>triggerDelayText</code>.
	 */
	private class TriggerDelayTextModifiedListener implements ModifyListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modifyText(ModifyEvent e) {
			suspendModelUpdateListener();
			
			if (triggerDelayText.getText().equals("")) {
				currentScanModule.setTriggerdelay(Double.NEGATIVE_INFINITY);
			} else {
				try {
					currentScanModule.setTriggerdelay(Double.parseDouble(
							triggerDelayText.getText()));
				} catch (NumberFormatException ex) {
					currentScanModule.setTriggerdelay(Double.NaN);
				}
			}	
			checkForErrors();
			resumeModelUpdateListener();
		}
	}
	
	/**
	 * {@link org.eclipse.swt.events.ModifyListener} of
	 * <code>settleTimeText</code>.
	 */
	private class SettleTimeTextModifiedListener implements ModifyListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modifyText(ModifyEvent e) {
			suspendModelUpdateListener();

			if (settleTimeText.getText().equals("")) {
				currentScanModule.setSettletime(Double.NEGATIVE_INFINITY);
			} else {
				try {
					currentScanModule.setSettletime(Double
							.parseDouble(settleTimeText.getText()));
				} catch (Exception ex) {
					currentScanModule.setSettletime(Double.NaN);
				}
			}
			checkForErrors();
			resumeModelUpdateListener();
		}
	}
	// ************************ Verify Listener ****************************

	/**
	 * {@link org.eclipse.swt.events.VerifyListener} of text widget
	 */
	private class TextDoubleVerifyListener implements VerifyListener {
		// this is a copy from DetectorChannelView

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void verifyText(VerifyEvent e) {

			switch (e.keyCode) {
			case SWT.BS: // Backspace
			case SWT.DEL: // Delete
			case SWT.HOME: // Home
			case SWT.END: // End
			case SWT.ARROW_LEFT: // Left arrow
			case SWT.ARROW_RIGHT: // Right arrow
				return;
			}

			String oldText = ((Text) (e.widget)).getText();

			if (!Character.isDigit(e.character)) {
				if (e.character == '.') {
					// character . is a valid character, if he is not in the
					// old string
					if (oldText.contains("."))
						e.doit = false;
				} else if (e.character == '-') {
					// character - is a valid character as first sign and after
					// an e
					if (oldText.isEmpty()) {
						// oldText is emtpy, - is valid
					} else if ((((Text) e.widget).getSelection().x) == 0) {
						// - is the first sign an valid
					} else {
						// wenn das letzte Zeichen von oldText ein e ist,
						// ist das minus auch erlaubt
						int index = oldText.length();
						if (oldText.substring(index - 1).equals("e")) {
							// letzte Zeichen ist ein e und damit erlaubt
						} else {
							e.doit = false;
						}
					}
				} else if (e.character == 'e') {
					// character e is a valid character, if he is not in the
					// old string
					if (oldText.contains("e"))
						e.doit = false;
				} else {
					e.doit = false; // disallow the action
				}
			}
		}
	}
}
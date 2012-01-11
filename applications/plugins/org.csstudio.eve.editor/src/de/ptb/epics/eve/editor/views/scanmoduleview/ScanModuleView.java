package de.ptb.epics.eve.editor.views.scanmoduleview;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import de.ptb.epics.eve.data.SaveAxisPositionsTypes;
import de.ptb.epics.eve.data.measuringstation.Event;
import de.ptb.epics.eve.data.measuringstation.filter.ExcludeDevicesOfScanModuleFilterManualUpdate;
import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.errors.AxisError;
import de.ptb.epics.eve.data.scandescription.errors.ChannelError;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.PlotWindowError;
import de.ptb.epics.eve.data.scandescription.errors.PositioningError;
import de.ptb.epics.eve.data.scandescription.errors.PostscanError;
import de.ptb.epics.eve.data.scandescription.errors.PrescanError;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventTypes;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.SelectionProviderWrapper;
import de.ptb.epics.eve.editor.graphical.editparts.ScanDescriptionEditPart;
import de.ptb.epics.eve.editor.graphical.editparts.ScanModuleEditPart;
import de.ptb.epics.eve.editor.views.EventComposite;

/**
 * <code>ScanModulView</code> shows the currently selected scan module.
 * 
 * @author ?
 * @author Marcus Michalsky
 * @author Hartmut Scherr
 */
public class ScanModuleView extends ViewPart implements ISelectionListener {
	
	/**
	 * the id of the <code>ScanModulView</code>.
	 */
	public static final String ID = 
			"de.ptb.epics.eve.editor.views.ScanModulView"; 
	
	private static Logger logger = Logger.getLogger(ScanModuleView.class);

	private ScanModule currentScanModule;

	private Composite top = null;

	private ExpandBar bar = null;

	private Composite generalComposite = null;	
	private GeneralCompositeControlListener generalCompositeControlListener;
	
	private Composite actionsComposite = null;
	private ActionsCompositeControlListener actionsCompositeControlListener;
	
	private Composite eventsComposite = null;
	private EventsCompositeControlListener eventsCompositeControlListener;

	private Label triggerDelayLabel = null;
	private Text triggerDelayText = null;
	private Label triggerDelayErrorLabel = null;
	private Label triggerDelayUnitLabel = null;
	private TriggerDelayTextModifiedListener triggerDelayTextModifiedListener;

	private Label settleTimeLabel = null;
	private Text settleTimeText = null;
	private Label settleTimeErrorLabel = null;
	private Label settleTimeUnitLabel = null;
	private SettleTimeTextModifiedListener settleTimeTextModifiedListener;
	
	private Button confirmTriggerCheckBox = null;
	private ConfirmTriggerCheckBoxSelectionListener 
			confirmTriggerCheckBoxSelectionListener;
	
	private Label saveMotorpositionsLabel = null;
	private Combo saveMotorpositionsCombo = null;
	private SaveMotorpositionsComboModifiedListener 
			saveMotorpositionsComboModifiedListener;
	
	private CTabFolder eventsTabFolder = null;
	private EventsTabFolderSelectionListener eventsTabFolderSelectionListener;
	
	private EventComposite pauseEventComposite = null;
	private EventComposite redoEventComposite = null;
	private EventComposite breakEventComposite = null;
	private EventComposite triggerEventComposite = null;

	private CTabFolder behaviorTabFolder = null;

	private Composite motorAxisComposite = null;
	private Composite detectorChannelComposite = null;
	private Composite prescanComposite = null;
	private Composite postscanComposite = null;
	private Composite positioningComposite = null;
	private Composite plotComposite = null;

	private Button appendScheduleEventCheckBox = null;
	private AppendScheduleEventCheckBoxSelectionListener
			appendScheduleEventCheckBoxSelectionListener;

	private String[] eventIDs;

	private ExpandItem item0;
	private ExpandItem item1;
	private ExpandItem item2;

	private CTabItem motorAxisTab;
	private CTabItem detectorChannelTab;
	private CTabItem prescanTab;
	private CTabItem postscanTab;
	private CTabItem positioningTab;
	private CTabItem plotTab;
	
	private CTabItem pauseEventsTabItem;
	private CTabItem redoEventsTabItem;
	private CTabItem breakEventsTabItem;
	private CTabItem triggerEventsTabItem;
	
	private ExcludeDevicesOfScanModuleFilterManualUpdate measuringStation;
	private ExcludeDevicesOfScanModuleFilterManualUpdate measuringStationPrescan;
	private ExcludeDevicesOfScanModuleFilterManualUpdate measuringStationPostscan;

	// the selection service only accepts one selection provider per view,
	// since we have multiple tabs with tables capable of providing selections 
	// a wrapper handles them and registers the active one with the global 
	// selection service
	protected SelectionProviderWrapper selectionProviderWrapper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite parent) {
		logger.debug("createPartControl");	
		
		parent.setLayout(new FillLayout());
		
		this.measuringStation = new ExcludeDevicesOfScanModuleFilterManualUpdate(
				true, true, false, false, false);
		this.measuringStation.setSource(
				Activator.getDefault().getMeasuringStation());
		// this.measuringStation.addModelUpdateListener(this);

		this.measuringStationPrescan = new ExcludeDevicesOfScanModuleFilterManualUpdate(
				false, false, true, false, false);
		this.measuringStationPrescan.setSource(
				Activator.getDefault().getMeasuringStation());
		// this.measuringStationPrescan.addModelUpdateListener(this);

		this.measuringStationPostscan = new ExcludeDevicesOfScanModuleFilterManualUpdate( 
				false, false, false, true, false);
		this.measuringStationPostscan.setSource(
				Activator.getDefault().getMeasuringStation());
		// this.measuringStationPostscan.addModelUpdateListener(this);

		if(Activator.getDefault().getMeasuringStation() == null) {
			final Label errorLabel = new Label(parent, SWT.NONE);
			errorLabel.setText("No Measuring Station has been loaded. " +
					"Please check Preferences!");
			return;
		}
		
		final java.util.List<Event> events = Activator.getDefault()
				.getMeasuringStation().getEvents();
		this.eventIDs = new String[events.size()];
		int i = 0;
		final Iterator<Event> it = events.iterator();
		while (it.hasNext()) {
			this.eventIDs[i++] = it.next().getID();
		}
		
		this.top = new Composite(parent, SWT.NONE);
		this.top.setLayout(new GridLayout());
		
		this.bar = new ExpandBar(this.top, SWT.V_SCROLL);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		this.bar.setLayoutData(gridData);
		
		// General Section
		createGeneralTabFolder();
		// Actions Section
		createActionsTabFolder();
		// Event Section
		createEventsTabFolder();
		
		this.setEnabledForAll(false);
		top.setVisible(false);
		
		// listen to selection changes (if a scan module is selected, its 
		// attributes are made available for editing)
		getSite().getWorkbenchWindow().getSelectionService().
				addSelectionListener(this);
		
		selectionProviderWrapper = new SelectionProviderWrapper();
		getSite().setSelectionProvider(selectionProviderWrapper);
	} // end of: createPartControl()

	/*
	 * called by CreatePartControl to create the contents of the first 
	 * expand item (General Tab)
	 */
	private void createGeneralTabFolder()
	{
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		this.generalComposite = new Composite(this.bar, SWT.NONE);
		this.generalComposite.setLayout(gridLayout);		
		this.generalCompositeControlListener = 
				new GeneralCompositeControlListener();	
		this.generalComposite.addControlListener(
				generalCompositeControlListener);
		
		// Trigger Delay
		this.triggerDelayLabel = new Label(this.generalComposite, SWT.NONE);
		this.triggerDelayLabel.setText("Trigger delay:");
		this.triggerDelayLabel.setToolTipText("Delay time after positioning");
		
		this.triggerDelayText = new Text(this.generalComposite, SWT.BORDER);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		this.triggerDelayText.setLayoutData(gridData);		
		this.triggerDelayTextModifiedListener = 
				new TriggerDelayTextModifiedListener();
		this.triggerDelayText.addModifyListener(
				triggerDelayTextModifiedListener);

		this.triggerDelayErrorLabel = new Label(this.generalComposite, SWT.NONE);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.CENTER;
		this.triggerDelayErrorLabel.setLayoutData(gridData);
		this.triggerDelayErrorLabel.setImage(PlatformUI.getWorkbench().
				getSharedImages().getImage(ISharedImages.IMG_OBJS_WARN_TSK));
		this.triggerDelayUnitLabel = new Label(this.generalComposite, SWT.NONE);
		this.triggerDelayUnitLabel.setText("s");

		// Settle Time
		this.settleTimeLabel = new Label(this.generalComposite, SWT.NONE);
		this.settleTimeLabel.setText("Settletime:");
		this.settleTimeLabel.setToolTipText(
				"Delay time after first positioning in the scan module");
		
		this.settleTimeText = new Text(this.generalComposite, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		this.settleTimeText.setLayoutData(gridData);
		this.settleTimeTextModifiedListener = 
				new SettleTimeTextModifiedListener();
		this.settleTimeText.addModifyListener(settleTimeTextModifiedListener);
		
		this.settleTimeErrorLabel = new Label(this.generalComposite, SWT.NONE);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.CENTER;
		this.settleTimeErrorLabel.setLayoutData(gridData);
		//this.settleTimeErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_WARN_TSK ) );
		this.settleTimeUnitLabel = new Label(this.generalComposite, SWT.NONE);
		this.settleTimeUnitLabel.setText("s");

		// Trigger Confirm 
		gridData = new GridData();
		gridData.horizontalSpan = 4;

		this.confirmTriggerCheckBox = new Button(this.generalComposite, SWT.CHECK);
		this.confirmTriggerCheckBox.setText("Confirm Trigger");
		this.confirmTriggerCheckBox.setToolTipText("Mark to ask before trigger");
		this.confirmTriggerCheckBox.setLayoutData(gridData);
		this.confirmTriggerCheckBoxSelectionListener = 
				new ConfirmTriggerCheckBoxSelectionListener();
		this.confirmTriggerCheckBox.addSelectionListener(
				confirmTriggerCheckBoxSelectionListener);
		
		// Save Motor Positions
		this.saveMotorpositionsLabel = new Label(this.generalComposite, SWT.NONE);
		this.saveMotorpositionsLabel.setText("Save all motorpositions");
		gridData = new GridData();
		gridData.horizontalSpan = 1;
		this.saveMotorpositionsLabel.setLayoutData(gridData);
		
		this.saveMotorpositionsCombo = new Combo(this.generalComposite, SWT.NONE);
		this.saveMotorpositionsCombo.setItems(new String[] { 
				"never", "before", "after", "both" });
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		this.saveMotorpositionsCombo.setLayoutData(gridData);
		this.saveMotorpositionsComboModifiedListener = 
				new SaveMotorpositionsComboModifiedListener();
		this.saveMotorpositionsCombo.addModifyListener(
				saveMotorpositionsComboModifiedListener);
		
		this.item0 = new ExpandItem(this.bar, SWT.NONE, 0);
		item0.setText("General");
		item0.setHeight(this.generalComposite.computeSize(
				SWT.DEFAULT, SWT.DEFAULT).y);
		item0.setControl(this.generalComposite);
	}
	
	/*
	 * called by CreatePartControl to create the contents of the second 
	 * expand item (Actions Tab)
	 */
	private void createActionsTabFolder() {

		GridLayout gridLayout = new GridLayout();

		this.actionsComposite = new Composite(this.bar, SWT.NONE);
		this.actionsComposite.setLayout(gridLayout);
		this.actionsCompositeControlListener = 
				new ActionsCompositeControlListener();
		this.actionsComposite.addControlListener(
				actionsCompositeControlListener);
		
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		this.behaviorTabFolder = new CTabFolder(this.actionsComposite, SWT.FLAT);
		this.behaviorTabFolder.setLayoutData(gridData);
		
		motorAxisComposite = new MotorAxisComposite(this, 
				behaviorTabFolder, SWT.NONE, this.measuringStation);
		detectorChannelComposite = new DetectorChannelComposite(this, 
				behaviorTabFolder, SWT.NONE, this.measuringStation);
		prescanComposite = new PrescanComposite(
				behaviorTabFolder, SWT.NONE, this.measuringStationPrescan);
		postscanComposite = new PostscanComposite(
				behaviorTabFolder, SWT.NONE, this.measuringStationPostscan);
		positioningComposite = new PositioningComposite(
				behaviorTabFolder, SWT.NONE);
		plotComposite = new PlotComposite(this, behaviorTabFolder, SWT.NONE);
		
		this.motorAxisTab = new CTabItem(this.behaviorTabFolder, SWT.FLAT);
		this.motorAxisTab.setText(" Motor Axes ");
		this.motorAxisTab.setToolTipText(
				"Select motor axes to be used in this scan module");
		this.motorAxisTab.setControl(this.motorAxisComposite);
		
		this.detectorChannelTab = new CTabItem(this.behaviorTabFolder, SWT.FLAT);
		this.detectorChannelTab.setText(" Detector Channels ");
		this.detectorChannelTab.setToolTipText(
				"Select detector channels to be used in this scan module");
		this.detectorChannelTab.setControl(this.detectorChannelComposite);
		
		this.prescanTab = new CTabItem(this.behaviorTabFolder, SWT.FLAT);
		this.prescanTab.setText(" Prescan ");
		this.prescanTab.setToolTipText(
				"Action to do before scan module is started");
		this.prescanTab.setControl(this.prescanComposite);
		
		this.postscanTab = new CTabItem(this.behaviorTabFolder, SWT.FLAT);
		this.postscanTab.setText(" Postscan ");
		this.postscanTab.setToolTipText("Action to do if scan module is done");
		this.postscanTab.setControl(this.postscanComposite);
		
		this.positioningTab = new CTabItem(this.behaviorTabFolder, SWT.FLAT);
		this.positioningTab.setText(" Positioning ");
		this.positioningTab.setToolTipText(
				"Move motor to calculated position after scan module is done");
		this.positioningTab.setControl(this.positioningComposite);

		this.plotTab = new CTabItem(this.behaviorTabFolder, SWT.FLAT);
		this.plotTab.setText(" Plot ");
		this.plotTab.setToolTipText("Plot settings for this scan module");
		this.plotTab.setControl(this.plotComposite);

		this.item1 = new ExpandItem(this.bar, SWT.NONE, 0);
		item1.setText("Actions");
		item1.setControl(this.actionsComposite);
	}
	
	/*
	 * Initializes eventsTabFolder
	 */
	private void createEventsTabFolder() {

		GridLayout gridLayout = new GridLayout();

		this.eventsComposite = new Composite(this.bar, SWT.NONE);
		this.eventsComposite.setLayout(gridLayout);
		this.eventsCompositeControlListener = 
			new EventsCompositeControlListener();	
		this.eventsComposite.addControlListener(eventsCompositeControlListener);
		
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;
		eventsTabFolder = new CTabFolder(this.eventsComposite, SWT.NONE);
		eventsTabFolder.setLayoutData(gridData);	
		this.eventsTabFolderSelectionListener = 
				new EventsTabFolderSelectionListener();	
		eventsTabFolder.addSelectionListener(eventsTabFolderSelectionListener);

		pauseEventComposite = new EventComposite(eventsTabFolder, SWT.NONE, 
				ControlEventTypes.PAUSE_EVENT);
		redoEventComposite = new EventComposite(eventsTabFolder, SWT.NONE, 
				ControlEventTypes.CONTROL_EVENT);
		breakEventComposite = new EventComposite(eventsTabFolder, SWT.NONE, 
				ControlEventTypes.CONTROL_EVENT);
		triggerEventComposite = new EventComposite(eventsTabFolder, SWT.NONE, 
				ControlEventTypes.CONTROL_EVENT);
		
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
		
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		appendScheduleEventCheckBox = new Button(this.eventsComposite, SWT.CHECK);
		appendScheduleEventCheckBox.setText("Append Schedule Event");
		appendScheduleEventCheckBox.setLayoutData(gridData);
		this.appendScheduleEventCheckBoxSelectionListener = 
				new AppendScheduleEventCheckBoxSelectionListener();
		this.appendScheduleEventCheckBox.addSelectionListener(
				appendScheduleEventCheckBoxSelectionListener);
		
		this.item2 = new ExpandItem(this.bar, SWT.NONE, 0);
		item2.setText("Event options");
		item2.setHeight(this.eventsComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		item2.setControl(this.eventsComposite);
	}	
	
	// ***********************************************************************
	// ********************** end of create part control *********************
	// ***********************************************************************
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
	}

	/**
	 * Returns the currently active scan module.
	 * 
	 * @return the scan module
	 */
	public ScanModule getCurrentScanModule() {
		return currentScanModule;
	}

	/**
	 * Sets the currently active scan module.
	 * 
	 * @param currentScanModule the scan module that should be set
	 */
	private void setCurrentScanModule(ScanModule currentScanModule) {
		logger.debug("setCurrentScanModule");
		// if there was already a scan module -> update it
		this.currentScanModule = currentScanModule;

		this.measuringStation.setScanModule(this.currentScanModule);
		this.measuringStationPrescan.setScanModule(this.currentScanModule);
		this.measuringStationPostscan.setScanModule(this.currentScanModule);
		//this.measuringStationPositioning.setScanModule(this.currentScanModule);

//		updateEvent(null);

		// is there a scan module selected (in the editor) ?
		if(this.currentScanModule != null) {
			
			this.setEnabledForAll(true);
			this.setPartName(this.currentScanModule.getName() + ":"
							 + this.currentScanModule.getId());	
			
			// set trigger delay text
			this.triggerDelayText.setText((this.currentScanModule.
					getTriggerdelay() != Double.NEGATIVE_INFINITY) 
					? "" + this.currentScanModule.getTriggerdelay() 
					: "");
			
			// set settle time text
			this.settleTimeText.setText((this.currentScanModule
					.getSettletime() != Double.NEGATIVE_INFINITY) 
					? "" + this.currentScanModule.getSettletime() 
					: "");
					
			// set the check box for confirm trigger
			this.confirmTriggerCheckBox.setSelection(
					this.currentScanModule.isTriggerconfirm());

			// select content from combo box for save all motor positions
//			this.saveMotorpositionsCombo.setText(
//					this.currentScanModule.getSaveAxisPositions().name());

			if(behaviorTabFolder.getSelection() == null)
				behaviorTabFolder.setSelection(0);
			
			((MotorAxisComposite) this.motorAxisComposite).setScanModule(
					this.currentScanModule);
			((DetectorChannelComposite) this.detectorChannelComposite).setScanModule(
					this.currentScanModule);
			((PrescanComposite) this.prescanComposite).setScanModule(
					this.currentScanModule);
			((PostscanComposite) this.postscanComposite).setScanModule(
					this.currentScanModule);
			((PositioningComposite) this.positioningComposite).setScanModule(
					this.currentScanModule);
			((PlotComposite) this.plotComposite).setScanModule(
					this.currentScanModule);

			this.triggerEventComposite.setControlEventManager(
					this.currentScanModule.getTriggerControlEventManager());
			this.breakEventComposite.setControlEventManager(
					this.currentScanModule.getBreakControlEventManager());
			this.redoEventComposite.setControlEventManager(
					this.currentScanModule.getRedoControlEventManager());
			this.pauseEventComposite.setControlEventManager(
					this.currentScanModule.getPauseControlEventManager());
			
			// TODO CheckBox for ScheduleIncident Start or End
			Event testEvent = new Event(currentScanModule.getChain().getId(), 
										currentScanModule.getId(), 
										Event.ScheduleIncident.END);
			this.appendScheduleEventCheckBox.setSelection(
					this.currentScanModule.getChain().getScanDescription().
					getEventById(testEvent.getID()) != null);
			
			
			checkForErrors();
			
			top.setVisible(true);
			
			// expand items
			item1.setExpanded(true);
			
		} else { // currentScanModule == null
			// no scan module selected -> reset contents
			
//			this.setEnabledForAll(false);
			this.setPartName("No Scan Module selected");
			
//			this.triggerDelayText.setText("");
//			this.settleTimeText.setText("");
//			this.confirmTriggerCheckBox.setSelection(false);
			
			((MotorAxisComposite) this.motorAxisComposite).setScanModule(null);
			((DetectorChannelComposite) this.detectorChannelComposite).
					setScanModule(null);
			((PrescanComposite) this.prescanComposite).setScanModule(null);
			((PostscanComposite) this.postscanComposite).setScanModule(null);
			((PositioningComposite) this.positioningComposite)
					.setScanModule(null);
			((PlotComposite) this.plotComposite).setScanModule(null);
			
			triggerEventComposite.setControlEventManager(null);
			breakEventComposite.setControlEventManager(null);
			redoEventComposite.setControlEventManager(null);
			pauseEventComposite.setControlEventManager(null);
			
			appendScheduleEventCheckBox.setSelection(false);

			selectionProviderWrapper.setSelectionProvider(null);

			top.setVisible(false);
		}
		

		
	}

	private void setEnabledForAll(final boolean enabled) {
		this.triggerDelayText.setEnabled(enabled);
		this.settleTimeText.setEnabled(enabled);
		this.confirmTriggerCheckBox.setEnabled(enabled);
		this.behaviorTabFolder.setEnabled(enabled);
		this.motorAxisComposite.setEnabled(enabled);
		this.detectorChannelComposite.setEnabled(enabled);
		// pre, post, positioning, plot ???
		this.eventsTabFolder.setEnabled(enabled);
		this.saveMotorpositionsCombo.setEnabled(enabled);

		this.appendScheduleEventCheckBox.setEnabled(enabled);

	}

	/*
	 * 
	 */
	private void checkForErrors()
	{
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
		
		final Iterator<IModelError> it = 
				this.currentScanModule.getModelErrors().iterator();
		while(it.hasNext()) {
			final IModelError modelError = it.next();
			if(modelError instanceof AxisError) {
				motorAxisErrors = true;
			} else if(modelError instanceof ChannelError) {
				detectorChannelErrors = true;
			} else if(modelError instanceof PrescanError) {
				prescanErrors = true;
			} else if(modelError instanceof PostscanError) {
				postscanErrors = true;
			} else if(modelError instanceof PositioningError) {
				positioningErrors = true;
			} else if(modelError instanceof PlotWindowError) {
				plotWindowErrors = true;
			} 
		}
		
		if(motorAxisErrors) {
			this.motorAxisTab.setImage( 
					PlatformUI.getWorkbench().getSharedImages().getImage(
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
		
		
		if(this.currentScanModule.getPauseControlEventManager().
								  getModelErrors().size() > 0) {
			this.pauseEventsTabItem.setImage(PlatformUI.getWorkbench().
											 getSharedImages().getImage( 
											ISharedImages.IMG_OBJS_ERROR_TSK));
		} else {
			this.pauseEventsTabItem.setImage(null);
		}
		if(this.currentScanModule.getRedoControlEventManager().
								  getModelErrors().size() > 0) {
			this.redoEventsTabItem.setImage(PlatformUI.getWorkbench().
											getSharedImages().getImage(
											ISharedImages.IMG_OBJS_ERROR_TSK));
		} else {
			this.redoEventsTabItem.setImage(null);
		}
		if( this.currentScanModule.getBreakControlEventManager().
								   getModelErrors().size() > 0) {
			this.breakEventsTabItem.setImage(PlatformUI.getWorkbench().
											 getSharedImages().getImage(
											 ISharedImages.IMG_OBJS_ERROR_TSK));
		} else {
			this.breakEventsTabItem.setImage(null);
		}
		if(this.currentScanModule.getTriggerControlEventManager().
								  getModelErrors().size() > 0) {
			this.triggerEventsTabItem.setImage(PlatformUI.getWorkbench().
											   getSharedImages().getImage(
											   ISharedImages.IMG_OBJS_ERROR_TSK));
		} else {
			this.triggerEventsTabItem.setImage(null);
		}
		
		if(this.triggerDelayText.getText().equals("")) {
			triggerDelayErrorLabel.setImage(PlatformUI.getWorkbench().
								   			getSharedImages().getImage(
								   		ISharedImages.IMG_OBJS_ERROR_TSK));
			triggerDelayErrorLabel.setToolTipText(
					"The trigger delay must not be empty!");
		} else {
			triggerDelayErrorLabel.setImage(null);
			triggerDelayErrorLabel.setToolTipText(null);
		}
		if( this.settleTimeText.getText().equals("")) {
			settleTimeErrorLabel.setImage(PlatformUI.getWorkbench().
										  getSharedImages().getImage(
										ISharedImages.IMG_OBJS_ERROR_TSK));
			settleTimeErrorLabel.setToolTipText(
					"The settletime must not be empty!");
		} else {
			settleTimeErrorLabel.setImage(null);
			settleTimeErrorLabel.setToolTipText(null);
		}
	}
		
	/*
	 * used by updateEvent() to re-enable listeners
	 */
	private void addListeners()
	{
		this.generalComposite.addControlListener(
				generalCompositeControlListener);
		this.triggerDelayText.addModifyListener(
				triggerDelayTextModifiedListener);
		this.settleTimeText.addModifyListener(settleTimeTextModifiedListener);
		this.confirmTriggerCheckBox.addSelectionListener(
				confirmTriggerCheckBoxSelectionListener);
		this.saveMotorpositionsCombo.addModifyListener(
				saveMotorpositionsComboModifiedListener);
		this.eventsTabFolder.addSelectionListener(
				eventsTabFolderSelectionListener);
		this.eventsComposite.addControlListener(eventsCompositeControlListener);
		this.appendScheduleEventCheckBox.addSelectionListener(
				appendScheduleEventCheckBoxSelectionListener);
	}
	
	/*
	 * used by updateEvent() to temporarily disable listeners (preventing 
	 * event loops)
	 */
	private void removeListeners()
	{
		this.generalComposite.removeControlListener(
				generalCompositeControlListener);
		this.triggerDelayText.removeModifyListener(
				triggerDelayTextModifiedListener);
		this.settleTimeText.removeModifyListener(settleTimeTextModifiedListener);
		this.confirmTriggerCheckBox.removeSelectionListener(
				confirmTriggerCheckBoxSelectionListener);
		this.saveMotorpositionsCombo.removeModifyListener(
				saveMotorpositionsComboModifiedListener);
		this.eventsTabFolder.removeSelectionListener(
				eventsTabFolderSelectionListener);
		this.eventsComposite.removeControlListener(
				eventsCompositeControlListener);
		this.appendScheduleEventCheckBox.removeSelectionListener(
				appendScheduleEventCheckBoxSelectionListener);
	}
	
	/*
	 * used by several control listeners
	 */
	private void resize() {
		// Caution: do not use getHeaderHeight() here
		// height values vary without changing the layout
		// which puts us into an endless loop
		
		int height = bar.getSize().y - 6 * 25;

		if (item0.getExpanded()) {
			height -= item0.getHeight();
		}

		int amount = 0;
		if (item1.getExpanded()) {
			amount++;
		}
		if (item2.getExpanded()) {
			amount++;
		}

		if (amount > 0) {
			height /= amount;
			if(item1.getExpanded()) {
				item1.setHeight(height < 200 ? 200 : height);
			}
			if(item2.getExpanded()) {
				item2.setHeight(height < 150 ? 150 : height);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		logger.debug("selection changed");
		
//		logger.debug(selection);

		if(selection instanceof IStructuredSelection) {
			if(((IStructuredSelection) selection).size() == 0) {

/*				if (this.currentScanModule != null) {
					if (this.currentScanModule.getAxes().length == 0) {
						if(logger.isDebugEnabled()) {
							logger.debug("selection is empty, scanModule: " + 
								this.currentScanModule.getId() + "-> ignore"); 
						}
						setCurrentScanModule(null);
					}
				} else {
					logger.debug(
					  "selection ist empty, no scanModule available -> ignore");
				}*/
				return;
			}
			// since at any given time this view can only display options of 
			// one device we take the first element of the selection
			Object o = ((IStructuredSelection) selection).toList().get(0);
			if (o instanceof ScanModuleEditPart) {
				// set new ScanModule
				if(logger.isDebugEnabled()) {
					logger.debug("ScanModule: " + ((ScanModule)((ScanModuleEditPart)o).getModel()).getId() + 
							" selected."); 
				}
				setCurrentScanModule((ScanModule)((ScanModuleEditPart)o).getModel());

			} else if (o instanceof ScanDescriptionEditPart) {
					logger.debug("selection is ScanDescriptionEditPart: " + o);
					System.out.println("\n\nNEU: Hier wurde eine ScanDescription selektiert");
					setCurrentScanModule(null);
			} else {
				logger.debug("selection other than ScanModule -> ignore: " + o);
			}
		}
	}
	
	// ************************************************************************
	// ******************** Listener ******************************************
	// ************************************************************************
	
	// ************************ Selection Listener ****************************
	
	/**
	 * {@link org.eclipse.swt.events.SelectionListener} of 
	 * <code>confirmTriggerCheckBox</code>.
	 */
	private class ConfirmTriggerCheckBoxSelectionListener implements SelectionListener {
		
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
			currentScanModule.setTriggerconfirm(
					confirmTriggerCheckBox.getSelection());		
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
			// update select box entries
			CTabItem wahlItem = eventsTabFolder.getSelection();
			EventComposite wahlComposite = (EventComposite)wahlItem.getControl();
			wahlComposite.setEventChoice();		
		}
	}
	
	/**
	 * {@link org.eclipse.swt.events.SelectionListener} of
	 * <code> appendScheduleEventCheckBox</code>.
	 */
	private class AppendScheduleEventCheckBoxSelectionListener 
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
			Event scheduleEvent = new Event(currentScanModule.getChain().getId(), 
											currentScanModule.getId(), 
											Event.ScheduleIncident.END);

			if (appendScheduleEventCheckBox.getSelection()) {
				if (currentScanModule.getChain().getScanDescription().
						getEventById(scheduleEvent.getID()) == null) {
					currentScanModule.getChain().getScanDescription().
						add(scheduleEvent);
				}
			} else {
				Event event = currentScanModule.getChain().getScanDescription().
											getEventById(scheduleEvent.getID());
				if (event != null) {
					// TODO
					//check if this event is used by any ControlEvents
					// and notify them, that we remove the event
					currentScanModule.getChain().getScanDescription().
									  remove(event);
				} 
			}		
		}
	}
	
	// ************************ Modify Listener ****************************
	
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
			System.out.println("TriggerDelayTextModifiedListener aufgerufen, warum?");
			if (triggerDelayText.getText().equals("")) {
				currentScanModule
							.setTriggerdelay(Double.NEGATIVE_INFINITY);
				triggerDelayErrorLabel.setImage(PlatformUI.getWorkbench().
									   getSharedImages().getImage( 
									   ISharedImages.IMG_OBJS_ERROR_TSK));
				triggerDelayErrorLabel.setToolTipText(
						"The trigger delay must not be empty!");
			} else {
				try {
					currentScanModule.setTriggerdelay(Double.parseDouble(
							triggerDelayText.getText()));
						triggerDelayErrorLabel.setImage(null);
						triggerDelayErrorLabel.setToolTipText(null);
				} catch (NumberFormatException ex) {
						triggerDelayErrorLabel.setImage(PlatformUI.
								getWorkbench().getSharedImages().getImage(
								ISharedImages.IMG_OBJS_ERROR_TSK));
						triggerDelayErrorLabel.setToolTipText(
						  "The trigger delay must be an floating point value!");
				}
			}	
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
			if (settleTimeText.getText().equals("")) {
				currentScanModule.setSettletime(Double.NEGATIVE_INFINITY);
				settleTimeErrorLabel.setImage(PlatformUI.getWorkbench().
											 getSharedImages().getImage(
											 ISharedImages.IMG_OBJS_ERROR_TSK));
				settleTimeErrorLabel.setToolTipText(
						"The settletime must not be empty!");
			} else {
				try {
					currentScanModule.setSettletime(
							Double.parseDouble(settleTimeText.getText()));
					settleTimeErrorLabel.setImage(null);
					settleTimeErrorLabel.setToolTipText(null);
				} catch (Exception ex) {
					currentScanModule.setSettletime(Double.NEGATIVE_INFINITY);
					settleTimeErrorLabel.setImage(PlatformUI.getWorkbench().
									getSharedImages().getImage(
									ISharedImages.IMG_OBJS_ERROR_TSK));
					settleTimeErrorLabel.setToolTipText(
							"The settletime must be an floating point value!");
				}
			}	
		}
	}
	
	/**
	 * {@link org.eclipse.swt.events.ModifyListener} of 
	 * <code>saveMotorpositionsCombo</code>.
	 */
	private class SaveMotorpositionsComboModifiedListener implements ModifyListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modifyText(ModifyEvent e) {
			currentScanModule.setSaveAxisPositions(SaveAxisPositionsTypes.
					stringToType(saveMotorpositionsCombo.getText()));		
		}
	}
	
	// ************************ Control Listener ****************************
	
	/**
	 * {@link org.eclipse.swt.events.ControlListener} of
	 * <code>actionsComposite</code>.
	 */
	private class ActionsCompositeControlListener implements ControlListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void controlMoved(ControlEvent e) {
			actionsComposite.setFocus();	
			resize();
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void controlResized(ControlEvent e) {
			resize();
		}
	}
	
	/**
	 * {@link org.eclipse.swt.events.ControlListener} of
	 * <code>eventsComposite</code>.
	 */
	private class EventsCompositeControlListener implements ControlListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void controlMoved(ControlEvent e) {
			eventsComposite.setFocus();
			resize();
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void controlResized(ControlEvent e) {
			resize();
		}
	}
	
	/**
	 * {@link org.eclipse.swt.events.ControlListener} of
	 * <code>generalComposite</code>.
	 */
	private class GeneralCompositeControlListener implements ControlListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void controlMoved(ControlEvent e) {
			resize();
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void controlResized(ControlEvent e) {
			resize();
		}
	}

}
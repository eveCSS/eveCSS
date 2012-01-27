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
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import de.ptb.epics.eve.data.measuringstation.Event;
import de.ptb.epics.eve.data.measuringstation.filter.ExcludeDevicesOfScanModuleFilterManualUpdate;
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
public class ScanModuleView extends ViewPart implements ISelectionListener,
					IModelUpdateListener {
	
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
	
	public CTabFolder eventsTabFolder = null;
	private EventsTabFolderSelectionListener eventsTabFolderSelectionListener;
	
	private EventComposite pauseEventComposite = null;
	private EventComposite redoEventComposite = null;
	private EventComposite breakEventComposite = null;
	private EventComposite triggerEventComposite = null;
	
	private CTabFolder actionsTabFolder = null;
	
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
	
	private ExpandItem itemGeneral;
	private ExpandItem itemActions;
	private ExpandItem itemEvents;
	
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
	private ExcludeDevicesOfScanModuleFilterManualUpdate 
			measuringStationPrescan;
	private ExcludeDevicesOfScanModuleFilterManualUpdate 
			measuringStationPostscan;
	
	// the selection service only accepts one selection provider per view,
	// since we have multiple tabs with tables capable of providing selections, 
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
		
		if(Activator.getDefault().getMeasuringStation() == null) {
			final Label errorLabel = new Label(parent, SWT.NONE);
			errorLabel.setText("No Measuring Station has been loaded. " +
					"Please check Preferences!");
			return;
		}
		
		this.measuringStation = new 
				ExcludeDevicesOfScanModuleFilterManualUpdate(
				true, true, false, false, false);
		this.measuringStation.setSource(
				Activator.getDefault().getMeasuringStation());
		
		this.measuringStationPrescan = new 
				ExcludeDevicesOfScanModuleFilterManualUpdate(
				false, false, true, false, false);
		this.measuringStationPrescan.setSource(
				Activator.getDefault().getMeasuringStation());
		
		this.measuringStationPostscan = new 
				ExcludeDevicesOfScanModuleFilterManualUpdate( 
				false, false, false, true, false);
		this.measuringStationPostscan.setSource(
				Activator.getDefault().getMeasuringStation());
		
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
		createGeneralExpandItem();
		// Actions Section
		createActionsExpandItem();
		// Event Section
		createEventsExpandItem();
		
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
	 * expand item (General)
	 */
	private void createGeneralExpandItem() {
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
		this.settleTimeUnitLabel = new Label(this.generalComposite, SWT.NONE);
		this.settleTimeUnitLabel.setText("s");
		
		// Trigger Confirm 
		gridData = new GridData();
		gridData.horizontalSpan = 4;
		
		this.confirmTriggerCheckBox = new Button(this.generalComposite, 
												SWT.CHECK);
		this.confirmTriggerCheckBox.setText("Confirm Trigger");
		this.confirmTriggerCheckBox.setToolTipText("Mark to ask before trigger");
		this.confirmTriggerCheckBox.setLayoutData(gridData);
		this.confirmTriggerCheckBoxSelectionListener = 
				new ConfirmTriggerCheckBoxSelectionListener();
		this.confirmTriggerCheckBox.addSelectionListener(
				confirmTriggerCheckBoxSelectionListener);
		
		this.itemGeneral = new ExpandItem(this.bar, SWT.NONE, 0);
		itemGeneral.setText("General");
		itemGeneral.setHeight(this.generalComposite.computeSize(
				SWT.DEFAULT, SWT.DEFAULT).y);
		itemGeneral.setControl(this.generalComposite);
	}
	
	/*
	 * called by CreatePartControl to create the contents of the second 
	 * expand item (Actions)
	 */
	private void createActionsExpandItem() {
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
		this.actionsTabFolder = new CTabFolder(this.actionsComposite, SWT.FLAT);
		this.actionsTabFolder.setLayoutData(gridData);
		
		motorAxisComposite = new MotorAxisComposite(this, 
				actionsTabFolder, SWT.NONE, this.measuringStation);
		detectorChannelComposite = new DetectorChannelComposite(this, 
				actionsTabFolder, SWT.NONE, this.measuringStation);
		prescanComposite = new PrescanComposite(
				actionsTabFolder, SWT.NONE, this.measuringStationPrescan);
		postscanComposite = new PostscanComposite(
				actionsTabFolder, SWT.NONE, this.measuringStationPostscan);
		positioningComposite = new PositioningComposite(
				actionsTabFolder, SWT.NONE);
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
		
		this.itemActions = new ExpandItem(this.bar, SWT.NONE, 0);
		itemActions.setText("Actions");
		itemActions.setControl(this.actionsComposite);
	}
	
	/*
	 * called by CreatePartControl to create the contents of the third 
	 * expand item (Events)
	 */
	private void createEventsExpandItem() {
		
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
		
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		appendScheduleEventCheckBox = new Button(this.eventsComposite, 
												SWT.CHECK);
		appendScheduleEventCheckBox.setText("Append Schedule Event");
		appendScheduleEventCheckBox.setLayoutData(gridData);
		this.appendScheduleEventCheckBoxSelectionListener = 
				new AppendScheduleEventCheckBoxSelectionListener();
		this.appendScheduleEventCheckBox.addSelectionListener(
				appendScheduleEventCheckBoxSelectionListener);
		
		this.itemEvents = new ExpandItem(this.bar, SWT.NONE, 0);
		itemEvents.setText("Events");
		itemEvents.setHeight(this.eventsComposite.computeSize(SWT.DEFAULT, 
							SWT.DEFAULT).y);
		itemEvents.setControl(this.eventsComposite);
	}
	
	// ***********************************************************************
	// ********************** end of create part control *********************
	// ***********************************************************************
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
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
	
	/**
	 * Sets the currently active scan module.
	 * 
	 * @param currentScanModule the scan module that should be set
	 */
	private void setCurrentScanModule(ScanModule currentScanModule) {
		logger.debug("setCurrentScanModule");

		if (this.currentScanModule != null) {
			this.currentScanModule.removeModelUpdateListener(this);
		}
		
		// if there was already a scan module -> update it
		this.currentScanModule = currentScanModule;
		
		this.measuringStation.setScanModule(this.currentScanModule);
		this.measuringStationPrescan.setScanModule(this.currentScanModule);
		this.measuringStationPostscan.setScanModule(this.currentScanModule);

		if (this.currentScanModule != null) {
			this.currentScanModule.addModelUpdateListener(this);
		}
		
		updateEvent(null);
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
		
		if (itemGeneral.getExpanded()) {
			height -= itemGeneral.getHeight();
		}
		
		int amount = 0;
		if (itemActions.getExpanded()) {
			amount++;
		}
		if (itemEvents.getExpanded()) {
			amount++;
		}
		
		if (amount > 0) {
			height /= amount;
			if(itemActions.getExpanded()) {
				itemActions.setHeight(height < 200 ? 200 : height);
			}
			if(itemEvents.getExpanded()) {
				itemEvents.setHeight(height < 150 ? 150 : height);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		logger.debug("selection changed");
		
		if(selection instanceof IStructuredSelection) {
			if(((IStructuredSelection) selection).size() == 0) {
				return;
			}
			// since at any given time this view can only display options of 
			// one device we take the first element of the selection
			Object o = ((IStructuredSelection) selection).toList().get(0);
			if (o instanceof ScanModuleEditPart) {
				// set new ScanModule
				if(logger.isDebugEnabled()) {
					logger.debug("ScanModule: " + ((ScanModule)(
							(ScanModuleEditPart)o).getModel()).getId() + 
							" selected."); 
				}
				setCurrentScanModule(
						(ScanModule)((ScanModuleEditPart)o).getModel());

			} else if (o instanceof ScanDescriptionEditPart) {
					logger.debug("selection is ScanDescriptionEditPart: " + o);
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
	
	@Override
	public void updateEvent(ModelUpdateEvent modelUpdateEvent) {

		// widgets will be updated according to set / changed model
		// -> temporarily disable listeners to prevent Event Loops
		// (and unnecessary duplicate calls)
		removeListeners();

		if(this.currentScanModule != null) {
			top.setVisible(true);
			this.setPartName(this.currentScanModule.getName() + ":"
							 + this.currentScanModule.getId());	
			
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
			this.confirmTriggerCheckBox.setSelection(
					this.currentScanModule.isTriggerconfirm());
			
			if(actionsTabFolder.getSelection() == null) {
				// TODO: Es gibt noch das Problem, das bei der ersten Auswahl
				// eines ScanModuls nur die MotorAxisView und nicht auch die
				// DetectorChannelView und die PlotWindowView angezeigt wird.
				// Wenn man hier die setSelection(0) wegnimmt, erscheinen in der
				// scanModuleView die TabFolder mit leeren Einträgen.
				actionsTabFolder.setSelection(0);
			}

			// setScanModule muß in der Reihenfolge aufgerufen werden, dass
			// das gerade selekierte Composite als letztes gesetzt wird,
			// damit der SelectionProvider richtig gesetzt ist.
			((PrescanComposite) this.prescanComposite).setScanModule(
					this.currentScanModule);
			((PostscanComposite) this.postscanComposite).setScanModule(
					this.currentScanModule);
			((PositioningComposite) this.positioningComposite).setScanModule(
					this.currentScanModule);
			
			System.out.println("\tScanModule ViewPart: " + 
					Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart());

			// View Part der ScanModuleView: 
			System.out.println("\tthis: " + this);
			System.out.println("\tthis.getSite().getPart(): " + this.getSite().getPart());
			System.out.println("\tthis.getPartName(): " + this.getPartName());

			switch (actionsTabFolder.getSelectionIndex()) {
			case 0:
// TODO 26.1.12: setSelection erzeugt einen Fehler, wenn man vom 1. zum 2.
// ScanModule schaltet in File 3er-sccan.scml
// Direkt nach dem Programmstart wurde nur das SM1 selektiert. Dort wurde nur
// die MotorAxisView korrekt angezeigt.
// Nach der Auswahl von SM2 wurden trotz Fehlermeldung alle 3 Views korrekt 
// angezeigt
// WARNING: Prevented recursive attempt to activate part de.ptb.epics.eve.editor.views.ScanModulView while still in the middle of activating part de.ptb.epics.eve.editor.graphical.GraphicalEditor

// Hinweis: es gibt allerdings noch einen setFocus() Aufruf im 
// DetectorChannelComposite at line 164
// "tableViewer.getControl().setFocus();"
// ohne den setFocus() Aufruf kommt der setSelection Fehler weiterhin, es wird
// dann aber beim Wechsel in das SM2 keine View mehr angezeigt. Alle 3 sind leer.

				
// 2. Hinweis: ohne actionsTabFolder.setSelection(x) Aufrufe kommt beim ersten
// SM weiterhin nur die MotorAxisView. Wenn man dann aber auf das 2. SM klickt
// sind alle 3 views leer. Auch beim hin- und herklicken bleiben alle views leer

// Außerdem gibt es den Effekt, das man auch nicht mehr die einzelnen Tabs
// sehen kann. Es wird nur die View aktualisiert, die zum gerade gedrückten
// Tab gehört. Durch wechsel von den SMs wird dann zwar eine andere View
// gezeigt, aber immer nur eine!
				
				System.out.println("\tSelections für MotorAxes Tab werden gesetzt.");
//				actionsTabFolder.setSelection(1);
				((DetectorChannelComposite) this.detectorChannelComposite).
						setScanModule(this.currentScanModule);
//				actionsTabFolder.setSelection(5);
				((PlotComposite) this.plotComposite).setScanModule(
						this.currentScanModule);
//				actionsTabFolder.setSelection(0);
				((MotorAxisComposite) this.motorAxisComposite).setScanModule(
						this.currentScanModule);
				break;
			case 1:
// 3. Hinweis: Habe jetzt nochmal ein bischen rumgeklickt. Der Fehler tritt nur
// dann auf, wenn eine oder mehrere View von Motor, Detector oder Plot auch
// wirklich offen sind. Ist nichts offen gibt es auch keinen Fehler.
// Bei 3 offenen Views tritt der Fehler manchmal auf
// Bei 2 offenen Views immer
// Bei 1 offenen View selten
				
// 4. Hinweis: Gerade nachdem ich den 3. Hinweis geschrieben habe und dann das
// ganze Verhalten nochmal getestet habe ist mir jetzt klar geworden, dass der
// Fehler sehr zufällig auftritt.
// Ich habe jetzt das Programm einmal gestartet und es gab nie Fehler, egal
// welches Tab angeklickt wurde und welche Views vorher schon sichtbar waren.

// Beim nächsten Start des Programms gab es die Fehler wieder wie unter 3
// beschrieben und zusätzlich das Verhalten, dass man zwischen den Tabs hin- 
// und her geklickt hat und garkeine Views aufgemacht wurden.
		
				System.out.println("\tSelections für DetectorChannels Tab werden gesetzt.");
				actionsTabFolder.setSelection(0);
				((MotorAxisComposite) this.motorAxisComposite).setScanModule(
						this.currentScanModule);
				actionsTabFolder.setSelection(5);
				((PlotComposite) this.plotComposite).setScanModule(
						this.currentScanModule);
				actionsTabFolder.setSelection(1);
				((DetectorChannelComposite) this.detectorChannelComposite).
						setScanModule(this.currentScanModule);
				break;
			case 5:
				System.out.println("\tSelections für Plot Tab werden gesetzt.");
			default:
				((MotorAxisComposite) this.motorAxisComposite).setScanModule(
						this.currentScanModule);
				((DetectorChannelComposite) this.detectorChannelComposite).
						setScanModule(this.currentScanModule);
				((PlotComposite) this.plotComposite).setScanModule(
						this.currentScanModule);
				break;
			}
			
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
			
			itemActions.setExpanded(true);
		} else { // currentScanModule == null
			// no scan module selected -> reset contents
			
			this.setPartName("No Scan Module selected");
			
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
			
			selectionProviderWrapper.setSelectionProvider(null);

// TODO 26.1.12: setVisible erzeugt folgenden Fehler, wenn man zwischen den scml-Files
// hin und her schaltet und die MotorAxis, DetectoChannel und PlotWindow View
// schon gefüllt war. Ansonsten tritt der Fehler nicht auf.
			top.setVisible(false);
		}
		addListeners();
	}

	/**
	 * {@link org.eclipse.swt.events.SelectionListener} of 
	 * <code>confirmTriggerCheckBox</code>.
	 */
	private class ConfirmTriggerCheckBoxSelectionListener implements 
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

			System.out.println("\tConfirm Trigger wurde gedrückt");
			System.out.println("\tWelche ViewPart ist Aktiv? " +
			Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart());
			
			System.out.println("\tWelche ViewPart ist Aktiv? " +
			 PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().getActivePart());
			System.out.println("\tWelche ViewPart ist Aktiv? " +
					 PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage());

			
			currentScanModule.setTriggerconfirm(
					confirmTriggerCheckBox.getSelection());
		}
	}
	
	/**
	 * {@link org.eclipse.swt.events.SelectionListener} of
	 * <code>eventsTabFolder</code>.
	 */
	private class EventsTabFolderSelectionListener implements 
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
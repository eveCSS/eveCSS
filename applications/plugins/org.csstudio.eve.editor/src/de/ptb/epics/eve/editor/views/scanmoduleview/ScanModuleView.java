package de.ptb.epics.eve.editor.views.scanmoduleview;

import org.apache.log4j.Logger;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import de.ptb.epics.eve.data.measuringstation.Event;
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
import de.ptb.epics.eve.editor.views.eventcomposite.EventComposite;
import de.ptb.epics.eve.editor.views.scanmoduleview.detectorchannelcomposite.DetectorChannelComposite;
import de.ptb.epics.eve.editor.views.scanmoduleview.motoraxiscomposite.MotorAxisComposite;
import de.ptb.epics.eve.editor.views.scanmoduleview.plotcomposite.PlotComposite;
import de.ptb.epics.eve.editor.views.scanmoduleview.positioningcomposite.PositioningComposite;
import de.ptb.epics.eve.editor.views.scanmoduleview.postscancomposite.PostscanComposite;
import de.ptb.epics.eve.editor.views.scanmoduleview.prescancomposite.PrescanComposite;

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

	private Composite top;

	private ExpandBar bar;

	private Composite generalComposite;

	private Composite actionsComposite;

	private Composite eventsComposite;

	private Label triggerDelayLabel;
	private Text triggerDelayText;
	private Label triggerDelayErrorLabel;
	private Label triggerDelayUnitLabel;
	private TriggerDelayTextModifiedListener triggerDelayTextModifiedListener;
	
	private Label settleTimeLabel;
	private Text settleTimeText;
	private Label settleTimeErrorLabel;
	private Label settleTimeUnitLabel;
	private SettleTimeTextModifiedListener settleTimeTextModifiedListener;
	
	private Button confirmTriggerCheckBox;
	private ConfirmTriggerCheckBoxSelectionListener 
			confirmTriggerCheckBoxSelectionListener;
	
	public CTabFolder eventsTabFolder;
	private EventsTabFolderSelectionListener eventsTabFolderSelectionListener;
	
	private EventComposite pauseEventComposite;
	private EventComposite redoEventComposite;
	private EventComposite breakEventComposite;
	private EventComposite triggerEventComposite;
	
	private CTabFolder actionsTabFolder;
	
	private Composite motorAxisComposite;
	private Composite detectorChannelComposite;
	private Composite prescanComposite;
	private Composite postscanComposite;
	private Composite positioningComposite;
	private Composite plotComposite;
	
	private Button appendScheduleEventCheckBox;
	private AppendScheduleEventCheckBoxSelectionListener
			appendScheduleEventCheckBoxSelectionListener;
	
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
	
	// the selection service only accepts one selection provider per view,
	// since we have multiple tabs with tables capable of providing selections, 
	// a wrapper handles them and registers the active one with the global 
	// selection service
	protected SelectionProviderWrapper selectionProviderWrapper;
	
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
		
		this.top = new Composite(parent, SWT.NONE);
		this.top.setLayout(new FillLayout());
		
		this.bar = new ExpandBar(this.top, SWT.V_SCROLL);
		
		// General Section
		createGeneralExpandItem();
		// Actions Section
		createActionsExpandItem();
		// Event Section
		createEventsExpandItem();
		
		restoreState();
		
		top.setVisible(false);
		
		// listen to selection changes (if a scan module is selected, its 
		// attributes are made available for editing)
		getSite().getWorkbenchWindow().getSelectionService().
				addSelectionListener(this);
		
		selectionProviderWrapper = new SelectionProviderWrapper();
		getSite().setSelectionProvider(selectionProviderWrapper);
	} // end of: createPartControl()
	
	/*
	 * 
	 */
	private void restoreState() {
		if (memento == null) {
			return;
		}
		boolean general = memento.getBoolean("expandGeneral") != null 
						? memento.getBoolean("expandGeneral")
						: true;
		this.itemGeneral.setExpanded(general);
		boolean actions = memento.getBoolean("expandActions") != null 
						? memento.getBoolean("expandActions")
						: true;
		this.itemActions.setExpanded(actions);
		boolean events = memento.getBoolean("expandEvents") != null 
						? memento.getBoolean("expandEvents")
						: true;
		this.itemEvents.setExpanded(events);
	}
	
	/*
	 * called by CreatePartControl to create the contents of the first 
	 * expand item (General)
	 */
	private void createGeneralExpandItem() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		this.generalComposite = new Composite(this.bar, SWT.NONE);
		this.generalComposite.setLayout(gridLayout);
		
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
		this.actionsComposite = new Composite(this.bar, SWT.NONE);
		this.actionsComposite.setLayout(new GridLayout());
		
		this.actionsTabFolder = new CTabFolder(this.actionsComposite, SWT.FLAT);
		this.actionsTabFolder.setSimple(false);
		this.actionsTabFolder.setBorderVisible(true);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
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
		
		this.itemActions = new ExpandItem(this.bar, SWT.NONE, 0);
		itemActions.setText("Actions");
		itemActions.setHeight(this.actionsComposite.computeSize(
				SWT.DEFAULT, SWT.DEFAULT).y);
		itemActions.setControl(this.actionsComposite);
	}
	
	/*
	 * called by CreatePartControl to create the contents of the third 
	 * expand item (Events)
	 */
	private void createEventsExpandItem() {
		this.eventsComposite = new Composite(this.bar, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		this.eventsComposite.setLayout(gridLayout);
		
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
	
	/**
	 * Sets the currently active scan module.
	 * 
	 * @param currentScanModule the scan module that should be set
	 */
	private void setCurrentScanModule(ScanModule currentScanModule) {
		logger.debug("setCurrentScanModule");
		
		// save the currently active part
		IWorkbenchPart activePart = getSite().getPage().getActivePart();
		
		if (this.currentScanModule != null) {
			this.currentScanModule.removeModelUpdateListener(this);
		}
		
		// if there was already a scan module -> update it
		this.currentScanModule = currentScanModule;
		
		if (this.currentScanModule != null) {
			this.currentScanModule.addModelUpdateListener(this);
			
			// activate the scan module view (to assure propagation of selections)
			this.getSite().getPage().activate(this);
		}
		updateEvent(null);
		
		if (this.currentScanModule != null) {
			// switch back to previously active part
			this.getSite().getPage().activate(activePart);
		}
	}
	
	/*
	 * 
	 */
	private void checkForErrors() {
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
				// copy into final var so it can be used in runnable
				final ScanModuleEditPart smep = (ScanModuleEditPart)o;
				if (this.currentScanModule != null) {
					ScanModule newScanModule = (ScanModule)
							((ScanModuleEditPart)o).getModel();
					if(!this.currentScanModule.equals(newScanModule)) {
						// to get rid of preventing recursive attempt to 
						// activate part, use asynchronous execution
						Display.getCurrent().asyncExec(new Runnable() {
							@Override public void run() {
								setCurrentScanModule((ScanModule)
										((ScanModuleEditPart)smep).getModel());
							}
						});
					}
				} else {
					Display.getCurrent().asyncExec(new Runnable() {
						@Override public void run() {
							setCurrentScanModule((ScanModule)
									((ScanModuleEditPart)smep).getModel());
						}
					});
				}
			} else if (o instanceof ScanDescriptionEditPart) {
				logger.debug("selection is ScanDescriptionEditPart: " + o);
				if(this.currentScanModule !=  null) {
					Display.getCurrent().asyncExec(new Runnable() {
						@Override public void run() {} {
							setCurrentScanModule(null);
						}
					});
				}
			} else {
				logger.debug("selection other than ScanModule -> ignore: " + o);
			}
		}
	}
	
	/*
	 * used by updateEvent() to re-enable listeners
	 */
	private void addListeners() {
		this.triggerDelayText.addModifyListener(
				triggerDelayTextModifiedListener);
		this.settleTimeText.addModifyListener(settleTimeTextModifiedListener);
		this.confirmTriggerCheckBox.addSelectionListener(
				confirmTriggerCheckBoxSelectionListener);
		this.eventsTabFolder.addSelectionListener(
				eventsTabFolderSelectionListener);
		this.appendScheduleEventCheckBox.addSelectionListener(
				appendScheduleEventCheckBoxSelectionListener);
	}
	
	/*
	 * used by updateEvent() to temporarily disable listeners (preventing 
	 * event loops)
	 */
	private void removeListeners() {
		this.triggerDelayText.removeModifyListener(
				triggerDelayTextModifiedListener);
		this.settleTimeText.removeModifyListener(settleTimeTextModifiedListener);
		this.confirmTriggerCheckBox.removeSelectionListener(
				confirmTriggerCheckBoxSelectionListener);
		this.eventsTabFolder.removeSelectionListener(
				eventsTabFolderSelectionListener);
		this.appendScheduleEventCheckBox.removeSelectionListener(
				appendScheduleEventCheckBoxSelectionListener);
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
			
			// select the first tab if none is selected
			if(actionsTabFolder.getSelection() == null) {
				actionsTabFolder.setSelection(0);
			}
			
			// TODO
			// setScanModule muß in der Reihenfolge aufgerufen werden, dass
			// das gerade selekierte Composite als letztes gesetzt wird,
			// damit der SelectionProvider richtig gesetzt ist.
			((PrescanComposite) this.prescanComposite).setScanModule(
					this.currentScanModule);
			((PostscanComposite) this.postscanComposite).setScanModule(
					this.currentScanModule);
			((PositioningComposite) this.positioningComposite).setScanModule(
					this.currentScanModule);
			
			switch (actionsTabFolder.getSelectionIndex()) {
			case 0:
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
				//actionsTabFolder.setSelection(0);
				((MotorAxisComposite) this.motorAxisComposite).setScanModule(
						this.currentScanModule);
				//actionsTabFolder.setSelection(5);
				((PlotComposite) this.plotComposite).setScanModule(
						this.currentScanModule);
				//actionsTabFolder.setSelection(1);
				((DetectorChannelComposite) this.detectorChannelComposite).
						setScanModule(this.currentScanModule);
				break;
			case 5:
			default:
				((MotorAxisComposite) this.motorAxisComposite).setScanModule(
						this.currentScanModule);
				((DetectorChannelComposite) this.detectorChannelComposite).
						setScanModule(this.currentScanModule);
				((PlotComposite) this.plotComposite).setScanModule(
						this.currentScanModule);
				break;
			}
			
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
			
			// TODO CheckBox for ScheduleIncident Start or End
			Event testEvent = new Event(currentScanModule.getChain().getId(), 
										currentScanModule.getId(), 
										Event.ScheduleIncident.END);
			this.appendScheduleEventCheckBox.setSelection(
					this.currentScanModule.getChain().getScanDescription().
					getEventById(testEvent.getID()) != null);
			
			checkForErrors();
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
			
			top.setVisible(false);
		}
		addListeners();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveState(IMemento memento) {
		memento.putBoolean("expandGeneral", itemGeneral.getExpanded());
		memento.putBoolean("expandActions", itemActions.getExpanded());
		memento.putBoolean("expandEvents", itemEvents.getExpanded());
	}

	// ************************************************************************
	// ******************** Listener ******************************************
	// ************************************************************************
	
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
			switch(eventsTabFolder.getSelectionIndex()) {
				case 0: selectionProviderWrapper.setSelectionProvider(
							pauseEventComposite.getTableViewer());
						break;
				case 1: selectionProviderWrapper.setSelectionProvider(
							redoEventComposite.getTableViewer());
						break;
				case 2: selectionProviderWrapper.setSelectionProvider(
							breakEventComposite.getTableViewer());
						break;
				case 3: selectionProviderWrapper.setSelectionProvider(
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
}
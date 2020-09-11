package de.ptb.epics.eve.editor.views.scanmoduleview.classiccomposite;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.EventImpacts;
import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;
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
import de.ptb.epics.eve.editor.views.eventcomposite.EventComposite;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleSelectionProvider;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleViewComposite;
import de.ptb.epics.eve.editor.views.scanmoduleview.classiccomposite.detectorchannelcomposite.DetectorChannelComposite;
import de.ptb.epics.eve.editor.views.scanmoduleview.classiccomposite.motoraxiscomposite.MotorAxisComposite;
import de.ptb.epics.eve.editor.views.scanmoduleview.classiccomposite.plotcomposite.PlotComposite;
import de.ptb.epics.eve.editor.views.scanmoduleview.classiccomposite.positioningcomposite.PositioningComposite;
import de.ptb.epics.eve.editor.views.scanmoduleview.classiccomposite.postscancomposite.PostscanComposite;
import de.ptb.epics.eve.editor.views.scanmoduleview.classiccomposite.prescancomposite.PrescanComposite;
import de.ptb.epics.eve.util.ui.swt.TextSelectAllFocusListener;
import de.ptb.epics.eve.util.ui.swt.TextSelectAllMouseListener;

/**
 * @author Marcus Michalsky
 * @since 1.31
 */
public class ClassicComposite extends ScanModuleViewComposite implements IModelUpdateListener {
	private static final Logger LOGGER = Logger.getLogger(
			ClassicComposite.class.getName());
	
	// TODO remove general composite
	
	private static final String MEMENTO_ACTIONS_COMPOSITE_MAXIMIZED = 
			"actionsCompositeMaximized";
	private static final String MEMENTO_EVENTS_COMPOSITE_MAXIMIZED = 
			"eventsCompositeMaximized";
	private static final String MEMENTO_ACTIONS_COMPOSITE_WEIGHT = 
			"actionsCompositeWeight";
	private static final String MEMENTO_EVENTS_COMPOSITE_WEIGHT =
			"eventsCompositeWeight";
	/*private static final String MEMENTO_AXES_SORT_STATE = 
			"AxesSortState";
	private static final String MEMENTO_CHANNEL_SORT_STATE = 
			"ChannelSortState";*/
	private static final String MEMENTO_ACTIONS_TAB_FOLDER_SELECTION_INDEX = 
			"actionsTabFolderSelectionIndex";
	private static final String MEMENTO_EVENTS_TAB_FOLDER_SELECTION_INDEX = 
			"eventsTabFolderSelectionIndex";
	
	
	private ScanModuleView parentView;
	private ScanModule currentScanModule;
	
	ScrolledComposite sc;
	Composite top;
	
	// general composite
	//private Text valueCountText;
	//private Text triggerDelayText;
	//private Text settleTimeText;
	//private Button triggerConfirmAxisCheckBox;
	//private Button triggerConfirmChannelCheckBox;
	//private Binding valueCountBinding;
	//private Binding triggerDelayBinding;
	//private Binding settleTimeBinding;
	
	private SashForm actionEventSashForm;
	
	// actions composite
	private Composite actionsComposite;
	private Label actionMaxIcon;
	private boolean actionsCompositeMaximized;
	private CTabFolder actionsTabFolder;
	//private CTabItem motorAxisTab;
	//private CTabItem detectorChannelTab;
	private CTabItem prescanTab;
	private CTabItem postscanTab;
	//private CTabItem positioningTab;
	//private CTabItem plotTab;
	//private ActionComposite motorAxisComposite;
	//private ActionComposite detectorChannelComposite;
	private ActionComposite prescanComposite;
	private ActionComposite postscanComposite;
	//private ActionComposite positioningComposite;
	//private ActionComposite plotComposite;
	
	// events composite
	private Composite eventsComposite;
	private Label eventMaxIcon;
	private boolean eventsCompositeMaximized;
	public CTabFolder eventsTabFolder;
	private CTabItem pauseEventsTabItem;
	private CTabItem redoEventsTabItem;
	private CTabItem breakEventsTabItem;
	private CTabItem triggerEventsTabItem;
	private EventComposite pauseEventComposite;
	private EventComposite redoEventComposite;
	private EventComposite breakEventComposite;
	private EventComposite triggerEventComposite;
	
	private Image restoreIcon;
	private Image maximizeIcon;
	
	public ClassicComposite(ScanModuleView parentView, Composite parent, int style) {
		super(parentView, parent, style);
		
		this.parentView = parentView;
		
		this.restoreIcon = Activator.getDefault().getImageRegistry()
				.get("RESTORE");
		this.maximizeIcon = Activator.getDefault().getImageRegistry()
				.get("MAXIMIZE");
		
		this.setLayout(new FillLayout());
		this.sc = new ScrolledComposite(this, SWT.V_SCROLL);
		
		this.top = new Composite(sc, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		this.top.setLayout(gridLayout);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setContent(top);
		
		// this.createGeneralComposite(top);
		
		this.actionEventSashForm = new SashForm(top, SWT.VERTICAL);
		this.actionEventSashForm.SASH_WIDTH = 4;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		this.actionEventSashForm.setLayoutData(gridData);
		this.createActionsComposite(this.actionEventSashForm);
		this.createEventsComposite(this.actionEventSashForm);
		sc.setMinSize(SWT.DEFAULT, SWT.DEFAULT);
		
		//this.bindValues();
	}

	/*private void createGeneralComposite(Composite parent) {
		Composite generalComposite = new Composite(parent, SWT.BORDER);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		generalComposite.setLayout(gridLayout);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		generalComposite.setLayoutData(gridData);
		
		Label valueCountLabel = new Label(generalComposite, SWT.NONE);
		valueCountLabel.setText("No of Measurements:");
		valueCountLabel.setToolTipText(
				"Number of Measurements taken for each motor position");
		this.valueCountText = new Text(generalComposite, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.horizontalIndent = 7;
		gridData.grabExcessHorizontalSpace = true;
		this.valueCountText.setLayoutData(gridData);
		this.valueCountText.addFocusListener(
				new TextSelectAllFocusListener(this.valueCountText));
		this.valueCountText.addMouseListener(
				new TextSelectAllMouseListener(this.valueCountText));
		this.valueCountText.addFocusListener(new TextFocusListener(
				this.valueCountText));
		
		Label triggerDelayLabel = new Label(generalComposite, SWT.NONE);
		triggerDelayLabel.setText("Trigger Delay (in s):");
		triggerDelayLabel.setToolTipText("delay in s before detectors are triggered");
		this.triggerDelayText = new Text(generalComposite, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.horizontalIndent = 7;
		gridData.grabExcessHorizontalSpace = true;
		this.triggerDelayText.setLayoutData(gridData);
		this.triggerDelayText.addFocusListener(new TextSelectAllFocusListener(
				this.triggerDelayText));
		this.triggerDelayText.addMouseListener(new TextSelectAllMouseListener(
				this.triggerDelayText));
		this.triggerDelayText.addFocusListener(new TextFocusListener(
				this.triggerDelayText));
		
		Label settleTimeLabel = new Label(generalComposite, SWT.NONE);
		settleTimeLabel.setText("Settle Time (in s):");
		settleTimeLabel.setToolTipText(
				"Delay time after first positioning in the scan module");
		this.settleTimeText = new Text(generalComposite, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.horizontalIndent = 7;
		gridData.grabExcessHorizontalSpace = true;
		this.settleTimeText.setLayoutData(gridData);
		this.settleTimeText.addFocusListener(new TextSelectAllFocusListener(
				this.settleTimeText));
		this.settleTimeText.addMouseListener(new TextSelectAllMouseListener(
				this.settleTimeText));
		this.settleTimeText.addFocusListener(new TextFocusListener(
				this.settleTimeText));
		
		Label triggerLabel = new Label(generalComposite, SWT.NONE);
		triggerLabel.setText("Manual Trigger:");
		triggerLabel.setLayoutData(new GridData());
		Composite triggerCheckBoxes = new Composite(generalComposite,
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
	}*/
	
	private void createActionsComposite(Composite parent) {
		this.actionsComposite = new Composite(parent, SWT.BORDER);
		this.actionsComposite.setLayout(new GridLayout(2, false));

		this.actionMaxIcon = new Label(this.actionsComposite, SWT.NONE);
		this.actionMaxIcon.setImage(maximizeIcon);
		this.actionMaxIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (actionsCompositeMaximized) {
					actionMaxIcon.setImage(maximizeIcon);
					actionMaxIcon.getParent().layout();
					actionEventSashForm.setMaximizedControl(null);
					actionsCompositeMaximized = false;
				} else {
					actionMaxIcon.setImage(restoreIcon);
					actionEventSashForm.setMaximizedControl(actionsComposite);
					actionsCompositeMaximized = true;
				}
			}
		});
		

		Label actionsLabel = new Label(this.actionsComposite, SWT.NONE);
		actionsLabel.setText("Actions:");

		this.actionsTabFolder = new CTabFolder(this.actionsComposite, SWT.FLAT);
		this.actionsTabFolder.setSimple(true);
		this.actionsTabFolder.setBorderVisible(true);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 2;
		actionsTabFolder.setLayoutData(gridData);
		
		/*this.motorAxisComposite = new MotorAxisComposite(
				this.parentView, actionsTabFolder, SWT.NONE);
		this.detectorChannelComposite = new DetectorChannelComposite(
				this.parentView, actionsTabFolder, SWT.NONE);*/
		this.prescanComposite = new PrescanComposite(
				this.parentView, actionsTabFolder, SWT.NONE);
		this.postscanComposite = new PostscanComposite(
				this.parentView, actionsTabFolder, SWT.NONE);
		/*this.positioningComposite = new PositioningComposite(
				this.parentView, actionsTabFolder, SWT.NONE);*/
		/*this.plotComposite = new PlotComposite(
				this.parentView, actionsTabFolder, SWT.NONE);*/
		
		/*this.motorAxisTab = new CTabItem(actionsTabFolder, SWT.FLAT);
		this.motorAxisTab.setText(" Motor Axes ");
		this.motorAxisTab.setToolTipText(
				"Select motor axes to be used in this scan module");
		this.motorAxisTab.setControl(motorAxisComposite);*/
		
		/*this.detectorChannelTab = new CTabItem(this.actionsTabFolder, SWT.FLAT);
		this.detectorChannelTab.setText(" Detector Channels ");
		this.detectorChannelTab
				.setToolTipText("Select detector channels to be used in this scan module");
		this.detectorChannelTab.setControl(this.detectorChannelComposite);*/

		this.prescanTab = new CTabItem(this.actionsTabFolder, SWT.FLAT);
		this.prescanTab.setText(" Prescan ");
		this.prescanTab
				.setToolTipText("Action to do before scan module is started");
		this.prescanTab.setControl(this.prescanComposite);

		this.postscanTab = new CTabItem(this.actionsTabFolder, SWT.FLAT);
		this.postscanTab.setText(" Postscan ");
		this.postscanTab.setToolTipText("Action to do if scan module is done");
		this.postscanTab.setControl(this.postscanComposite);

		/*this.positioningTab = new CTabItem(this.actionsTabFolder, SWT.FLAT);
		this.positioningTab.setText(" Positioning ");
		this.positioningTab
				.setToolTipText("Move motor to calculated position after scan module is done");
		this.positioningTab.setControl(this.positioningComposite);*/

		/*this.plotTab = new CTabItem(this.actionsTabFolder, SWT.FLAT);
		this.plotTab.setText(" Plot ");
		this.plotTab.setToolTipText("Plot settings for this scan module");
		this.plotTab.setControl(this.plotComposite);*/
		
		this.actionsCompositeMaximized = false;
	}
	
	private void createEventsComposite(Composite parent) {
		this.eventsComposite = new Composite(parent, SWT.BORDER);
		this.eventsComposite.setLayout(new GridLayout(2, false));

		this.eventMaxIcon = new Label(eventsComposite, SWT.NONE);
		this.eventMaxIcon.setImage(maximizeIcon);
		this.eventMaxIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (eventsCompositeMaximized) {
					eventMaxIcon.setImage(maximizeIcon);
					eventMaxIcon.getParent().layout();
					actionEventSashForm.setMaximizedControl(null);
					eventsCompositeMaximized = false;
				} else {
					eventMaxIcon.setImage(restoreIcon);
					actionEventSashForm.setMaximizedControl(eventsComposite);
					eventsCompositeMaximized = true;
				}
			}
		});
		
		Label eventLabel = new Label(eventsComposite, SWT.NONE);
		eventLabel.setText("Events:");

		this.eventsTabFolder = new CTabFolder(this.eventsComposite, SWT.NONE);
		this.eventsTabFolder.setSimple(true);
		this.eventsTabFolder.setBorderVisible(true);
		eventsTabFolder.addSelectionListener(
				new EventsTabFolderSelectionListener());
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 2;
		this.eventsTabFolder.setLayoutData(gridData);
		
		pauseEventComposite = new EventComposite(eventsTabFolder, SWT.NONE,
				ControlEventTypes.PAUSE_EVENT, this.parentView);
		redoEventComposite = new EventComposite(eventsTabFolder, SWT.NONE,
				ControlEventTypes.CONTROL_EVENT, this.parentView);
		breakEventComposite = new EventComposite(eventsTabFolder, SWT.NONE,
				ControlEventTypes.CONTROL_EVENT, this.parentView);
		triggerEventComposite = new EventComposite(eventsTabFolder, SWT.NONE,
				ControlEventTypes.CONTROL_EVENT, this.parentView);

		this.pauseEventsTabItem = new CTabItem(eventsTabFolder, SWT.NONE);
		this.pauseEventsTabItem.setText(" Pause ");
		this.pauseEventsTabItem
				.setToolTipText("Configure event to pause and resume this scan module");
		this.pauseEventsTabItem.setControl(pauseEventComposite);
		this.redoEventsTabItem = new CTabItem(eventsTabFolder, SWT.NONE);
		this.redoEventsTabItem.setText(" Redo ");
		this.redoEventsTabItem
				.setToolTipText("Repeat the last acquisition, if redo event occurs");
		this.redoEventsTabItem.setControl(redoEventComposite);
		this.breakEventsTabItem = new CTabItem(eventsTabFolder, SWT.NONE);
		this.breakEventsTabItem.setText(" Skip ");
		this.breakEventsTabItem
				.setToolTipText("Finish this scan module and continue with next");
		this.breakEventsTabItem.setControl(breakEventComposite);
		this.triggerEventsTabItem = new CTabItem(eventsTabFolder, SWT.NONE);
		this.triggerEventsTabItem.setText(" Trigger ");
		this.triggerEventsTabItem
				.setToolTipText("Wait for trigger event before moving to next position");
		this.triggerEventsTabItem.setControl(triggerEventComposite);
		
		this.eventsCompositeMaximized = false;
	}
	
	private void bindValues() {
		/*DataBindingContext context = new DataBindingContext();
		ISelectionProvider selectionProvider = new ScanModuleSelectionProvider(
				ScanModuleTypes.CLASSIC);
		IObservableValue selectionObservable = ViewersObservables
				.observeSingleSelection(selectionProvider);*/
		
		/*IObservableValue valueCountTargetObservable = SWTObservables
				.observeText(this.valueCountText, SWT.Modify);
		IObservableValue valueCountModelObservable = BeansObservables.observeDetailValue(
				selectionObservable, ScanModule.class,
				ScanModule.VALUE_COUNT_PROP, Integer.class);
		UpdateValueStrategy valueCountTargetToModelStrategy = 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE);
		valueCountTargetToModelStrategy
				.setAfterGetValidator(new ValueCountValidator());
		valueCountTargetToModelStrategy.setConverter(new ValueCountConverter());
		this.valueCountBinding = context.bindValue(valueCountTargetObservable,
				valueCountModelObservable, valueCountTargetToModelStrategy,
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));
		ControlDecorationSupport.create(this.valueCountBinding, SWT.LEFT);
		
		IObservableValue triggerDelayTargetObservable = SWTObservables.observeText(
				this.triggerDelayText, SWT.Modify);
		IObservableValue triggerDelayModelObservable = BeansObservables.observeDetailValue(
				selectionObservable, ScanModule.class, 
				ScanModule.TRIGGER_DELAY_PROP, Double.class);
		UpdateValueStrategy triggerDelayTargetToModelStrategy = 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE);
		triggerDelayTargetToModelStrategy
				.setAfterGetValidator(new TriggerDelaySettleTimeValidator("Settle Time"));
		triggerDelayTargetToModelStrategy
				.setConverter(new TriggerDelaySettleTimeConverter());
		UpdateValueStrategy triggerDelayModelToTargetStrategy = 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE);
		triggerDelayModelToTargetStrategy
				.setConverter(new ModelToTargetConverter());
		this.triggerDelayBinding = context.bindValue(
				triggerDelayTargetObservable, triggerDelayModelObservable,
				triggerDelayTargetToModelStrategy,
				triggerDelayModelToTargetStrategy);
		ControlDecorationSupport.create(this.triggerDelayBinding, SWT.LEFT);
		
		IObservableValue settleTimeTargetObservable = SWTObservables.observeText(
				this.settleTimeText, SWT.Modify);
		IObservableValue settleTimeModelObservable = BeansObservables.observeDetailValue(
				selectionObservable, ScanModule.class,
				ScanModule.SETTLE_TIME_PROP, Double.class);
		UpdateValueStrategy settleTimeTargetToModelStrategy = 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE);
		UpdateValueStrategy settleTimeModelToTargetStrategy = 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE);
		settleTimeModelToTargetStrategy
				.setConverter(new ModelToTargetConverter());
		settleTimeTargetToModelStrategy
				.setAfterGetValidator(new TriggerDelaySettleTimeValidator(
						"Settle Time"));
		settleTimeTargetToModelStrategy
				.setConverter(new TriggerDelaySettleTimeConverter());
		this.settleTimeBinding = context.bindValue(settleTimeTargetObservable,
				settleTimeModelObservable, settleTimeTargetToModelStrategy,
				settleTimeModelToTargetStrategy);
		ControlDecorationSupport.create(this.settleTimeBinding, SWT.LEFT);
		
		IObservableValue axisTriggerTargetObservable = SWTObservables
				.observeSelection(this.triggerConfirmAxisCheckBox);
		IObservableValue axisTriggerModelObservable = BeansObservables.observeDetailValue(
				selectionObservable, ScanModule.class,
				ScanModule.TRIGGER_CONFIRM_AXIS_PROP, Boolean.class);
		Binding axisTriggerBinding = context.bindValue(
				axisTriggerTargetObservable, axisTriggerModelObservable,
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE),
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));
		axisTriggerBinding.getClass();
		
		IObservableValue channelTriggerTargetObservable = SWTObservables
				.observeSelection(this.triggerConfirmChannelCheckBox);
		IObservableValue channelTriggerModelObservable = BeansObservables
				.observeDetailValue(selectionObservable, ScanModule.class,
						ScanModule.TRIGGER_CONFIRM_CHANNEL_PROP, Boolean.class);
		Binding channelTriggerBinding = context.bindValue(
				channelTriggerTargetObservable, channelTriggerModelObservable,
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE),
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));
		channelTriggerBinding.getClass();*/
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ScanModuleTypes getType() {
		return ScanModuleTypes.CLASSIC;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setScanModule(ScanModule scanModule) {
		LOGGER.debug("ClassicComposite#setScanModule: " + scanModule);
		// if there was a scan module shown before, stop listening to changes
		if (this.currentScanModule != null) {
			this.currentScanModule.removeModelUpdateListener(this);
		}
		
		// set the new scan module as the current one
		this.currentScanModule = scanModule;
		
		// tell the action composites about the change
		//this.motorAxisComposite.setScanModule(scanModule);
		//this.detectorChannelComposite.setScanModule(scanModule);
		this.prescanComposite.setScanModule(scanModule);
		this.postscanComposite.setScanModule(scanModule);
		//this.positioningComposite.setScanModule(scanModule);
		//this.plotComposite.setScanModule(scanModule);
		
		if (this.currentScanModule != null) {
			// new scan module
			this.currentScanModule.addModelUpdateListener(this);
			
			if (this.eventsTabFolder.getSelection() == null) {
				this.eventsTabFolder.setSelection(0);
			}
			int selectionIndex = this.actionsTabFolder.getSelectionIndex();
			if (selectionIndex == -1) {
				this.actionsTabFolder.setSelection(0);
			} else {
				this.actionsTabFolder.setSelection(selectionIndex);
			}
			sc.setMinSize(this.top.getBounds().width + 
					this.top.getBounds().x, 
					this.top.getBounds().height + 
					this.top.getBounds().y);
		} else {
			// no scan module selected -> reset contents
			this.parentView.setSelectionProvider(null);
		}
		updateEvent(null);
		this.layout();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
		if (this.currentScanModule != null) {
			this.triggerEventComposite.setEvents(this.currentScanModule,
					EventImpacts.TRIGGER);
			this.breakEventComposite.setEvents(this.currentScanModule,
					EventImpacts.BREAK);
			this.redoEventComposite.setEvents(this.currentScanModule,
					EventImpacts.REDO);
			this.pauseEventComposite.setEvents(this.currentScanModule,
					EventImpacts.PAUSE);
			
			checkForErrors();
		} else {
			triggerEventComposite.setEvents(this.currentScanModule, null);
			breakEventComposite.setEvents(this.currentScanModule, null);
			redoEventComposite.setEvents(this.currentScanModule, null);
			pauseEventComposite.setEvents(this.currentScanModule, null);
		}
	}
	
	private void checkForErrors() {
		// check errors in Actions Tab
		//this.motorAxisTab.setImage(null);
		//this.detectorChannelTab.setImage(null);
		this.prescanTab.setImage(null);
		this.postscanTab.setImage(null);
		//this.positioningTab.setImage(null);
		//this.plotTab.setImage(null);
		//boolean motorAxisErrors = false;
		//boolean detectorChannelErrors = false;
		boolean prescanErrors = false;
		boolean postscanErrors = false;
		boolean positioningErrors = false;
		boolean plotWindowErrors = false;

		for (IModelError error : this.currentScanModule.getModelErrors()) {
			/*if (error instanceof AxisError) {
				motorAxisErrors = true;
			} else if (error instanceof ChannelError) {
				detectorChannelErrors = true;
			} else  */ if (error instanceof PrescanError) {
				prescanErrors = true;
			} else if (error instanceof PostscanError) {
				postscanErrors = true;
			} else if (error instanceof PositioningError) {
				positioningErrors = true;
			} else if (error instanceof PlotWindowError) {
				plotWindowErrors = true;
			}
		}

		/*if (motorAxisErrors) {
			this.motorAxisTab.setImage(PlatformUI.getWorkbench()
					.getSharedImages()
					.getImage(ISharedImages.IMG_OBJS_ERROR_TSK));
		}
		if (detectorChannelErrors) {
			this.detectorChannelTab.setImage(PlatformUI.getWorkbench()
					.getSharedImages()
					.getImage(ISharedImages.IMG_OBJS_ERROR_TSK));
		}*/
		if (prescanErrors) {
			this.prescanTab.setImage(PlatformUI.getWorkbench()
					.getSharedImages()
					.getImage(ISharedImages.IMG_OBJS_ERROR_TSK));
		}
		if (postscanErrors) {
			this.postscanTab.setImage(PlatformUI.getWorkbench()
					.getSharedImages()
					.getImage(ISharedImages.IMG_OBJS_ERROR_TSK));
		}
		/*if (positioningErrors) {
			this.positioningTab.setImage(PlatformUI.getWorkbench()
					.getSharedImages()
					.getImage(ISharedImages.IMG_OBJS_ERROR_TSK));
		}*/
		/*if (plotWindowErrors) {
			this.plotTab.setImage(PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJS_ERROR_TSK));
		}*/

		// check errors in Events Tab
		this.pauseEventsTabItem.setImage(null);
		this.redoEventsTabItem.setImage(null);
		this.breakEventsTabItem.setImage(null);
		this.triggerEventsTabItem.setImage(null);

		for (ControlEvent event : this.currentScanModule.getPauseEvents()) {
			if (!event.getModelErrors().isEmpty()) {
				this.pauseEventsTabItem.setImage(PlatformUI.getWorkbench()
						.getSharedImages()
						.getImage(ISharedImages.IMG_OBJS_ERROR_TSK));
			}
		}

		for (ControlEvent event : this.currentScanModule.getRedoEvents()) {
			if (!event.getModelErrors().isEmpty()) {
				this.redoEventsTabItem.setImage(PlatformUI.getWorkbench()
						.getSharedImages()
						.getImage(ISharedImages.IMG_OBJS_ERROR_TSK));
			}
		}

		for (ControlEvent event : this.currentScanModule.getBreakEvents()) {
			if (!event.getModelErrors().isEmpty()) {
				this.breakEventsTabItem.setImage(PlatformUI.getWorkbench()
						.getSharedImages()
						.getImage(ISharedImages.IMG_OBJS_ERROR_TSK));
			}
		}

		for (ControlEvent event : this.currentScanModule.getTriggerEvents()) {
			if (!event.getModelErrors().isEmpty()) {
				this.triggerEventsTabItem.setImage(PlatformUI.getWorkbench()
						.getSharedImages()
						.getImage(ISharedImages.IMG_OBJS_ERROR_TSK));
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveState(IMemento memento) {
		// remember maximized states of sash form
		memento.putBoolean(MEMENTO_ACTIONS_COMPOSITE_MAXIMIZED,
				this.actionsCompositeMaximized);
		memento.putBoolean(MEMENTO_EVENTS_COMPOSITE_MAXIMIZED,
				this.eventsCompositeMaximized);

		// save composite heights
		memento.putInteger(MEMENTO_ACTIONS_COMPOSITE_WEIGHT, 
				actionEventSashForm.getWeights()[0]);
		memento.putInteger(MEMENTO_EVENTS_COMPOSITE_WEIGHT, 
				actionEventSashForm.getWeights()[1]);

		// remember sort state of action composite viewers
		/*memento.putInteger(MEMENTO_AXES_SORT_STATE,
				this.motorAxisComposite.getSortState());
		memento.putInteger(MEMENTO_CHANNEL_SORT_STATE,
				this.detectorChannelComposite.getSortState());*/
		
		// remember selected action tab
		memento.putInteger(MEMENTO_ACTIONS_TAB_FOLDER_SELECTION_INDEX, 
				this.actionsTabFolder.getSelectionIndex());
		memento.putInteger(MEMENTO_EVENTS_TAB_FOLDER_SELECTION_INDEX, 
				this.eventsTabFolder.getSelectionIndex());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void restoreState(IMemento memento) {
		// restore maximized states
		this.actionsCompositeMaximized = (memento
				.getBoolean(MEMENTO_ACTIONS_COMPOSITE_MAXIMIZED) == null) 
				? false
				: memento.getBoolean(MEMENTO_ACTIONS_COMPOSITE_MAXIMIZED);
		this.eventsCompositeMaximized = (memento
				.getBoolean(MEMENTO_EVENTS_COMPOSITE_MAXIMIZED) == null) 
				? false
				: memento.getBoolean(MEMENTO_EVENTS_COMPOSITE_MAXIMIZED);
		if (this.actionsCompositeMaximized) {
			this.actionMaxIcon.setImage(restoreIcon);
			this.actionEventSashForm.setMaximizedControl(actionsComposite);
		}
		if (this.eventsCompositeMaximized) {
			this.eventMaxIcon.setImage(restoreIcon);
			this.actionEventSashForm.setMaximizedControl(eventsComposite);
		}
		
		// restore sash form weights
		int[] weights = new int[2];
		weights[0] = (memento.getInteger(MEMENTO_ACTIONS_COMPOSITE_WEIGHT) == null) 
						? 1 : memento.getInteger(MEMENTO_ACTIONS_COMPOSITE_WEIGHT);
		weights[1] = (memento.getInteger(MEMENTO_EVENTS_COMPOSITE_WEIGHT) == null) 
						? 1 : memento.getInteger(MEMENTO_EVENTS_COMPOSITE_WEIGHT);
		actionEventSashForm.setWeights(weights);
		
		// restore sort state of action composite viewers
		/*if (memento.getInteger(MEMENTO_AXES_SORT_STATE) != null) {
			this.motorAxisComposite.setSortState(memento
					.getInteger(MEMENTO_AXES_SORT_STATE));
		}
		if (memento.getInteger(MEMENTO_CHANNEL_SORT_STATE) != null) {
			this.detectorChannelComposite.setSortState(memento
					.getInteger(MEMENTO_CHANNEL_SORT_STATE));
		}*/
		
		// restore selected tabs of tab folders
		if (memento.getInteger(MEMENTO_ACTIONS_TAB_FOLDER_SELECTION_INDEX) != null) {
			this.actionsTabFolder.setSelection(memento.getInteger(
					MEMENTO_ACTIONS_TAB_FOLDER_SELECTION_INDEX));
		}
		if (memento.getInteger(MEMENTO_EVENTS_TAB_FOLDER_SELECTION_INDEX) != null) {
			this.eventsTabFolder.setSelection(memento.getInteger(
					MEMENTO_EVENTS_TAB_FOLDER_SELECTION_INDEX));
		}
	}
	
	/**
	 * For Legacy Compatibility of Code Smells from EventMenuContributionHelper
	 * which accessed this (public) attribute directly before (when the classic 
	 * parts were contained directly in the view).
	 * @return the selection index of the events tab folder or -1
	 * @since 1.31
	 */
	public int getEventsTabFolderSelectionIndex() {
		return this.eventsTabFolder.getSelectionIndex();
	}
	
	private class EventsTabFolderSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			switch (eventsTabFolder.getSelectionIndex()) {
			case 0:
				parentView.setSelectionProvider(
						pauseEventComposite.getTableViewer());
				break;
			case 1:
				parentView.setSelectionProvider(
						redoEventComposite.getTableViewer());
				break;
			case 2:
				parentView.setSelectionProvider(
						breakEventComposite.getTableViewer());
				break;
			case 3:
				parentView.setSelectionProvider(
						triggerEventComposite.getTableViewer());
				break;
			default:
				break;
			}
		}
	}
	
	/*private class TextFocusListener extends FocusAdapter {
		private Text widget;

		public TextFocusListener(Text widget) {
			this.widget = widget;
		}
		
		@Override
		public void focusLost(FocusEvent e) {
			if (this.widget == valueCountText) {
				valueCountBinding.updateModelToTarget();
			} else if (this.widget == triggerDelayText) {
				triggerDelayBinding.updateModelToTarget();
			} else if (this.widget == settleTimeText) {
				settleTimeBinding.updateModelToTarget();
			}
		}
	}*/
}

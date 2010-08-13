/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.editor.views;

import java.util.Iterator;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;

import de.ptb.epics.eve.data.SaveAxisPositionsTypes;
import de.ptb.epics.eve.data.measuringstation.Event;
import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.data.scandescription.ScanModul;
import de.ptb.epics.eve.data.scandescription.errors.AxisError;
import de.ptb.epics.eve.data.scandescription.errors.ChannelError;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.PlotWindowError;
import de.ptb.epics.eve.data.scandescription.errors.PositioningError;
import de.ptb.epics.eve.data.scandescription.errors.PostscanError;
import de.ptb.epics.eve.data.scandescription.errors.PrescanError;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.Activator;

public class ScanModulView extends ViewPart implements IModelUpdateListener {

	public static final String ID = "de.ptb.epics.eve.editor.views.ScanModulView"; // TODO
																							// Needs
																							// to
																							// be
																							// whatever
																							// is
																							// mentioned
																							// in
																							// plugin.xml
																							// //
																							// @jve:decl-index=0:

	private boolean filling;
	private boolean plotErrors;

	private ScanModul currentScanModul; // @jve:decl-index=0:

	private Composite top = null;

	private ExpandBar bar = null;

	private Composite generalComposite = null;
	private Composite actionsComposite = null;
	private Composite plottingComposite = null;
	private Composite eventsComposite = null;

	private Label triggerDelayLabel = null;

	private Text triggerDelayText = null;

	private Label triggerDelayErrorLabel = null;

	private Label triggerDelayUnitLabel = null;

	private Label settleTimeLabel = null;

	private Text settleTimeText = null;

	private Label settleTimeErrorLabel = null;

	private Label settleTimeUnitLabel = null;

	private Button confirmTriggerCheckBox = null;

	private Label saveMotorpositionsLabel = null;

	private Combo saveMotorpositionsCombo = null;

	private Table plotWindowsTable = null;

	private Button addPlotWindowButton = null;

	private CTabFolder eventsTabFolder = null;

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

	private Button appendScheduleEventCheckBox = null;

	private ModifyListener modifyListener; // @jve:decl-index=0:

	private SelectionListener selectionListener; // @jve:decl-index=0:

	private MenuItem plotWindowRemoveMenuItem;

	private MenuItem plotWindowChangeIDMenuItem;

	private ControlListener itemControlListener;

	private String[] eventIDs;

	private ExpandItem item0;
	private ExpandItem item1;
	private ExpandItem item2;
	private ExpandItem item3;

	private CTabItem motorAxisTab;
	private CTabItem detectorChannelTab;
	private CTabItem prescanTab;
	private CTabItem postscanTab;
	private CTabItem positioningTab;
	
	private CTabItem pauseEventsTabItem;
	private CTabItem redoEventsTabItem;
	private CTabItem breakEventsTabItem;
	private CTabItem triggerEventsTabItem;
	
	
	@Override
	public void createPartControl(final Composite parent) {
		// TODO Auto-generated method stub
		
		parent.setLayout(new FillLayout());
		
		if( Activator.getDefault().getMeasuringStation() == null ) {
			final Label errorLabel = new Label( parent, SWT.NONE );
			errorLabel.setText( "No Measuring Station has been loaded. Please check Preferences!" );
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
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		this.generalComposite = new Composite(this.bar, SWT.NONE);
		this.generalComposite.setLayout(gridLayout);

		// Trigger Delay
		this.triggerDelayLabel = new Label(this.generalComposite, SWT.NONE);
		this.triggerDelayLabel.setText("Trigger delay:");
		this.triggerDelayLabel.setToolTipText("Delay time after positioning");
		this.triggerDelayText = new Text(this.generalComposite, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		this.triggerDelayText.setLayoutData(gridData);

		this.triggerDelayErrorLabel = new Label(this.generalComposite, SWT.NONE);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.CENTER;
		this.triggerDelayErrorLabel.setLayoutData(gridData);
		this.triggerDelayErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_WARN_TSK ) );

		this.triggerDelayUnitLabel = new Label(this.generalComposite, SWT.NONE);
		this.triggerDelayUnitLabel.setText("s");

		// Settle Time
		this.settleTimeLabel = new Label(this.generalComposite, SWT.NONE);
		this.settleTimeLabel.setText("Settletime:");
		this.settleTimeLabel.setToolTipText("Delay time after first positioning in the scan module");

		this.settleTimeText = new Text(this.generalComposite, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		this.settleTimeText.setLayoutData(gridData);

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

		// Save Motor Positions
		this.saveMotorpositionsLabel = new Label(this.generalComposite, SWT.NONE);
		this.saveMotorpositionsLabel.setText("Save all motorpositions");
		gridData = new GridData();
		gridData.horizontalSpan = 1;
		this.saveMotorpositionsLabel.setLayoutData(gridData);

		this.saveMotorpositionsCombo = new Combo(this.generalComposite, SWT.NONE);
		this.saveMotorpositionsCombo.setItems(new String[] { "never", "before",
				"after", "both" });
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		this.saveMotorpositionsCombo.setLayoutData(gridData);
		
		this.item0 = new ExpandItem(this.bar, SWT.NONE, 0);
		item0.setText("General");
		item0.setHeight(this.generalComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		item0.setControl(this.generalComposite);

		// Actions Section
		createActionsTabFolder();

		this.item1 = new ExpandItem(this.bar, SWT.NONE, 0);
		item1.setText("Actions");
		item1.setHeight(this.actionsComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		item1.setControl(this.actionsComposite);

		// Plot Section
		gridLayout = new GridLayout();
		this.plottingComposite = new Composite(this.bar, SWT.NONE);
		this.plottingComposite.setLayout(gridLayout);

		plotWindowsTable = new Table(this.plottingComposite, SWT.FULL_SELECTION);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		plotWindowsTable.setLayoutData(gridData);

		Menu menu = new Menu(plotWindowsTable);
		plotWindowRemoveMenuItem = new MenuItem(menu, SWT.MENU);
		plotWindowRemoveMenuItem.setText("Remove Plot Window");
		plotWindowRemoveMenuItem.setEnabled(false);

		plotWindowChangeIDMenuItem = new MenuItem(menu, SWT.MENU);
		plotWindowChangeIDMenuItem.setText("Change ID");
		plotWindowChangeIDMenuItem.setEnabled(false);

		plotWindowsTable.setMenu(menu);
		plotWindowsTable.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected( final SelectionEvent e ) {
				// TODO Auto-generated method stub
			}

			public void widgetSelected( final SelectionEvent e ) {
				IViewReference[] ref = getSite().getPage().getViewReferences();
				PlotWindowView plotWindowView = null;
				for (int i = 0; i < ref.length; ++i) {
					if (ref[i].getId().equals(PlotWindowView.ID)) {
						plotWindowView = (PlotWindowView) ref[i]
								.getPart(false);
					}
				}
				if( plotWindowView != null ) {
					final PlotWindow plotWindow = (PlotWindow)plotWindowsTable.getSelection()[0].getData();
					plotWindowView.setPlotWindow( plotWindow );
				}
			}
		});
		
		addPlotWindowButton = new Button(this.plottingComposite, SWT.NONE);
		addPlotWindowButton.setText("Add Plot Window");

		plotWindowsTable.setHeaderVisible(true);
		plotWindowsTable.setLinesVisible(true);

		TableColumn tableColumn1 = new TableColumn(plotWindowsTable, SWT.NONE);
		tableColumn1.setWidth(260);
		tableColumn1.setText("Plot Window");
		tableColumn1.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_WARN_TSK ) );

		
		this.item2 = new ExpandItem(this.bar, SWT.NONE, 0);
		item2.setText("Plot");
		item2.setHeight(this.plottingComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		item2.setControl(this.plottingComposite);

		// Event Section
		createEventsTabFolder();
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		appendScheduleEventCheckBox = new Button(this.eventsComposite, SWT.CHECK);
		appendScheduleEventCheckBox.setText("Append Schedule Event");
		appendScheduleEventCheckBox.setLayoutData(gridData);

		this.item3 = new ExpandItem(this.bar, SWT.NONE, 0);
		item3.setText("Event options");
		item3.setHeight(this.eventsComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		item3.setControl(this.eventsComposite);

		this.setEnabledForAll(false);
		this.createListener();
		this.appendListener();

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	/**
	 * This method initializes eventsTabFolder
	 * 
	 */
	private void createEventsTabFolder() {

		GridLayout gridLayout = new GridLayout();

		this.eventsComposite = new Composite(this.bar, SWT.NONE);
		this.eventsComposite.setLayout(gridLayout);

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;
		eventsTabFolder = new CTabFolder(this.eventsComposite, SWT.NONE);
		eventsTabFolder.setLayoutData(gridData);
		eventsTabFolder.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}

			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				// Einträge in der Auswahlliste werden aktualisiert
				CTabItem wahlItem = eventsTabFolder.getSelection();
				EventComposite wahlComposite = (EventComposite)wahlItem.getControl();
				wahlComposite.setEventChoice();
			}
		});
		
		pauseEventComposite = new EventComposite(eventsTabFolder, SWT.NONE);
		redoEventComposite = new EventComposite(eventsTabFolder, SWT.NONE);
		breakEventComposite = new EventComposite(eventsTabFolder, SWT.NONE);
		triggerEventComposite = new EventComposite(eventsTabFolder, SWT.NONE);
		
		this.pauseEventsTabItem = new CTabItem(eventsTabFolder, SWT.NONE);
		this.pauseEventsTabItem.setText(" Pause ");
		this.pauseEventsTabItem.setToolTipText("Configure event to pause and resume this scan module");
		this.pauseEventsTabItem.setControl(pauseEventComposite);
		this.redoEventsTabItem = new CTabItem(eventsTabFolder, SWT.NONE);
		this.redoEventsTabItem.setText(" Redo ");
		this.redoEventsTabItem.setToolTipText("Repeat the last acquisition, if redo event occurs");
		this.redoEventsTabItem.setControl(redoEventComposite);
		this.breakEventsTabItem = new CTabItem(eventsTabFolder, SWT.NONE);
		this.breakEventsTabItem.setText(" Break ");
		this.breakEventsTabItem.setToolTipText("Finish this scan module and continue with next");
		this.breakEventsTabItem.setControl(breakEventComposite);
		this.triggerEventsTabItem = new CTabItem(eventsTabFolder, SWT.NONE);
		this.triggerEventsTabItem.setText(" Trigger ");
		this.triggerEventsTabItem.setToolTipText("Wait for trigger event before moving to next position");
		this.triggerEventsTabItem.setControl(triggerEventComposite);
	}

	/**
	 * create TabFolder for actions
	 * 
	 */
	private void createActionsTabFolder() {

		GridLayout gridLayout = new GridLayout();

		this.actionsComposite = new Composite(this.bar, SWT.NONE);
		this.actionsComposite.setLayout(gridLayout);

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		this.behaviorTabFolder = new CTabFolder(this.actionsComposite, SWT.FLAT);
		this.behaviorTabFolder.setLayoutData(gridData);

		createMotorAxisComposite();
		createDetectorChannelComposite();
		createPrescanComposite();
		createPostscanComposite();
		createPositioningComposite();
		
		this.motorAxisTab = new CTabItem(this.behaviorTabFolder, SWT.FLAT);
		this.motorAxisTab.setText(" Motor Axes ");
		this.motorAxisTab.setToolTipText("Select motor axes to be used in this scan module");
		this.motorAxisTab.setControl(this.motorAxisComposite);
		this.detectorChannelTab = new CTabItem(this.behaviorTabFolder, SWT.FLAT);
		this.detectorChannelTab.setText(" Detector Channels ");
		this.detectorChannelTab.setToolTipText("Select detector channels to be used in this scan module");
		this.detectorChannelTab.setControl(this.detectorChannelComposite);
		this.prescanTab = new CTabItem(this.behaviorTabFolder, SWT.FLAT);
		this.prescanTab.setText(" Prescan ");
		this.prescanTab.setToolTipText("Action to do before scan module is started");
		this.prescanTab.setControl(this.prescanComposite);
		this.postscanTab = new CTabItem(this.behaviorTabFolder, SWT.FLAT);
		this.postscanTab.setText(" Postscan ");
		this.postscanTab.setToolTipText("Action to do if scan module is done");
		this.postscanTab.setControl(this.postscanComposite);
		this.positioningTab = new CTabItem(this.behaviorTabFolder, SWT.FLAT);
		this.positioningTab.setText(" Positioning ");
		this.positioningTab.setToolTipText("Move motor to calculated position after scan module is done");
		this.positioningTab.setControl(this.positioningComposite);

	}


	/**
	 * This method initializes motorAxisComposite
	 * 
	 */
	private void createMotorAxisComposite() {
		motorAxisComposite = new MotorAxisComposite(behaviorTabFolder,
				SWT.NONE);
	}

	/**
	 * This method initializes detectorChannelComposite
	 * 
	 */
	private void createDetectorChannelComposite() {
		detectorChannelComposite = new DetectorChannelComposite(behaviorTabFolder,
				SWT.NONE);
	}

	/**
	 * This method initializes prescanComposite
	 * 
	 */
	private void createPrescanComposite() {
		prescanComposite = new PrescanComposite(behaviorTabFolder,
				SWT.NONE);
	}

	/**
	 * This method initializes postscanComposite
	 * 
	 */
	private void createPostscanComposite() {
		postscanComposite = new PostscanComposite(behaviorTabFolder,
				SWT.NONE);
	}
	
	/**
	 * This method initializes positioningComposite
	 * 
	 */
	private void createPositioningComposite() {
		positioningComposite = new PositioningComposite(behaviorTabFolder,
				SWT.NONE);
	}

	public ScanModul getCurrentScanModul() {
		return currentScanModul;
	}

	public void setCurrentScanModul(ScanModul currentScanModul) {
		if( this.currentScanModul != null ) {
			this.currentScanModul.removeModelUpdateListener( this );
		}
		this.currentScanModul = currentScanModul;

		if( this.currentScanModul != null ) {
			this.currentScanModul.addModelUpdateListener( this );
		}
		this.fillFields();
	}

	private void setEnabledForAll(final boolean enabled) {
		this.triggerDelayText.setEnabled(enabled);
		this.settleTimeText.setEnabled(enabled);
		this.confirmTriggerCheckBox.setEnabled(enabled);
		this.behaviorTabFolder.setEnabled(enabled);
		this.motorAxisComposite.setEnabled(enabled);
		// TODO wird dieses Composite hier überhaupt enabled?
		// Für Prescan, Postscan und Positioning findet das
		// hier ja auch nicht statt. (Hartmut 6.5.10)
		this.detectorChannelComposite.setEnabled(enabled);
		this.plotWindowsTable.setEnabled(enabled);
		this.addPlotWindowButton.setEnabled(enabled);
		this.eventsTabFolder.setEnabled(enabled);
		this.saveMotorpositionsCombo.setEnabled(enabled);
		// this.pauseEventComposite.setEnabledForAll(enabled);
		// this.redoEventComposite.setEnabledForAll(enabled);
		// this.breakEventComposite.setEnabledForAll(enabled);
		// this.triggerEventComposite.setEnabledForAll(enabled);

		this.appendScheduleEventCheckBox.setEnabled(enabled);

	}

	private void fillFields() {

		IViewReference[] ref = getSite().getPage().getViewReferences();
		ScanView scanView = null;
		for (int i = 0; i < ref.length; ++i) {
			if (ref[i].getId().equals(ScanView.ID)) {
				scanView = (ScanView) ref[i].getPart(false);
			}
		}

		MotorAxisView motorAxisView = null;
		for (int i = 0; i < ref.length; ++i) {
			if (ref[i].getId().equals(MotorAxisView.ID)) {
				motorAxisView = (MotorAxisView) ref[i].getPart(false);
			}
		}

		PlotWindowView plotWindowView = null;
		for (int i = 0; i < ref.length; ++i) {
			if (ref[i].getId().equals(PlotWindowView.ID)) {
				plotWindowView = (PlotWindowView) ref[i].getPart(false);
			}
		}

		DetectorChannelView detectorChannelView = null;
		
		for (int i = 0; i < ref.length; ++i) {
			if (ref[i].getId().equals(DetectorChannelView.ID)) {
				detectorChannelView = (DetectorChannelView) ref[i]
						.getPart(false);
			}
		}
		if( detectorChannelView != null ) {
					detectorChannelView.setChannel( null );
		}
		
		if( motorAxisView != null ) {
			motorAxisView.setAxis( null, -1 );
		}
		
		if (this.currentScanModul != null) {
			this.setEnabledForAll(true);
			this.setPartName(this.currentScanModul.getName() + ":"
					+ this.currentScanModul.getId());

			scanView.setCurrentChain(this.currentScanModul.getChain());
			
			
		} else {
			this.setEnabledForAll(false);
			this.setPartName("No Scan Modul loaded");
			scanView.setCurrentChain(null);
			
		}
		
		if( plotWindowView != null ) {
			plotWindowView.setPlotWindow( null );
			plotWindowView.setScanModul( currentScanModul );
		}
		
		this.filling = true;
		try {
			if (this.currentScanModul != null) {
				this.triggerDelayText.setText((this.currentScanModul
						.getTriggerdelay() != Double.NEGATIVE_INFINITY) ? ""
						+ this.currentScanModul.getTriggerdelay() : "");
				this.settleTimeText.setText((this.currentScanModul
						.getSettletime() != Double.NEGATIVE_INFINITY) ? ""
						+ this.currentScanModul.getSettletime() : "");
				if( this.triggerDelayText.getText().equals( "" ) ) {
					triggerDelayErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
					triggerDelayErrorLabel.setToolTipText( "The trigger delay must not be empty!" );
				} else {
					triggerDelayErrorLabel.setImage( null );
					triggerDelayErrorLabel.setToolTipText( null );
				}
				if( this.settleTimeText.getText().equals( "" ) ) {
					settleTimeErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
					settleTimeErrorLabel.setToolTipText( "The settletime must not be empty!" );
				} else {
					settleTimeErrorLabel.setImage( null );
					settleTimeErrorLabel.setToolTipText( null );
				}
				
				this.confirmTriggerCheckBox.setSelection(this.currentScanModul.isTriggerconfirm());

				this.saveMotorpositionsCombo.setText(this.currentScanModul.getSaveAxisPositions().name());
				
				this.triggerEventComposite
						.setControlEventManager(this.currentScanModul
								.getTriggerControlEventManager());
				this.breakEventComposite
						.setControlEventManager(this.currentScanModul
								.getBreakControlEventManager());
				this.redoEventComposite
						.setControlEventManager(this.currentScanModul
								.getRedoControlEventManager());
				this.pauseEventComposite
						.setControlEventManager(this.currentScanModul
								.getPauseControlEventManager());

				this.updatePlotWindowsTable();

				((MotorAxisComposite) this.motorAxisComposite)
				.setScanModul(this.currentScanModul);

				((DetectorChannelComposite) this.detectorChannelComposite)
				.setScanModul(this.currentScanModul);

 				((PrescanComposite) this.prescanComposite)
				.setScanModul(this.currentScanModul);

				((PostscanComposite) this.postscanComposite)
				.setScanModul(this.currentScanModul);

				((PositioningComposite) this.positioningComposite)
						.setScanModul(this.currentScanModul);

				// TODO CheckBox for ScheduleIncident Start or End
				Event testEvent = new Event(currentScanModul.getChain().getId(), currentScanModul.getId(), Event.ScheduleIncident.END);
				this.appendScheduleEventCheckBox.setSelection(this.currentScanModul.getChain()
							.getScanDescription().getEventById(testEvent.getID()) != null);
				
				this.motorAxisTab.setImage( null );
				this.detectorChannelTab.setImage( null );
				this.prescanTab.setImage( null );
				this.postscanTab.setImage( null );
				this.positioningTab.setImage( null );
				
				boolean motorAxisErrors = false;
				boolean detectorChannelErrors = false;
				boolean prescanErrors = false;
				boolean postscanErrors = false;
				boolean positioningErrors = false;
				boolean plotWindowErrors = false;
				
				final Iterator< IModelError > it = this.currentScanModul.getModelErrors().iterator();
				while( it.hasNext() ) {
					final IModelError modelError = it.next();
					if( modelError instanceof AxisError ) {
						motorAxisErrors = true;
					} else if( modelError instanceof ChannelError ) {
						detectorChannelErrors = true;
					} else if( modelError instanceof PrescanError ) {
						prescanErrors = true;
					} else if( modelError instanceof PostscanError ) {
						postscanErrors = true;
					} else if( modelError instanceof PositioningError ) {
						positioningErrors = true;
					} else if( modelError instanceof PlotWindowError ) {
						plotWindowErrors = true;
					} 
				}
				if( motorAxisErrors ) {
					this.motorAxisTab.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
				}
				if( detectorChannelErrors ) {
					this.detectorChannelTab.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
				}
				if( prescanErrors ) {
					this.prescanTab.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
				}
				if( postscanErrors ) {
					this.postscanTab.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
				}
				if( positioningErrors ) {
					this.positioningTab.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
				}
				if( plotWindowErrors ) {
					// TODO: Hier wird noch nichts gemacht
					System.out.println("Achtung: Im ScanModul sind Plot Errors vorhanden");
				}
				if( this.currentScanModul.getPauseControlEventManager().getModelErrors().size() > 0 ) {
					this.pauseEventsTabItem.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
				} else {
					this.pauseEventsTabItem.setImage( null );
				}
				if( this.currentScanModul.getRedoControlEventManager().getModelErrors().size() > 0 ) {
					this.redoEventsTabItem.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
				} else {
					this.redoEventsTabItem.setImage( null );
				}
				if( this.currentScanModul.getBreakControlEventManager().getModelErrors().size() > 0 ) {
					this.breakEventsTabItem.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
				} else {
					this.breakEventsTabItem.setImage( null );
				}
				if( this.currentScanModul.getTriggerControlEventManager().getModelErrors().size() > 0 ) {
					this.triggerEventsTabItem.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
				} else {
					this.triggerEventsTabItem.setImage( null );
				}
			} else {
				this.triggerDelayText.setText("");
				this.settleTimeText.setText("");
				this.confirmTriggerCheckBox.setSelection(false);
				// this.triggerEventComposite.setEvent( null );
				// this.breakEventComposite.setEvent( null );
				// this.redoEventComposite.setEvent( null );
				// this.pauseEventComposite.setEvent( null );
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		this.filling = false;
	}

	private void appendListener() {
		this.triggerDelayText.addModifyListener(this.modifyListener);
		this.settleTimeText.addModifyListener(this.modifyListener);
		this.confirmTriggerCheckBox
				.addSelectionListener(this.selectionListener);
		this.saveMotorpositionsCombo.addModifyListener(this.modifyListener);
		this.addPlotWindowButton.addSelectionListener(this.selectionListener);

		this.plotWindowRemoveMenuItem
				.addSelectionListener(this.selectionListener);
		this.plotWindowChangeIDMenuItem
				.addSelectionListener(this.selectionListener);
		this.appendScheduleEventCheckBox
				.addSelectionListener(this.selectionListener);

		this.generalComposite.addControlListener(this.itemControlListener);
		this.actionsComposite.addControlListener(this.itemControlListener);
		this.plottingComposite.addControlListener(this.itemControlListener);
		this.eventsComposite.addControlListener(this.itemControlListener);

	}

	private void createListener() {
		this.modifyListener = new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				if (!filling) {
					if (e.widget == triggerDelayText) {

						if (triggerDelayText.getText().equals("")) {
						currentScanModul
									.setTriggerdelay(Double.NEGATIVE_INFINITY);
						triggerDelayErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
						triggerDelayErrorLabel.setToolTipText( "The trigger delay must not be empty!" );
						
						} else {
							try {
								currentScanModul
										.setTriggerdelay(Double
												.parseDouble(triggerDelayText
														.getText()));
								triggerDelayErrorLabel.setImage( null );
								triggerDelayErrorLabel.setToolTipText( null );
							} catch (Exception ex) {
								triggerDelayErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
								triggerDelayErrorLabel.setToolTipText( "The trigger delay must be an floating point value!" );
							}
						}

					} else if (e.widget == settleTimeText) {

						if (settleTimeText.getText().equals("")) {
							
							currentScanModul.setSettletime(Double.NEGATIVE_INFINITY);
							settleTimeErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
							settleTimeErrorLabel.setToolTipText( "The settletime must not be empty!" );
						} else {
							try {
								currentScanModul.setSettletime(Double.parseDouble(settleTimeText.getText()));
								settleTimeErrorLabel.setImage( null );
								settleTimeErrorLabel.setToolTipText( null );
							} catch (Exception ex) {
								currentScanModul.setSettletime(Double.NEGATIVE_INFINITY);
								settleTimeErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
								settleTimeErrorLabel.setToolTipText( "The settletime must be an floating point value!" );
							}
						}
					} else if (e.widget == saveMotorpositionsCombo) {
						currentScanModul.setSaveAxisPositions(SaveAxisPositionsTypes.stringToType(saveMotorpositionsCombo.getText()));
					}
				}
			}
		};

		this.selectionListener = new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {

			}

			public void widgetSelected(SelectionEvent e) {
				try {
					if (!filling) {
						if (e.widget == confirmTriggerCheckBox) {
							currentScanModul
									.setTriggerconfirm(confirmTriggerCheckBox
											.getSelection());
						} else if (e.widget == addPlotWindowButton) {
							int newID = 1;
							PlotWindow[] plotWindows = currentScanModul
									.getPlotWindows();
							do {
								boolean repeat = false;
								for (int i = 0; i < plotWindows.length; ++i) {

									if (plotWindows[i].getId() == newID) {

										newID++;
										repeat = true;
										break;
									}
								}
								if (!repeat)
									break;
							} while (true);
							PlotWindow plotWindow = new PlotWindow();
							plotWindow.setId(newID);
							currentScanModul.add(plotWindow);
							fillFields();
						} else if (e.widget == plotWindowRemoveMenuItem) {
							TableItem[] selectedItems = plotWindowsTable
									.getSelection();
							int[] selectedIndexes = plotWindowsTable
									.getSelectionIndices();
							for (int i = 0; i < selectedItems.length; ++i) {
								currentScanModul
										.remove((PlotWindow) selectedItems[i]
												.getData());
							}
							plotWindowsTable.remove(selectedIndexes);
							fillFields();
						} else if (e.widget == appendScheduleEventCheckBox) {
							Event scheduleEvent = new Event(currentScanModul.getChain().getId(), currentScanModul.getId(), Event.ScheduleIncident.END);

							if (appendScheduleEventCheckBox.getSelection()) {
								if (currentScanModul.getChain().getScanDescription().getEventById(
												scheduleEvent.getID()) == null) {
									currentScanModul.getChain().getScanDescription().add(scheduleEvent);

									// TODO why do we need a new chain? if needed uncomment
/*									Chain[] chains = currentScanModul
											.getChain().getScanDescription()
											.getChains().toArray(new Chain[0]);
									int newId = 1;
									do {
										boolean repeat = false;
										for (int i = 0; i < chains.length; ++i) {

											if (chains[i].getId() == newId) {

												newId++;
												repeat = true;
												break;
											}
										}
										if (!repeat)
											break;
									} while (true);
									Chain chain = new Chain(newId);
									StartEvent startEvent = new StartEvent( event, chain );
									chain.setStartEvent(startEvent);
									currentScanModul.getChain().getScanDescription().add(chain);
*/
								}
							} 
							else {
								Event event = currentScanModul.getChain().getScanDescription().getEventById(scheduleEvent.getID());
								if (event != null) {

									// TODO
									//check if this event is used by any ControlEvents
									// and notify them, that we remove the event
									currentScanModul.getChain().getScanDescription().remove(event);

									
									// the commented code searches the event in all chains
									// and will not remove it, if it is used as startevent.
									// startevents are regular events now, it it was removed
									// the chain will not start, which is perfectly ok.
									// Users have to add a new startevent
/*
									// loop through all chains, check if this event is used as a start event
									Iterator< Chain > chainIterator = currentScanModul.getChain().getScanDescription().getChains().iterator();
									StartEvent startEvent = null;
									while( chainIterator.hasNext() ) {
										Chain chain = chainIterator.next();
										if( chain.getStartEvent().getEvent() == event ) {
											startEvent = chain.getStartEvent();
											break;
										}
									}
									
									
									if( startEvent.getConnector() != null
											&& startEvent.getConnector().getChildScanModul() != null) {
										appendScheduleEventCheckBox.setSelection(true);
										Shell shell = getSite().getShell();
										MessageBox box = new MessageBox(shell, SWT.OK);
										box.setText("Cannot remove Event");
										box.setMessage("I cannot remove a Shedule Event if Scan Modules are appended to it.\nPlease remove the Scan Modules first to remove the Event.");
										box.open();
									} 
									else {

										Iterator<Chain> it = currentScanModul
												.getChain()
												.getScanDescription()
												.getChains().iterator();
										while (it.hasNext()) {
											Chain currentChain = it.next();
											if (currentChain.getStartEvent().getEvent() == event) {
												currentScanModul.getChain()
														.getScanDescription()
														.remove(currentChain);
												break;
											}
										}
										currentScanModul.getChain()
												.getScanDescription().remove(
														event);

									}
*/									
								} 
							}
						} else if (e.widget == plotWindowChangeIDMenuItem) {
							Shell shell = getSite().getShell();
							TableItem[] selectedItems = plotWindowsTable
									.getSelection();
							for (int i = 0; i < selectedItems.length; ++i) {
								PlotWindow plotWindow = (PlotWindow) selectedItems[i]
										.getData();
								InputDialog dialog = new InputDialog(shell,
										"Change ID for Plot Window",
										"Please enter the new ID", "" + plotWindow.getId(), null);
								if (InputDialog.OK == dialog.open()) {
									// Id wird nur gesetzt, wenn es sie noch nicht gibt
									int newId = Integer.parseInt( dialog.getValue());

									PlotWindow[] plotWindows = currentScanModul.getPlotWindows();
									boolean setId = true;
									for (int j = 0; j < plotWindows.length; ++j) {
										if (newId == plotWindows[j].getId()) {
											// Id wird nicht gesetzt, da schon vorhanden
											setId = false;
										}
									}
									if (setId) {
										plotWindow.setId( newId );
									}
								}
							}
							fillFields();
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		};
		
		this.itemControlListener = new ControlListener() {

			public void controlMoved(final ControlEvent e) {
				this.function();
			}

			public void controlResized(final ControlEvent e) {
				this.function();

			}
			// Caution: do not use getHeaderHeight() here
			// height values vary without changing the layout
			// which puts us into an endless loop
			private void function() {
				int height = bar.getSize().y - 6 * 25;
/*				height -= item0.getHeaderHeight();
				height -= item1.getHeaderHeight();
				height -= item2.getHeaderHeight();
				height -= item3.getHeaderHeight();
*/
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
				if (item3.getExpanded()) {
					amount++;
				}

				if (amount > 0) {
					height /= amount;
					if (item1.getExpanded()) {
						item1.setHeight(height < 200 ? 200 : height);
					}
					if (item2.getExpanded()) {
						item2.setHeight(height < 150 ? 150 : height);
					}
					if (item3.getExpanded()) {
						item3.setHeight(height < 150 ? 150 : height);
					}
				}
			}
		};
	}

	protected void updatePlotWindowsTable() {
		this.plotWindowsTable.removeAll();
		PlotWindow[] plotWindows = this.currentScanModul.getPlotWindows();
		for (int i = 0; i < plotWindows.length; ++i) {
			TableItem item = new TableItem(this.plotWindowsTable, SWT.NONE);
			item.setText(new String[] { "" + plotWindows[i].getId() });
			item.setData(plotWindows[i]);
			
			final Iterator< IModelError > it = plotWindows[i].getModelErrors().iterator();
			while( it.hasNext() ) {
				final IModelError modelError = it.next();
				if( modelError instanceof PlotWindowError ) {
					item.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
				} 
			}
			
		}
		if (this.plotWindowsTable.getItems().length == 0) {
			this.plotWindowRemoveMenuItem.setEnabled(false);
			this.plotWindowChangeIDMenuItem.setEnabled(false);
		} else {
			this.plotWindowRemoveMenuItem.setEnabled(true);
			this.plotWindowChangeIDMenuItem.setEnabled(true);
		}
	}

	protected void updatePlotWindowsTableError() {

		PlotWindow[] plotWindows = this.currentScanModul.getPlotWindows();

		TableItem[] tableItems = this.plotWindowsTable.getItems();

		int plotlength = plotWindows.length;
		int tablelength = tableItems.length;
		
		for (int i = 0; i < Math.min(plotlength, tablelength); ++i) {

			tableItems[i].setImage((Image)null);

			final Iterator< IModelError > it = plotWindows[i].getModelErrors().iterator();
			while( it.hasNext() ) {
				final IModelError modelError = it.next();
				if( modelError instanceof PlotWindowError ) {
					tableItems[i].setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
				} 
			}
		}
	}
	
	@Override
	public void updateEvent( final ModelUpdateEvent modelUpdateEvent ) {
		
		this.filling = true;

		this.motorAxisTab.setImage( null );
		this.detectorChannelTab.setImage( null );
		this.prescanTab.setImage( null );
		this.postscanTab.setImage( null );
		this.positioningTab.setImage( null );
		
		boolean motorAxisErrors = false;
		boolean detectorChannelErrors = false;
		boolean prescanErrors = false;
		boolean postscanErrors = false;
		boolean positioningErrors = false;
		boolean plotWindowErrors = false;
		
		final Iterator< IModelError > it = this.currentScanModul.getModelErrors().iterator();
		while( it.hasNext() ) {
			final IModelError modelError = it.next();
			if( modelError instanceof AxisError ) {
				motorAxisErrors = true;
			} else if( modelError instanceof ChannelError ) {
				detectorChannelErrors = true;
			} else if( modelError instanceof PrescanError ) {
				prescanErrors = true;
			} else if( modelError instanceof PostscanError ) {
				postscanErrors = true;
			} else if( modelError instanceof PositioningError ) {
				positioningErrors = true;
			} else if( modelError instanceof PlotWindowError ) {
				plotWindowErrors = true;
			} 
		}
		if( motorAxisErrors ) {
			this.motorAxisTab.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
		}
		if( detectorChannelErrors ) {
			this.detectorChannelTab.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
		}
		if( prescanErrors ) {
			this.prescanTab.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
		}
		if( postscanErrors ) {
			this.postscanTab.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
		}
		if( positioningErrors ) {
			this.positioningTab.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
		}
		if( plotWindowErrors ) {
			this.plotErrors = true;
			this.updatePlotWindowsTableError();
		}
		else {
			if (this.plotErrors) {
				// es gibt noch einen plotError der zurückgesetzt werden muß
				this.updatePlotWindowsTableError();
			}
			this.plotErrors = false;
		}

		if( this.currentScanModul.getPauseControlEventManager().getModelErrors().size() > 0 ) {
			this.pauseEventsTabItem.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
		} else {
			this.pauseEventsTabItem.setImage( null );
		}
		if( this.currentScanModul.getRedoControlEventManager().getModelErrors().size() > 0 ) {
			this.redoEventsTabItem.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
		} else {
			this.redoEventsTabItem.setImage( null );
		}
		if( this.currentScanModul.getBreakControlEventManager().getModelErrors().size() > 0 ) {
			this.breakEventsTabItem.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
		} else {
			this.breakEventsTabItem.setImage( null );
		}
		if( this.currentScanModul.getTriggerControlEventManager().getModelErrors().size() > 0 ) {
			this.triggerEventsTabItem.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
		} else {
			this.triggerEventsTabItem.setImage( null );
		}
		
		this.filling = false;
		
	}
} // @jve:decl-index=0:visual-constraint="10,10,342,376"

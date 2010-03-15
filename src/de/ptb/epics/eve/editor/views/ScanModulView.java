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
import org.eclipse.ui.IViewReference;
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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;

import de.ptb.epics.eve.data.SaveAxisPositionsTypes;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Event;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.data.scandescription.ScanModul;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.Helper;

public class ScanModulView extends ViewPart {

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

	private Combo motorAxisCombo = null;
	private String motorStrings[] = null;
	
	private Combo detectorChannelCombo = null;
	private String detectorStrings[] = null;

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

	private Table motorAxisTable = null;

	private Button addMotorAxisButton = null;

	private Table detectorChannelsTable = null;

	private Button addDetectorChannelButton = null;

	private Button appendScheduleEventCheckBox = null;

	private ModifyListener modifyListener; // @jve:decl-index=0:

	private SelectionListener selectionListener; // @jve:decl-index=0:

	private MenuItem motorAxisRemoveMenuItem;

	private MenuItem detectorChannelRemoveMenuItem;

	private MenuItem plotWindowRemoveMenuItem;

	private MenuItem plotWindowChangeIDMenuItem;

	private ControlListener itemControlListener;

	private String[] eventIDs;

	private ExpandItem item0;
	private ExpandItem item1;
	private ExpandItem item2;
	private ExpandItem item3;

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

		this.triggerDelayUnitLabel = new Label(this.generalComposite, SWT.NONE);
		this.triggerDelayUnitLabel.setText("ms");

		// Settle Time
		this.settleTimeLabel = new Label(this.generalComposite, SWT.NONE);
		this.settleTimeLabel.setText("Settletime:");

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

		this.settleTimeUnitLabel = new Label(this.generalComposite, SWT.NONE);
		this.settleTimeUnitLabel.setText("ms");

		// Trigger Confirm 
		gridData = new GridData();
		gridData.horizontalSpan = 4;

		this.confirmTriggerCheckBox = new Button(this.generalComposite, SWT.CHECK);
		this.confirmTriggerCheckBox.setText("Confirm Trigger");
		this.confirmTriggerCheckBox.setLayoutData(gridData);

		// Save Motor Positions
		this.saveMotorpositionsLabel = new Label(this.generalComposite, SWT.NONE);
		this.saveMotorpositionsLabel.setText("Save all motorpositions");
		gridData = new GridData();
		gridData.horizontalSpan = 1;
		this.saveMotorpositionsLabel.setLayoutData(gridData);

		this.saveMotorpositionsCombo = new Combo(this.generalComposite,
				SWT.NONE);
//		SWT.DROP_DOWN | SWT.READ_ONLY);
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
	 * This method initializes motorAxisCombo
	 * 
	 */
	private void createMotorAxisCombo() {
		motorAxisCombo = new Combo(motorAxisComposite, SWT.NONE);
		// hier wird nur eine sortierte Liste erstellt mit allen Motorachsen
		this.motorStrings = (Activator.getDefault().getMeasuringStation()
		.getAxisFullIdentifyer().toArray(new String[0]));
		
	}

	/**
	 * This method initializes detectorChannelCombo
	 * 
	 */
	private void createDetectorChannelCombo() {
		detectorChannelCombo = new Combo(detectorChannelComposite, SWT.NONE);
		// hier wird nur eine sortierte Liste erstellt mit allen Detektorkanälen
		this.detectorStrings = (Activator.getDefault()
				.getMeasuringStation().getChannelsFullIdentifyer().toArray(new String[0]));
	
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

		pauseEventComposite = new EventComposite(eventsTabFolder, SWT.NONE);
		redoEventComposite = new EventComposite(eventsTabFolder, SWT.NONE);
		breakEventComposite = new EventComposite(eventsTabFolder, SWT.NONE);
		triggerEventComposite = new EventComposite(eventsTabFolder, SWT.NONE);
		CTabItem tabItem = new CTabItem(eventsTabFolder, SWT.NONE);
		tabItem.setText(" Pause ");
		tabItem.setToolTipText("Configure event to pause and resume this scan module");
		tabItem.setControl(pauseEventComposite);
		CTabItem tabItem1 = new CTabItem(eventsTabFolder, SWT.NONE);
		tabItem1.setText(" Redo ");
		tabItem1.setToolTipText("Repeat the last acquisition, if redo event occurs");
		tabItem1.setControl(redoEventComposite);
		CTabItem tabItem2 = new CTabItem(eventsTabFolder, SWT.NONE);
		tabItem2.setText(" Break ");
		tabItem2.setToolTipText("Finish the current scan module and continue with next");
		tabItem2.setControl(breakEventComposite);
		CTabItem tabItem3 = new CTabItem(eventsTabFolder, SWT.NONE);
		tabItem3.setText(" Trigger ");
		tabItem3.setToolTipText("Wait for trigger event before moving to next position");
		tabItem3.setControl(triggerEventComposite);
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
		CTabItem tabItem4 = new CTabItem(this.behaviorTabFolder, SWT.FLAT);
		tabItem4.setText(" Motor Axes ");
		tabItem4.setToolTipText("Select motor axes to be used in this scan module");
		tabItem4.setControl(this.motorAxisComposite);
		CTabItem tabItem5 = new CTabItem(this.behaviorTabFolder, SWT.FLAT);
		tabItem5.setText(" Detector Channels ");
		tabItem5.setToolTipText("Select detector channels to be used in this scan module");
		tabItem5.setControl(this.detectorChannelComposite);
		CTabItem tabItem6 = new CTabItem(this.behaviorTabFolder, SWT.FLAT);
		tabItem6.setText(" Prescan ");
		tabItem6.setToolTipText("Action to do before scan module is started");
		tabItem6.setControl(this.prescanComposite);
		CTabItem tabItem7 = new CTabItem(this.behaviorTabFolder, SWT.FLAT);
		tabItem7.setText(" Postscan ");
		tabItem7.setToolTipText("Action to do if scan module is done");
		tabItem7.setControl(this.postscanComposite);
		CTabItem tabItem8 = new CTabItem(this.behaviorTabFolder, SWT.FLAT);
		tabItem8.setText(" Positioning ");
		tabItem8.setToolTipText("Move motor to calculated position after scan module is done");
		tabItem8.setControl(this.positioningComposite);

	}

	/**
	 * This method initializes motorAxisComposite
	 * 
	 */
	private void createMotorAxisComposite() {
		GridData gridData6 = new GridData();
		gridData6.horizontalSpan = 2;
		gridData6.verticalAlignment = GridData.FILL;
		gridData6.grabExcessHorizontalSpace = true;
		gridData6.grabExcessVerticalSpace = true;
		gridData6.horizontalAlignment = GridData.FILL;
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 2;
		motorAxisComposite = new Composite(behaviorTabFolder, SWT.NONE);
		motorAxisTable = new Table(motorAxisComposite, SWT.FULL_SELECTION);
		motorAxisTable.setHeaderVisible(true);
		motorAxisTable.setLayoutData(gridData6);
		motorAxisTable.setLinesVisible(true);
		motorAxisTable.addSelectionListener( new SelectionListener() {

			public void widgetDefaultSelected( final SelectionEvent e ) {
				// TODO Auto-generated method stub
				
			}

			public void widgetSelected( final SelectionEvent e ) {
				IViewReference[] ref = getSite().getPage().getViewReferences();
				MotorAxisView motorAxisView = null;
				for (int i = 0; i < ref.length; ++i) {
					if (ref[i].getId().equals(MotorAxisView.ID)) {
						motorAxisView = (MotorAxisView) ref[i].getPart(false);
					}
				}
				if( motorAxisView != null ) {
					final String axisName = motorAxisTable.getSelection()[0].getText( 0 );
					Axis[] axis = currentScanModul.getAxis();
					for( int i = 0; i < axis.length; ++i ) {
						if( axis[i].getMotorAxis().getFullIdentifyer().equals( axisName ) ) {
							double stepamount = -1.0;
							
							for( int j = 0; j < axis.length; ++j ) {
								if( axis[j].isMainAxis() ) {
									stepamount = axis[j].getStepCount();
									break;
								}
							}
							motorAxisView.setAxis( axis[i], stepamount, currentScanModul );
						}
					}
					
				}
			}
			
		});

		Menu menu = new Menu(motorAxisTable);
		motorAxisRemoveMenuItem = new MenuItem(menu, SWT.MENU);
		motorAxisRemoveMenuItem.setText("Remove");
		motorAxisRemoveMenuItem.setEnabled(false);
		motorAxisTable.setMenu(menu);

		createMotorAxisCombo();
		motorAxisComposite.setLayout(gridLayout1);

		gridData6 = new GridData();
		gridData6.horizontalAlignment = GridData.FILL;
		gridData6.verticalAlignment = GridData.CENTER;
		gridData6.grabExcessHorizontalSpace = true;
		motorAxisCombo.setLayoutData( gridData6 );

		addMotorAxisButton = new Button(motorAxisComposite, SWT.NONE);
		addMotorAxisButton.setText("Add");

		gridData6 = new GridData();
		gridData6.horizontalAlignment = GridData.END;
		gridData6.verticalAlignment = GridData.CENTER;
		addMotorAxisButton.setLayoutData( gridData6 );
		
		TableColumn tableColumn2 = new TableColumn(motorAxisTable, SWT.NONE);
		tableColumn2.setWidth(250);
		tableColumn2.setText("Motor Axis");
		TableColumn tableColumn3 = new TableColumn(motorAxisTable, SWT.NONE);
		tableColumn3.setWidth(80);
		tableColumn3.setText("Stepfunction");
	}

	/**
	 * This method initializes detectorChannelComposite
	 * 
	 */
	private void createDetectorChannelComposite() {
		GridData gridData7 = new GridData();
		gridData7.horizontalSpan = 2;
		gridData7.verticalAlignment = GridData.FILL;
		gridData7.grabExcessHorizontalSpace = true;
		gridData7.grabExcessVerticalSpace = true;
		gridData7.horizontalAlignment = GridData.FILL;
		GridLayout gridLayout6 = new GridLayout();
		gridLayout6.numColumns = 2;
		detectorChannelComposite = new Composite(behaviorTabFolder, SWT.NONE);
		detectorChannelsTable = new Table(detectorChannelComposite,
				SWT.FULL_SELECTION);
		detectorChannelsTable.setHeaderVisible(true);
		detectorChannelsTable.setLayoutData(gridData7);
		detectorChannelsTable.setLinesVisible(true);
		TableColumn tableColumn4 = new TableColumn(detectorChannelsTable,
				SWT.NONE);
		tableColumn4.setWidth(250);
		tableColumn4.setText("Detector Channel");
		TableColumn tableColumn5 = new TableColumn(detectorChannelsTable,
				SWT.NONE);
		tableColumn5.setWidth(80);
		tableColumn5.setText("Average");

		Menu menu = new Menu(detectorChannelsTable);
		detectorChannelRemoveMenuItem = new MenuItem(menu, SWT.MENU);
		detectorChannelRemoveMenuItem.setText("Remove");
		detectorChannelRemoveMenuItem.setEnabled(false);
		detectorChannelsTable.setMenu(menu);
		detectorChannelsTable.addSelectionListener( new SelectionListener() {

			public void widgetDefaultSelected( final SelectionEvent e ) {
				// TODO Auto-generated method stub
				
			}

			public void widgetSelected( final SelectionEvent e ) {
				IViewReference[] ref = getSite().getPage().getViewReferences();
				DetectorChannelView detectorChannelView = null;
				for (int i = 0; i < ref.length; ++i) {
					if (ref[i].getId().equals(DetectorChannelView.ID)) {
						detectorChannelView = (DetectorChannelView) ref[i]
								.getPart(false);
					}
				}
				if( detectorChannelView != null ) {
					final String channelName = detectorChannelsTable.getSelection()[0].getText( 0 );
					Channel[] channels = currentScanModul.getChannels();
					for( int i = 0; i < channels.length; ++i ) {
						if( channels[i].getDetectorChannel().getFullIdentifyer().equals( channelName ) ) {
							detectorChannelView.setChannel( channels[i] );
						}
					}
					
				}
			}
			
		});

		createDetectorChannelCombo();
		detectorChannelComposite.setLayout(gridLayout6);

		gridData7 = new GridData();
		gridData7.horizontalAlignment = GridData.FILL;
		gridData7.verticalAlignment = GridData.CENTER;
		gridData7.grabExcessHorizontalSpace = true;
		detectorChannelCombo.setLayoutData( gridData7 );
		
		addDetectorChannelButton = new Button(detectorChannelComposite,
				SWT.NONE);
		addDetectorChannelButton.setText("Add");

		gridData7 = new GridData();
		gridData7.horizontalAlignment = GridData.END;
		gridData7.verticalAlignment = GridData.CENTER;
		addDetectorChannelButton.setLayoutData( gridData7 );
	
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
		this.currentScanModul = currentScanModul;

		this.fillFields();
	}

	private void setEnabledForAll(final boolean enabled) {
		this.triggerDelayText.setEnabled(enabled);
		this.settleTimeText.setEnabled(enabled);
		this.confirmTriggerCheckBox.setEnabled(enabled);
		this.behaviorTabFolder.setEnabled(enabled);
		this.motorAxisComposite.setEnabled(enabled);
		this.motorAxisTable.setEnabled(enabled);
		this.motorAxisCombo.setEnabled(enabled);
		this.addMotorAxisButton.setEnabled(enabled);
		this.detectorChannelComposite.setEnabled(enabled);
		this.detectorChannelsTable.setEnabled(enabled);
		this.detectorChannelCombo.setEnabled(enabled);
		this.addDetectorChannelButton.setEnabled(enabled);
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
				this.confirmTriggerCheckBox.setSelection(this.currentScanModul
						.isTriggerconfirm());
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

				this.updateMotorAxisTable();
				this.updateDetectorChannelsTable();
				this.updatePlotWindowsTable();

				this.saveMotorpositionsCombo.setText(SaveAxisPositionsTypes
						.typeToString(this.currentScanModul
								.getSaveAxisPositions()));

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

			} else {
				this.triggerDelayText.setText("");
				this.settleTimeText.setText("");
				this.confirmTriggerCheckBox.setSelection(false);
				// this.triggerEventComposite.setEvent( null );
				// this.breakEventComposite.setEvent( null );
				// this.redoEventComposite.setEvent( null );
				// this.pauseEventComposite.setEvent( null );
				this.motorAxisTable.removeAll();
				this.detectorChannelsTable.removeAll();
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
//TODO Neuer Listener
		this.saveMotorpositionsCombo.addModifyListener(this.modifyListener);
		this.addMotorAxisButton.addSelectionListener(this.selectionListener);
		this.addDetectorChannelButton
				.addSelectionListener(this.selectionListener);
		this.addPlotWindowButton.addSelectionListener(this.selectionListener);

		this.motorAxisCombo.addModifyListener(this.modifyListener);
		this.detectorChannelCombo.addModifyListener(this.modifyListener);
		this.motorAxisRemoveMenuItem
				.addSelectionListener(this.selectionListener);
		this.detectorChannelRemoveMenuItem
				.addSelectionListener(this.selectionListener);
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
							Color oldColor = triggerDelayText.getBackground();
							triggerDelayText.setBackground(new Color(oldColor
									.getDevice(), 255, 255, 255));
							oldColor.dispose();
							currentScanModul
									.setTriggerdelay(Double.NEGATIVE_INFINITY);
						} else {
							try {
								currentScanModul
										.setTriggerdelay(Double
												.parseDouble(triggerDelayText
														.getText()));
								Color oldColor = triggerDelayText
										.getBackground();
								triggerDelayText.setBackground(new Color(
										oldColor.getDevice(), 255, 255, 255));
								oldColor.dispose();
							} catch (Exception ex) {
								Color oldColor = triggerDelayText
										.getBackground();
								triggerDelayText.setBackground(new Color(
										oldColor.getDevice(), 255, 0, 0));
								oldColor.dispose();
								currentScanModul
										.setTriggerdelay(Double.NEGATIVE_INFINITY);
							}
						}

					} else if (e.widget == settleTimeText) {

						if (settleTimeText.getText().equals("")) {
							Color oldColor = settleTimeText.getBackground();
							settleTimeText.setBackground(new Color(oldColor
									.getDevice(), 255, 255, 255));
							oldColor.dispose();
							currentScanModul
									.setSettletime(Double.NEGATIVE_INFINITY);
						} else {
							try {
								currentScanModul.setSettletime(Double
										.parseDouble(settleTimeText.getText()));
								Color oldColor = settleTimeText.getBackground();
								settleTimeText.setBackground(new Color(oldColor
										.getDevice(), 255, 255, 255));
								oldColor.dispose();
							} catch (Exception ex) {
								Color oldColor = settleTimeText.getBackground();
								settleTimeText.setBackground(new Color(oldColor
										.getDevice(), 255, 0, 0));
								oldColor.dispose();
								currentScanModul
										.setSettletime(Double.NEGATIVE_INFINITY);
							}
						}
					} else if (e.widget == motorAxisCombo) {
						if (Helper.contains(motorAxisCombo.getItems(),
								motorAxisCombo.getText())
								|| motorAxisCombo.getText().equals("")) {
							Color oldColor = motorAxisCombo.getBackground();
							motorAxisCombo.setBackground(new Color(oldColor
									.getDevice(), 255, 255, 255));
							oldColor.dispose();
						} else {
							Color oldColor = motorAxisCombo.getBackground();
							motorAxisCombo.setBackground(new Color(oldColor
									.getDevice(), 255, 0, 0));
							oldColor.dispose();
						}
					} else if (e.widget == detectorChannelCombo) {
						if (Helper.contains(detectorChannelCombo.getItems(),
								detectorChannelCombo.getText())
								|| detectorChannelCombo.getText().equals("")) {
							Color oldColor = detectorChannelCombo
									.getBackground();
							detectorChannelCombo.setBackground(new Color(
									oldColor.getDevice(), 255, 255, 255));
							oldColor.dispose();
						} else {
							Color oldColor = detectorChannelCombo
									.getBackground();
							detectorChannelCombo.setBackground(new Color(
									oldColor.getDevice(), 255, 0, 0));
							oldColor.dispose();
						}
					} else if (e.widget == saveMotorpositionsCombo) {
						currentScanModul
								.setSaveAxisPositions(SaveAxisPositionsTypes
										.stringToType(saveMotorpositionsCombo
												.getText()));
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
						} else if (e.widget == addMotorAxisButton) {
							if (!motorAxisCombo.getText().equals("")) {
								final TableItem[] tableItems = motorAxisTable
										.getItems();
								for (int i = 0; i < tableItems.length; ++i) {
									if (tableItems[i].getText(0).equals(
											motorAxisCombo.getText())) {
										motorAxisTable.setSelection(i);
										return;
									}
								}
								MotorAxis motorAxis = (MotorAxis) Activator
										.getDefault().getMeasuringStation()
										.getAbstractDeviceByFullIdentifyer(
												motorAxisCombo.getText());
								if (motorAxis != null) {
									Axis axis = new Axis( currentScanModul );
									axis.setMotorAxis(motorAxis);
									currentScanModul.add(axis);
									fillFields();
								}
							}

						} else if (e.widget == addDetectorChannelButton) {
							if (!detectorChannelCombo.getText().equals("")) {
								final TableItem[] tableItems = detectorChannelsTable
										.getItems();
								for (int i = 0; i < tableItems.length; ++i) {
									if (tableItems[i].getText(0).equals(
											detectorChannelCombo.getText())) {
										detectorChannelsTable.setSelection(i);
										return;
									}
								}
								DetectorChannel detectorChannel = (DetectorChannel) Activator
										.getDefault().getMeasuringStation()
										.getAbstractDeviceByFullIdentifyer(
												detectorChannelCombo.getText());
								// System.err.println( detectorChannel );
								if (detectorChannel != null) {
									Channel channel = new Channel( currentScanModul );
									channel.setDetectorChannel(detectorChannel);
									currentScanModul.add(channel);
									fillFields();
								}
							}
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
						} else if (e.widget == motorAxisRemoveMenuItem) {
							TableItem[] selectedItems = motorAxisTable
									.getSelection();
							int[] selectedIndexes = motorAxisTable
									.getSelectionIndices();
							for (int i = 0; i < selectedItems.length; ++i) {
								currentScanModul.remove((Axis) selectedItems[i]
										.getData());
							}
							motorAxisTable.remove(selectedIndexes);
							fillFields();
						} else if (e.widget == detectorChannelRemoveMenuItem) {
							TableItem[] selectedItems = detectorChannelsTable
									.getSelection();
							int[] selectedIndexes = detectorChannelsTable
									.getSelectionIndices();
							for (int i = 0; i < selectedItems.length; ++i) {
								currentScanModul
										.remove((Channel) selectedItems[i]
												.getData());
							}
							detectorChannelsTable.remove(selectedIndexes);
							IViewReference[] ref = getSite().getPage().getViewReferences();
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
									
									plotWindow.setId( Integer.parseInt( dialog.getValue() ) );

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
						item1.setHeight(height < 150 ? 150 : height);
						System.out.println("item1: " + height);
					}
					if (item2.getExpanded()) {
						item2.setHeight(height < 150 ? 150 : height);
						System.out.println("item2: " + height);
					}
					if (item3.getExpanded()) {
						item3.setHeight(height < 150 ? 150 : height);
						System.out.println("item3: " + height);
					}
				}
			}
		};
	}

	protected void updateMotorAxisTable() {
		// TODO es sollen Listen verwendet werden, die aufgrund verschiedener Parameter eingeschränkt werden können
		// Alle Einträge der ComboBox werden gesetzt
		motorAxisCombo.setItems(motorStrings);
		this.motorAxisTable.removeAll();
		Axis[] axis = this.currentScanModul.getAxis();
		for (int i = 0; i < axis.length; ++i) {

			TableItem item = new TableItem(this.motorAxisTable, SWT.NONE);
			item.setText(new String[] {
					axis[i].getMotorAxis().getFullIdentifyer(),
					axis[i].getStepfunction() });
			item.setData(axis[i]);
			// Table Eintrag wird aus der Combo-Box entfernt
			motorAxisCombo.remove(axis[i].getMotorAxis().getFullIdentifyer());
		}
		if (this.motorAxisTable.getItems().length == 0) {
			this.motorAxisRemoveMenuItem.setEnabled(false);
		} else {
			this.motorAxisRemoveMenuItem.setEnabled(true);
		}
	}

	protected void updateDetectorChannelsTable() {
		// Alle Einträge der ComboBox werden gesetzt
		detectorChannelCombo.setItems(detectorStrings);
		this.detectorChannelsTable.removeAll();
		Channel[] channels = this.currentScanModul.getChannels();
		for (int i = 0; i < channels.length; ++i) {
			TableItem item = new TableItem(this.detectorChannelsTable, SWT.NONE);
			item.setText(new String[] {
					channels[i].getDetectorChannel().getFullIdentifyer(),
					"" + channels[i].getAverageCount() });
			item.setData(channels[i]);
			// Table Eintrag wird aus der Combo-Box entfernt
			detectorChannelCombo.remove(channels[i].getDetectorChannel().getFullIdentifyer());
		}
		if (this.detectorChannelsTable.getItems().length == 0) {
			this.detectorChannelRemoveMenuItem.setEnabled(false);
		} else {
			this.detectorChannelRemoveMenuItem.setEnabled(true);
		}
	}

	protected void updatePlotWindowsTable() {
		this.plotWindowsTable.removeAll();
		PlotWindow[] plotWindows = this.currentScanModul.getPlotWindows();
		for (int i = 0; i < plotWindows.length; ++i) {
			TableItem item = new TableItem(this.plotWindowsTable, SWT.NONE);
			item.setText(new String[] { "" + plotWindows[i].getId() });
			item.setData(plotWindows[i]);
		}
		if (this.plotWindowsTable.getItems().length == 0) {
			this.plotWindowRemoveMenuItem.setEnabled(false);
			this.plotWindowChangeIDMenuItem.setEnabled(false);
		} else {
			this.plotWindowRemoveMenuItem.setEnabled(true);
			this.plotWindowChangeIDMenuItem.setEnabled(true);
		}
	}
} // @jve:decl-index=0:visual-constraint="10,10,342,376"

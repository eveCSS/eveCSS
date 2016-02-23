package de.ptb.epics.eve.viewer.views.engineview.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Version;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.ecp1.client.interfaces.IConnectionStateListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IEngineVersionListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IErrorListener;
import de.ptb.epics.eve.ecp1.client.model.Error;
import de.ptb.epics.eve.ecp1.types.EngineStatus;
import de.ptb.epics.eve.ecp1.types.ErrorType;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.IUpdateListener;
import de.ptb.epics.eve.viewer.preferences.PreferenceConstants;
import de.ptb.epics.eve.viewer.views.engineview.ButtonManager;
import de.ptb.epics.eve.viewer.views.engineview.EngineDisconnected;
import de.ptb.epics.eve.viewer.views.engineview.EngineState;

/**
 * <code>EngineView</code>.
 * 
 * @author Hartmut Scherr
 * @author Marcus Michalsky
 */
public final class EngineView extends ViewPart implements IUpdateListener,
		IConnectionStateListener, IErrorListener, IEngineVersionListener , 
		PropertyChangeListener {
	public static final String REPEAT_COUNT_PROP = "repeatCount";

	/** the public identifier of the view */
	public static final String id = "EngineView";
	
	private static Logger logger = Logger.getLogger(EngineView.class.getName());

	private Composite top = null;
	private ScrolledComposite sc = null;

	private Label engineLabel;
	private Composite engineComposite;
	private Button startButton;
	private Button killButton;
	private Button connectButton;
	private Button disconnectButton;
	private Label statusLabel;

	private Label scanLabel;
	private Composite scanComposite;
	private Button playButton;
	private Button pauseButton;
	private Button stopButton;
	private Button skipButton;
	private Button haltButton;
	// private Button triggerButton;
	private Button autoPlayToggleButton;
	private Label repeatCountLabel;
	private Text repeatCountText;
	private Binding repeatCountBinding;
	private IObservableValue repeatCountModelObservable;
	private IObservableValue repeatCountGUIObservable;
	private ISWTObservableValue repeatCountGUIDelayedObservable;
	private ControlDecorationSupport repeatCountTextControlDecoration;

	private Label loadedScmlLabel;
	private Text loadedScmlText;

	private Label chainFilenameLabel;
	private Text filenameText;

	private Label commentLabel;
	private Text commentText;
	private Button commentSendButton;

	private ProgressBarComposite progressBarComposite;
	
	private Table statusTable;
	// Wenn nötig, wird shellTable[] im Programmablauf vergrößert
	private Shell shellTable[] = new Shell[10];

	private int repeatCount;
	
	private PropertyChangeSupport propertyChangeSupport = 
			new PropertyChangeSupport(this);
	
	DataBindingContext context = new DataBindingContext();
	
	private Image playIcon;
	private Image pauseIcon;
	private Image stopIcon;
	private Image skipIcon;
	private Image haltIcon;
	private Image autoPlayOnIcon;
	private Image autoPlayOffIcon;
	private Image triggerIcon;
	
	private Map<Integer, TableItem> chainIdItems;
	private Map<Integer, HashMap<Integer, TableItem>> scanMItemByChainId;
	
	private ButtonManager buttonManager;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite parent) {
		chainIdItems = new HashMap<Integer, TableItem>();
		scanMItemByChainId = new HashMap<Integer, HashMap<Integer, TableItem> >();

		playIcon = Activator.getDefault().getImageRegistry().get("PLAY16");
		pauseIcon = Activator.getDefault().getImageRegistry().get("PAUSE16");
		stopIcon = Activator.getDefault().getImageRegistry().get("STOP16");
		skipIcon = Activator.getDefault().getImageRegistry().get("SKIP16");
		haltIcon = Activator.getDefault().getImageRegistry().get("HALT16");
		autoPlayOnIcon = Activator.getDefault().getImageRegistry().
				get("AUTOPLAY_ON");
		autoPlayOffIcon = Activator.getDefault().getImageRegistry().
				get("AUTOPLAY_OFF");
		triggerIcon = Activator.getDefault().getImageRegistry().get("TRIGGER16");
		
		parent.setLayout(new FillLayout());
		
		this.sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		
		this.top = new Composite(sc, SWT.NONE);
		GridLayout gridLayout;
		gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		this.top.setLayout(gridLayout);
		
		sc.setContent(this.top);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		
		this.engineLabel = new Label(this.top, SWT.NONE);
		this.engineLabel.setText("ENGINE:");
		
		this.engineComposite = new Composite(this.top, SWT.NONE);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		this.engineComposite.setLayout(gridLayout);
		GridData gridData;
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 3;
		this.engineComposite.setLayoutData(gridData);
		
		this.startButton = new Button(this.engineComposite, SWT.PUSH);
		this.startButton.setText("start");
		this.startButton.setToolTipText("Start engine");
		this.startButton.addSelectionListener(new StartButtonSelectionListener());
		
		this.killButton = new Button(this.engineComposite, SWT.PUSH);
		this.killButton.setText("kill");
		this.killButton.setToolTipText("Kill engine");
		this.killButton.addSelectionListener(new KillButtonSelectionListener());
		
		this.connectButton = new Button(this.engineComposite, SWT.PUSH);
		this.connectButton.setText("connect");
		this.connectButton.setToolTipText("Connect to Engine");
		this.connectButton.addSelectionListener(
				new ConnectButtonSelectionListener());
		
		this.disconnectButton = new Button(this.engineComposite, SWT.PUSH);
		this.disconnectButton.setText("disconnect");
		this.disconnectButton.setToolTipText("Disconnect Engine");
		this.disconnectButton.addSelectionListener(
				new DisconnectButtonSelectionListener());
		
		this.statusLabel = new Label(this.engineComposite, SWT.NONE);
		this.statusLabel.setText("not connected");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 4;
		this.statusLabel.setLayoutData(gridData);
		
		this.scanLabel = new Label(this.top, SWT.NONE);
		this.scanLabel.setText("SCAN:");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		this.scanLabel.setLayoutData(gridData);
		
		this.scanComposite = new Composite(this.top, SWT.NONE);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		this.scanComposite.setLayout(gridLayout);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 3;
		this.scanComposite.setLayoutData(gridData);
		
		Composite scanButtonComposite = new Composite(this.scanComposite, SWT.NONE);
		RowLayout rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 5;
		scanButtonComposite.setLayout(rowLayout);
		
		this.playButton = new Button(scanButtonComposite, SWT.PUSH);
		this.playButton.setImage(playIcon);
		this.playButton.setToolTipText("Play");
		this.playButton.addSelectionListener(new PlayButtonSelectionListener());
		
		this.pauseButton = new Button(scanButtonComposite, SWT.PUSH);
		this.pauseButton.setImage(pauseIcon);
		this.pauseButton.setToolTipText("Pause");
		this.pauseButton.addSelectionListener(new PauseButtonSelectionListener());
		
		this.stopButton = new Button(scanButtonComposite, SWT.PUSH );
		this.stopButton.setImage(stopIcon);
		this.stopButton.setToolTipText("Stop");
		this.stopButton.addSelectionListener(new StopButtonSelectionListener());
		
		this.skipButton = new Button(scanButtonComposite, SWT.PUSH);
		this.skipButton.setImage(skipIcon);
		this.skipButton.setToolTipText("Skip");
		this.skipButton.addSelectionListener(new SkipButtonSelectionListener());
		
		this.haltButton = new Button(scanButtonComposite, SWT.PUSH);
		this.haltButton.setImage(haltIcon);
		this.haltButton.setToolTipText("Halt");
		this.haltButton.addSelectionListener(new HaltButtonSelectionListener());
		
		/*
		this.triggerButton = new Button(scanButtonComposite, SWT.PUSH);
		this.triggerButton.setImage(triggerIcon);
		this.triggerButton.setToolTipText("Manual Trigger");
		// TODO
		*/
		this.autoPlayToggleButton = new Button(scanButtonComposite, SWT.TOGGLE);
		this.autoPlayToggleButton.setImage(autoPlayOffIcon);
		this.autoPlayToggleButton.setSelection(false);
		this.autoPlayToggleButton.setToolTipText("AutoPlay is off");
		this.autoPlayToggleButton.addSelectionListener(
				new AutoPlayToggleButtonSelectionListener());
		
		this.repeatCountLabel = new Label(this.scanComposite, SWT.NONE);
		this.repeatCountLabel.setText("repeat count:");
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;
		gridData.grabExcessHorizontalSpace = true;
		this.repeatCountLabel.setLayoutData(gridData);
		this.repeatCountText = new Text(this.scanComposite, SWT.BORDER);
		this.repeatCountText.setTextLimit(5);
		gridData = new GridData();
		gridData.horizontalIndent = 7;
		gridData.widthHint = 50;
		this.repeatCountText.setLayoutData(gridData);
		repeatCount = 0;
		repeatCountText.setText("0");
		this.repeatCountText.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				repeatCountBinding.updateModelToTarget();
				repeatCountBinding.validateTargetToModel();
				repeatCountText.setSelection(0);
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				repeatCountText.selectAll();
			}
		});
		this.repeatCountText.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (e.button == 1) {
					repeatCountText.selectAll();
				}
				super.mouseDown(e);
			}
		});
		
		this.loadedScmlLabel = new Label(this.top, SWT.NONE);
		this.loadedScmlLabel.setText("loaded File:");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		this.loadedScmlLabel.setLayoutData(gridData);
		
		this.loadedScmlText = new Text(this.top, SWT.BORDER);
		this.loadedScmlText.setEditable(false);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		this.loadedScmlText.setLayoutData(gridData);
		
		this.chainFilenameLabel = new Label(this.top, SWT.NONE);
		this.chainFilenameLabel.setText("Filename:");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		this.chainFilenameLabel.setLayoutData(gridData);
		
		this.filenameText = new Text(this.top, SWT.BORDER | SWT.TRAIL);
		this.filenameText.setEditable(false);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		this.filenameText.setLayoutData(gridData);
		
		this.commentLabel = new Label(this.top, SWT.NONE);
		this.commentLabel.setText("live Comment:");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		this.commentLabel.setLayoutData(gridData);
		
		this.commentText = new Text( this.top, SWT.BORDER);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.commentText.setLayoutData(gridData);
		
		this.commentSendButton = new Button(this.top, SWT.NONE);
		this.commentSendButton.setText("Send to File");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		this.commentSendButton.setEnabled(false);
		this.commentSendButton.setLayoutData(gridData);
		this.commentSendButton.addSelectionListener(
				new CommentSendButtonSelectionListener());
		
		this.progressBarComposite = new ProgressBarComposite(top, SWT.BORDER, 
				Activator.getDefault().getEcp1Client().isRunning());
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 4;
		gridData.grabExcessHorizontalSpace = true;
		this.progressBarComposite.setLayoutData(gridData);
		
		// Tabelle für die Statuswerte erzeugen
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 4;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		
		this.statusTable = new Table(top, SWT.NONE);
		this.statusTable.setHeaderVisible(true);
		this.statusTable.setLinesVisible(true);
		this.statusTable.setLayoutData(gridData);
		TableColumn tableColumn = new TableColumn(this.statusTable, SWT.NONE);
		tableColumn.setWidth(50);
		tableColumn.setText("Chain");
		TableColumn tableColumn1 = new TableColumn(this.statusTable, SWT.NONE);
		tableColumn1.setWidth(45);
		tableColumn1.setText("SM ID");
		TableColumn tableColumn2 = new TableColumn(this.statusTable, SWT.NONE);
		tableColumn2.setWidth(100);
		tableColumn2.setText("SM Name");
		TableColumn tableColumn3 = new TableColumn(this.statusTable, SWT.NONE);
		tableColumn3.setWidth(80);
		tableColumn3.setText("Status");
		TableColumn tableColumn4 = new TableColumn(this.statusTable, SWT.NONE);
		tableColumn4.setWidth(80);
		tableColumn4.setText("Reason");
		TableColumn tableColumn5 = new TableColumn(this.statusTable, SWT.NONE);
		tableColumn5.setWidth(20);
		tableColumn5.setText("Remaining Time");
		
		// SelectionListener um zu erkennen, wann eine Zeile selektiert wird
		this.statusTable.addSelectionListener(new StatusTableSelectionListener());
		
		this.buttonManager = ButtonManager.getInstance();
		this.buttonManager.addPropertyChangeListener(
				ButtonManager.ENGINE_STATE_PROP, this);
		this.refreshButtons(EngineDisconnected.getInstance());
		
		Activator.getDefault().getEcp1Client()
				.addEngineStatusListener(progressBarComposite);
		Activator.getDefault().getEcp1Client()
				.addErrorListener(progressBarComposite);
		Activator.getDefault().getEcp1Client()
				.addConnectionStateListener(progressBarComposite);
		Activator.getDefault().getEcp1Client()
				.addChainProgressListener(progressBarComposite);
		Activator.getDefault().getEcp1Client().addEngineVersionListener(this);
		
		Activator.getDefault().getChainStatusAnalyzer().addUpdateListener(this);
		Activator.getDefault().getEcp1Client().addErrorListener(this);
//		this.rebuildText(0);
		Activator.getDefault().getEcp1Client().addConnectionStateListener(this);

		this.sc.setMinSize(this.top.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		// test whether the engine is already running
		if (Activator.getDefault().getEcp1Client().isRunning()) {
			this.setConnectionStatus(ConnectionStatus.CONNECTED);
		} else {
			this.setConnectionStatus(ConnectionStatus.DISCONNECTED);
		}
		
		this.propertyChangeSupport.addPropertyChangeListener(
				EngineView.REPEAT_COUNT_PROP, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
						Activator.getDefault().getEcp1Client()
								.getPlayListController()
								.setRepeatCount((Integer) evt.getNewValue());
			}
		});
		this.bindValues();
	}
	
	private void bindValues() {
		this.repeatCountModelObservable = BeansObservables.observeValue(
				this, EngineView.REPEAT_COUNT_PROP);
		this.repeatCountGUIObservable = SWTObservables.observeText(
				this.repeatCountText, SWT.Modify);
		UpdateValueStrategy targetToModel = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		targetToModel.setAfterGetValidator(new IValidator() {
			@Override
			public IStatus validate(Object value) {
				try {
					int intValue = Integer.parseInt((String) value);
					if (intValue < 0 || intValue > 65535) {
						return ValidationStatus
								.error("Repeat count must be in [0, 65535]");
					}
				} catch (NumberFormatException e) {
					return ValidationStatus.error("cannot parse integer", e);
				}
				return ValidationStatus.ok();
			}});
		UpdateValueStrategy modelToTarget = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		modelToTarget.setConverter(new RepeatCountModelToTargetConverter());
		this.repeatCountGUIDelayedObservable = SWTObservables.observeDelayedValue(
				500, (ISWTObservableValue) this.repeatCountGUIObservable);
		this.repeatCountBinding = context.bindValue(
				this.repeatCountGUIDelayedObservable, 
				this.repeatCountModelObservable, 
				targetToModel, 
				modelToTarget);
		this.repeatCountTextControlDecoration = ControlDecorationSupport.create(
				this.repeatCountBinding, SWT.LEFT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		this.top.setFocus();
	}

	/**
	 * 
	 * @param filename
	 */
	public void setActualFilename(final String filename) {
		// der Name des geladenen scml-Files wird angezeigt
		this.filenameText.getDisplay().syncExec(new Runnable() {
			@Override public void run() {
				filenameText.setText(filename);
				int lastSign = filename.length();
				filenameText.setSelection(lastSign);
			}
		});
	}


	/*
	 * 
	 */
	private void setCurrentRepeatCount(final int repeatCount) {
	//	if (this.repeatCount != repeatCount) {
			//this.repeatCount = repeatCount;
			this.propertyChangeSupport.firePropertyChange(
					EngineView.REPEAT_COUNT_PROP, this.repeatCount,
					this.repeatCount = repeatCount);
			/*this.repeatCountText.getDisplay().syncExec(new Runnable() {
				@Override public void run() {
					repeatCountText.setText(String.valueOf(repeatCount));
				}
			});*/
		//}
	}
	
	/*
	 * 
	 */
	private void setConnectionStatus(ConnectionStatus status) {
		final String engineHost = Activator.getDefault().getPreferenceStore().
				getString(PreferenceConstants.P_DEFAULT_ENGINE_ADDRESS);
		final Integer enginePort = Activator.getDefault().getPreferenceStore()
				.getInt(PreferenceConstants.P_DEFAULT_ENGINE_PORT);
		switch(status) {
			case CONNECTED:		// engine stuff
								this.statusLabel.setText("connected to " + 
										engineHost + ":" + 
										Integer.toString(enginePort));
								// scan stuff
								this.scanLabel.setEnabled(true);
								this.autoPlayToggleButton.setEnabled(true);
								this.repeatCountLabel.setEnabled(true);
								this.repeatCountText.setEnabled(true);
								this.repeatCountText.setText(String.valueOf(
										this.repeatCount));
								
								this.loadedScmlLabel.setEnabled(true);
								this.loadedScmlText.setEnabled(true);
								this.chainFilenameLabel.setEnabled(true);
								this.filenameText.setEnabled(true);
								this.commentLabel.setEnabled(true);
								this.commentText.setEnabled(true);
								// table stuff
								this.statusTable.setEnabled(true);
								break;
			case DISCONNECTED:	// engine stuff
								this.statusLabel.setText("not connected");
								// scan stuff
								this.scanLabel.setEnabled(false);
								this.autoPlayToggleButton.setEnabled(false);
								this.repeatCountLabel.setEnabled(false);
								this.repeatCountText.setText("");
								this.repeatCountText.setEnabled(false);
								
								this.loadedScmlLabel.setEnabled(false);
								this.loadedScmlText.setText("");
								this.loadedScmlText.setEnabled(false);
								this.chainFilenameLabel.setEnabled(false);
								this.filenameText.setText("");
								this.filenameText.setEnabled(false);
								this.commentLabel.setEnabled(false);
								this.commentText.setText("");
								this.commentText.setEnabled(false);
								this.commentSendButton.setEnabled(false);
								// engine stuff
								this.statusTable.removeAll();
								chainIdItems.clear();
								for ( int chainId : scanMItemByChainId.keySet()) {
									scanMItemByChainId.get(chainId).clear();
								}
								scanMItemByChainId.clear();
								this.statusTable.setEnabled(false);
								break;
		}
	}
	
	/*
	 * 
	 */
	private void setAutoPlay(boolean autoPlay) {
		if(autoPlay) {
			autoPlayToggleButton.setToolTipText("AutoPlay is on");
			autoPlayToggleButton.setImage(autoPlayOnIcon);
		} else {
			autoPlayToggleButton.setToolTipText("AutoPlay is off");
			autoPlayToggleButton.setImage(autoPlayOffIcon);
		}
		autoPlayToggleButton.setSelection(autoPlay);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateOccured(final int chainId, final int remainTime) {
		if (remainTime < 0) return;
		this.statusTable.getDisplay().syncExec(new Runnable() {
			@Override public void run() {
				if (chainIdItems.containsKey(chainId)){
					String humanReadableTime = "";
					if (remainTime / 3600 > 0) {
						int hours = remainTime / 3600;
						int minutes = remainTime / 60 - hours * 60;
						int seconds = remainTime % 60;
						humanReadableTime = String.format("%dh %dmin %02ds", 
							hours, minutes, seconds);
					} else {
						int minutes = remainTime / 60;
						int seconds = remainTime % 60;
						humanReadableTime = String.format(
							"%dmin %02ds", minutes, seconds);
					}
					chainIdItems.get(chainId).setText( 5, humanReadableTime);
				}
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillChainStatus(final int chainId, final String status) {
		this.statusTable.getDisplay().syncExec(new Runnable() {
			@Override public void run() {
				if (chainIdItems.containsKey(chainId)){
					chainIdItems.get(chainId).setText( 3, status);
				}
				else {
					TableItem tableItem = new TableItem( statusTable, 0 );
					chainIdItems.put(chainId, tableItem);
					tableItem.setText( 0, " "+chainId);
					tableItem.setText( 1, " ");
					tableItem.setText( 2, " ");
					tableItem.setText( 3, status);
					tableItem.setText( 4, " ");
					tableItem.setText( 5, " ");
				}
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillScanModuleStatus(final int chainId, final int scanModuleId, 
			final String status, final String reason) {
		this.statusTable.getDisplay().syncExec(new Runnable() {
			@Override public void run() {
				String scanModuleName = " ";
				if(Activator.getDefault().getCurrentScanDescription() != null){
					Chain chain = Activator.getDefault().getCurrentScanDescription().getChain(chainId);
					if (chain != null){
						ScanModule sm = chain.getScanModuleById(scanModuleId);
						if (sm != null){
							scanModuleName = sm.getName();
						}
					}
				}
				if (!scanMItemByChainId.containsKey(chainId)){
					scanMItemByChainId.put(chainId, new HashMap<Integer, TableItem>());
				}
				HashMap<Integer, TableItem> ScanModuleItems = scanMItemByChainId.get(chainId);
				if (ScanModuleItems.containsKey(scanModuleId)){
					ScanModuleItems.get(scanModuleId).setText( 3, status);
					ScanModuleItems.get(scanModuleId).setText( 4, reason);
				}
				else {
					TableItem tableItem = new TableItem( statusTable, 0 );
					ScanModuleItems.put(scanModuleId, tableItem);
					tableItem.setText( 0, " "+chainId);
					tableItem.setText( 1, " "+scanModuleId);
					tableItem.setText( 2, " "+scanModuleName); 
					tableItem.setText( 3, status);
					tableItem.setText( 4, reason);
					tableItem.setText( 5, " ");
				}
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearStatusTable() {
		// die Tabelle mit den Statusanzeigen wird geleert
		this.statusTable.getDisplay().syncExec(new Runnable() {
			@Override public void run() {
				statusTable.removeAll();
				chainIdItems.clear();
				for ( int chainId : scanMItemByChainId.keySet()) {
					scanMItemByChainId.get(chainId).clear();
				}
				scanMItemByChainId.clear();
			}
		});
	}


	/**
	 * {@inheritDoc}
	 */
	public void setLoadedScmlFile(final String filename) {
		logger.debug("loaded scml File: " + filename);
		// der Name des geladenen scml-Files wird angezeigt
		this.loadedScmlText.getDisplay().syncExec( new Runnable() {
			@Override public void run() {
				loadedScmlText.setText(filename);
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillEngineStatus(EngineStatus engineStatus, int repeatCount) {
		logger.debug(engineStatus);
		
		setCurrentRepeatCount(repeatCount);

		switch(engineStatus) {
			case IDLE_XML_LOADED:
				this.playButton.getDisplay().syncExec(new Runnable() {
					@Override public void run() {
						// alte Info-Fenster des letzten XML-Files werden gelöscht
						for ( int j=0; j<shellTable.length; j++) {
							if (shellTable[j] != null) {
								if (!shellTable[j].isDisposed()) {
									shellTable[j].dispose();
								}
							}
						}
					}
				});
				break;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAutoPlayStatus(final boolean autoPlayStatus) {
		this.autoPlayToggleButton.getDisplay().syncExec(new Runnable() {
			@Override public void run() {
				setAutoPlay(autoPlayStatus);
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void disableSendToFile() {
		this.commentSendButton.getDisplay().syncExec(new Runnable() {
			@Override public void run() {
				commentSendButton.setEnabled(false);
			}
		});
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stackConnected() {
		Display.getDefault().syncExec(new Runnable() {
			@Override public void run() {
				setConnectionStatus(ConnectionStatus.CONNECTED);
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stackDisconnected() {
		Display.getDefault().syncExec(new Runnable() {
			@Override public void run() {
				setConnectionStatus(ConnectionStatus.DISCONNECTED);
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void errorOccured(Error error) {
		if (error.getErrorType() == ErrorType.FILENAME) {
			setActualFilename(error.getText());
			// send to File wird erlaubt
			this.commentSendButton.getDisplay().syncExec( new Runnable() {
				@Override public void run() {
					commentSendButton.setEnabled(true);
				}
			});
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void engineVersionChanged(final int version, final int revision,
			final int patchlevel) {
		if (!versionMatch(new Version(version, revision, patchlevel))) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					MessageDialog.openWarning(Display.getCurrent()
							.getActiveShell(), "Version mismatch",
							"eveCSS Version "
									+ Platform.getProduct().getDefiningBundle()
											.getVersion().getMajor()
									+ "." 
									+ Platform.getProduct().getDefiningBundle()
											.getVersion().getMinor()
									+ " does not match Engine Version "
									+ version 
									+ "." 
									+ revision 
									+ " !");
				}
			});
		}
	}

	/**
	 * {@inheritDoc}
	 * @since 1.25
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals(ButtonManager.ENGINE_STATE_PROP)) {
			if (e.getNewValue() != null) {
				this.refreshButtons((EngineState)e.getNewValue());
			}
		}
	}

	private void refreshButtons(final EngineState engineState) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				startButton.setEnabled(engineState.isStart());
				killButton.setEnabled(engineState.isKill());
				connectButton.setEnabled(engineState.isConnect());
				disconnectButton.setEnabled(engineState.isDisconnect());
				
				playButton.setEnabled(engineState.isPlay());
				pauseButton.setEnabled(engineState.isPause());
				stopButton.setEnabled(engineState.isStop());
				skipButton.setEnabled(engineState.isSkip());
				haltButton.setEnabled(engineState.isHalt());
				// triggerButton.setEnabled(engineState.isTrigger());
			}
		});
	}
	
	/**
	 * 
	 * @param property
	 * @param listener
	 */
	public void addPropertyChangeListener(String property, PropertyChangeListener listener) {
		this.propertyChangeSupport.addPropertyChangeListener(property, listener);
	}
	
	/**
	 * 
	 * @param property
	 * @param listener
	 */
	public void removePropertyChangeListener(String property, PropertyChangeListener listener) {
		this.propertyChangeSupport.removePropertyChangeListener(property, listener);
	}
	
	/**
	 * {@link org.eclipse.swt.events.SelectionListener} of Start Button from
	 * <code>EngineView</code>.
	 */
	private class StartButtonSelectionListener implements SelectionListener {
		
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
			IHandlerService handlerService = (IHandlerService) 
				PlatformUI.getWorkbench().getService(IHandlerService.class);
			try {
				handlerService.executeCommand(
						"de.ptb.epics.eve.viewer.startCommand", null);
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
			}
		}
	}

	/**
	 * {@link org.eclipse.swt.events.SelectionListener} of Kill Button from
	 * <code>EngineView</code>.
	 */
	private class KillButtonSelectionListener implements SelectionListener {
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
			Activator.getDefault().getEcp1Client().getPlayController().
					shutdownEngine();
		}
	}

	/**
	 * {@link org.eclipse.swt.events.SelectionListener} of Connect Button from
	 * <code>EngineView</code>.
	 */
	private class ConnectButtonSelectionListener implements SelectionListener {
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
			if( !Activator.getDefault().getEcp1Client().isRunning()) {
				// start ecp1Client
				IHandlerService handlerService = (IHandlerService) PlatformUI.
						getWorkbench().getService(IHandlerService.class);
				try {
					handlerService.executeCommand(
						"de.ptb.epics.eve.viewer.connectCommand", null);
				} catch (Exception e2) {
					logger.error(e2.getMessage(), e2);
				}
			}
		}
	}

	/**
	 * {@link org.eclipse.swt.events.SelectionListener} of Disconnect Button 
	 * from <code>EngineView</code>.
	 */
	private class DisconnectButtonSelectionListener implements SelectionListener {
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
			if( Activator.getDefault().getEcp1Client().isRunning()) {
				// start ecp1Client
				IHandlerService handlerService = (IHandlerService) PlatformUI.
						getWorkbench().getService(IHandlerService.class);
				try {
					handlerService.executeCommand(
						"de.ptb.epics.eve.viewer.disconnectCommand", null);
				} catch (Exception e2) {
					logger.error(e2.getMessage(), e2);
				}
			}
		}
	}

	/**
	 * {@link org.eclipse.swt.events.SelectionListener} of Play Button from
	 * <code>EngineView</code>.
	 */
	private class PlayButtonSelectionListener implements SelectionListener {
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
			Activator.getDefault().getEcp1Client().getPlayController().start();
		}
	}

	/**
	 * {@link org.eclipse.swt.events.SelectionListener} of Pause Button from
	 * <code>EngineView</code>.
	 */
	private class PauseButtonSelectionListener implements SelectionListener {
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
			Activator.getDefault().getEcp1Client().getPlayController().pause();
		}
	}
		
	/**
	 * {@link org.eclipse.swt.events.SelectionListener} of Stop Button from
	 * <code>EngineView</code>.
	 */
	private class StopButtonSelectionListener implements SelectionListener {
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
			Activator.getDefault().getEcp1Client().getPlayController().stop();
		}
	}
	
	/**
	 * {@link org.eclipse.swt.events.SelectionListener} of Skip Button from
	 * <code>EngineView</code>.
	 */
	private class SkipButtonSelectionListener implements SelectionListener {
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
			Activator.getDefault().getEcp1Client().getPlayController().
					breakExecution();
		}
	}

	/**
	 * {@link org.eclipse.swt.events.SelectionListener} of Halt Button from
	 * <code>EngineView</code>.
	 */
	private class HaltButtonSelectionListener implements SelectionListener {
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
			Activator.getDefault().getEcp1Client().getPlayController().halt();
		}
	}
	
	/**
	 * 
	 */
	private class AutoPlayToggleButtonSelectionListener 
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
			setAutoPlay(autoPlayToggleButton.getSelection());
			Activator.getDefault().getEcp1Client().getPlayListController().
				setAutoplay(autoPlayToggleButton.getSelection());
		}
	}
	
	/**
	 * {@link org.eclipse.swt.events.SelectionListener}
	 *  of SendtoFile Button from <code>EngineView</code>.
	 */
	private class CommentSendButtonSelectionListener 
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
			Activator.getDefault().getEcp1Client().getPlayController().
					addLiveComment(commentText.getText());
		}
	}

	/**
	 * {@link org.eclipse.swt.events.SelectionListener}
	 * <p>
	 * Wenn eine Zeile in der Tabelle der Chains und ScanModule angeklickt 
	 * wird, öffnet sich ein separates Fenster in dem die Detail der Chain/SM 
	 * zu sehen sind. Beim nochmaligen anklicken wird das Fenster wieder 
	 * entfernt.
	 */
	private class StatusTableSelectionListener implements SelectionListener {
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
			// für die selektierte Zeile wird ein Info-Fenster angezeigt
			// mit den Details der Chain oder des ScanModuls
			int selection = statusTable.getSelectionIndex();
			
			// nachsehen ob es für diese selection schon eine shellTable gibt
			if (selection >= shellTable.length) {
				// shellTable muss vergrößert werden (auf 5 mehr als selection)
				Shell tempShell[] = new Shell[selection+5];
				System.arraycopy(shellTable, 0, tempShell, 0, shellTable.length);
				shellTable = tempShell;
			}
			
			// Überprüfen, ob Info schon da ist oder nicht.
			// Wenn ja, Info wieder wegnehmen.
			if (shellTable[selection] != null) {
				// Info ist vorhanden, da shellTable gesetzt
				
				// Wenn Info-Fenster wirklich nicht gelöscht wurde, wird es jetzt
				// gelöscht, ansonsten wird das Info-Fenster geöffnet
				if (!shellTable[selection].isDisposed()) {
					shellTable[selection].dispose();
					shellTable[selection] = null;
					return;
				}
			}
			
			final TableItem[] rows = statusTable.getItems();

			int aktChain = Integer.parseInt(rows[selection].getText(0).trim());
			int aktSM;
			if (rows[selection].getText(1).trim().equals("")) {
				aktSM = 0;
			} else {
				aktSM = Integer.parseInt(rows[selection].getText(1).trim());
			}
			
			Chain displayChain = Activator.getDefault()
					.getCurrentScanDescription().getChain(aktChain);
			
			if (aktSM > 0) {
				// ScanModule Zeile wurde ausgewählt, ScanModule Infos anzeigen
				
				Display display = Activator.getDefault().getWorkbench()
						.getDisplay();
				Shell chainShell = new Shell(display);
				chainShell.setSize(650,400);
				chainShell.setText("Scan Module Info");
				
				GridLayout gridLayout = new GridLayout();
				gridLayout.numColumns = 6;
				GridData gridData;
				
				chainShell.setLayout(gridLayout);

				// 1. Zeile wird gefüllt
				Label chainLabel = new Label(chainShell,SWT.NONE);
				chainLabel.setText("Chain ID:");
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				chainLabel.setLayoutData(gridData);
				Label chainText = new Label(chainShell,SWT.NONE);
				chainText.setText(rows[selection].getText(0));
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				chainText.setLayoutData(gridData);
				
				Label numOfMeasurementsLabel = new Label(chainShell,SWT.NONE);
				numOfMeasurementsLabel.setText("No of Measurements:");
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				numOfMeasurementsLabel.setLayoutData(gridData);
				Label numOfMeasurementsText = new Label(chainShell,SWT.NONE);
				numOfMeasurementsText.setText(""+displayChain.getScanModuleById(aktSM).getValueCount());
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				numOfMeasurementsText.setLayoutData(gridData);

				Label storageLabel = new Label(chainShell,SWT.NONE);
				storageLabel.setText("Storage:");
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				storageLabel.setLayoutData(gridData);
				Label storageText = new Label(chainShell,SWT.NONE);
				storageText.setText(""+displayChain.getScanModuleById(aktSM).getStorage());
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				storageText.setLayoutData(gridData);
				
				// 2. Zeile wird gefüllt
				Label smLabel = new Label(chainShell,SWT.NONE);
				smLabel.setText("Scan Module ID:");
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				smLabel.setLayoutData(gridData);
				Label smText = new Label(chainShell,SWT.NONE);
				smText.setText(rows[selection].getText(1));
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				smText.setLayoutData(gridData);

				Label trigDelLabel = new Label(chainShell,SWT.NONE);
				trigDelLabel.setText("Trigger Delay:");
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				trigDelLabel.setLayoutData(gridData);
				Label trigDelText = new Label(chainShell,SWT.NONE);
				trigDelText.setText(""+displayChain.getScanModuleById(aktSM).getTriggerDelay());
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				trigDelText.setLayoutData(gridData);
				
				Label confLabelMot = new Label(chainShell,SWT.NONE);
				confLabelMot.setText("Manual Trigger Motors:");
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				confLabelMot.setLayoutData(gridData);
				Label confTextMot = new Label(chainShell,SWT.NONE);
				if (displayChain.getScanModuleById(aktSM).isTriggerConfirmAxis()) {
					confTextMot.setText(" YES ");
				} else {
					confTextMot.setText(" NO ");
				}
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				confTextMot.setLayoutData(gridData);
				
				// 3. Zeile wird gefüllt
				Label smName = new Label(chainShell,SWT.NONE);
				smName.setText("Scan Module Name:");
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				smName.setLayoutData(gridData);
				Label smNameText = new Label(chainShell,SWT.NONE);
				smNameText.setText(rows[selection].getText(2));
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				smName.setLayoutData(gridData);
				
				Label settleLabel = new Label(chainShell,SWT.NONE);
				settleLabel.setText("Settle Time:");
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				settleLabel.setLayoutData(gridData);
				Label settleText = new Label(chainShell,SWT.NONE);
				settleText.setText(""+displayChain.getScanModuleById(aktSM).getSettleTime());
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				settleText.setLayoutData(gridData);
				
				Label confLabelDet = new Label(chainShell,SWT.NONE);
				confLabelDet.setText("Manual Trigger Detectors:");
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				confLabelDet.setLayoutData(gridData);
				Label confTextDet = new Label(chainShell,SWT.NONE);
				if (displayChain.getScanModuleById(aktSM).isTriggerConfirmChannel()) {
					confTextDet.setText(" YES ");
				} else {
					confTextDet.setText(" NO ");
				}
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				confTextDet.setLayoutData(gridData);

				SashForm sashForm = new SashForm(chainShell, SWT.VERTICAL);
				sashForm.SASH_WIDTH = 4;
				gridData = new GridData();
				gridData.horizontalSpan = 6;
				gridData.horizontalAlignment = GridData.FILL;
				gridData.verticalAlignment = GridData.FILL;
				gridData.grabExcessHorizontalSpace = true;
				gridData.grabExcessVerticalSpace = true;
				sashForm.setLayoutData(gridData);
				
				// Tabelle für die Motor Axes erzeugen
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				gridData.verticalAlignment = GridData.FILL;
				gridData.horizontalSpan = 2;
				gridData.grabExcessHorizontalSpace = true;
				gridData.grabExcessVerticalSpace = true;
				
				Table motTable = new Table(sashForm, SWT.NONE);
				motTable.setHeaderVisible(true);
				motTable.setLinesVisible(true);
				motTable.setLayoutData(gridData);
				TableColumn motColumn = new TableColumn(motTable, SWT.NONE);
				motColumn.setWidth(130);
				motColumn.setText("Motor Axis");
				TableColumn motColumn1 = new TableColumn(motTable, SWT.NONE);
				motColumn1.setWidth(100);
				motColumn1.setText("Function");
				TableColumn motColumn2 = new TableColumn(motTable, SWT.NONE);
				motColumn2.setWidth(160);
				motColumn2.setText("Start, Plugin, File");
				TableColumn motColumn3 = new TableColumn(motTable, SWT.NONE);
				motColumn3.setWidth(80);
				motColumn3.setText("Stop");
				TableColumn motColumn4 = new TableColumn(motTable, SWT.NONE);
				motColumn4.setWidth(80);
				motColumn4.setText("Stepwidth");
	
				Axis[] axis = displayChain.getScanModuleById(aktSM).getAxes();
				for (int i=0; i<axis.length; i++) {
					// Neuer Tabelleneintrag muß gemacht werden
					switch (axis[i].getStepfunction()) {
					case ADD:
					case MULTIPLY:
						TableItem tableItem = new TableItem( motTable, 0 );
						tableItem.setText( 0, axis[i].getAbstractDevice().getName());
						tableItem.setText( 1, axis[i].getStepfunction().toString());
						tableItem.setText( 2, axis[i].getStart().toString());
						tableItem.setText( 3, axis[i].getStop().toString());
						tableItem.setText( 4, axis[i].getStepwidth().toString());
						break;
					case FILE:
						TableItem tableItemFile = new TableItem( motTable, 0 );
						tableItemFile.setText( 0, axis[i].getAbstractDevice().getName());
						tableItemFile.setText( 1, axis[i].getStepfunction().toString());
						tableItemFile.setText( 2, axis[i].getFile().getAbsolutePath());
						break;
					case PLUGIN:
						TableItem tableItemPlug = new TableItem( motTable, 0 );
						tableItemPlug.setText( 0, axis[i].getAbstractDevice().getName());
						tableItemPlug.setText( 1, axis[i].getStepfunction().toString());
						tableItemPlug.setText( 2, axis[i].getPluginController().getPlugin().getName());
						break;
					case POSITIONLIST:
						TableItem tableItemPos = new TableItem( motTable, 0 );
						tableItemPos.setText( 0, axis[i].getAbstractDevice().getName());
						tableItemPos.setText( 1, axis[i].getStepfunction().toString());
						tableItemPos.setText( 2, axis[i].getPositionlist());
						break;
					default:
						break;
					}
				}
	
				// Tabelle für die Detector Channels erzeugen
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				gridData.verticalAlignment = GridData.FILL;
				gridData.horizontalSpan = 2;
				gridData.grabExcessHorizontalSpace = true;
				gridData.grabExcessVerticalSpace = true;
				
				Table detTable = new Table(sashForm, SWT.NONE);
				detTable.setHeaderVisible(true);
				detTable.setLinesVisible(true);
				detTable.setLayoutData(gridData);
				TableColumn detColumn = new TableColumn(detTable, SWT.NONE);
				detColumn.setWidth(130);
				detColumn.setText("Detector Channel");
				TableColumn detColumn1 = new TableColumn(detTable, SWT.NONE);
				detColumn1.setWidth(70);
				detColumn1.setText("Average");
				TableColumn detColumn2 = new TableColumn(detTable, SWT.NONE);
				detColumn2.setWidth(70);
				detColumn2.setText("Deferred");
				TableColumn detColumn3 = new TableColumn(detTable, SWT.NONE);
				detColumn3.setWidth(70);
				detColumn3.setText("Max. Dev.");
				TableColumn detColumn4 = new TableColumn(detTable, SWT.NONE);
				detColumn4.setWidth(70);
				detColumn4.setText("Minimum");
				TableColumn detColumn5 = new TableColumn(detTable, SWT.NONE);
				detColumn5.setWidth(100);
				detColumn5.setText("Max. Attempts");
				TableColumn detColumn6 = new TableColumn(detTable, SWT.NONE);
				detColumn6.setWidth(100);
				detColumn6.setText("Norm. Channel");
				
				Channel[] channels = displayChain.getScanModuleById(aktSM).getChannels();
				for (int i=0; i<channels.length; i++) {
					// Neuer Tabelleneintrag muß gemacht werden
					TableItem tableItem = new TableItem( detTable, 0 );
					tableItem.setText( 0, channels[i].getAbstractDevice().getName());
					tableItem.setText( 1, "" + channels[i].getAverageCount());
					tableItem.setText( 2, "" + channels[i].isDeferred());
					Double d = channels[i].getMaxDeviation();
					if (!d.isInfinite()) {
						tableItem.setText( 3, "" + d);
					}
					d = channels[i].getMinimum();
					if (!d.isInfinite()) {
						tableItem.setText( 4, "" + d);
					}
					if (channels[i].getMaxAttempts() != Integer.MIN_VALUE) {
						tableItem.setText( 5, "" + channels[i].getMaxAttempts());
					}
					if (channels[i].getNormalizeChannel() != null) {
						tableItem.setText( 6, "" + channels[i].getNormalizeChannel().getName());
					}
				}
				
				chainShell.open();
				shellTable[selection] = chainShell;
			} else {
				// Chain Infos anzeigen
				Display display = Activator.getDefault().getWorkbench().getDisplay();
				Shell chainShell = new Shell(display);
				chainShell.setSize(500,200);
				chainShell.setText("Chain Info");
				
				GridLayout gridLayout = new GridLayout();
				gridLayout.numColumns = 2;
				GridData gridData;
				
				chainShell.setLayout(gridLayout);
				
				Label chainLabel = new Label(chainShell,SWT.NONE);
				chainLabel.setText("Chain ID:");
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				chainLabel.setLayoutData(gridData);
				Label chainText = new Label(chainShell,SWT.NONE);
				chainText.setText(rows[selection].getText(0));
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				chainText.setLayoutData(gridData);
				
				Label descLabel = new Label(chainShell,SWT.NONE);
				descLabel.setText("Save Scan-Description:");
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				descLabel.setLayoutData(gridData);
				Label descText = new Label(chainShell,SWT.NONE);
				if (displayChain.isSaveScanDescription()) {
					descText.setText(" YES ");
				}
				else {
					descText.setText(" NO ");
				}
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				descText.setLayoutData(gridData);
	
				Label confLabel = new Label(chainShell,SWT.NONE);
				confLabel.setText("Confirm Save:");
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				confLabel.setLayoutData(gridData);
				Label confText = new Label(chainShell,SWT.NONE);
				if (displayChain.isConfirmSave()) {
					confText.setText(" YES ");
				}
				else {
					confText.setText(" NO ");
				}
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				confText.setLayoutData(gridData);
	
				Label autoincrLabel = new Label(chainShell,SWT.NONE);
				autoincrLabel.setText("Add Autoincrementing Number to Filename:");
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				autoincrLabel.setLayoutData(gridData);
				Label autoincrText = new Label(chainShell,SWT.NONE);
				if (displayChain.isAutoNumber()) {
					autoincrText.setText(" YES ");
				}
				else {
					autoincrText.setText(" NO ");
				}
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				autoincrText.setLayoutData(gridData);
	
				Label commentLabel = new Label(chainShell,SWT.NONE);
				commentLabel.setText("Comment:");
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				commentLabel.setLayoutData(gridData);
				Label commentText = new Label(chainShell,SWT.NONE);
				commentText.setText(displayChain.getComment());
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				commentText.setLayoutData(gridData);
				
				chainShell.open();
				shellTable[selection] = chainShell;
			}
		}
	}
	
	/*
	 * 
	 */
	private enum ConnectionStatus {
		CONNECTED,
		DISCONNECTED
	}

	/**
	 * @return the repeatCount
	 */
	public int getRepeatCount() {
		return repeatCount;
	}

	/**
	 * @param repeatCount the repeatCount to set
	 */
	public void setRepeatCount(int repeatCount) {
		this.propertyChangeSupport.firePropertyChange(
				EngineView.REPEAT_COUNT_PROP, this.repeatCount,
				this.repeatCount = repeatCount);
	}

	private boolean versionMatch(Version target) {
		Version platform = Platform.getProduct().getDefiningBundle()
				.getVersion();
		return platform.getMajor() == target.getMajor()
				&& platform.getMinor() == target.getMinor();
	}
}
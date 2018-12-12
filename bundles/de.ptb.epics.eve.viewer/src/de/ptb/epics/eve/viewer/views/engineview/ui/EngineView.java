package de.ptb.epics.eve.viewer.views.engineview.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Version;

import de.ptb.epics.eve.ecp1.client.interfaces.IChainStatusListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IConnectionStateListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IEngineStatusListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IEngineVersionListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IErrorListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IPlayListController;
import de.ptb.epics.eve.ecp1.client.interfaces.IPlayListListener;
import de.ptb.epics.eve.ecp1.client.model.Error;
import de.ptb.epics.eve.ecp1.commands.ChainStatusCommand;
import de.ptb.epics.eve.ecp1.types.ChainStatus;
import de.ptb.epics.eve.ecp1.types.EngineStatus;
import de.ptb.epics.eve.ecp1.types.ErrorType;
import de.ptb.epics.eve.util.net.NetUtil;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.preferences.PreferenceConstants;
import de.ptb.epics.eve.viewer.views.engineview.ButtonManager;
import de.ptb.epics.eve.viewer.views.engineview.enginestate.EngineDisconnected;
import de.ptb.epics.eve.viewer.views.engineview.enginestate.EngineState;
import de.ptb.epics.eve.viewer.views.messagesview.Levels;
import de.ptb.epics.eve.viewer.views.messagesview.ViewerMessage;

/**
 * <code>EngineView</code>.
 * 
 * @author Hartmut Scherr
 * @author Marcus Michalsky
 */
public final class EngineView extends ViewPart implements IConnectionStateListener, IErrorListener,
		IEngineVersionListener, IEngineStatusListener, IChainStatusListener, IPlayListListener, 
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
	
	private ButtonManager buttonManager;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite parent) {
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
		this.playButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean autoplayOn = Activator.getDefault().getEcp1Client().
						getPlayListController().isAutoplay();
				boolean playlistEmpty = Activator.getDefault().getEcp1Client().
						getPlayListController().getEntries().isEmpty();
				boolean repeatCountPositive = getRepeatCount() > 0;
				if (!autoplayOn && (!playlistEmpty || repeatCountPositive)) {
					StringBuilder sb = new StringBuilder();
					if (!playlistEmpty) {
						sb.append("The playlist is not empty. ");
					}
					if (repeatCountPositive) {
						sb.append("Repeat Count is > 0. ");
					}
					sb.append("But Autoplay is off! Should it be turned on?");
					boolean confirmed = MessageDialog.openConfirm(getSite().
							getShell(), "Turn Autoplay on?", sb.toString());
					if (confirmed) {
						Activator.getDefault().getEcp1Client().
							getPlayListController().setAutoplay(true);
					}
				}
				Activator.getDefault().getEcp1Client().getPlayController().
					start();
			}
		});

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
		this.autoPlayToggleButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean autoplayOn = Activator.getDefault().getEcp1Client().
						getPlayListController().isAutoplay();
				boolean playlistEmpty = Activator.getDefault().getEcp1Client().
						getPlayListController().getEntries().isEmpty();
				boolean repeatCountPositive = getRepeatCount() > 0;
				if (autoplayOn && (!playlistEmpty || repeatCountPositive)) {
					autoPlayToggleButton.setSelection(true);
					StringBuilder sb = new StringBuilder();
					if (!playlistEmpty) {
						sb.append("The playlist is not empty. ");
					}
					if (repeatCountPositive) {
						sb.append("Repeat Count is > 0. ");
					}
					sb.append("Turn Autoplay off anyway?");
					boolean confirmed = MessageDialog.openConfirm(getSite().
							getShell(), "Turn Autoplay off ?", sb.toString());
					if (!confirmed) {
						return;
					}
					autoPlayToggleButton.setSelection(false);
				}
				setAutoPlay(autoPlayToggleButton.getSelection());
				Activator.getDefault().getEcp1Client().getPlayListController().
					setAutoplay(autoPlayToggleButton.getSelection());
			}
		});
		
		this.repeatCountLabel = new Label(this.scanComposite, SWT.NONE);
		this.repeatCountLabel.setText("Repeat Count:");
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
		this.loadedScmlLabel.setText("Loaded File:");
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
		this.commentLabel.setText("Log Message:");
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
		
		StatusTableComposite statusTableComposite = 
				new StatusTableComposite(top, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 4;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		statusTableComposite.setLayoutData(gridData);

		this.buttonManager = ButtonManager.getInstance();
		this.buttonManager.addPropertyChangeListener(
				ButtonManager.ENGINE_STATE_PROP, this);
		this.refreshButtons(EngineDisconnected.getInstance());
		
		Activator.getDefault().getEcp1Client().addEngineVersionListener(this);
		
		Activator.getDefault().getEcp1Client().addEngineStatusListener(this);
		Activator.getDefault().getEcp1Client().addChainStatusListener(this);
		Activator.getDefault().getEcp1Client().getPlayListController().
				addPlayListListener(this);
		Activator.getDefault().getEcp1Client().addErrorListener(this);
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
	 */
	@Override
	public void playListHasChanged(IPlayListController playListController) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void autoPlayHasChanged(final IPlayListController playListController) {
		this.autoPlayToggleButton.getDisplay().syncExec(new Runnable() {
			@Override public void run() {
				setAutoPlay(playListController.isAutoplay());
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void chainStatusChanged(ChainStatusCommand chainStatusCommand) {
		if (ChainStatus.STORAGE_DONE.equals(chainStatusCommand.getChainStatus())) {
			this.commentSendButton.getDisplay().syncExec(new Runnable() {
				@Override public void run() {
					commentSendButton.setEnabled(false);
				}
			});
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void engineStatusChanged(EngineStatus engineStatus, 
			final String xmlName, int repeatCount) {
		if (!EngineStatus.LOADING_XML.equals(engineStatus)) {
			this.setCurrentRepeatCount(repeatCount);
			logger.debug("loaded scml File: " + xmlName);
			this.loadedScmlText.getDisplay().syncExec( new Runnable() {
				@Override public void run() {
					loadedScmlText.setText(xmlName);
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
	
	private void connect() {
		buttonManager.tryingToConnect(true);
		String engineHost = Activator.getDefault().getPreferenceStore().
				getString(PreferenceConstants.P_DEFAULT_ENGINE_ADDRESS);
		
		Integer enginePort = Activator.getDefault().getPreferenceStore()
				.getInt(PreferenceConstants.P_DEFAULT_ENGINE_PORT);
		final String message = "trying to connect to " 
				+ engineHost + ":" + enginePort;
				logger.info(message);
		Activator.getDefault().getMessageList().add(
			new ViewerMessage(Levels.INFO, message));

		final IHandlerService handlerService = (IHandlerService) PlatformUI
				.getWorkbench().getService(IHandlerService.class);
		Job job = new Job("connect") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					handlerService.executeCommand(
							"de.ptb.epics.eve.viewer.connectCommand", null);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					if (e instanceof ExecutionException) {
						buttonManager.tryingToConnect(false);
					}
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule(500);
	}
	
	private class StartButtonSelectionListener extends SelectionAdapter {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			String host = Activator.getDefault().getPreferenceStore().
					getString(PreferenceConstants.P_DEFAULT_ENGINE_ADDRESS);
			if (!NetUtil.isItMe(host)) {
				String message = "Engine could not be started on remote host (" 
						+ host 
						+ "). Change preferences to 'localhost' to use start.";
				logger.error(message);
				MessageDialog.openError(Display.getDefault().getActiveShell(), 
						"Remote Host not supported", message);
				return;
			}
			IHandlerService handlerService = (IHandlerService) 
					PlatformUI.getWorkbench().getService(IHandlerService.class);
				try {
					handlerService.executeCommand(
							"de.ptb.epics.eve.viewer.startCommand", null);
				} catch (Exception ex) {
					logger.error(ex.getMessage(), ex);
					return;
				}
			connect();
		}
	}

	private class KillButtonSelectionListener extends SelectionAdapter {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			Activator.getDefault().getEcp1Client().getPlayController().
					shutdownEngine();
		}
	}

	private class ConnectButtonSelectionListener extends SelectionAdapter {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			connect();
		}
	}

	private class DisconnectButtonSelectionListener extends SelectionAdapter {
		
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

	private class PauseButtonSelectionListener extends SelectionAdapter {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			Activator.getDefault().getEcp1Client().getPlayController().pause();
		}
	}
	
	private class StopButtonSelectionListener extends SelectionAdapter {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			Activator.getDefault().getEcp1Client().getPlayController().stop();
		}
	}
	
	private class SkipButtonSelectionListener extends SelectionAdapter {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			Activator.getDefault().getEcp1Client().getPlayController().
					breakExecution();
		}
	}

	private class HaltButtonSelectionListener extends SelectionAdapter {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			Activator.getDefault().getEcp1Client().getPlayController().halt();
		}
	}
	
	private class CommentSendButtonSelectionListener 
			extends SelectionAdapter {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			Activator.getDefault().getEcp1Client().getPlayController().
					addLiveComment(commentText.getText());
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
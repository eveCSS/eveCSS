package de.ptb.epics.eve.viewer.views;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ScanModul;
import de.ptb.epics.eve.ecp1.client.interfaces.IConnectionStateListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IErrorListener;
import de.ptb.epics.eve.ecp1.client.model.Error;
import de.ptb.epics.eve.ecp1.intern.EngineStatus;
import de.ptb.epics.eve.ecp1.intern.ErrorType;
import de.ptb.epics.eve.preferences.PreferenceConstants;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.IUpdateListener;

/**
 * A simple view implementation, which only displays a label.
 * 
 * @author Hartmut Scherr
 */
public final class EngineView extends ViewPart implements IUpdateListener, IConnectionStateListener, IErrorListener {

	private Composite top = null;

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
	private Button autoPlayOnButton;
	private Button autoPlayOffButton;
	private Label repeatCountLabel;
	private Text repeatCountText;

	private Label loadedScmlLabel;
	private Text loadedScmlText;

	private Label chainFilenameLabel;
	private Text filenameText;

	private Label commentLabel;
	private Text commentText;
	private Button commentSendButton;

	private Table statusTable;
	//TODO: ShellTable ist auf 10 Einträge vordefiniert,
	// besser eine LinkedList machen, damit beliebig viele Einträge existieren können
	private Shell shellTable[] = new Shell[10];
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl( final Composite parent ) {
		
		final Image playIcon = Activator.getDefault().getImageRegistry().get("PLAY16");
		final Image pauseIcon = Activator.getDefault().getImageRegistry().get("PAUSE16");
		final Image stopIcon = Activator.getDefault().getImageRegistry().get("STOP16");
		final Image skipIcon = Activator.getDefault().getImageRegistry().get("SKIP16");
		final Image haltIcon = Activator.getDefault().getImageRegistry().get("HALT16");
		final Image autoPlayIcon = Activator.getDefault().getImageRegistry().get("PLAYALL16");

		parent.setLayout( new FillLayout() );		

		GridLayout gridLayout;
		GridData gridData;
		
		this.top = new Composite( parent, SWT.NONE );
		gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		this.top.setLayout(gridLayout);

		this.engineLabel = new Label( this.top, SWT.NONE );
		this.engineLabel.setText("ENGINE:");

		this.engineComposite = new Composite( this.top, SWT.NONE );
		gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		this.engineComposite.setLayout(gridLayout);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 3;
		this.engineComposite.setLayoutData( gridData );
		
		this.startButton = new Button( this.engineComposite, SWT.PUSH );
		this.startButton.setText("start");
		this.startButton.setToolTipText( "Start engine" );
		this.startButton.addSelectionListener( new StartButtonSelectionListener());

		this.killButton = new Button( this.engineComposite, SWT.PUSH );
		this.killButton.setText("kill");
		this.killButton.setToolTipText( "Kill engine" );
		this.killButton.addSelectionListener( new KillButtonSelectionListener());

		this.connectButton = new Button( this.engineComposite, SWT.PUSH );
		this.connectButton.setText("connect");
		this.connectButton.setToolTipText( "Connect to Engine" );
		this.connectButton.addSelectionListener( new ConnectButtonSelectionListener());
		
		this.disconnectButton = new Button( this.engineComposite, SWT.PUSH );
		this.disconnectButton.setText("disconnect");
		this.disconnectButton.setToolTipText( "Disconnect Engine" );
		this.disconnectButton.addSelectionListener( new DisconnectButtonSelectionListener());
		
		this.statusLabel = new Label( this.engineComposite, SWT.NONE );
		this.statusLabel.setText("not connected");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 4;
		this.statusLabel.setLayoutData( gridData );

		this.scanLabel = new Label( this.top, SWT.NONE );
		this.scanLabel.setText("SCAN:");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 1;
		this.scanLabel.setLayoutData( gridData );
		
		this.scanComposite = new Composite( this.top, SWT.NONE );
		gridLayout = new GridLayout();
		gridLayout.numColumns = 9;
		this.scanComposite.setLayout(gridLayout);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 3;
		this.scanComposite.setLayoutData( gridData );

		this.playButton = new Button( this.scanComposite, SWT.PUSH );
		this.playButton.setImage(playIcon);
		this.playButton.setToolTipText( "Play" );
		this.playButton.addSelectionListener( new PlayButtonSelectionListener());

		this.pauseButton = new Button( this.scanComposite, SWT.PUSH );
		this.pauseButton.setImage(pauseIcon);
		this.pauseButton.setToolTipText( "Pause" );
		this.pauseButton.addSelectionListener( new PauseButtonSelectionListener());

		this.stopButton = new Button( this.scanComposite, SWT.PUSH );
		this.stopButton.setImage(stopIcon);
		this.stopButton.setToolTipText( "Stop" );
		this.stopButton.addSelectionListener( new StopButtonSelectionListener());

		this.skipButton = new Button( this.scanComposite, SWT.PUSH );
		this.skipButton.setImage(skipIcon);
		this.skipButton.setToolTipText( "Skip" );
		this.skipButton.addSelectionListener( new SkipButtonSelectionListener());

		this.haltButton = new Button( this.scanComposite, SWT.PUSH );
		this.haltButton.setImage(haltIcon);
		this.haltButton.setToolTipText( "Halt" );
		this.haltButton.addSelectionListener( new HaltButtonSelectionListener());

		this.autoPlayOnButton = new Button( this.scanComposite, SWT.TOGGLE );
		this.autoPlayOnButton.setImage(autoPlayIcon);
		this.autoPlayOnButton.setToolTipText( "AutoPlayOn" );
		this.autoPlayOnButton.addSelectionListener( new AutoPlayOnButtonSelectionListener());

		this.autoPlayOffButton = new Button( this.scanComposite, SWT.TOGGLE );
		this.autoPlayOffButton.setImage(autoPlayIcon);
		this.autoPlayOffButton.setToolTipText( "AutoPlayOff" );
		this.autoPlayOffButton.addSelectionListener( new AutoPlayOffButtonSelectionListener());

		this.repeatCountLabel = new Label( this.scanComposite, SWT.NONE );
		this.repeatCountLabel.setText("repeat count:");
		this.repeatCountText = new Text( this.scanComposite, SWT.BORDER );
		// TODO: sobald repeat Count funktioniert wird die Eingabe auch erlaubt
		this.repeatCountText.setEnabled(false);
		
		this.loadedScmlLabel = new Label( this.top, SWT.NONE );
		this.loadedScmlLabel.setText("loaded File:");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		this.loadedScmlLabel.setLayoutData( gridData );

		this.loadedScmlText = new Text( this.top, SWT.BORDER );
		this.loadedScmlText.setEditable( false );
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		this.loadedScmlText.setLayoutData( gridData );

		this.chainFilenameLabel = new Label( this.top, SWT.NONE );
		this.chainFilenameLabel.setText("Filename:");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		this.chainFilenameLabel.setLayoutData( gridData );

		this.filenameText = new Text( this.top, SWT.BORDER | SWT.TRAIL);
		this.filenameText.setEditable( false );
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		this.filenameText.setLayoutData( gridData );
				
		this.commentLabel = new Label( this.top, SWT.NONE );
		this.commentLabel.setText("live Comment:");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		this.commentLabel.setLayoutData( gridData );

		this.commentText = new Text( this.top, SWT.BORDER);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 1;
		this.commentText.setLayoutData( gridData );
		
		this.commentSendButton = new Button( this.top, SWT.NONE);
		this.commentSendButton.setText( "Send to File" );
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		this.commentSendButton.setEnabled(false);
		this.commentSendButton.setLayoutData( gridData );
		this.commentSendButton.addSelectionListener(new CommentSendButtonSelectionListener()); 
		
		// Tabelle für die Statuswerte erzeugen
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 10;
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
		tableColumn1.setWidth(100);
		tableColumn1.setText("Scan Module");
		TableColumn tableColumn2 = new TableColumn(this.statusTable, SWT.NONE);
		tableColumn2.setWidth(80);
		tableColumn2.setText("Status");
		TableColumn tableColumn3 = new TableColumn(this.statusTable, SWT.NONE);
		tableColumn3.setWidth(120);
		tableColumn3.setText("remaining Time");

		// SelectionListener um zu erkennen, wann eine Zeile selektiert wird
		this.statusTable.addSelectionListener(new StatusTableSelectionListener());

		Activator.getDefault().getChainStatusAnalyzer().addUpdateLisner( this );
		Activator.getDefault().getEcp1Client().addErrorListener(this);
		this.rebuildText(0);
		Activator.getDefault().getEcp1Client().addConnectionStateListener( this );

		// If Ecp1Client running (connected), enable disconnect and kill
		// else enable connect and start Button
		if (Activator.getDefault().getEcp1Client().isRunning()) {
			this.connectButton.setEnabled(false);
			this.startButton.setEnabled(false);
			this.disconnectButton.setEnabled(true);
			this.killButton.setEnabled(true);
		} else {
			this.disconnectButton.setEnabled(false);
			this.connectButton.setEnabled(true);
			this.killButton.setEnabled(false);
			this.startButton.setEnabled(true);

			// disable scan buttons if engine disconnected
			playButton.setEnabled(false);
			pauseButton.setEnabled(false);
			stopButton.setEnabled(false);
			skipButton.setEnabled(false);
			haltButton.setEnabled(false);

		}
	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {

	}

	public void updateOccured(int remainTime) {
		this.rebuildText(remainTime);
		
	}

	public void clearStatusTable() {
		// die Tabelle mit den Statusanzeigen wird geleert
		this.statusTable.getDisplay().syncExec( new Runnable() {
			public void run() {
				statusTable.removeAll();
			}
		});
	}

	public void disableSendToFile() {
		// send to File wird verboten
		this.commentSendButton.getDisplay().syncExec( new Runnable() {
			public void run() {
				commentSendButton.setEnabled(false);
			}
		});
	}
	
	public void fillStatusTable(final int chainId, final int scanModuleId, final String statString, final int remainTime) {
		// Wenn die scanModuleId -1 ist, wird eine Zeile geändert in der nur die chainId eingetragen ist

		this.statusTable.getDisplay().syncExec( new Runnable() {
			public void run() {
				
				final TableItem[] rows = statusTable.getItems();
				boolean neu = true;
				for ( int i=0; i<rows.length; i++) {
					String text0 = rows[i].getText(0).toString().trim();
					String text1 = rows[i].getText(1).toString().trim();
					int cell0 = Integer.parseInt(text0);
					int cell1;

					if (text1.equals("")) {
						// smid-Feld ist leer, cell1 wird auf -1 gesetzt
						cell1 = -1;
					}
					else {
						cell1 = Integer.parseInt(text1.trim());
					}
					
					if ( (chainId == cell0) && (scanModuleId == cell1)) {
						// neuen Wert für die Zeile eintragen
						neu = false;
						rows[i].setText(2, statString);
						if (cell1 == -1) {
							rows[i].setText(3, ""+remainTime);
						}
					}
				};
				
				if (neu) {
					// neuer Tabelleneintrag muß gemacht werden
					TableItem tableItem = new TableItem( statusTable, 0 );

					tableItem.setText( 0, " "+chainId);
					if (scanModuleId == -1) {
						tableItem.setText( 1, " ");
						tableItem.setText( 3, ""+remainTime);
					}
					else {
						tableItem.setText( 1, " "+scanModuleId);
					}
					tableItem.setText( 2, statString);
				}
			}
		});
	}
	
	private void rebuildText(int remainTime) {

		if( Activator.getDefault().getCurrentScanDescription() != null ) {
			final Iterator< Chain > it = Activator.getDefault().getCurrentScanDescription().getChains().iterator();

			while( it.hasNext() ) {
				final Chain currentChain = it.next();

				if( Activator.getDefault().getChainStatusAnalyzer().getRunningChains().contains( currentChain ) ) {
					fillStatusTable(currentChain.getId(), -1, "running", remainTime);

				} else if( Activator.getDefault().getChainStatusAnalyzer().getExitedChains().contains( currentChain ) ) {
					fillStatusTable(currentChain.getId(), -1, "exited", remainTime);

				} else {
					fillStatusTable(currentChain.getId(), -1, "idle", remainTime);

				}
				final List< ScanModul > scanModules = currentChain.getScanModuls();
				
				final List< ScanModul > running = Activator.getDefault().getChainStatusAnalyzer().getExecutingScanModules();
				for( final ScanModul scanModule : running ) {
					if( scanModules.contains( scanModule ) ) {
						fillStatusTable(currentChain.getId(), scanModule.getId(), "running", remainTime);
					}
				}
				
				final List< ScanModul > exited = Activator.getDefault().getChainStatusAnalyzer().getExitingScanModules();
				for( final ScanModul scanModule : exited ) {
					if( scanModules.contains( scanModule ) ) {
						fillStatusTable(currentChain.getId(), scanModule.getId(), "exited", remainTime);
					}
				}

				final List< ScanModul > initialized = Activator.getDefault().getChainStatusAnalyzer().getInitializingScanModules();
				for( final ScanModul scanModule : initialized ) {
					if( scanModules.contains( scanModule ) ) {
						fillStatusTable(currentChain.getId(), scanModule.getId(), "initialized", remainTime);
					}
				}
			
				final List< ScanModul > paused = Activator.getDefault().getChainStatusAnalyzer().getPausedScanModules();
				for( final ScanModul scanModule : paused ) {
					if( scanModules.contains( scanModule ) ) {
						fillStatusTable(currentChain.getId(), scanModule.getId(), "paused", remainTime);
					}
				}

				final List< ScanModul > waiting = Activator.getDefault().getChainStatusAnalyzer().getWaitingScanModules();
				for( final ScanModul scanModule : waiting ) {
					if( scanModules.contains( scanModule ) ) {
						fillStatusTable(currentChain.getId(), scanModule.getId(), "waiting for trigger", remainTime);
					}
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stackConnected() {
		this.connectButton.setEnabled(false);
		this.startButton.setEnabled(false);
		this.disconnectButton.setEnabled(true);
		this.killButton.setEnabled(true);

		// Output connected to host.
		final String engineString = de.ptb.epics.eve.preferences.Activator.getDefault().getPreferenceStore().getString( PreferenceConstants.P_DEFAULT_ENGINE_ADDRESS );
		this.statusLabel.getDisplay().syncExec( new Runnable() {
			public void run() {
				statusLabel.setText("connected to " + engineString);
			}
		});

		//TODO: Die Scan-Knöpfe können wieder erlaubt werden!
		// Wie ist der EngineStatus?
	
//		Activator.getDefault().getEcp1Client().
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stackDisconnected() {

		if (!this.loadedScmlText.isDisposed()) this.loadedScmlText.getDisplay().syncExec( new Runnable() {

			public void run() {
				if (!loadedScmlText.isDisposed()) {

					disconnectButton.setEnabled(false);
					connectButton.setEnabled(true);
					killButton.setEnabled(false);
					startButton.setEnabled(true);
					statusLabel.setText("not connected");

					// disable scan buttons if engine disconnected
					playButton.setEnabled(false);
					pauseButton.setEnabled(false);
					stopButton.setEnabled(false);
					skipButton.setEnabled(false);
					haltButton.setEnabled(false);
				}
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	public void setLoadedScmlFile(final String filename) {
		// der Name des geladenen scml-Files wird angezeigt
		this.loadedScmlText.getDisplay().syncExec( new Runnable() {
			public void run() {
				loadedScmlText.setText(filename);
			}
		});
	}

	public void setActualFilename(final String filename) {
		// der Name des geladenen scml-Files wird angezeigt
		this.filenameText.getDisplay().syncExec(new Runnable() {
			public void run() {
				filenameText.setText(filename);
			}
		});
	}

	@Override
	public void fillEngineStatus(EngineStatus engineStatus) {

		switch(engineStatus) {
			case IDLE_NO_XML_LOADED:
				this.playButton.getDisplay().syncExec( new Runnable() {
					public void run() {
						playButton.setEnabled(false);
						pauseButton.setEnabled(false);
						stopButton.setEnabled(false);
						skipButton.setEnabled(false);
						haltButton.setEnabled(false);
					}
				});
				break;
			case IDLE_XML_LOADED:
				this.playButton.getDisplay().syncExec( new Runnable() {
					public void run() {
						playButton.setEnabled(true);
						pauseButton.setEnabled(false);
						stopButton.setEnabled(false);
						skipButton.setEnabled(false);
						haltButton.setEnabled(true);
						// alte Info-Fenster des letzten XML-Files werden gelöscht
						for ( int j=0; j<10; j++) {
							if (shellTable[j] != null) {
								if (!shellTable[j].isDisposed())
									shellTable[j].dispose();
							}
						}
					}
				});
				break;
			case EXECUTING:
				this.playButton.getDisplay().syncExec( new Runnable() {
					public void run() {
						playButton.setEnabled(false);
						pauseButton.setEnabled(true);
						stopButton.setEnabled(true);
						skipButton.setEnabled(true);
						haltButton.setEnabled(true);
					}
				});
				break;
			case PAUSED:
				this.playButton.getDisplay().syncExec( new Runnable() {
					public void run() {
						playButton.setEnabled(true);
						pauseButton.setEnabled(false);
						stopButton.setEnabled(true);
						skipButton.setEnabled(true);
						haltButton.setEnabled(true);
					}
				});
				break;
			case STOPPED:
				this.playButton.getDisplay().syncExec( new Runnable() {
					public void run() {
						playButton.setEnabled(false);
						pauseButton.setEnabled(false);
						stopButton.setEnabled(false);
						skipButton.setEnabled(false);
						haltButton.setEnabled(false);
					}
				});
				break;
			case HALTED:
				this.playButton.getDisplay().syncExec( new Runnable() {
					public void run() {
						playButton.setEnabled(false);
						pauseButton.setEnabled(false);
						stopButton.setEnabled(false);
						skipButton.setEnabled(false);
						haltButton.setEnabled(false);
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
		this.autoPlayOnButton.getDisplay().syncExec( new Runnable() {
			public void run() {
				if (autoPlayStatus == true) {
					autoPlayOnButton.setEnabled(false);
					autoPlayOffButton.setEnabled(true);
				} else {
					autoPlayOnButton.setEnabled(true);
					autoPlayOffButton.setEnabled(false);
				}
			}
		});
		
	}

	// Wenn eine Zeile in der Tabelle der Chains und ScanModule angeklickt wird,
	// öffnet sich ein separates Fenster in dem die Detail der Chain/SM zu sehen sind
	// Beim nochmaligen anklicken wird das Fenster wieder entfernt.
	
	/**
	 * 
	 * @author scherr
	 *
	 */
	class StatusTableSelectionListener implements SelectionListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
			
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
			// für die selektierte Zeile wird ein Info-Fenster angezeigt
			// mit den Details der Chain oder des ScanModuls
			int selection = statusTable.getSelectionIndex();
			
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
			}
			else {
				aktSM = Integer.parseInt(rows[selection].getText(1).trim());
			}

			Chain displayChain = Activator.getDefault().getCurrentScanDescription().getChain(aktChain);
		
			if (aktSM > 0) {
				// ScanModule Zeile wurde ausgewählt, ScanModule Infos anzeigen

				Display display = Activator.getDefault().getWorkbench().getDisplay();
				Shell chainShell = new Shell(display);
				chainShell.setSize(600,400);
				chainShell.setText("Scan Module Info");
				
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
				trigDelLabel.setText("Trigger delay:");
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				trigDelLabel.setLayoutData(gridData);
				Label trigDelText = new Label(chainShell,SWT.NONE);
				trigDelText.setText(""+displayChain.getScanModulById(aktSM).getTriggerdelay());
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				trigDelText.setLayoutData(gridData);

				Label settleLabel = new Label(chainShell,SWT.NONE);
				settleLabel.setText("Settletime:");
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				settleLabel.setLayoutData(gridData);
				Label settleText = new Label(chainShell,SWT.NONE);
				settleText.setText(""+displayChain.getScanModulById(aktSM).getSettletime());
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				settleText.setLayoutData(gridData);

				Label confLabel = new Label(chainShell,SWT.NONE);
				confLabel.setText("Confirm Trigger:");
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				confLabel.setLayoutData(gridData);
				Label confText = new Label(chainShell,SWT.NONE);
				if (displayChain.getScanModulById(aktSM).isTriggerconfirm()) {
					confText.setText(" YES ");
				}
				else {
					confText.setText(" NO ");
				}
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				confText.setLayoutData(gridData);

				Label saveMotLabel = new Label(chainShell,SWT.NONE);
				saveMotLabel.setText("Save all motorpositions:");
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				saveMotLabel.setLayoutData(gridData);
				Label saveMotText = new Label(chainShell,SWT.NONE);
				saveMotText.setText(displayChain.getScanModulById(aktSM).getSaveAxisPositions().toString());
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				saveMotText.setLayoutData(gridData);
				
				// Tabelle für die Motor Axes erzeugen
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				gridData.verticalAlignment = GridData.FILL;
				gridData.horizontalSpan = 2;
				gridData.grabExcessHorizontalSpace = true;
				gridData.grabExcessVerticalSpace = true;
			    
			    Table motTable = new Table(chainShell, SWT.NONE);
			    motTable.setHeaderVisible(true);
			    motTable.setLinesVisible(true);
			    motTable.setLayoutData(gridData);
				TableColumn motColumn = new TableColumn(motTable, SWT.NONE);
				motColumn.setWidth(250);
				motColumn.setText("Motor Axis");
				TableColumn motColumn1 = new TableColumn(motTable, SWT.NONE);
				motColumn1.setWidth(100);
				motColumn1.setText("Start");
				TableColumn motColumn2 = new TableColumn(motTable, SWT.NONE);
				motColumn2.setWidth(100);
				motColumn2.setText("Stop");
				TableColumn motColumn3 = new TableColumn(motTable, SWT.NONE);
				motColumn3.setWidth(100);
				motColumn3.setText("Stepwidth");

				Axis[] axis = displayChain.getScanModulById(aktSM).getAxis();
				for (int i=0; i<axis.length; i++) {
					// Neuer Tabelleneintrag muß gemacht werden
					TableItem tableItem = new TableItem( motTable, 0 );
					tableItem.setText( 0, axis[i].getAbstractDevice().getFullIdentifyer());
					tableItem.setText( 1, axis[i].getStart());
					tableItem.setText( 2, axis[i].getStop());
					tableItem.setText( 3, axis[i].getStepwidth());
				}

				// Tabelle für die Detector Channels erzeugen
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				gridData.verticalAlignment = GridData.FILL;
				gridData.horizontalSpan = 2;
				gridData.grabExcessHorizontalSpace = true;
				gridData.grabExcessVerticalSpace = true;
			    
			    Table detTable = new Table(chainShell, SWT.NONE);
			    detTable.setHeaderVisible(true);
			    detTable.setLinesVisible(true);
			    detTable.setLayoutData(gridData);
				TableColumn detColumn = new TableColumn(detTable, SWT.NONE);
				detColumn.setWidth(250);
				detColumn.setText("Detector Channel");
				TableColumn detColumn1 = new TableColumn(detTable, SWT.NONE);
				detColumn1.setWidth(100);
				detColumn1.setText("Average");

				Channel[] channels = displayChain.getScanModulById(aktSM).getChannels();
				for (int i=0; i<channels.length; i++) {
					// Neuer Tabelleneintrag muß gemacht werden
					TableItem tableItem = new TableItem( detTable, 0 );
					tableItem.setText( 0, channels[i].getAbstractDevice().getFullIdentifyer());
					tableItem.setText( 1, "" + channels[i].getAverageCount());
				}
				
				chainShell.open();
				shellTable[selection] = chainShell;
			}
			else {
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

	@Override
	public void errorOccured(Error error) {
		if (error.getErrorType() == ErrorType.FILENAME) {
			// Aktueller Filename wird gesetzt
			setActualFilename(error.getText());
			// send to File wird erlaubt
			this.commentSendButton.getDisplay().syncExec( new Runnable() {
				public void run() {
					commentSendButton.setEnabled(true);
				}
			});
		}
	}

	///////////////////////////////////////////////////////////
	// Hier kommen jetzt die verschiedenen Listener Klassen
	///////////////////////////////////////////////////////////
	/**
	 * <code>SelectionListener</code> of Play Button from
	 * <code>EngineView</code>
	 */
	class PlayButtonSelectionListener implements SelectionListener {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected( final SelectionEvent e ) {
		}
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected( final SelectionEvent e ) {
			System.out.println("Play Knopf im Engine Window gedrückt!");
			Activator.getDefault().getEcp1Client().getPlayController().start();
		}
	}

	/**
	 * <code>SelectionListener</code> of Pause Button from
	 * <code>EngineView</code>
	 */
	class PauseButtonSelectionListener implements SelectionListener {
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
			System.out.println("Pause Knopf im Engine Window gedrückt!");
			Activator.getDefault().getEcp1Client().getPlayController().pause();
		}
	}
		
	/**
	 * <code>SelectionListener</code> of Stop Button from
	 * <code>EngineView</code>
	 */
	class StopButtonSelectionListener implements SelectionListener {
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
			System.out.println("Stop Knopf im Engine Window gedrückt!");
			Activator.getDefault().getEcp1Client().getPlayController().stop();
		}
	}
	
	/**
	 * <code>SelectionListener</code> of Skip Button from
	 * <code>EngineView</code>
	 */
	class SkipButtonSelectionListener implements SelectionListener {
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
			System.out.println("Skip Knopf im Engine Window gedrückt!");
			Activator.getDefault().getEcp1Client().getPlayController().breakExecution();
		}
	}

	/**
	 * <code>SelectionListener</code> of Halt Button from
	 * <code>EngineView</code>
	 */
	class HaltButtonSelectionListener implements SelectionListener {
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
			System.out.println("Halt Knopf im Engine Window gedrückt!");
			Activator.getDefault().getEcp1Client().getPlayController().halt();
		}
	}
	
	/**
	 * <code>SelectionListener</code> of Start Button from
	 * <code>EngineView</code>
	 */
	class StartButtonSelectionListener implements SelectionListener {
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
			// Hier muß ein Shell-Skript ausgeführt werden, dass
			// die Engine dann startet.
			MessageDialog.openWarning(null, "Warning", "Start löst noch keine Aktion aus!");
		}
	}

	/**
	 * <code>SelectionListener</code> of Kill Button from
	 * <code>EngineView</code>
	 */
	class KillButtonSelectionListener implements SelectionListener {
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
			Activator.getDefault().getEcp1Client().getPlayController().shutdownEngine();
		}
	}

	/**
	 * <code>SelectionListener</code> of Connect Button from
	 * <code>EngineView</code>
	 */
	class ConnectButtonSelectionListener implements SelectionListener {
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
				IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);
				try {
					handlerService.executeCommand("de.ptb.epics.eve.viewer.connectCommand", null);
				} catch (Exception e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			}
		}
	}

	/**
	 * <code>SelectionListener</code> of Disconnect Button from
	 * <code>EngineView</code>
	 */
	class DisconnectButtonSelectionListener implements SelectionListener {
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
				IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);
				try {
					handlerService.executeCommand("de.ptb.epics.eve.viewer.disconnectCommand", null);
				} catch (Exception e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			}
		}
	}

	/**
	 * <code>SelectionListener</code> of AutoPlayOn Button from
	 * <code>EngineView</code>
	 */
	class AutoPlayOnButtonSelectionListener implements SelectionListener {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected( final SelectionEvent e ) {
		}
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected( final SelectionEvent e ) {
			System.out.println("AutoPlayOn Knopf im Engine Window gedrückt!");
			Activator.getDefault().getEcp1Client().getPlayListController().setAutoplay(true);
		}
	}

	/**
	 * <code>SelectionListener</code> of AutoPlayOff Button from
	 * <code>EngineView</code>
	 */
	class AutoPlayOffButtonSelectionListener implements SelectionListener {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected( final SelectionEvent e ) {
		}
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected( final SelectionEvent e ) {
			System.out.println("AutoPlayOff Knopf im Engine Window gedrückt!");
			Activator.getDefault().getEcp1Client().getPlayListController().setAutoplay(false);
		}
	}
	
	/**
	 * <code>SelectionListener</code> of SendtoFile Button from
	 * <code>EngineView</code>
	 */
	class CommentSendButtonSelectionListener implements SelectionListener {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
		}
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
			Activator.getDefault().getEcp1Client().getPlayController().addLiveComment(commentText.getText());
		}
	}
	
	
}

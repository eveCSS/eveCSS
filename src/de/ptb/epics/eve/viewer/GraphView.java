/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package de.ptb.epics.eve.viewer;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.ScanModul;
import de.ptb.epics.eve.ecp1.client.interfaces.IConnectionStateListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IEngineStatusListener;
import de.ptb.epics.eve.ecp1.client.model.PlayListEntry;
import de.ptb.epics.eve.ecp1.intern.EngineStatus;
import de.ptb.epics.eve.viewer.actions.AddFileToPlayListAction;

/**
 * A simple view implementation, which only displays a label.
 * 
 * @author Sven Wende
 *
 */
public final class GraphView extends ViewPart implements IUpdateListener, IConnectionStateListener {

	private Composite top = null;

	private Button playButton;
	private Button pauseButton;
	private Button stopButton;
	private Button skipButton;
	private Button haltButton;
	private Button killButton;
	private Button triggerButton;
	private Button autoPlayOnButton;
	private Button autoPlayOffButton;

	private Label loadedScmlLabel;
	private Text loadedScmlText;

	private Label chainFilenameLabel;
	private Text filenameText;

	private Table statusTable;
	
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
		final Image killIcon = Activator.getDefault().getImageRegistry().get("KILL16");
		final Image triggerIcon = Activator.getDefault().getImageRegistry().get("TRIGGER16");
		final Image autoPlayIcon = Activator.getDefault().getImageRegistry().get("PLAYALL16");

		parent.setLayout( new FillLayout() );		

		GridLayout gridLayout;
		GridData gridData;
		
		this.top = new Composite( parent, SWT.NONE );
		gridLayout = new GridLayout();
		gridLayout.numColumns = 9;
		this.top.setLayout(gridLayout);

		this.playButton = new Button( this.top, SWT.PUSH );
		this.playButton.setImage(playIcon);
		this.playButton.setToolTipText( "Play" );
		this.playButton.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected( final SelectionEvent e ) {
			}
			public void widgetSelected( final SelectionEvent e ) {
				System.out.println("Play Knopf im Graph Window gedrückt!");
				Activator.getDefault().getEcp1Client().getPlayController().start();
			}
		});

		this.pauseButton = new Button( this.top, SWT.PUSH );
		this.pauseButton.setImage(pauseIcon);
		this.pauseButton.setToolTipText( "Pause" );
		this.pauseButton.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected( final SelectionEvent e ) {
			}
			public void widgetSelected( final SelectionEvent e ) {
				System.out.println("Pause Knopf im Graph Window gedrückt!");
				Activator.getDefault().getEcp1Client().getPlayController().pause();
			}
		});

		
		this.stopButton = new Button( this.top, SWT.PUSH );
		this.stopButton.setImage(stopIcon);
		this.stopButton.setToolTipText( "Stop" );
		this.stopButton.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected( final SelectionEvent e ) {
			}
			public void widgetSelected( final SelectionEvent e ) {
				System.out.println("Stop Knopf im Graph Window gedrückt!");
				Activator.getDefault().getEcp1Client().getPlayController().stop();
			}
		});

		this.skipButton = new Button( this.top, SWT.PUSH );
		this.skipButton.setImage(skipIcon);
		this.skipButton.setToolTipText( "Skip" );
		this.skipButton.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected( final SelectionEvent e ) {
			}
			public void widgetSelected( final SelectionEvent e ) {
				System.out.println("Skip Knopf im Graph Window gedrückt!");
				Activator.getDefault().getEcp1Client().getPlayController().breakExecution();
			}
		});
		
		this.haltButton = new Button( this.top, SWT.PUSH );
		this.haltButton.setImage(haltIcon);
		this.haltButton.setToolTipText( "Halt" );
		this.haltButton.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected( final SelectionEvent e ) {
			}
			public void widgetSelected( final SelectionEvent e ) {
				System.out.println("Halt Knopf im Graph Window gedrückt!");
				Activator.getDefault().getEcp1Client().getPlayController().halt();
			}
		});
		
		this.killButton = new Button( this.top, SWT.PUSH );
		this.killButton.setImage(killIcon);
		this.killButton.setToolTipText( "Kill" );
		this.killButton.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected( final SelectionEvent e ) {
			}
			public void widgetSelected( final SelectionEvent e ) {
				System.out.println("Kill Knopf im Graph Window gedrückt!");
				Activator.getDefault().getEcp1Client().getPlayController().shutdownEngine();
			}
		});
		
		this.triggerButton = new Button( this.top, SWT.PUSH );
		this.triggerButton.setImage(triggerIcon);
		this.triggerButton.setToolTipText( "Trigger" );
		this.triggerButton.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected( final SelectionEvent e ) {
			}
			public void widgetSelected( final SelectionEvent e ) {
				System.out.println("Trigger Knopf im Graph Window gedrückt!");
				System.out.println("   hat noch keine Verwendung");
				
			}
		});
		
		this.autoPlayOnButton = new Button( this.top, SWT.TOGGLE );
		this.autoPlayOnButton.setImage(autoPlayIcon);
		this.autoPlayOnButton.setToolTipText( "AutoPlayOn" );
		this.autoPlayOnButton.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected( final SelectionEvent e ) {
			}
			public void widgetSelected( final SelectionEvent e ) {
				System.out.println("AutoPlayOn Knopf im Graph Window gedrückt!");
				Activator.getDefault().getEcp1Client().getPlayListController().setAutoplay(true);
			}
		});

		this.autoPlayOffButton = new Button( this.top, SWT.TOGGLE );
		this.autoPlayOffButton.setImage(autoPlayIcon);
		this.autoPlayOffButton.setToolTipText( "AutoPlayOff" );
		this.autoPlayOffButton.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected( final SelectionEvent e ) {
			}
			public void widgetSelected( final SelectionEvent e ) {
				System.out.println("AutoPlayOff Knopf im Graph Window gedrückt!");
				Activator.getDefault().getEcp1Client().getPlayListController().setAutoplay(false);
			}
		});

		this.loadedScmlLabel = new Label( this.top, SWT.NONE );
		this.loadedScmlLabel.setText("loaded scml File:");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 4;
		this.loadedScmlLabel.setLayoutData( gridData );

		this.loadedScmlText = new Text( this.top, SWT.BORDER );
		this.loadedScmlText.setEditable( false );
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 5;
		this.loadedScmlText.setLayoutData( gridData );

		this.chainFilenameLabel = new Label( this.top, SWT.NONE );
		this.chainFilenameLabel.setText("Filename:");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 3;
		this.chainFilenameLabel.setLayoutData( gridData );

		this.filenameText = new Text( this.top, SWT.BORDER );
		this.filenameText.setEditable( false );
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 6;
		this.filenameText.setLayoutData( gridData );

		// Tabelle für die Statuswerte erzeugen
		
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 9;
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
		tableColumn3.setWidth(100);
		tableColumn3.setText("remaining Time");

		Activator.getDefault().getChainStatusAnalyzer().addUpdateLisner( this );
		this.rebuildText(0);
		Activator.getDefault().getEcp1Client().addConnectionStateListener( this );
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

				if (currentChain.getSaveFilename()!= null) {
					this.filenameText.getDisplay().syncExec( new Runnable() {
						public void run() {
							filenameText.setText(currentChain.getSaveFilename());
						}
					});
				}
				
				
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

	@Override
	public void stackConnected() {
		// TODO unsure if this is the correct way to do it
		ToolBarManager toolBarManager = (ToolBarManager) getViewSite().getActionBars().getToolBarManager();
		int index = toolBarManager.indexOf("de.ptb.epics.eve.viewer.connectCommand");
		if (index >= 0) toolBarManager.getControl().getItem(index).setEnabled(false);
		index = toolBarManager.indexOf("de.ptb.epics.eve.viewer.disconnectCommand");
		if (index >= 0) toolBarManager.getControl().getItem(index).setEnabled(true);
	}

	@Override
	public void stackDisconnected() {
		// TODO unsure if this is the correct way to do it
		if (!this.loadedScmlText.isDisposed()) this.loadedScmlText.getDisplay().syncExec( new Runnable() {

			public void run() {
				if (!loadedScmlText.isDisposed()) {
					ToolBarManager toolBarManager = (ToolBarManager) getViewSite().getActionBars().getToolBarManager();
					int index = toolBarManager.indexOf("de.ptb.epics.eve.viewer.connectCommand");
					if (index >= 0) toolBarManager.getControl().getItem(index).setEnabled(true);
					index = toolBarManager.indexOf("de.ptb.epics.eve.viewer.disconnectCommand");
					if (index >= 0) toolBarManager.getControl().getItem(index).setEnabled(false);
				}
			}
		});
		
	}

	public void setLoadedScmlFile(final String filename) {
		// der Name des geladenen scml-Files wird angezeigt
		this.loadedScmlText.getDisplay().syncExec( new Runnable() {
			public void run() {
				loadedScmlText.setText(filename);
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
						killButton.setEnabled(false);
						triggerButton.setEnabled(false);
					}
				});
				break;
			case IDLE_XML_LOADED:
				this.playButton.getDisplay().syncExec( new Runnable() {
					public void run() {
						playButton.setEnabled(true);
						pauseButton.setEnabled(false);
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
						killButton.setEnabled(true);
					}
				});
				break;
			case PAUSED:
				this.playButton.getDisplay().syncExec( new Runnable() {
					public void run() {
						playButton.setEnabled(true);
						pauseButton.setEnabled(false);
					}
				});
				break;
			case STOPPED:
				this.playButton.getDisplay().syncExec( new Runnable() {
					public void run() {
						playButton.setEnabled(true);
						pauseButton.setEnabled(false);
					}
				});
				break;
			case HALTED:
				this.playButton.getDisplay().syncExec( new Runnable() {
					public void run() {
						playButton.setEnabled(true);
						pauseButton.setEnabled(false);
					}
				});
				break;
		}
	}

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

}

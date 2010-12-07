package de.ptb.epics.eve.viewer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.data.scandescription.ScanModul;
import de.ptb.epics.eve.ecp1.client.interfaces.IChainStatusListener;
import de.ptb.epics.eve.ecp1.intern.ChainStatusCommand;

public class ChainStatusAnalyzer implements IChainStatusListener {

	private final List< Chain > idleChains;
	private final List< Chain > runningChains;
	private final List< Chain > exitedChains;
	private final List< ScanModul > initializingScanModules;
	private final List< ScanModul > executingScanModules;
	private final List< ScanModul > pausedScanModules;
	private final List< ScanModul > waitingScanModules;
	private final List< ScanModul > exitedScanModules;

	private final List< IUpdateListener > updateListener;
	
	public ChainStatusAnalyzer() {
		this.updateListener = new ArrayList< IUpdateListener >();

		this.idleChains = new ArrayList< Chain >();
		this.runningChains = new ArrayList< Chain >();
		this.exitedChains = new ArrayList< Chain >();
		
		this.initializingScanModules = new ArrayList< ScanModul >();
		this.executingScanModules = new ArrayList< ScanModul >();
		this.pausedScanModules = new ArrayList< ScanModul >();
		this.waitingScanModules = new ArrayList< ScanModul >();
		this.exitedScanModules = new ArrayList< ScanModul >();
		
//		this.updateListener = new ArrayList< IUpdateListener >();
	}
	
	public void chainStatusChanged( final ChainStatusCommand chainStatusCommand ) {

		System.out.println("\nChainStatusAnalyzer, Chain Stauts Changed");
		if( Activator.getDefault().getCurrentScanDescription() == null ) {
			System.out.println("   Abbruch, keine ScanDescription vorhanden");
			return;
		}
		List< Chain > chains = null;

		System.out.println("   Status: " + chainStatusCommand.getChainStatus());
		
		switch( chainStatusCommand.getChainStatus() ) {
			case IDLE:
				System.out.println("   case IDLE");
				chains = Activator.getDefault().getCurrentScanDescription().getChains();
				for( int i = 0; i < chains.size(); ++i ) {
					if( chains.get( i ).getId() == chainStatusCommand.getChainId() ) {
						this.idleChains.add( chains.get( i ) );
						this.runningChains.remove( chains.get( i ) );
						this.exitedChains.remove( chains.get( i ) );
						System.out.println("      chain " + chains.get(i).getId() + " zur idle Liste hinzugefügt");
						break;
					}
				}
				break;
				
			case STARTING_SM:
				System.out.println("   case STARTING_SM");
				chains = Activator.getDefault().getCurrentScanDescription().getChains();
				for( int i = 0; i < chains.size(); ++i ) {
					if( chains.get( i ).getId() == chainStatusCommand.getChainId() ) {
						List< ScanModul > scanModules = chains.get( i ).getScanModuls();
						for( int j = 0; j < scanModules.size(); ++j ) {
							if( scanModules.get( j ).getId() == chainStatusCommand.getScanModulId() ) {
								this.initializingScanModules.add( scanModules.get( j ) );
								this.executingScanModules.remove( scanModules.get( j ) );
								this.pausedScanModules.remove( scanModules.get( j ) );
								this.waitingScanModules.remove( scanModules.get( j ) );
								this.exitedScanModules.remove( scanModules.get( j ) );
								System.out.println("      scanModule " + scanModules.get(j).getId() + " zur initialize Liste hinzugefügt");
							}
						}
					}
				}
				break;
				
			case EXECUTING_SM:
				System.out.println("   case EXECUTING_SM");
				chains = Activator.getDefault().getCurrentScanDescription().getChains();
				for( int i = 0; i < chains.size(); ++i ) {
					if( chains.get( i ).getId() == chainStatusCommand.getChainId() ) {
						List< ScanModul > scanModules = chains.get( i ).getScanModuls();
						for( int j = 0; j < scanModules.size(); ++j ) {
							if( scanModules.get( j ).getId() == chainStatusCommand.getScanModulId() ) {
								this.initializingScanModules.remove( scanModules.get( j ) );
								this.executingScanModules.add( scanModules.get( j ) );
								this.pausedScanModules.remove( scanModules.get( j ) );
								this.waitingScanModules.remove( scanModules.get( j ) );
								this.exitedScanModules.remove( scanModules.get( j ) );
								System.out.println("      scanModule " + scanModules.get(j).getId() + " zur executing Liste hinzugefügt");
								
								this.idleChains.remove( scanModules.get( j ).getChain() );
								if (!this.runningChains.contains(scanModules.get( j ).getChain())) {
//									System.out.println("   chain i noch nicht in der Liste vorhanden");

									this.runningChains.add( scanModules.get( j ).getChain() );
									System.out.println("      chain " + scanModules.get(j).getChain().getId() + " zur running Liste hinzugefügt");
								};
//								this.runningChains.add( scanModules.get( j ).getChain() );
//								System.out.println("      chain " + i + " zur running Liste hinzugefügt");
							}
//							final PlotWindow[] plotWindows = scanModules.get( j ).getPlotWindows();
//							for( int k = 0; k < plotWindows.length; ++k ) {
//								System.out.println( "Verarbeite Plot Fenster: " + plotWindows[i].getId() );
//								final PlotWindow plotWindow = plotWindows[k];
//								Activator.getDefault().getWorkbench().getDisplay().syncExec( new Runnable() {
//
//									public void run() {
//										
//										IViewReference[] ref = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().getActivePart().getSite().getPage().getViewReferences();
//										PlotView view = null;
//										for( int l = 0; l < ref.length; ++l ) {
//											if( ref[l].getId().equals( PlotView.ID ) ) {
//												view = (PlotView)ref[l].getPart( false );
//												if( view.getId() == -1 ) {
//													view.setId( plotWindow.getId() );
//													view.setPlotWindow( plotWindow );
//													break;
//												} else if( view.getId() == plotWindow.getId() ) {
//													view.setPlotWindow( plotWindow );
//													break;
//												}
//												view = null;
//											}
//											if( view == null ) {
//												// Neuen View erzeugen
//											}
//										}
//										
//									}
//									
//								});
//								
//							}
						}
					}
				}
				break;
				
			case SM_PAUSED:
				System.out.println("   case SM_PAUSED");
				chains = Activator.getDefault().getCurrentScanDescription().getChains();
				for( int i = 0; i < chains.size(); ++i ) {
					if( chains.get( i ).getId() == chainStatusCommand.getChainId() ) {
						List< ScanModul > scanModules = chains.get( i ).getScanModuls();
						for( int j = 0; j < scanModules.size(); ++j ) {
							if( scanModules.get( j ).getId() == chainStatusCommand.getScanModulId() ) {
								this.initializingScanModules.remove( scanModules.get( j ) );
								this.executingScanModules.remove( scanModules.get( j ) );
								this.pausedScanModules.add( scanModules.get( j ) );
								this.waitingScanModules.remove( scanModules.get( j ) );
								this.exitedScanModules.remove( scanModules.get( j ) );
								System.out.println("      scanModule " + scanModules.get(j).getId() + " zur paused Liste hinzugefügt");
								
								this.idleChains.add( scanModules.get( j ).getChain() );
								this.runningChains.remove( scanModules.get( j ).getChain() );
								System.out.println("      chain " + scanModules.get(j).getChain().getId() + " zur idle Liste hinzugefügt");
							}
						}
					}
				}
				break;
				
			case WAITING_FOR_MANUAL_TRIGGER:
				System.out.println("   case WAITING_FOR_MANUAL_TRIGGER");
				chains = Activator.getDefault().getCurrentScanDescription().getChains();
				for( int i = 0; i < chains.size(); ++i ) {
					if( chains.get( i ).getId() == chainStatusCommand.getChainId() ) {
						List< ScanModul > scanModules = chains.get( i ).getScanModuls();
						for( int j = 0; j < scanModules.size(); ++j ) {
							if( scanModules.get( j ).getId() == chainStatusCommand.getScanModulId() ) {
								this.initializingScanModules.remove( scanModules.get( j ) );
								this.executingScanModules.remove( scanModules.get( j ) );
								this.pausedScanModules.remove( scanModules.get( j ) );
								this.waitingScanModules.add( scanModules.get( j ) );
								this.exitedScanModules.add( scanModules.get( j ) );
								System.out.println("      scanModule " + scanModules.get(j).getId() + " zur trigger Liste hinzugefügt");
								
								this.idleChains.add( scanModules.get( j ).getChain() );
								this.runningChains.remove( scanModules.get( j ).getChain() );
								System.out.println("      chain " + scanModules.get(j).getChain().getId() + " zur idle Liste hinzugefügt");
							}
						}
					}
				}
				break;
				
			case EXITING_SM:
				System.out.println("   case EXITING_SM");
				chains = Activator.getDefault().getCurrentScanDescription().getChains();
				for( int i = 0; i < chains.size(); ++i ) {
					if( chains.get( i ).getId() == chainStatusCommand.getChainId() ) {
						List< ScanModul > scanModules = chains.get( i ).getScanModuls();
						for( int j = 0; j < scanModules.size(); ++j ) {
							if( scanModules.get( j ).getId() == chainStatusCommand.getScanModulId() ) {
								this.initializingScanModules.remove( scanModules.get( j ) );
								this.executingScanModules.remove( scanModules.get( j ) );
								this.pausedScanModules.remove( scanModules.get( j ) );
								this.waitingScanModules.remove( scanModules.get( j ) );
								this.exitedScanModules.add( scanModules.get( j ) );
								System.out.println("      scanModule " + scanModules.get(j).getId() + " zur exit Liste hinzugefügt");
								
								// Dadurch das ein SM beendet ist, ist nicht auch die chain beendet
								// this.idleChains.add( scanModules.get( j ).getChain() );
								// this.runningChains.remove( scanModules.get( j ).getChain() );
							}
						}
					}
				}
				break;
				
			case EXITING_CHAIN:
				System.out.println("   case EXITING_CHAIN");
				chains = Activator.getDefault().getCurrentScanDescription().getChains();
				for( int i = 0; i < chains.size(); ++i ) {
					if( chains.get( i ).getId() == chainStatusCommand.getChainId() ) {
						this.idleChains.remove( chains.get( i ) );
						this.runningChains.remove( chains.get( i ) );
						this.exitedChains.add( chains.get( i ) );
						System.out.println("      chain " + chains.get(i).getId() + " zur exit Liste hinzugefügt");
					}
				}
				break;

			case STORAGE_DONE:
				System.out.println("   case STORAGE_DONE");
				chains = Activator.getDefault().getCurrentScanDescription().getChains();
				for( int i = 0; i < chains.size(); ++i ) {
					if( chains.get( i ).getId() == chainStatusCommand.getChainId() ) {
						System.out.println("      Filename der Chain:");
						System.out.println("      Name: " + chains.get(i).getSaveFilename());
					}
				}
				break;
				
		}
		final Iterator< IUpdateListener > it = this.updateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateOccured();
		}

	}

	public boolean addUpdateLisner( final IUpdateListener updateListener ) {
		return this.updateListener.add( updateListener );
	}

	public boolean remove( final IUpdateListener updateListener ) {
		return this.updateListener.remove( updateListener );
	}
	
	
	public List< Chain > getIdleChains() {
		return new ArrayList< Chain >( this.idleChains );
	}
	
	public List< Chain > getRunningChains() {
		return new ArrayList< Chain >( this.runningChains );
	}

	public List< Chain > getExitedChains() {
		return new ArrayList< Chain >( this.exitedChains );
	}
	
	public List< ScanModul > getInitializingScanModules() {
		return new ArrayList< ScanModul >( this.initializingScanModules );
	}
	
	public List< ScanModul > getExecutingScanModules() {
		return new ArrayList< ScanModul >( this.executingScanModules );
	}
	
	public List< ScanModul > getPausedScanModules() {
		return new ArrayList< ScanModul >( this.pausedScanModules );
	}
	
	public List< ScanModul > getWaitingScanModules() {
		return new ArrayList< ScanModul >( this.waitingScanModules );
	}

	public List< ScanModul > getExitingScanModules() {
		return new ArrayList< ScanModul >( this.exitedScanModules );
	}

	public void reset() {
		this.idleChains.clear();
		this.runningChains.clear();
		this.exitedChains.clear();

		this.executingScanModules.clear();
		this.initializingScanModules.clear();
		this.pausedScanModules.clear();
		this.waitingScanModules.clear();
		this.exitedScanModules.clear();

		System.out.println("\n RRR EEE SSS EEE TTT der ChainStatusListen");
		// Hier muß auch die Tabelle im GraphikView Bereich geleert werden
//		clearStatusTable(); Wie macht man das?

		final Iterator< IUpdateListener > it = this.updateListener.iterator();
		while( it.hasNext() ) {
			it.next().clearStatusTable();
		}

		
	}

}

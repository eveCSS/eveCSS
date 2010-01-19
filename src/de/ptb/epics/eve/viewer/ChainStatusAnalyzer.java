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
	private final List< ScanModul > initializingScanModules;
	private final List< ScanModul > executingScanModules;
	private final List< ScanModul > pausedScanModules;
	private final List< ScanModul > waitingScanModules;
	
	private final List< IUpdateListener > updateListener;
	
	public ChainStatusAnalyzer() {
		this.idleChains = new ArrayList< Chain >();
		this.runningChains = new ArrayList< Chain >();
		
		this.initializingScanModules = new ArrayList< ScanModul >();
		this.executingScanModules = new ArrayList< ScanModul >();
		this.pausedScanModules = new ArrayList< ScanModul >();
		this.waitingScanModules = new ArrayList< ScanModul >();
		
		this.updateListener = new ArrayList< IUpdateListener >();
	}
	
	public void chainStatusChanged( final ChainStatusCommand chainStatusCommand ) {
		if( Activator.getDefault().getCurrentScanDescription() == null ) {
			return;
		}
		List< Chain > chains = null;
		switch( chainStatusCommand.getChainStatus() ) {
			case IDLE:
				chains = Activator.getDefault().getCurrentScanDescription().getChains();
				for( int i = 0; i < chains.size(); ++i ) {
					if( chains.get( i ).getId() == chainStatusCommand.getChainId() ) {
						this.idleChains.add( chains.get( i ) );
						this.runningChains.remove( chains.get( i ) );
						break;
					}
				}
				break;
				
			case STARTING_SM:
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
							}
						}
					}
				}
				break;
				
			case EXECUTING_SM:
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
							}
							final PlotWindow[] plotWindows = scanModules.get( j ).getPlotWindows();
							for( int k = 0; k < plotWindows.length; ++k ) {
								System.out.println( "Verarbeite Plot Fenster: " + plotWindows[i].getId() );
								final PlotWindow plotWindow = plotWindows[k];
								Activator.getDefault().getWorkbench().getDisplay().syncExec( new Runnable() {

									public void run() {
										
										IViewReference[] ref = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().getActivePart().getSite().getPage().getViewReferences();
										PlotView view = null;
										for( int l = 0; l < ref.length; ++l ) {
											if( ref[l].getId().equals( PlotView.ID ) ) {
												view = (PlotView)ref[l].getPart( false );
												if( view.getId() == -1 ) {
													view.setId( plotWindow.getId() );
													view.setPlotWindow( plotWindow );
													break;
												} else if( view.getId() == plotWindow.getId() ) {
													view.setPlotWindow( plotWindow );
													break;
												}
												view = null;
											}
											if( view == null ) {
												// Neuen View erzeugen
											}
										}
										
									}
									
								});
								
							}
						}
					}
				}
				break;
				
			case SM_PAUSED:
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
								
							}
						}
					}
				}
				break;
				
			case WAITING_FOR_MANUAL_TRIGGER:
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
							}
						}
					}
				}
				break;
				
			case EXITING_SM:
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
							}
						}
					}
				}
				break;
				
			case EXITING_CHAIN:
				chains = Activator.getDefault().getCurrentScanDescription().getChains();
				for( int i = 0; i < chains.size(); ++i ) {
					if( chains.get( i ).getId() == chainStatusCommand.getChainId() ) {
						this.idleChains.remove( chains.get( i ) );
						this.runningChains.remove( chains.get( i ) );
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
	

}

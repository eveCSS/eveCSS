package de.ptb.epics.eve.viewer;

import java.util.HashMap;

import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.ecp1.client.interfaces.IChainStatusListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IConnectionStateListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IEngineStatusListener;
import de.ptb.epics.eve.ecp1.intern.ChainStatus;
import de.ptb.epics.eve.ecp1.intern.ChainStatusCommand;
import de.ptb.epics.eve.ecp1.intern.EngineStatus;
import de.ptb.epics.eve.viewer.views.PlotView;

/**
 * 
 * @author ?
 *
 */
public class PlotViewDispatcher implements 
		IEngineStatusListener, IChainStatusListener, IConnectionStateListener {

	private ScanDescription scanDescription;
	private EngineStatus engineStatus;
	private boolean dispatchDelayed;
	private int chid;
	private int smid;
	
	/**
	 * Constructs a <code>PlotViewDispatcher</code>.
	 */
	public PlotViewDispatcher(){
		scanDescription = null;
		Activator.getDefault().getEcp1Client().addChainStatusListener(this);
		Activator.getDefault().getEcp1Client().addEngineStatusListener(this);
		//engineStatus = EngineStatus.EXECUTING;
	}

	/**
	 * 
	 * @param scanDescription
	 */
	public void setScanDescription(ScanDescription scanDescription){
		this.scanDescription = scanDescription;
		if (dispatchDelayed && (engineStatus == EngineStatus.EXECUTING)){
			dispatchDelayed = false;
			Activator.getDefault().getWorkbench().getDisplay().syncExec( new Runnable() {

				public void run() {
					doDispatch(chid, smid);
				}
			});				
		}
	}

	/*
	 * called by setScanDescription
	 */
	private void doDispatch(int chid, int smid){

		HashMap<Integer, PlotWindow> windowIdMap = new HashMap<Integer, PlotWindow>();
		
		if (scanDescription == null) return;
		Chain chain = scanDescription.getChain(chid);
		if (chain == null) return;
		
		ScanModule sm = chain.getScanModulById(smid);
		if (sm == null) return;
		
		PlotWindow[] plotWindows = sm.getPlotWindows();
		for (PlotWindow plotWindow : plotWindows) {
			windowIdMap.put(plotWindow.getId(), plotWindow);
			System.out.println("Found plotWindow with id " + plotWindow.getId());
		}
		
		// create a new plotView for all remaining windowIds without corresponding plotView
		for (Integer windowId : windowIdMap.keySet()) {
			IViewReference viewRef = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().getActivePart().getSite().getPage().findViewReference(PlotView.ID, windowId.toString());
			if (viewRef == null) {
				// TODO create new plotView with secondary id windowI
				try {
					System.out.println("opening plot with secondary id "+windowId.toString());
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(PlotView.ID, windowId.toString(), IWorkbenchPage.VIEW_ACTIVATE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		IViewReference[] ref = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().getActivePart().getSite().getPage().getViewReferences();
		for (IViewReference iViewRef : ref) {
			int plotViewId = 0;
			if( iViewRef.getId().equals( PlotView.ID ) ) {
				PlotView plotView = (PlotView)iViewRef.getPart( true );
				if (plotView == null) System.err.println("PlotView is null");
				try {
					if ((plotView.getViewSite()!= null) && (plotView.getViewSite().getSecondaryId() != null))
						plotViewId = Integer.parseInt(plotView.getViewSite().getSecondaryId());
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					return;
				}
				System.out.println("PlotView with windowId " + plotViewId);
				if (windowIdMap.containsKey(plotViewId)) {
					plotView.setPlotWindow(windowIdMap.get(plotViewId), chid, smid);
					windowIdMap.remove(plotViewId);
				}
				else {
					// TODO dispose unused plotView
					// we leave this to the user for now
				}
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void engineStatusChanged(EngineStatus engineStatus, String xmlName, int repeatCount) {
		this.engineStatus = engineStatus;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void chainStatusChanged(ChainStatusCommand chainStatusCommand) {
		
		this.chid = chainStatusCommand.getChainId();
		this.smid = chainStatusCommand.getScanModulId();
		if (chainStatusCommand.getChainStatus() == ChainStatus.EXECUTING_SM) {
			// If we connect to a running engine, we wait for the current 
			//scanDescription to arrive
			if (scanDescription == null) {
				dispatchDelayed = true;
			}
			else {
				dispatchDelayed = false;
				Activator.getDefault().getWorkbench().getDisplay().syncExec( 
					new Runnable() {

						public void run() {
							doDispatch(chid, smid);
						}
				});				
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stackConnected() {
		// make sure we start with a fresh scanDescription
		scanDescription = null;
		dispatchDelayed = false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stackDisconnected() {
		// invalidate scanDescription
		scanDescription = null;				
		dispatchDelayed = false;
	}
}
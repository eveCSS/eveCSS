package de.ptb.epics.eve.viewer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Set;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.ecp1.client.interfaces.IChainStatusListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IChainProgressListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IConnectionStateListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IEngineStatusListener;
import de.ptb.epics.eve.ecp1.commands.ChainStatusCommand;
import de.ptb.epics.eve.ecp1.commands.ChainProgressCommand;
import de.ptb.epics.eve.ecp1.types.ChainStatus;
import de.ptb.epics.eve.ecp1.types.EngineStatus;
import de.ptb.epics.eve.ecp1.types.ScanModuleReason;
import de.ptb.epics.eve.ecp1.types.ScanModuleStatus;

public class ChainStatusAnalyzer implements IEngineStatusListener,
IConnectionStateListener, IChainStatusListener, IChainProgressListener {

	private static Logger logger = Logger.getLogger(ChainStatusAnalyzer.class.getName());

	private HashMap<Integer, ChainStatusCommand> LastChainStatus;

	private final List<IUpdateListener> updateListener;

	public ChainStatusAnalyzer() {

		LastChainStatus = new HashMap<Integer, ChainStatusCommand>();

		this.updateListener = new ArrayList<IUpdateListener>();

	}

	@Override
	public void engineStatusChanged(EngineStatus engineStatus, String xmlName,
			int repeatCount) {
		logger.debug(engineStatus);
		
		if (engineStatus == EngineStatus.LOADING_XML) {
			for (IUpdateListener iul : this.updateListener) {
				iul.clearStatusTable();
			}

		} else  {
			for (IUpdateListener iul : this.updateListener) {
				iul.setLoadedScmlFile(xmlName);
				iul.fillEngineStatus(engineStatus, repeatCount);
			}
		}
	}

	public void setAutoPlayStatus(boolean autoPlayStatus) {

		final Iterator<IUpdateListener> it = this.updateListener.iterator();
		while (it.hasNext()) {
			it.next().setAutoPlayStatus(autoPlayStatus);
		}

	}

	public void chainProgressChanged(final ChainProgressCommand chainProgressCommand) {
		for (IUpdateListener iul : this.updateListener) {
			iul.updateOccured(chainProgressCommand.getChainId(), 
					chainProgressCommand.getRemainingTime());
		}
	}
	
	public void chainStatusChanged(final ChainStatusCommand chainStatusCommand) {
		
		logger.debug(chainStatusCommand.getChainStatus());

		int chid = chainStatusCommand.getChainId();
		ChainStatusCommand csd = chainStatusCommand;
		
		if (LastChainStatus.containsKey(chid)) {
			ChainStatusCommand oldStatus = LastChainStatus.get(chid);
			Set<Integer> oldSMIds = oldStatus.getAllScanModuleIds();
			for (IUpdateListener iul : this.updateListener) {
				if (oldStatus.getChainStatus() != csd.getChainStatus()) 
					iul.fillChainStatus(chid, ChainStatus.toString(csd.getChainStatus()));
				for (int smid : csd.getAllScanModuleIds()) {
					if (!(oldSMIds.contains(smid) && (csd.getScanModuleStatus(smid) == oldStatus.getScanModuleStatus(smid)) 
							&& csd.getScanModuleReason(smid) == oldStatus.getScanModuleReason(smid))){
						iul.fillScanModuleStatus(chid, smid, ScanModuleStatus.toString(csd.getScanModuleStatus(smid)),
								ScanModuleReason.toString(csd.getScanModuleReason(smid)));
					}
				}
			}
		}
		else {
			for (IUpdateListener iul : this.updateListener) {
				iul.fillChainStatus(chid, ChainStatus.toString(csd.getChainStatus()));
				for (int smid : csd.getAllScanModuleIds()) {
					iul.fillScanModuleStatus(chid, smid, ScanModuleStatus.toString(csd.getScanModuleStatus(smid)),
							ScanModuleReason.toString(csd.getScanModuleReason(smid)));
				}
			}
		}
		LastChainStatus.put(chid, chainStatusCommand);
		
		if (csd.getChainStatus() == ChainStatus.STORAGE_DONE){
			for (IUpdateListener iul : this.updateListener) {
				iul.disableSendToFile();
			}
		}

		
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stackConnected() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stackDisconnected() {
		LastChainStatus.clear();
	}


	public boolean addUpdateListener(final IUpdateListener updateListener) {
		return this.updateListener.add(updateListener);
	}

	public boolean removeUpdateListener(final IUpdateListener updateListener) {
		return this.updateListener.remove(updateListener);
	}

}

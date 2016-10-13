package de.ptb.epics.eve.viewer.views.engineview.statustable;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.ecp1.client.interfaces.IChainProgressListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IChainStatusListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IConnectionStateListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IEngineStatusListener;
import de.ptb.epics.eve.ecp1.commands.ChainProgressCommand;
import de.ptb.epics.eve.ecp1.commands.ChainStatusCommand;
import de.ptb.epics.eve.ecp1.types.EngineStatus;

/**
 * Keeps track of progress of running scans by maintaining a ScanInfo.
 *
 * @author Marcus Michalsky
 * @since 1.26
 */
public class StatusTracker implements IConnectionStateListener, 
		IEngineStatusListener, IChainProgressListener, IChainStatusListener {
	private static final Logger LOGGER = Logger.getLogger(
			StatusTracker.class.getName());
	
	public static final String SCAN_INFO_PROPERTY = "scanInfo";
	public static final String CHAIN_INFO_PROPERTY = "chainInfo";
	private PropertyChangeSupport propertyChangeSupport;
	
	private ScanInfo scanInfo;
	
	public StatusTracker() {
		this.scanInfo = new ScanInfo();
		this.propertyChangeSupport = new PropertyChangeSupport(this);
	}

	/**
	 * @return the scanInfo
	 */
	public ScanInfo getScanInfo() {
		return scanInfo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void chainProgressChanged(ChainProgressCommand chainProgressCommand) {
		ChainInfo chainInfo = this.scanInfo.getChainInfo(
				chainProgressCommand.getChainId());
		if (chainInfo == null) {
			LOGGER.error("requested chain with id " + chainProgressCommand.getChainId() 
				+ " not found."
				+ "Remaining Time could not be updated!");
			return;
		}
		chainInfo.setRemainingTime(chainProgressCommand.getRemainingTime());
		this.propertyChangeSupport.firePropertyChange(
				StatusTracker.CHAIN_INFO_PROPERTY, null, scanInfo);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ChainProgress (Id: " 
					+ chainProgressCommand.getChainId() + ", " + "PositionCounter: "
					+ chainProgressCommand.getPositionCounter() + ", " + "Remaining Time: "
					+ chainProgressCommand.getRemainingTime() + ", " + "TimeStamp: "
					+ chainProgressCommand.getTimeStamp() + ")");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void chainStatusChanged(ChainStatusCommand chainStatusCommand) {
		this.scanInfo.setChainStatus(chainStatusCommand.getChainId(), 
				chainStatusCommand.getChainStatus());
		for (int i : chainStatusCommand.getAllScanModuleIds()) {
			this.scanInfo.getChainInfo(chainStatusCommand.getChainId()).
				setScanModuleStatus(i, chainStatusCommand.getScanModuleStatus(i));
			this.scanInfo.getChainInfo(chainStatusCommand.getChainId()).
				setScanModuleReason(i, chainStatusCommand.getScanModuleReason(i));
		}
		this.propertyChangeSupport.firePropertyChange(
				StatusTracker.CHAIN_INFO_PROPERTY, null, scanInfo);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(this.scanInfo.toString());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void engineStatusChanged(EngineStatus engineStatus, String xmlName, int repeatCount) {
		if (EngineStatus.LOADING_XML.equals(engineStatus)) {
			ScanInfo oldValue = this.scanInfo;
			this.scanInfo = new ScanInfo();
			this.propertyChangeSupport.firePropertyChange(
					StatusTracker.SCAN_INFO_PROPERTY, oldValue, this.scanInfo);
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
		ScanInfo oldValue = this.scanInfo;
		this.scanInfo = null;
		this.propertyChangeSupport.firePropertyChange(
				StatusTracker.SCAN_INFO_PROPERTY, oldValue, this.scanInfo);
	}
	
	/**
	 * Adds a listener for the given property
	 * @param property the property to listen to
	 * @param listener the listener
	 */
	public void addPropertyChangeListener(String property, PropertyChangeListener listener) {
		this.propertyChangeSupport.addPropertyChangeListener(property, listener);
	}

	/**
	 * Removes a listener for the given property
	 * @param property the property to stop listen to
	 * @param listener the listener
	 */
	public void removePropertyChangeListener(String property, PropertyChangeListener listener) {
		this.propertyChangeSupport.removePropertyChangeListener(property, listener);
	}
}
package de.ptb.epics.eve.viewer;

import de.ptb.epics.eve.ecp1.types.EngineStatus;

public interface IUpdateListener {
	
	/**
	 * 
	 * @param remainTime
	 */
	void updateOccured(int chainId, int remainTime);
	
	/**
	 * 
	 */
	void clearStatusTable();
	
	/**
	 * 
	 * @param chainId
	 * @param status
	 */
	void fillChainStatus(int chainId, String status);

	/**
	 * 
	 * @param chainId
	 * @param scanModuleId
	 * @param status
	 */
	void fillScanModuleStatus(int chainId, int scanModuleId, String status, String reason);

	/**
	 * 
	 * @param name
	 */
	void setLoadedScmlFile(String name);
	
	/**
	 * 
	 * @param engineStatus
	 * @param repeatCount
	 */
	void fillEngineStatus(EngineStatus engineStatus, int repeatCount);
	
	/**
	 * 
	 * @param autoPlayStatus
	 */
	void setAutoPlayStatus(boolean autoPlayStatus);
	
	/**
	 * 
	 */
	void disableSendToFile();
}
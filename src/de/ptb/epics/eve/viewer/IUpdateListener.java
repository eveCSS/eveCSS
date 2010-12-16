package de.ptb.epics.eve.viewer;

import de.ptb.epics.eve.ecp1.intern.EngineStatus;

public interface IUpdateListener {
	
	public void updateOccured(int remainTime);
	public void clearStatusTable();
	public void fillStatusTable(int chainId, int scanModuleId, String status, int remainTime);
	public void setLoadedScmlFile(String name);
	public void fillEngineStatus(EngineStatus engineStatus);
	public void setAutoPlayStatus(boolean autoPlayStatus);

}

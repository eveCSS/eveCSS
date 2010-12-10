package de.ptb.epics.eve.viewer;

import de.ptb.epics.eve.ecp1.intern.EngineStatus;

public interface IUpdateListener {
	
	public void updateOccured();
	public void clearStatusTable();
	public void fillStatusTable(int chainId, int scanModuleId, String status);
	public void setLoadedScmlFile(String name);
	public void fillEngineStatus(EngineStatus engineStatus);

}

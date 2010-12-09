package de.ptb.epics.eve.viewer;

public interface IUpdateListener {
	
	public void updateOccured();
	public void clearStatusTable();
	public void fillStatusTable(int chainId, int scanModuleId, String status);
	public void setLoadedScmlFile(String name);

}

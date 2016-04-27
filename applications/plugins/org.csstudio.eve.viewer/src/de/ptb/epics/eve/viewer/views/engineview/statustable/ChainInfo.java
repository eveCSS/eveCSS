package de.ptb.epics.eve.viewer.views.engineview.statustable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.ptb.epics.eve.ecp1.types.ChainStatus;
import de.ptb.epics.eve.ecp1.types.ScanModuleReason;
import de.ptb.epics.eve.ecp1.types.ScanModuleStatus;
import de.ptb.epics.eve.viewer.Activator;

/**
 * Represents status information of a chain (and its contained scan modules).
 * 
 * @author Marcus Michalsky
 * @since 1.26
 */
public class ChainInfo {
	private int id;
	private ChainStatus status;
	private Map<Integer, ScanModuleInfo> scanModuleInfos;
	
	// TODO When switching to Java 8 Duration should be used !!!:
	private Integer remainingTime;
	
	public ChainInfo(int id) {
		this.id = id;
		this.status = ChainStatus.UNKNOWN;
		this.scanModuleInfos = new HashMap<>();
		this.remainingTime = null;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the status
	 */
	public ChainStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(ChainStatus status) {
		this.status = status;
	}
	
	/**
	 * Sets the status of the scan module with the given id.
	 * @param scanModuleId the id of the scan module of which the status should be set
	 * @param status the status to set
	 */
	public void setScanModuleStatus(int scanModuleId, ScanModuleStatus status) {
		ScanModuleInfo scanModuleInfo = scanModuleInfos.get(scanModuleId);
		if (scanModuleInfo == null) {
			scanModuleInfo = new ScanModuleInfo(scanModuleId);
			scanModuleInfo.setName(Activator.getDefault().
					getCurrentScanDescription().getChain(this.id)
					.getScanModuleById(scanModuleId).getName());
			this.scanModuleInfos.put(scanModuleId, scanModuleInfo);
		}
		scanModuleInfo.setStatus(status);
	}
	
	/**
	 * Sets the reason of the scan module with the given id.
	 * @param scanModuleId the id of the scan module of which the status should be set
	 * @param reason the reason to set
	 */
	public void setScanModuleReason(int scanModuleId, ScanModuleReason reason) {
		ScanModuleInfo scanModuleInfo = scanModuleInfos.get(scanModuleId);
		scanModuleInfo.setReason(reason);
	}

	/**
	 * @return the remainingTime
	 */
	public Integer getRemainingTime() {
		return remainingTime;
	}

	/**
	 * @param remainingTime the remainingTime to set
	 */
	public void setRemainingTime(Integer remainingTime) {
		this.remainingTime = remainingTime;
	}
	
	/**
	 * Returns status information of all contained scan modules.
	 * @return status information of all contained scan modules
	 */
	public Collection<ScanModuleInfo> getScanModuleInfos() {
		return this.scanModuleInfos.values();
	}
	
	/**
	 * Returns status information of the scan module with the given id
	 * @param scanModuleId the id of the scan module to get information from
	 * @return status information of the scan module with the given id
	 */
	public ScanModuleInfo getScanModuleInfo(int scanModuleId) {
		return this.scanModuleInfos.get(scanModuleId);
	}
}
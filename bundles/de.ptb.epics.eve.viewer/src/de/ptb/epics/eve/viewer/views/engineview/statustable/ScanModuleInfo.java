package de.ptb.epics.eve.viewer.views.engineview.statustable;

import de.ptb.epics.eve.ecp1.types.ScanModuleReason;
import de.ptb.epics.eve.ecp1.types.ScanModuleStatus;

/**
 * Represents status information of a scan module.
 * 
 * @author Marcus Michalsky
 * @since 1.26
 */
public class ScanModuleInfo {
	private int id;
	private String name;
	private ScanModuleStatus status;
	private ScanModuleReason reason;
	
	public ScanModuleInfo(int id) {
		this.id = id;
		this.name = "SM " + id;
		this.status = ScanModuleStatus.UNKNOWN;
		this.reason = ScanModuleReason.NONE;
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the status
	 */
	public ScanModuleStatus getStatus() {
		return status;
	}
	
	/**
	 * @param status the status to set
	 */
	public void setStatus(ScanModuleStatus status) {
		this.status = status;
	}

	/**
	 * @return the reason
	 */
	public ScanModuleReason getReason() {
		return reason;
	}

	/**
	 * @param reason the reason to set
	 */
	public void setReason(ScanModuleReason reason) {
		this.reason = reason;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.name + " (id: " + this.id + ", status: " 
			+ this.status + ", reason: " + this.reason + ")";
	}
}
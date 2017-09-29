package de.ptb.epics.eve.viewer.views.engineview.statustable;

/**
 * Entry of StatusTable.
 * 
 * @author Marcus Michalsky
 * @since 1.26
 */
public class StatusTableElement {
	private Integer chainId;
	private Integer scanModuleId;
	private String scanModuleName;
	private String status;
	private String reason;
	private Integer remainingTime;
	
	public StatusTableElement() {
		this.chainId = null;
		this.scanModuleId = null;
		this.scanModuleName = "";
		this.status = "";
		this.reason = "";
		this.remainingTime = null;
	}
	
	/**
	 * @return the chainId
	 */
	public Integer getChainId() {
		return chainId;
	}

	/**
	 * @param chainId the chainId to set
	 */
	public void setChainId(Integer chainId) {
		this.chainId = chainId;
	}

	/**
	 * @return the scanModuleId
	 */
	public Integer getScanModuleId() {
		return scanModuleId;
	}

	/**
	 * @param scanModuleId the scanModuleId to set
	 */
	public void setScanModuleId(Integer scanModuleId) {
		this.scanModuleId = scanModuleId;
	}

	/**
	 * @return the scanModuleName
	 */
	public String getScanModuleName() {
		return scanModuleName;
	}
	
	/**
	 * @param scanModuleName the scanModuleName to set
	 */
	public void setScanModuleName(String scanModuleName) {
		this.scanModuleName = scanModuleName;
	}
	
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}
	
	/**
	 * @param reason the reason to set
	 */
	public void setReason(String reason) {
		this.reason = reason;
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
}
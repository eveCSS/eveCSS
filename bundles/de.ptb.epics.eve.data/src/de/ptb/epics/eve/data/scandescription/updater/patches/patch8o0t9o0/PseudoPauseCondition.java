package de.ptb.epics.eve.data.scandescription.updater.patches.patch8o0t9o0;

import de.ptb.epics.eve.data.ComparisonTypes;
import de.ptb.epics.eve.data.DataTypes;

/**
 * Pseudo PauseCondition used during SCML Patching. Original could not be used 
 * due to missing measuringstation to search for the device by id (PauseCondition
 * expects an AbstractDevice which is not available).
 * 
 * @author Marcus Michalsky
 * @since 1.36
 */
public class PseudoPauseCondition {
	private String deviceId;
	private DataTypes type;
	private ComparisonTypes operator;
	private String pauseLimit;
	private String continueLimit;
	
	/**
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}
	
	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	/**
	 * @return the type
	 */
	public DataTypes getType() {
		return type;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(DataTypes type) {
		this.type = type;
	}
	
	/**
	 * @return the operator
	 */
	public ComparisonTypes getOperator() {
		return operator;
	}
	
	/**
	 * @param operator the operator to set
	 */
	public void setOperator(ComparisonTypes operator) {
		this.operator = operator;
	}
	
	/**
	 * @return the pauseLimit
	 */
	public String getPauseLimit() {
		return pauseLimit;
	}
	
	/**
	 * @param pauseLimit the pauseLimit to set
	 */
	public void setPauseLimit(String pauseLimit) {
		this.pauseLimit = pauseLimit;
	}
	
	/**
	 * @return the continueLimit
	 */
	public String getContinueLimit() {
		return continueLimit;
	}
	
	/**
	 * @param continueLimit the continueLimit to set
	 */
	public void setContinueLimit(String continueLimit) {
		this.continueLimit = continueLimit;
	}
}

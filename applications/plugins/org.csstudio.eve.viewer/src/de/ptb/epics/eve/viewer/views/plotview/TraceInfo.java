package de.ptb.epics.eve.viewer.views.plotview;

import de.ptb.epics.eve.ecp1.types.DataModifier;

/**
 * @author Marcus Michalsky
 * @since 1.13
 */
public class TraceInfo {
	private String motorId;
	private String detectorId;
	private String normalizeId;
	private DataModifier modifier;
	
	/**
	 * 
	 */
	public TraceInfo() {
		this.motorId = "";
		this.detectorId = "";
		this.normalizeId = "";
		this.modifier = DataModifier.UNMODIFIED;
	}

	/**
	 * @return the motorId
	 */
	public String getMotorId() {
		return motorId;
	}

	/**
	 * @param motorId the motorId to set
	 */
	public void setMotorId(String motorId) {
		this.motorId = motorId;
	}

	/**
	 * @return the detectorId
	 */
	public String getDetectorId() {
		return detectorId;
	}

	/**
	 * @param detectorId the detectorId to set
	 */
	public void setDetectorId(String detectorId) {
		this.detectorId = detectorId;
	}

	/**
	 * @return the normalizeId
	 */
	public String getNormalizeId() {
		return normalizeId;
	}

	/**
	 * @param normalizeId the normalizeId to set
	 */
	public void setNormalizeId(String normalizeId) {
		this.normalizeId = normalizeId;
	}

	/**
	 * @return the modifier
	 */
	public DataModifier getModifier() {
		return modifier;
	}

	/**
	 * @param modifier the modifier to set
	 */
	public void setModifier(DataModifier modifier) {
		this.modifier = modifier;
	}
}
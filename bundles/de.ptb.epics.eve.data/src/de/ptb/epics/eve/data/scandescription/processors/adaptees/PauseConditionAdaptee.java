package de.ptb.epics.eve.data.scandescription.processors.adaptees;

import de.ptb.epics.eve.data.ComparisonTypes;
import de.ptb.epics.eve.data.DataTypes;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class PauseConditionAdaptee {
	private String id;
	private ComparisonTypes operator;
	private DataTypes pauseType;
	private String pauseLimit;
	private DataTypes continueType;
	private String continueLimit;
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
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
	 * @return the pauseType
	 */
	public DataTypes getPauseType() {
		return pauseType;
	}
	
	/**
	 * @param pauseType the pauseType to set
	 */
	public void setPauseType(DataTypes pauseType) {
		this.pauseType = pauseType;
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
	 * @return the continueType
	 */
	public DataTypes getContinueType() {
		return continueType;
	}
	
	/**
	 * @param continueType the continueType to set
	 */
	public void setContinueType(DataTypes continueType) {
		this.continueType = continueType;
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

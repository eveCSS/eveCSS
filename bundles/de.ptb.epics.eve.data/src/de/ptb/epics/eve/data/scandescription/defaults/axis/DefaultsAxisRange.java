package de.ptb.epics.eve.data.scandescription.defaults.axis;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Marcus Michalsky
 * @since 1.28
 */
@XmlType(propOrder = {"expression", "positionList"})
public class DefaultsAxisRange extends DefaultsAxisMode {
	private String expression;
	private String positionList;
	
	/**
	 * @return the expression
	 */
	public String getExpression() {
		return expression;
	}
	
	/**
	 * @param expression the expression to set
	 */
	@XmlElement(name="expression")
	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	/**
	 * @return the positionList
	 */
	public String getPositionList() {
		return positionList;
	}
	
	/**
	 * @param positionlist the positionList to set
	 */
	@XmlElement(name="positionlist")
	public void setPositionList(String positionlist) {
		this.positionList = positionlist;
	}
}
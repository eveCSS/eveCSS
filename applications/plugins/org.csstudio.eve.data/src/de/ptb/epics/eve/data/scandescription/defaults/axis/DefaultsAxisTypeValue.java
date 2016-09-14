package de.ptb.epics.eve.data.scandescription.defaults.axis;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import de.ptb.epics.eve.data.DataTypes;

/**
 * @author Marcus Michalsky
 * @since 1.8
 */
public class DefaultsAxisTypeValue {
	private DataTypes type;
	private String value;
	
	/**
	 * 
	 */
	public DefaultsAxisTypeValue() {
		this.type = null;
		this.value = null;
	}
	
	/**
	 * @param type the data type
	 * @param value the value
	 */
	public DefaultsAxisTypeValue(DataTypes type, String value) {
		this.type = type;
		this.value = value;
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
	@XmlAttribute(name="type")
	public void setType(DataTypes type) {
		this.type = type;
	}
	
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * @param value the value to set
	 */
	@XmlValue
	public void setValue(String value) {
		this.value = value;
	}
}
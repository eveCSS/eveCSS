package de.ptb.epics.eve.data.scandescription.defaults;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Marcus Michalsky
 * @since 1.8
 */
public class DefaultsAxisPluginParameter {
	private String name;
	private String value;
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	@XmlAttribute(name="name")
	public void setName(String name) {
		this.name = name;
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
	@XmlElement(name="parameter")
	public void setValue(String value) {
		this.value = value;
	}
}
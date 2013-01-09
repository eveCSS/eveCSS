package de.ptb.epics.eve.data.scandescription.defaults;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Marcus Michalsky
 * @since 1.8
 */
public class DefaultsAxisPlugin extends DefaultsAxisMode {
	private String name;
	private List<DefaultsAxisPluginParameter> parameters;
	
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
	 * @return the parameters
	 */
	public List<DefaultsAxisPluginParameter> getParameters() {
		return parameters;
	}

	/**
	 * @param parameters the parameters to set
	 */
	@XmlElement(name="parameter")
	public void setParameters(List<DefaultsAxisPluginParameter> parameters) {
		this.parameters = parameters;
	}
}
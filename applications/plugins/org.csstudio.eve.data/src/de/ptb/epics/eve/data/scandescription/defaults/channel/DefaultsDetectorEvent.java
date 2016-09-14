package de.ptb.epics.eve.data.scandescription.defaults.channel;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Marcus Michalsky
 * @since 1.23
 */
@XmlType(name = "detectorevent")
public class DefaultsDetectorEvent extends DefaultsControlEvent {
	private String id;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	@XmlElement(name = "id")
	public void setId(String id) {
		this.id = id;
	}
	
	public int getChainId() {
		return Integer.parseInt(this.id.substring(2, 3));
	}
	
	public int getScanModuleId() {
		return Integer.parseInt(this.id.substring(4, 5));
	}
	
	public String getDetectorId() {
		return this.id.substring(6);
	}
}
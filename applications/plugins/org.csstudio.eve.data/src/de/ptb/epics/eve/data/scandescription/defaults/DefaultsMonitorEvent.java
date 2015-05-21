package de.ptb.epics.eve.data.scandescription.defaults;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import de.ptb.epics.eve.data.scandescription.Limit;

/**
 * @author Marcus Michalsky
 * @since 1.23
 */
@XmlType(name = "monitorevent")
public class DefaultsMonitorEvent extends DefaultsControlEvent {
	private String id;
	private Limit limit;
	
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
	
	/**
	 * @return the limit
	 */
	public Limit getLimit() {
		return limit;
	}
	
	/**
	 * @param limit the limit to set
	 */
	@XmlElement(name = "limit")
	public void setLimit(Limit limit) {
		this.limit = limit;
	}
}
package de.ptb.epics.eve.data.scandescription.defaults.channel;

import javax.xml.bind.annotation.*;

/**
 * @author Marcus Michalsky
 * @since 1.8
 */
@XmlType(propOrder = { "id", "normalizeId", "mode" })
public class DefaultsChannel implements Comparable<DefaultsChannel> {
	private String id;
	private String normalizeId;
	private DefaultsChannelMode mode;
	
	/**
	 * 
	 */
	public DefaultsChannel() {
		this.id = null;
		this.normalizeId = null;
		this.mode = null;
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @param id the id to set
	 */
	@XmlElement(name="channelid")
	public void setId(String id) {
		this.id = id;
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
	@XmlElement(name="normalize_id")
	public void setNormalizeId(String normalizeId) {
		this.normalizeId = normalizeId;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "[Channel:" + this.id + "]";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(DefaultsChannel other) {
		return this.getId().compareTo(other.getId());
	}

	/**
	 * @return the mode
	 */
	public DefaultsChannelMode getMode() {
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	@XmlElements(value = {
			@XmlElement(name="standard", type=DefaultsChannelModeStandard.class),
			@XmlElement(name="interval", type=DefaultsChannelModeInterval.class)
	})
	public void setMode(DefaultsChannelMode mode) {
		this.mode = mode;
	}
}
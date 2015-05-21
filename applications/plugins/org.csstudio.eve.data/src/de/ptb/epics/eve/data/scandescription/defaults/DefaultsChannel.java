package de.ptb.epics.eve.data.scandescription.defaults;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.*;

/**
 * @author Marcus Michalsky
 * @since 1.8
 */
@XmlType(propOrder = { "id", "averageCount", "maxDeviation", "minimum",
		"maxAttempts", "normalizeId", "redoEvents", "deferred" })
public class DefaultsChannel implements Comparable<DefaultsChannel> {
	private String id;
	private Integer averageCount;
	private Double maxDeviation;
	private Double minimum;
	private Integer maxAttempts;
	private String normalizeId;
	private List<DefaultsRedoEvent> redoEvents;
	private Boolean deferred;
	
	/**
	 * 
	 */
	public DefaultsChannel() {
		this.id = null;
		this.averageCount = null;
		this.maxDeviation = null;
		this.minimum = null;
		this.maxAttempts = null;
		this.normalizeId = null;
		this.deferred = false;
		this.redoEvents = new ArrayList<DefaultsRedoEvent>();
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
	 * @return the averageCount
	 * @throws NullPointerException if not set
	 */
	public Integer getAverageCount() {
		return averageCount;
	}
	
	/**
	 * @param averageCount the averageCount to set
	 */
	@XmlElement(name="averagecount")
	public void setAverageCount(Integer averageCount) {
		this.averageCount = averageCount;
	}
	
	/**
	 * @return the maxDeviation
	 */
	public Double getMaxDeviation() {
		return maxDeviation;
	}
	
	/**
	 * @param maxDeviation the maxDeviation to set
	 */
	@XmlElement(name="maxdeviation")
	public void setMaxDeviation(Double maxDeviation) {
		this.maxDeviation = maxDeviation;
	}
	
	/**
	 * @return the minimum
	 */
	public Double getMinimum() {
		return minimum;
	}
	
	/**
	 * @param minimum the minimum to set
	 */
	@XmlElement(name="minimum")
	public void setMinimum(Double minimum) {
		this.minimum = minimum;
	}
	
	/**
	 * @return the maxAttempts
	 */
	public Integer getMaxAttempts() {
		return maxAttempts;
	}

	/**
	 * @param maxAttempts the maxAttempts to set
	 */
	@XmlElement(name="maxattempts")
	public void setMaxAttempts(Integer maxAttempts) {
		this.maxAttempts = maxAttempts;
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
	 * @return the redoEvents
	 */
	public List<DefaultsRedoEvent> getRedoEvents() {
		return redoEvents;
	}

	/**
	 * @param redoEvents the redoEvents to set
	 */
	@XmlElement(name = "redoevent")
	public void setRedoEvents(List<DefaultsRedoEvent> redoEvents) {
		this.redoEvents = redoEvents;
	}

	/**
	 * @return the deferred
	 */
	public boolean isDeferred() {
		return deferred;
	}
	
	/**
	 * @param deferred the deferred to set
	 */
	@XmlElement(name="deferredtrigger")
	public void setDeferred(boolean deferred) {
		this.deferred = deferred;
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
}
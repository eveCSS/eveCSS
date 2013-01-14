package de.ptb.epics.eve.data.scandescription.defaults;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Marcus Michalsky
 * @since 1.8
 */
@XmlType(propOrder = { "id", "averageCount", "maxDeviation", "minimum",
		"maxAttempts", "normalizeId", "deferred" })
public class DefaultsChannel implements Comparable<DefaultsChannel> {
	private String id;
	private Integer averageCount;
	private Double maxDeviation;
	private Double minimum;
	private Integer maxAttempts;
	private String normalizeId;
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
	public int getAverageCount() {
		if (this.averageCount == null) {
			throw new NullPointerException("Value not set!");
		}
		return averageCount;
	}
	/**
	 * @param averageCount the averageCount to set
	 */
	@XmlElement(name="averagecount")
	public void setAverageCount(int averageCount) {
		this.averageCount = averageCount;
	}
	/**
	 * @return the maxDeviation
	 * @throws NullPointerException if not set
	 */
	public double getMaxDeviation() {
		if (this.maxDeviation == null) {
			throw new NullPointerException("Value not set!");
		}
		return maxDeviation;
	}
	/**
	 * @param maxDeviation the maxDeviation to set
	 */
	@XmlElement(name="maxdeviation")
	public void setMaxDeviation(double maxDeviation) {
		this.maxDeviation = maxDeviation;
	}
	/**
	 * @return the minimum
	 * @throws NullPointerException if not set
	 */
	public double getMinimum() {
		if (this.minimum == null) {
			throw new NullPointerException("Value not set!");
		}
		return minimum;
	}
	/**
	 * @param minimum the minimum to set
	 */
	@XmlElement(name="minimum")
	public void setMinimum(double minimum) {
		this.minimum = minimum;
	}
	/**
	 * @return the maxAttempts
	 * @throws NullPointerException if not set
	 */
	public int getMaxAttempts() {
		if (this.maxAttempts == null) {
			throw new NullPointerException("Value not set!");
		}
		return maxAttempts;
	}

	/**
	 * @param maxAttempts the maxAttempts to set
	 */
	@XmlElement(name="maxattempts")
	public void setMaxAttempts(int maxAttempts) {
		this.maxAttempts = maxAttempts;
	}

	/**
	 * @return the normalizeId
	 * @throws NullPointerException if not set
	 */
	public String getNormalizeId() {
		if (this.normalizeId == null) {
			throw new NullPointerException("Value not set!");
		}
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
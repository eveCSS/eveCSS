package de.ptb.epics.eve.data.scandescription.defaults;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author Marcus Michalsky
 * @since 1.8
 */
public class DefaultChannel {
	private String id;
	private Integer averageCount;
	private Integer maxAttempts;
	private Double maxDeviation;
	private Double minimum;
	private Boolean deferred;
	
	/**
	 * 
	 */
	public DefaultChannel() {
		this.id = null;
		this.averageCount = null;
		this.maxAttempts = null;
		this.maxDeviation = null;
		this.minimum = null;
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
	public void setMaxAttempts(int maxAttempts) {
		this.maxAttempts = maxAttempts;
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
	public void setMinimum(double minimum) {
		this.minimum = minimum;
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
}
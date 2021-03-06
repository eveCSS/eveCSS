package de.ptb.epics.eve.data.scandescription.defaults.channel;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import de.ptb.epics.eve.data.scandescription.channelmode.ChannelModes;

/**
 * @author Marcus Michalsky
 * @since 1.27
 */
@XmlType(propOrder = {"averageCount", "maxDeviation", "minimum",
		"maxAttempts", "redoEvents", "deferred"})
public class DefaultsChannelModeStandard extends DefaultsChannelMode {
	private Integer averageCount;
	private Double maxDeviation;
	private Double minimum;
	private Integer maxAttempts;
	private List<DefaultsRedoEvent> redoEvents;
	private Boolean deferred;
	
	public DefaultsChannelModeStandard() {
		this.averageCount = null;
		this.maxDeviation = null;
		this.minimum = null;
		this.maxAttempts = null;
		this.deferred = false;
		this.redoEvents = new ArrayList<DefaultsRedoEvent>();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ChannelModes getType() {
		return ChannelModes.STANDARD;
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
}
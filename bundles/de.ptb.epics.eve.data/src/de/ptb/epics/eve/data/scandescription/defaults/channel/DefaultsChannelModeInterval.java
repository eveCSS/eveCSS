package de.ptb.epics.eve.data.scandescription.defaults.channel;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import de.ptb.epics.eve.data.scandescription.channelmode.ChannelModes;

/**
 * @author Marcus Michalsky
 * @since 1.27
 */
@XmlType(propOrder = {"triggerInterval", "stoppedBy"})
public class DefaultsChannelModeInterval extends DefaultsChannelMode {
	private double triggerInterval;
	private String stoppedBy;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ChannelModes getType() {
		return ChannelModes.INTERVAL;
	}
	
	/**
	 * @return the triggerInterval
	 */
	public double getTriggerInterval() {
		return triggerInterval;
	}
	
	/**
	 * @param triggerInterval the triggerInterval to set
	 */
	@XmlElement(name = "triggerinterval")
	public void setTriggerInterval(double triggerInterval) {
		this.triggerInterval = triggerInterval;
	}
	
	/**
	 * @return the stoppedBy
	 */
	public String getStoppedBy() {
		return stoppedBy;
	}
	
	/**
	 * @param stoppedBy the stoppedBy to set
	 */
	@XmlElement(name = "stoppedby")
	public void setStoppedBy(String stoppedBy) {
		this.stoppedBy = stoppedBy;
	}
}
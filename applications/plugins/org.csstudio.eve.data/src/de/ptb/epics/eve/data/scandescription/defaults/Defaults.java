package de.ptb.epics.eve.data.scandescription.defaults;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * 
 * @author Marcus Michalsky
 * @since 1.8
 */
@XmlRootElement(name="defaults", namespace="http://www.ptb.de/epics/SCML")
public class Defaults {
	private List<DefaultAxis> axes;
	private List<DefaultChannel> channels;
	
	/**
	 * @return the axes
	 */
	public List<DefaultAxis> getAxes() {
		return axes;
	}
	/**
	 * @param axes the axes to set
	 */
	@XmlElementWrapper(name="axes")
	@XmlElement(name="axis")
	public void setAxes(List<DefaultAxis> axes) {
		this.axes = axes;
	}
	/**
	 * @return the channels
	 */
	public List<DefaultChannel> getChannels() {
		return channels;
	}
	/**
	 * @param channels the channels to set
	 */
	@XmlElementWrapper(name="channels")
	@XmlElement(name="channel")
	public void setChannels(List<DefaultChannel> channels) {
		this.channels = channels;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("Axes: ");
		String axesString = "";
		for (DefaultAxis axis : this.axes) {
			axesString += axis.toString() + ", ";
		}
		if (!axesString.isEmpty()) {
			buff.append(axesString.substring(0, axesString.length()-2));
		} else {
			buff.append("none");
		}
		buff.append("\n");
		buff.append("Channels: ");
		String channelsString = "";
		for (DefaultChannel channel : this.channels) {
			channelsString += channel.toString() + ", ";
		}
		if (!channelsString.isEmpty()) {
			buff.append(channelsString.substring(0, channelsString.length()-2));
		} else {
			buff.append("none");
		}
		return buff.toString();
	}
}
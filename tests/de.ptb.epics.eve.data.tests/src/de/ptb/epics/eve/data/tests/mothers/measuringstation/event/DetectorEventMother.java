package de.ptb.epics.eve.data.tests.mothers.measuringstation.event;

import de.ptb.epics.eve.data.measuringstation.event.DetectorEvent;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.tests.mothers.scandescription.ChannelMother;

/**
 * Fabricates DetectorEvent test fixtures and tailors them.
 * 
 * @author Marcus Michalsky
 * @since 1.27
 */
public class DetectorEventMother {

	/**
	 * Creates a detector event based on a generic channel.
	 * @return a detector event
	 */
	public static DetectorEvent createNewDetectorEvent() {
		return new DetectorEvent(ChannelMother.createNewChannel());
	}
	
	/**
	 * Creates a detector event based on the given channel.
	 * @param channel the channel the detector event should be based on
	 * @return a detector event based on the given channel
	 */
	public static DetectorEvent createNewDetectorEvent(Channel channel) {
		return new DetectorEvent(channel);
	}
}
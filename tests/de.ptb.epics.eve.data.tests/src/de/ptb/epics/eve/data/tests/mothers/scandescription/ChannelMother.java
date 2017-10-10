package de.ptb.epics.eve.data.tests.mothers.scandescription;

import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.DetectorChannelMother;

/**
 * Fabricates channel test fixtures and tailors them.
 * 
 * @author Marcus Michalsky
 * @since 1.27
 */
public class ChannelMother {
	
	/**
	 * Creates a new channel.
	 * @return a new channel
	 */
	public static Channel createNewChannel() {
		Channel channel = new Channel(ScanModuleMother.createNewScanModule(), 
				DetectorChannelMother.createNewDetectorChannel());
		return channel;
	}
	
	/**
	 * Creates a new channel within the given scan module.
	 * @param scanModule the scan module the channel should be contained in
	 * @return a new channel within the given scan module
	 */
	public static Channel createNewChannel(ScanModule scanModule) {
		Channel channel = new Channel(scanModule, 
				DetectorChannelMother.createNewDetectorChannel());
		return channel;
	}
}

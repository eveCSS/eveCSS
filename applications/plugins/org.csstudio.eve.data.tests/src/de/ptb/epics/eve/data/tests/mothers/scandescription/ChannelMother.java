package de.ptb.epics.eve.data.tests.mothers.scandescription;

import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.DetectorChannelMother;

/**
 * Fabricates channel test fixtures and tailors them.
 * 
 * @author Marcus Michalsky
 * @since 1.27
 */
public class ChannelMother {
	public static Channel createNewChannel() {
		Channel channel = new Channel(ScanModuleMother.createNewScanModule(), 
				DetectorChannelMother.createNewDetectorChannel());
		return channel;
	}
}

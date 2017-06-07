package de.ptb.epics.eve.data.measuringstation.event.tests;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.channelmode.ChannelModes;
import de.ptb.epics.eve.data.tests.mothers.scandescription.ChainMother;
import de.ptb.epics.eve.data.tests.mothers.scandescription.ChannelMother;
import de.ptb.epics.eve.data.tests.mothers.scandescription.ControlEventMother;
import de.ptb.epics.eve.data.tests.mothers.scandescription.ScanModuleMother;

/**
 * @author Marcus Michalsky
 * @since 1.27
 */
public class DetectorEventTest {
	
	@Test
	public void testRemoveChannelInvalidate() {
		Chain chain = ChainMother.createNewChain();
		ScanModule scanModule = ScanModuleMother.createNewScanModule();
		chain.add(scanModule);
		Channel channel = ChannelMother.createNewChannel(scanModule);
		scanModule.add(channel);
		assertTrue(chain.getStopEvents().isEmpty());
		
		chain.addStopEvent(ControlEventMother.createNewDetectorReadyEvent(channel));
		assertFalse(chain.getStopEvents().isEmpty());
		
		scanModule.remove(channel);
		assertTrue(chain.getStopEvents().isEmpty());
	}
	
	@Test
	@Ignore("not implemented yet")
	public void testRemoveScanModuleOfChannelInvalidate() {
		// Invalidation of event if scan module the channel is in is removed
		// is not implemented yet
	}
	
	@Test
	public void testChangeChannelModeInvalidate() {
		Chain chain = ChainMother.createNewChain();
		ScanModule scanModule = ScanModuleMother.createNewScanModule();
		chain.add(scanModule);
		Channel channel = ChannelMother.createNewChannel(scanModule);
		scanModule.add(channel);
		assertTrue(chain.getStopEvents().isEmpty());
		
		chain.addStopEvent(ControlEventMother.createNewDetectorReadyEvent(channel));
		assertFalse(chain.getStopEvents().isEmpty());
		
		channel.setChannelMode(ChannelModes.INTERVAL);
		assertTrue(chain.getStopEvents().isEmpty());
	}
}
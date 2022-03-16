package de.ptb.epics.eve.data.scandescription.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.tests.mothers.scandescription.ChainMother;
import de.ptb.epics.eve.data.tests.mothers.scandescription.ChannelMother;
import de.ptb.epics.eve.data.tests.mothers.scandescription.ControlEventMother;

/**
 * @author Marcus Michalsky
 * @since 1.36.1
 */
public class ScanModuleDetectorEventTest {
	
	/**
	 * @see #6500
	 */
	@Test
	public void testRemoveDetectorOfDetectorEvent() {
		Chain chain = ChainMother.createNewChain();
		
		ScanModule sm1 = new ScanModule(1);
		chain.add(sm1);
		Channel channel1 = ChannelMother.createNewChannel(sm1);
		sm1.add(channel1);
		
		ScanModule sm2 = new ScanModule(2);
		chain.add(sm2);
		ControlEvent event = ControlEventMother.createNewDetectorReadyEvent(
				channel1);
		sm2.addRedoEvent(event);
		
		assertEquals(event, sm2.getRedoEvents().get(0));
		assertFalse(sm2.getRedoEvents().isEmpty());
		
		sm1.remove(channel1);
		assertTrue(sm2.getRedoEvents().isEmpty());
	}
	
	/**
	 * @see #6500
	 */
	@Ignore("transitive invalidate mechanism not implemented yet")
	@Test
	public void testRemoveScanModuleContainingDetectorOfDetectorEvent() {
		Chain chain = ChainMother.createNewChain();
		
		ScanModule sm1 = new ScanModule(1);
		chain.add(sm1);
		Channel channel1 = ChannelMother.createNewChannel(sm1);
		sm1.add(channel1);
		
		ScanModule sm2 = new ScanModule(2);
		chain.add(sm2);
		ControlEvent event = ControlEventMother.createNewDetectorReadyEvent(
				channel1);
		sm2.addRedoEvent(event);
		
		assertEquals(event, sm2.getRedoEvents().get(0));
		assertFalse(sm2.getRedoEvents().isEmpty());
		
		chain.remove(sm1);
		assertTrue(sm2.getRedoEvents().isEmpty()); // fails
	}
	
	@Ignore("multiple chains not supported yet")
	@Test
	public void testRemoveChainContainingScanModuleContainingDetectorOfDetectorEvent() {
		// TODO
	}
}

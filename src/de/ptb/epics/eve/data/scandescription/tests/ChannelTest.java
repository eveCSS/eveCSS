package de.ptb.epics.eve.data.scandescription.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import de.ptb.epics.eve.data.scandescription.Channel;

public class ChannelTest {

	@Test
	public void testUpdateEvent() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddModelUpdateListener() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveModelUpdateListener() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testGetModelErrors() {
		Channel ch = new Channel(null);
		
		assertTrue(ch.getModelErrors().size() > 0);
	}

}

package de.ptb.epics.eve.data.scandescription.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.ptb.epics.eve.data.scandescription.Postscan;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.DeviceMother;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.OptionMother;

public class PostscanTest {

	@Test
	public void testNewInstanceDevice() {
		Postscan postscan = new Postscan(DeviceMother.createNewDevice());
		Postscan copy = Postscan.newInstance(postscan);
		// due to no equals implementation (see #5876) attributes have to be 
		// checked directly
		assertEquals(postscan.getAbstractDevice(), copy.getAbstractDevice());
		assertEquals(postscan.isReset(), copy.isReset());
		assertEquals(postscan.getValue(), copy.getValue());
	}
	
	@Test
	public void testNewInstanceOption() {
		Postscan postscan = new Postscan(OptionMother.createNewOption());
		Postscan copy = Postscan.newInstance(postscan);
		// due to no equals implementation (see #5876) attributes have to be 
		// checked directly
		assertEquals(postscan.getAbstractDevice(), copy.getAbstractDevice());
		assertEquals(postscan.isReset(), copy.isReset());
		assertEquals(postscan.getValue(), copy.getValue());
	}
}

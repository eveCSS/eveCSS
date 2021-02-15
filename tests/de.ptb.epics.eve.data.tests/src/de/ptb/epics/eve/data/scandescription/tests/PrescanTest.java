package de.ptb.epics.eve.data.scandescription.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.ptb.epics.eve.data.scandescription.Prescan;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.DeviceMother;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.OptionMother;

public class PrescanTest {
	
	@Test
	public void testNewInstanceDevice() {
		Prescan prescan = new Prescan(DeviceMother.createNewDevice());
		Prescan copy = Prescan.newInstance(prescan);
		// due to no equals implementation (see #5876) attributes have to be 
		// checked directly
		assertEquals(prescan.getAbstractDevice(), copy.getAbstractDevice());
		assertEquals(prescan.getValue(), copy.getValue());
	}
	
	@Test
	public void testNewInstanceOption() {
		Prescan prescan = new Prescan(OptionMother.createNewOption());
		Prescan copy = Prescan.newInstance(prescan);
		// due to no equals implementation (see #5876) attributes have to be 
		// checked directly
		assertEquals(prescan.getAbstractDevice(), copy.getAbstractDevice());
		assertEquals(prescan.getValue(), copy.getValue());
	}
}

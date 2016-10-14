package de.ptb.epics.eve.data.measuringstation.event.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.tests.mothers.scandescription.ChainMother;
import de.ptb.epics.eve.data.tests.mothers.scandescription.ControlEventMother;
import de.ptb.epics.eve.data.tests.mothers.scandescription.ScanModuleMother;

/**
 * @author Marcus Michalsky
 * @since 1.27
 */
public class ScheduleEventTest {

	@Test
	public void testRemoveScanModuleInvalidate() {
		Chain chain1 = ChainMother.createNewChain();
		ScanModule scanModule1 = ScanModuleMother.createNewScanModule();
		chain1.add(scanModule1);
		
		Chain chain2 = ChainMother.createNewChain();
		ScanModule scanModule2 = ScanModuleMother.createNewScanModule();
		chain2.add(scanModule2);
		
		assertTrue(scanModule1.getBreakEvents().isEmpty());
		scanModule1.addBreakEvent(ControlEventMother.createNewScheduleEvent(scanModule2));
		assertFalse(scanModule1.getBreakEvents().isEmpty());
		chain2.remove(scanModule2);
		assertTrue(scanModule1.getBreakEvents().isEmpty());
	}
}
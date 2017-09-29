package de.ptb.epics.eve.data.tests.mothers.measuringstation.event;

import de.ptb.epics.eve.data.measuringstation.event.ScheduleEvent;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.tests.mothers.scandescription.ScanModuleMother;

/**
 * Fabricates schedule events and tailors them.
 * @author Marcus Michalsky
 * @since 1.27
 */
public class ScheduleEventMother {
	
	/**
	 * Creates a new schedule event based on a generic scan module.
	 * @return a schedule event based on a generic scan module
	 */
	public static ScheduleEvent createNewScheduleEvent() {
		return new ScheduleEvent(ScanModuleMother.createNewScanModule());
	}
	
	/**
	 * Creates a new schedule event based on the given scan module.
	 * @param scanModule the scan module the schedule event should be based on
	 * @return a schedule event based on the given scan module
	 */
	public static ScheduleEvent createNewScheduleEvent(ScanModule scanModule) {
		return new ScheduleEvent(scanModule);
	}
}
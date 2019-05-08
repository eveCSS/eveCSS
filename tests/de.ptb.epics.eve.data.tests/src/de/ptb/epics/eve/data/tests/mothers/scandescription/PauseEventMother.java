package de.ptb.epics.eve.data.tests.mothers.scandescription;

import de.ptb.epics.eve.data.EventTypes;
import de.ptb.epics.eve.data.measuringstation.event.DetectorEvent;
import de.ptb.epics.eve.data.measuringstation.event.ScheduleEvent;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.PauseEvent;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.event.DetectorEventMother;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.event.ScheduleEventMother;

/**
 * @author Marcus Michalsky
 * @since 1.31
 */
public class PauseEventMother {
	private PauseEventMother() {}
	
	/**
	 * Creates a detector ready pause event based on the given channel. 
	 * @param channel the channel the pause event should be based on
	 * @return a detector ready pause event based on the given channel
	 */
	public static PauseEvent createNewDetectorReadyEvent(Channel channel) {
		DetectorEvent detectorEvent = DetectorEventMother.createNewDetectorEvent(channel);
		return new PauseEvent(EventTypes.DETECTOR, detectorEvent, detectorEvent.getId());
	}
	
	/**
	 * Creates a schedule pause event based on the given scan module.
	 * @param scanModule the scan module the pause event should be based on
	 * @return a schedule pause event based on the given scan module
	 */
	public static PauseEvent createNewScheduleEvent(ScanModule scanModule) {
		ScheduleEvent scheduleEvent = ScheduleEventMother.createNewScheduleEvent(scanModule);
		return new PauseEvent(EventTypes.SCHEDULE, scheduleEvent, scheduleEvent.getId());
	}
}

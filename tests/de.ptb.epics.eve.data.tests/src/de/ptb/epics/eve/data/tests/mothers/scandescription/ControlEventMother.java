package de.ptb.epics.eve.data.tests.mothers.scandescription;

import de.ptb.epics.eve.data.EventTypes;
import de.ptb.epics.eve.data.measuringstation.event.DetectorEvent;
import de.ptb.epics.eve.data.measuringstation.event.ScheduleEvent;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.event.DetectorEventMother;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.event.ScheduleEventMother;

/**
 * Fabricates control event test fixtures and tailors them.
 * 
 * @author Marcus Michalsky
 * @since 1.27
 */
public class ControlEventMother {
	
	/**
	 * Creates a detector ready control event based on a generic channel.
	 * @return a detector ready control event based on a generic channel
	 */
	public static ControlEvent createNewDetectorReadyEvent() {
		DetectorEvent detectorEvent = DetectorEventMother.createNewDetectorEvent();
		return new ControlEvent(EventTypes.DETECTOR, detectorEvent, detectorEvent.getId());
	}
	
	/**
	 * Creates a detector ready control event based on the given channel. 
	 * @param channel the channel the control event should be based on
	 * @return a detector ready control event based on the given channel
	 */
	public static ControlEvent createNewDetectorReadyEvent(Channel channel) {
		DetectorEvent detectorEvent = DetectorEventMother.createNewDetectorEvent(channel);
		return new ControlEvent(EventTypes.DETECTOR, detectorEvent, detectorEvent.getId());
	}
	
	/**
	 * Creates a schedule control event based on a generic scan module.
	 * @return a schedule control event based on a generic scan module
	 */
	public static ControlEvent createNewScheduleEvent() {
		ScheduleEvent scheduleEvent = ScheduleEventMother.createNewScheduleEvent();
		return new ControlEvent(EventTypes.SCHEDULE, scheduleEvent, scheduleEvent.getId());
	}
	
	/**
	 * Creates a schedule control event based on the given scan module.
	 * @param scanModule the scan module the control event should be based on
	 * @return a schedule control event based on the given scan module
	 */
	public static ControlEvent createNewScheduleEvent(ScanModule scanModule) {
		ScheduleEvent scheduleEvent = ScheduleEventMother.createNewScheduleEvent(scanModule);
		return new ControlEvent(EventTypes.SCHEDULE, scheduleEvent, scheduleEvent.getId());
	}
}
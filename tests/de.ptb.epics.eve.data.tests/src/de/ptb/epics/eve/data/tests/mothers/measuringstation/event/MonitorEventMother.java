package de.ptb.epics.eve.data.tests.mothers.measuringstation.event;

import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.measuringstation.event.MonitorEvent;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.DetectorChannelMother;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.DeviceMother;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.MotorAxisMother;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.OptionMother;

/**
 * Fabricates monitor events and tailors them.
 * @author Marcus Michalsky
 * @since 1.27
 */
public class MonitorEventMother {
	
	/**
	 * Creates a monitor event based on a generic motor axis.
	 * @return a monitor event based on a generic motor axis
	 */
	public static MonitorEvent createMotorAxisMonitorEvent() {
		return new MonitorEvent(MotorAxisMother.createNewMotorAxis());
	}
	
	/**
	 * Creates a monitor event based on a generic detector channel.
	 * @return a monitor event based on a generic detector channel
	 */
	public static MonitorEvent createDetectorChannelMonitorEvent() {
		return new MonitorEvent(DetectorChannelMother.createNewDetectorChannel());
	}
	
	/**
	 * Creates a monitor event based on a generic device.
	 * @return a monitor event based on a generic device
	 */
	public static MonitorEvent createDeviceMonitorEvent() {
		return new MonitorEvent(DeviceMother.createNewDevice());
	}
	
	/**
	 * Creates a monitor event based on a generic option.
	 * @return a monitor event based on a generic option
	 */
	public static MonitorEvent createOptionMonitorEvent() {
		return new MonitorEvent(OptionMother.createNewOption());
	}
	
	/**
	 * Creates a monitor event based on the given motor axis.
	 * @param motorAxis the motor axis the monitor event should be based on
	 * @return a monitor event based on the given motor axis
	 */
	public static MonitorEvent createMonitorEvent(MotorAxis motorAxis) {
		return new MonitorEvent(motorAxis);
	}
	
	/**
	 * Creates a monitor event based on the given detector channel.
	 * @param detectorChannel the detector channel the monitor event should be based on
	 * @return a monitor evnet based on the given detector channel
	 */
	public static MonitorEvent createMonitorEvent(DetectorChannel detectorChannel) {
		return new MonitorEvent(detectorChannel);
	}
	
	/**
	 * Creates a monitor event based on the given device.
	 * @param device the device the monitor event should be based on
	 * @return a monitor event based on the given device
	 */
	public static MonitorEvent createMonitorEvent(Device device) {
		return new MonitorEvent(device);
	}
	
	/**
	 * Creates a monitor event based on the given option.
	 * @param option the option the monitor event should be based on
	 * @return a monitor event based on the given option
	 */
	public static MonitorEvent createMonitorEvent(Option option) {
		return new MonitorEvent(option);
	}
}
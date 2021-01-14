package de.ptb.epics.eve.data.tests.mothers.measuringstation;

import java.util.Calendar;

import de.ptb.epics.eve.data.measuringstation.Device;

/**
 * Fabricates devices and tailors them.
 * @author Marcus Michalsky
 * @since 1.27
 */
public class DeviceMother {

	/**
	 * Creates a device.
	 * @return a device
	 */
	public static Device createNewDevice() {
		Device device = new Device();
		String name = "Device-" + Calendar.getInstance().getTime().getTime();
		device.setId(name);
		device.setName(name);
		device.setClassName("DeviceClass");
		device.setValue(FunctionMother.createNewDoubleTypeFunction());
		
		return device;
	}
}

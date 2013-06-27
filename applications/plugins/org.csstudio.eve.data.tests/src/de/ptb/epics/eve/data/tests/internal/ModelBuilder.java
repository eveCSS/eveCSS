package de.ptb.epics.eve.data.tests.internal;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.MethodTypes;
import de.ptb.epics.eve.data.TransportTypes;
import de.ptb.epics.eve.data.TypeValue;
import de.ptb.epics.eve.data.measuringstation.Access;
import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.data.measuringstation.Function;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.Option;

public class ModelBuilder {

	public static Motor createMotorWithAxisAndOptions() {
		Motor m = ModelBuilder.createMotor("Motor1");
		m.add(ModelBuilder.createOption("Motor1-Option1"));
		MotorAxis ma = ModelBuilder.createMotorAxis("Motor1-Axis1");
		ma.add(ModelBuilder.createOption("Motor1-Axis1-Option1"));
		m.add(ma);
		return m;
	}

	public static Motor createMotor(String id) {
		Motor m = new Motor();
		m.setId(id);
		m.setName(id);
		m.setClassName("MotorClass");

		return m;
	}

	public static MotorAxis createMotorAxis(String id) {
		MotorAxis ma = new MotorAxis();
		ma.setId(id);
		ma.setName(id);
		ma.setClassName("MotorAxisClass");
		return ma;
	}

	public static Option createOption(String id) {
		Option o = new Option();
		o.setId(id);
		o.setName(id);
		o.setClassName("OptionClass");
		o.setValue(new Function(new Access(MethodTypes.GET)));
		return o;
	}

	public static Device createDevice(String id) {
		Device device = new Device();
		device.setId(id);
		device.setName(id);
		device.setClassName("DeviceClass");
		Access access = new Access("accessVarId", DataTypes.STRING, 0,
				MethodTypes.GET, TransportTypes.LOCAL, 0);
		TypeValue typeValue = new TypeValue(DataTypes.STRING, "open, close");
		Function function = new Function(access, typeValue);
		device.setValue(function);
		return device;
	}
}

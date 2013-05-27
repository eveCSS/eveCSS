package de.ptb.epics.eve.data.tests.internal;

import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.Option;

public class ModelBuilder {

	protected static Motor createMotorWithAxisAndOptions() {
		Motor m = ModelBuilder.createMotor("Motor1");
		m.add(ModelBuilder.createOption("Motor1-Option1"));
		MotorAxis ma = ModelBuilder.createMotorAxis("Motor1-Axis1");
		ma.add(ModelBuilder.createOption("Motor1-Axis1-Option1"));
		m.add(ma);
		return m;
	}
	
	protected static Motor createMotor(String id) {
		Motor m = new Motor();
		m.setId(id);
		
		return m;
	}
	
	protected static MotorAxis createMotorAxis(String id) {
		MotorAxis ma = new MotorAxis();
		ma.setId(id);
		
		return ma;
	}
	
	protected static Option createOption(String id) {
		Option o = new Option();
		o.setId(id);
		
		return o;
	}
}

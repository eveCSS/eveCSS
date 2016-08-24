package de.ptb.epics.eve.data.tests.mothers.measuringstation;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.MeasuringStation;

/**
 * Fabricates measuring station test fixtures and tailors them
 * 
 * @author Marcus Michalsky
 * @since 1.27
 */
public class MeasuringStationMother {
	
	public static IMeasuringStation createNewMeasuringStation() {
		IMeasuringStation measuringStation = new MeasuringStation();
		
		return measuringStation;
	}
	
	public static IMeasuringStation addMotorWithAxis(MeasuringStation measuringStation) {
		measuringStation.add(MotorMother.addMotorAxis(MotorMother.createNewMotor()));
		return measuringStation;
	}
	
	public static IMeasuringStation addDetectorWithChannel(MeasuringStation measuringStation) {
		measuringStation.add(DetectorMother.addDetectorChannel(DetectorMother.createNewDetector()));
		return measuringStation;
	}
}
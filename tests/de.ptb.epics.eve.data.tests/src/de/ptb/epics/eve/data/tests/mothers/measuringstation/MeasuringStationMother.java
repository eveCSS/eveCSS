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
		return new MeasuringStation();
	}

	public static IMeasuringStation addMotorWithAxis(
			MeasuringStation measuringStation) {
		measuringStation.add(MotorMother.addMotorAxis(
				MotorMother.createNewMotor()));
		return measuringStation;
	}

	public static IMeasuringStation addDetectorWithChannel(
			MeasuringStation measuringStation) {
		measuringStation.add(DetectorMother.addDetectorChannel(
				DetectorMother.createNewDetector()));
		return measuringStation;
	}

	public static IMeasuringStation addDetectorWithChannelAndOptionWithMonitor(
			MeasuringStation measuringStation) {
		measuringStation.add(DetectorMother.addDetectorChannel(
				DetectorMother.addOptionWithMonitor(
						DetectorMother.createNewDetector())));
		return measuringStation;
	}
}
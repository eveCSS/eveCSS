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
	private MeasuringStationMother() {
	}
	
	public static MeasuringStation createNewEmptyMeasuringStation() {
		return new MeasuringStation();
	}

	public static MeasuringStation addMotorWithAxis(
			MeasuringStation measuringStation) {
		measuringStation.add(MotorMother.addMotorAxis(
				MotorMother.createNewMotor()));
		return measuringStation;
	}

	public static MeasuringStation addDetectorWithChannel(
			MeasuringStation measuringStation) {
		measuringStation.add(DetectorMother.addDetectorChannel(
				DetectorMother.createNewDetector()));
		return measuringStation;
	}

	public static MeasuringStation addDetectorWithChannelAndOptionWithMonitor(
			MeasuringStation measuringStation) {
		measuringStation.add(DetectorMother.addDetectorChannel(
				DetectorMother.addOptionWithMonitor(
						DetectorMother.createNewDetector())));
		return measuringStation;
	}
}
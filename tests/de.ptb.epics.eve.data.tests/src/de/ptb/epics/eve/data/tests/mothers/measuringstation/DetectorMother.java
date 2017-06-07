package de.ptb.epics.eve.data.tests.mothers.measuringstation;

import java.util.Calendar;

import de.ptb.epics.eve.data.measuringstation.Detector;

/**
 * Fabricates Detector test fixtures and tailors them.
 * 
 * @author Marcus Michalsky
 * @since 1.26
 */
public class DetectorMother {
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public static Detector createNewDetector() {
		Detector detector = new Detector();
		String name = "Detector-" + Calendar.getInstance().getTime().getTime();
		detector.setId(name);
		detector.setName(name);
		detector.setClassName("detectorClass");
		
		detector.setStop(FunctionMother.createNewFunction());
		detector.setStatus(FunctionMother.createNewFunction());
		
		detector.setUnit(UnitMother.createNewUnit());
		
		return detector;
	}
	
	/**
	 * Adds a trigger to the given detector.
	 * 
	 * @param detector the detector the trigger should be added to
	 * @return the given detector with a new trigger
	 */
	public static Detector addTrigger(Detector detector) {
		detector.setTrigger(FunctionMother.createNewFunction());
		return detector;
	}
	
	/**
	 * Adds a detector channel to the given detector.
	 * 
	 * @param detector the detector the detector channel should be added to
	 * @return the given detector with a new detector channel
	 */
	public static Detector addDetectorChannel(Detector detector) {
		detector.add(DetectorChannelMother.createNewDetectorChannel());
		return detector;
	}
	
	/**
	 * Adds an option to the given detector.
	 * 
	 * @param detector the detector the option should be added to
	 * @return the given detector with a new option
	 */
	public static Detector addOption(Detector detector) {
		detector.add(OptionMother.createNewOption());
		return detector;
	}
}
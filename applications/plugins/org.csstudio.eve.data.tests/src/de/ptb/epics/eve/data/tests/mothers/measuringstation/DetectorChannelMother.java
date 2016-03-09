package de.ptb.epics.eve.data.tests.mothers.measuringstation;

import java.util.Calendar;

import de.ptb.epics.eve.data.measuringstation.DetectorChannel;

/**
 * Fabricates DetectorChannel test fixtures and tailors them.
 * 
 * @author Marcus Michalsky
 * @since 1.26
 */
public class DetectorChannelMother {

	/**
	 * 
	 * @return
	 */
	public static DetectorChannel createNewDetectorChannel() {
		DetectorChannel channel = new DetectorChannel(
				FunctionMother.createNewFunction());
		String name = "DetectorChannel-" + 
				Calendar.getInstance().getTime().getTime();
		channel.setId(name);
		channel.setName(name);
		channel.setClassName("DetectorChannelClass");
		
		channel.setStop(FunctionMother.createNewFunction());
		channel.setRead(FunctionMother.createNewFunction());
		
		return channel;
	}
	
	/**
	 * Adds an option to the given detector channel.
	 * 
	 * @param detector the detector channel the option should be added to
	 * @return the given detector channel with a new option
	 */
	public static DetectorChannel addOption(DetectorChannel detectorChannel) {
		detectorChannel.add(OptionMother.createNewOption());
		return detectorChannel;
	}
}
package de.ptb.epics.eve.data.measuringstation.filter.tests;

import java.io.BufferedWriter;
import java.io.FileWriter;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.tests.internal.Configurator;

/**
 * @author Marcus Michalsky
 * @since 0.4.1
 */
public class PrintFilteredMeasuringStation {

	private static Logger logger = 
		Logger.getLogger(PrintFilteredMeasuringStation.class.getName());
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			FileWriter fstream = new FileWriter("log/station.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			
			IMeasuringStation measuringStation = 
				Configurator.getMeasuringStation();
			
			for(Motor m : measuringStation.getMotors()) {
				for(MotorAxis ma : m.getAxes()) {
					for(Option o : ma.getOptions()) {
						
					}
				}
			}
			
			out.write("Hello Java");
			out.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

}

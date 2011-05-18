/**
 * 
 */
package de.ptb.epics.eve.data.tests;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.xml.DOMConfigurator;
import org.xml.sax.SAXException;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.processors.MeasuringStationLoader;

/**
 * 
 * 
 * @author Marcus Michalsky
 * @since 0.4.1
 */
public class Configurator {

	private static final File schemaFile = new File("xml/scml.xsd");
	private static final File descriptionFile = new File("xml/test.xml");
	
	/**
	 * Returns an 
	 * {@link de.ptb.epics.eve.data.measuringstation.IMeasuringStation}.
	 * 
	 * @return an 
	 * 		{@link de.ptb.epics.eve.data.measuringstation.IMeasuringStation}
	 */
	public static IMeasuringStation getMeasuringStation()
	{
		final MeasuringStationLoader measuringStationLoader = 
			new MeasuringStationLoader(schemaFile);
		
		try {
			measuringStationLoader.load(descriptionFile);
		} catch (ParserConfigurationException e) {
			e.printStackTrace(); return null;
		} catch (SAXException e) {
			e.printStackTrace(); return null;
		} catch (IOException e) {
			e.printStackTrace(); return null;
		}
		
		return measuringStationLoader.getMeasuringStation();
	}
	
	/**
	 * 
	 */
	public static void configureLogging()
	{
		DOMConfigurator.configure("log4j-conf.xml");
	}
}

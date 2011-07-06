package de.ptb.epics.eve.data.tests.internal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.xml.sax.SAXException;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.processors.MeasuringStationLoader;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.processors.ScanDescriptionLoader;

/**
 * <code>Configurator</code> contains the configuration of the tests used.
 * 
 * @author Marcus Michalsky
 * @since 0.4.1
 */
public class Configurator {

	private static Logger logger = 
			Logger.getLogger(Configurator.class.getName());
	
	private static final File schemaFile = new File("xml/scml.xsd");
	private static final File descriptionFile = new File("xml/test.xml");
	
	private static final File scan = new File("scml/scan1.scml");
	
	private static boolean configured = false;
	
	/**
	 * Returns a 
	 * {@link de.ptb.epics.eve.data.measuringstation.IMeasuringStation} to test 
	 * with.
	 * 
	 * @return an 
	 * 		{@link de.ptb.epics.eve.data.measuringstation.IMeasuringStation} to 
	 * 		test with
	 * @deprecated tests should use {@link #getMeasuringStations()} instead and 
	 * 			do the tests for each station returned.
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
	 * Returns all available 
	 * {@link de.ptb.epics.eve.data.measuringstation.IMeasuringStation}s.
	 * 
	 * @return all available 
	 * 		{@link de.ptb.epics.eve.data.measuringstation.IMeasuringStation}s
	 */
	public static List<IMeasuringStation> getMeasuringStations() {
		List<IMeasuringStation> stations = new ArrayList<IMeasuringStation>();
		
		final MeasuringStationLoader measuringStationLoader = 
			new MeasuringStationLoader(schemaFile);
		
		File test = new File("xml/test.xml");
		File qnim = new File("xml/qnim.xml");
		
		try {
			measuringStationLoader.load(test);
			stations.add(measuringStationLoader.getMeasuringStation());
			measuringStationLoader.load(qnim);
			stations.add(measuringStationLoader.getMeasuringStation());
		} catch (ParserConfigurationException e) {
			logger.error(e.getMessage(), e);
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return stations;
	}
	
	/**
	 * Returns a 
	 * {@link de.ptb.epics.eve.data.scandescription.ScanDescription} to test 
	 * with.
	 * 
	 * @return a 
	 * 		{@link de.ptb.epics.eve.data.scandescription.ScanDescription} to 
	 * 		test with 
	 */
	public static ScanDescription getScanDescription()
	{
		ScanDescriptionLoader loader = new ScanDescriptionLoader(
				getMeasuringStation(), schemaFile);
		
		try {
			loader.load(scan);
		} catch (ParserConfigurationException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}

		return loader.getScanDescription();
	}
	
	/**
	 * Loads the Log4j configuration.
	 */
	public static void configureLogging()
	{
		if(!configured)
		{
			DOMConfigurator.configure("log4j-conf.xml");
			configured = true;
		}
	}
	
	/**
	 * Returns the schema file.
	 * 
	 * @return the schema file
	 */
	public static File getSchemaFile()
	{
		return schemaFile;
	}
}
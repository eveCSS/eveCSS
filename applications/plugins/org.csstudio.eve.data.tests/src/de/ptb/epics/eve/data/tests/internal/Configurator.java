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
	
	private static boolean configured = false;
	
	private static final File schemaFile = new File("xml/scml.xsd");
	private static final File descriptionFile = new File("xml/test.xml"); // deprecated
	
	private static final File scan = new File("scml/scan1.scml"); // deprecated

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
		File sx700 = new File("xml/sx700.xml");
		
		try {
			measuringStationLoader.load(test);
			stations.add(measuringStationLoader.getMeasuringStation());
			measuringStationLoader.load(qnim);
			stations.add(measuringStationLoader.getMeasuringStation());
			measuringStationLoader.load(sx700);
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
	 * 
	 * @return
	 */
	public static List<Pair<IMeasuringStation, List<ScanDescription>>> getScanDescriptions() {
		// TODO fetching all available scan descriptions in the scan folder is 
		// not a good idea, need a folder with "reliable" scan descriptions
		// to work with
		final List<Pair<IMeasuringStation, List<ScanDescription>>> pairs = 
				new ArrayList<Pair<IMeasuringStation, List<ScanDescription>>>();
		
		final MeasuringStationLoader measuringStationLoader = 
			new MeasuringStationLoader(schemaFile);
		
		File test = new File("xml/test.xml");
		File qnim = new File("xml/qnim.xml");
		File sx700 = new File("xml/sx700.xml");
		
		try {
			logger.debug("loading test station");
			measuringStationLoader.load(test);
			
			List<ScanDescription> testScans = new ArrayList<ScanDescription>();
			
			for(File f : getFiles(new File("/scml/test/scan"))) {
				logger.debug("loading scan description: " + f.getName());
				ScanDescriptionLoader loader = new ScanDescriptionLoader(
					measuringStationLoader.getMeasuringStation(), schemaFile);
				loader.load(f);
				testScans.add(loader.getScanDescription());
			}
			
			Pair<IMeasuringStation, List<ScanDescription>> pTest = 
					new Pair<IMeasuringStation, List<ScanDescription>>(
							measuringStationLoader.getMeasuringStation(), 
							testScans);
			pairs.add(pTest);
			
			/* *** */
			
			measuringStationLoader.load(qnim);
			
			List<ScanDescription> qnimScans = new ArrayList<ScanDescription>();
			
			for(File f : getFiles(new File("/scml/qnim/scan"))) {
				ScanDescriptionLoader loader = new ScanDescriptionLoader(
					measuringStationLoader.getMeasuringStation(), schemaFile);
				loader.load(f);
				qnimScans.add(loader.getScanDescription());
			}
			
			Pair<IMeasuringStation, List<ScanDescription>> pQnim = 
					new Pair<IMeasuringStation, List<ScanDescription>>(
							measuringStationLoader.getMeasuringStation(), 
							testScans);
			pairs.add(pQnim);
			
			/* *** */
			
			// measuringStationLoader.load(sx700);

		} catch (ParserConfigurationException e) {
			logger.error(e.getMessage(), e);
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		
		for(Pair<IMeasuringStation, List<ScanDescription>> p : pairs) {
			logger.debug("MeasuringStation: " + p.getFirstValue().getName());
			logger.debug("Scan Descriptions: " + p.getSecondValue().toString());
		}
		
		return pairs;
	}
	
	/**
	 * Returns a 
	 * {@link de.ptb.epics.eve.data.scandescription.ScanDescription} to test 
	 * with.
	 * 
	 * @return a 
	 * 		{@link de.ptb.epics.eve.data.scandescription.ScanDescription} to 
	 * 		test with 
	 * @deprecated uses {@link #getMeasuringStation()} - should 
	 * 				be replaced in the future by a station depended getter
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
	
	/* ********************* */
	private static List<File> getFiles(File dir) {
		List<File> files = new ArrayList<File>();
		for(File f : dir.listFiles()) {
			if(f.isFile() && f.getName().endsWith(".scml")) {
				files.add(f);
			}
		}
		return files;
	}
}
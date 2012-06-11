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
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.ScanModule;

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
			new MeasuringStationLoader(Configurator.getSchemaFile());
		
		File test = new File("xml/test.xml");
		File qnim = new File("xml/qnim.xml");
		File sx700 = new File("xml/sx700.xml");
		File bigref = new File("xml/bigref.xml");
		File kmc = new File("xml/kmc.xml");
		File rfa = new File("xml/rfa.xml");
		
		try {
			measuringStationLoader.load(test);
			stations.add(measuringStationLoader.getMeasuringStation());
			measuringStationLoader.load(qnim);
			stations.add(measuringStationLoader.getMeasuringStation());
			measuringStationLoader.load(sx700);
			stations.add(measuringStationLoader.getMeasuringStation());
			measuringStationLoader.load(bigref);
			stations.add(measuringStationLoader.getMeasuringStation());
			measuringStationLoader.load(kmc);
			stations.add(measuringStationLoader.getMeasuringStation());
			measuringStationLoader.load(rfa);
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
	 * Returns a {@link de.ptb.epics.eve.data.scandescription.ScanDescription} 
	 * containing one {@link de.ptb.epics.eve.data.scandescription.Chain} and 
	 * one {@link de.ptb.epics.eve.data.scandescription.ScanModule}.
	 * 
	 * @param ims the {@link de.ptb.epics.eve.data.measuringstation.MeasuringStation}
	 * @return a scan description containing one chain and one scan module
	 */
	public static ScanDescription getBasicScanDescription(IMeasuringStation ims) {
		ScanDescription sd = new ScanDescription(ims);
		
		Chain ch = new Chain(1);
		ScanModule sm = new ScanModule(1);
		ch.add(sm);
		sd.add(ch);
		return sd;
	}
	
	/**
	 * Loads the Log4j configuration.
	 */
	public static void configureLogging() {
		if(!configured) {
			DOMConfigurator.configure("log4j-conf.xml");
			configured = true;
		}
	}
	
	/**
	 * Returns the schema file.
	 * 
	 * @return the schema file
	 */
	public static File getSchemaFile() {
		return new File("../org.csstudio.eve.resources/cfg/schema.xsd");
	}
}
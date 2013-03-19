package de.ptb.epics.eve.data.tests.internal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

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
		
		File bigref = new File("xml/bigref.xml");
		File euvr = new File("xml/euvr.xml");
		File kmc = new File("xml/kmc.xml");
		File newref = new File("xml/newref.xml");
		File nrfa = new File("xml/nrfa.xml");
		File oldref = new File("xml/oldref.xml");
		File pgm = new File("xml/pgm.xml");
		File qnim = new File("xml/qnim.xml");
		File rfa = new File("xml/rfa.xml");
		File sx700 = new File("xml/sx700.xml");
		File test = new File("xml/test.xml");
		File trfa = new File("xml/trfa.xml");
		
		try {
			measuringStationLoader.load(bigref);
			stations.add(measuringStationLoader.getMeasuringStation());
			measuringStationLoader.load(euvr);
			stations.add(measuringStationLoader.getMeasuringStation());
			measuringStationLoader.load(kmc);
			stations.add(measuringStationLoader.getMeasuringStation());
			measuringStationLoader.load(newref);
			stations.add(measuringStationLoader.getMeasuringStation());
			measuringStationLoader.load(nrfa);
			stations.add(measuringStationLoader.getMeasuringStation());
			measuringStationLoader.load(oldref);
			stations.add(measuringStationLoader.getMeasuringStation());
			measuringStationLoader.load(pgm);
			stations.add(measuringStationLoader.getMeasuringStation());
			measuringStationLoader.load(qnim);
			stations.add(measuringStationLoader.getMeasuringStation());
			measuringStationLoader.load(rfa);
			stations.add(measuringStationLoader.getMeasuringStation());
			measuringStationLoader.load(sx700);
			stations.add(measuringStationLoader.getMeasuringStation());
			measuringStationLoader.load(test);
			stations.add(measuringStationLoader.getMeasuringStation());
			measuringStationLoader.load(trfa);
			stations.add(measuringStationLoader.getMeasuringStation());
		} catch (ParserConfigurationException e) {
			System.err.println(e.getMessage());
			return null;
		} catch (SAXException e) {
			System.err.println(e.getMessage());
			return null;
		} catch (IOException e) {
			System.err.println(e.getMessage());
			return null;
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
	 * Returns the schema file.
	 * 
	 * @return the schema file
	 */
	public static File getSchemaFile() {
		return new File(System.getenv("WORKSPACE") + 
			"/repo/applications/plugins/org.csstudio.eve.resources/cfg/schema.xsd");
	}
}
package de.ptb.epics.eve.data.tests.internal;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.FileLocator;
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
		
		List<File> files = new ArrayList<File>();
		
		try {
			if (System.getenv("WORKSPACE") == null) {
				System.out.println(Configurator.class.getClassLoader().getResource("../../xml/pgm.xml"));
				String basedir = "../../";
				files.add(new File(basedir + "pgm.xml"));
			} else {
				URL url = new URL(
						"platform:/plugin/de.ptb.epics.eve.data.tests/xml/pgm.xml");
				files.add(new File(FileLocator.toFileURL(url).toURI()));
			}
			
			for(File file : files) {
				measuringStationLoader.load(file);
				stations.add(measuringStationLoader.getMeasuringStation());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
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
		return de.ptb.epics.eve.resources.Activator.getXMLSchema();
	}
}
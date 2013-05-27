package de.ptb.epics.eve.data.tests.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.MeasuringStation;
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
		stations.add(Configurator.createMeasuringStation());
		return stations;
	}
	
	/**
	 * 
	 * @return
	 * @author Marcus Michalsky
	 * @since 1.12
	 */
	public static IMeasuringStation getMeasuringStation() {
		return Configurator.createMeasuringStation();
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
		// NOTE: works only when run with Jenkins...
		// see also: https://wiki.jenkins-ci.org/display/JENKINS/Building+a+software+project#Buildingasoftwareproject-JenkinsSetEnvironmentVariables
		return new File(System.getenv("WORKSPACE") + 
			"/repo/applications/plugins/org.csstudio.eve.resources/cfg/schema.xsd");
	}
	
	/**
	 * 
	 * @return
	 * @author Marcus Michalsky
	 * @since 1.12
	 */
	protected static IMeasuringStation createMeasuringStation() {
		MeasuringStation ims = new MeasuringStation();
		ims.setLoadedFileName("JUNIT");
		ims.setVersion("2.2");
		ims.setSchemaFileName(Configurator.getSchemaFile().getAbsolutePath());
		
		ims.add(ModelBuilder.createMotorWithAxisAndOptions());
		
		return ims;
	}
}
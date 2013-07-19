package de.ptb.epics.eve.resources.init;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Version;
import org.xml.sax.SAXException;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.processors.MeasuringStationLoader;
import de.ptb.epics.eve.preferences.PreferenceConstants;

/**
 * @author Marcus Michalsky
 * @since 1.14
 */
public class Startup {
	
	/**
	 * 
	 * @return
	 */
	public static Parameters readStartupParameters() {
		String[] args = Platform.getCommandLineArgs();
		Parameters params = new Parameters();
		int i = 0;
		while (i < args.length) {
			if (args[i].equals("-eve.root")) {
				i++;
				params.setRootDir(args[i]);
			}
			if (args[i].equals("-eve.debug")) {
				i++;
				params.setDebug(args[i].equals("1") ? true : false);
			}
			i++;
		}
		if (params.getRootDir() != null && !params.getRootDir().endsWith("/")) {
			params.setRootDir(params.getRootDir() + "/");
		}
		return params;
	}
	
	/**
	 * 
	 * @param path
	 * @throws Exception
	 */
	public static void checkRootDir(String rootDir) throws Exception {
		String path = new String(rootDir);
		if(!path.endsWith("/")) {
			path += "/";
		}
		File file = new File(path + "eve/");
		if(!file.exists()) {
			String message = "Root Directory not found!";
			throw new Exception(message);
		}
	}
	
	/**
	 * 
	 * @param path
	 * @throws Exception
	 */
	public static void configureLogging(String rootDir, boolean debug,
			Logger logger) throws Exception {
		// setting property so that the log4j configuration file can access it
		System.setProperty("eve.logdir", rootDir + "eve/log");
		Version eveVersion = Platform.getProduct().getDefiningBundle()
				.getVersion();
		System.setProperty("eve.version", eveVersion.getMajor() + "."
				+ eveVersion.getMinor());
		String pathToConfigFile = rootDir + "eve/logger-debug.xml";
		if (debug) {
			// the logging configuration is taken from the eve root if present
			// or included default otherwise
			if (new File(pathToConfigFile).exists()) {
				DOMConfigurator.configure(pathToConfigFile);
				logger.debug("found debug logging configuration in eve root");
			} else {
				File debugConf = de.ptb.epics.eve.resources.Activator
						.getLoggerConfiguration(true);
				if (debugConf == null) {
					String message = "debug logging conf could not be loaded!";
					logger.fatal(message);
					throw new Exception(message);
				}
				DOMConfigurator.configure(debugConf.getAbsolutePath());
				logger.debug("no debug logging configuration found -> using default");
			}
		} else {
			// for deployed application use internal configuration
			File appConf = de.ptb.epics.eve.resources.Activator
					.getLoggerConfiguration(false);
			if (appConf == null) {
				String message = "app logging conf could not be loaded!";
				logger.fatal(message);
				throw new Exception(message);
			}
			DOMConfigurator.configure(appConf.getAbsolutePath());
		}
	}
	
	public static File loadSchemaFile(Logger logger) throws Exception {
		File schemaFile = de.ptb.epics.eve.resources.Activator.getXMLSchema();
		if(schemaFile == null) {
			String message = "Could not load schema file!";
			logger.fatal(message);
			throw new Exception(message);
		}
		return schemaFile;
	}
	
	/**
	 * 
	 * @param rootDir
	 * @param logger
	 * @return
	 * @throws Exception 
	 */
	public static IMeasuringStation loadMeasuringStation(String rootDir, 
			File schema, Logger logger) throws Exception {
		IMeasuringStation measuringStation = null;
		File deviceDefinition = null;
		
		// if no eve.root is given, use internal default definition
		if (rootDir == null) {
			deviceDefinition = de.ptb.epics.eve.resources.Activator.
					getDefaultDeviceDefinition();
			logger.info("No 'eve.root' given, using default device definition");
		} else {
			// get entry stored in the preferences
			final String preferencesEntry = de.ptb.epics.eve.preferences.Activator.
					getDefault().getPreferenceStore().getString(
					PreferenceConstants.P_DEFAULT_MEASURING_STATION_DESCRIPTION);
			
			if(preferencesEntry.isEmpty()) {
				File pathToDefaultMeasuringStation = 
						new File(rootDir + "eve/default.xml");
				if(!pathToDefaultMeasuringStation.exists()) {
					String message = "Could not find 'default.xml' in 'eve.root'!";
					logger.fatal(message);
					throw new Exception(message);
				}
				deviceDefinition = new File(rootDir + "eve/default.xml");
			} else {
				File measuringStationFile = new File(preferencesEntry);
				if(!measuringStationFile.exists()) {
					// preferences entry present, but target does not exist
					String message = "Could not find device definition file at " + 
							measuringStationFile;
					logger.fatal(message);
					throw new Exception(message);
				}
				deviceDefinition = new File(preferencesEntry);
			}
		}
		
		if (deviceDefinition == null) {
			String message = 
					"Could not determine location of device definition!";
			logger.fatal(message);
			throw new Exception(message);
		}
		
		try {
			final MeasuringStationLoader measuringStationLoader = 
					new MeasuringStationLoader(schema);
			measuringStationLoader.load(deviceDefinition);
			measuringStation = measuringStationLoader.getMeasuringStation();
		} catch (IllegalArgumentException e) {
			measuringStation = null;
			logger.fatal(e.getMessage(), e);
		} catch (ParserConfigurationException e) {
			measuringStation = null;
			logger.fatal(e.getMessage(), e);
		} catch (SAXException e) {
			measuringStation = null;
			logger.fatal(e.getMessage(), e);
		} catch (IOException e) {
			measuringStation = null;
			logger.fatal(e.getMessage(), e);
		}
		return measuringStation;
	}
}
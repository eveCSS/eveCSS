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
 * Collection of routines used by different plug-ins during startup for 
 * initialization.
 * 
 * @author Marcus Michalsky
 * @since 1.14
 */
public final class Startup {
	
	private Startup() {
	}
	
	/**
	 * Reads the startup parameters from the eclipse platform and returns them 
	 * encapsulated in a {@link de.ptb.epics.eve.resources.init.Parameters}
	 * object.
	 * 
	 * @return the startup parameters
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
		if (!params.useDefaultDevices() && !params.getRootDir().endsWith("/")) {
			params.setRootDir(params.getRootDir() + "/");
		}
		return params;
	}
	
	/**
	 * Verifies the given root directory. If and only if <code>&lt;rootDir&gt;/eve/</code> exists 
	 * the method returns normally. Otherwise an exception is thrown.
	 * 
	 * @param path the root dir path
	 * @throws Exception if <code>&lt;rootDir&gt;/eve/</code> does not exist
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
	 * Configures logging either for debugging (<code>debug == true</code>) or 
	 * release (<code>debug == false</code>). If <code>debug == true</code> the 
	 * logging configuration at <code>&lt;rootDir&gt;/eve/logger-debug.xml</code> will be used.
	 * If none is found an integrated default configuration is used. 
	 * Release logging always used the integrated configuration.
	 * 
	 * @param rootDir the eve root path where the custom debug configuration is located
	 * @param debug <code>true</code> for debugging, <code>false</code> for release
	 * @param logger the logger to log messages to
	 * @throws Exception if the internal configuration could not be loaded
	 */
	public static void configureLogging(String rootDir, boolean debug,
			Logger logger) throws Exception {
		// setting property so that the log4j configuration file can access it
		System.setProperty("eve.logdir", rootDir + "eve/log");
		Version eveVersion = Platform.getProduct().getDefiningBundle()
				.getVersion();
		System.setProperty("eve.version", eveVersion.getMajor() + "."
				+ eveVersion.getMinor());
		System.setProperty("eve.messagedir", rootDir + "eve/messages");
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
	
	/**
	 * Loads the (integrated) schema file.
	 * 
	 * @param logger the logger to log messages to
	 * @return the loaded schema file
	 * @throws Exception if schema could not be loaded
	 */
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
	 * Loads the device definition file. If no root directory is given 
	 * (<code>null</code>) a default device definition will be returned.
	 * Otherwise the device definition at the location set in the preferences 
	 * is used. If there is no preferences entry the device definition at 
	 * <code>&lt;rootDir&gt;/eve/default.xml</code> is loaded.
	 * Returns <code>null</code> if a load error occured.
	 * 
	 * @param logger the logger messages should go to
	 * @return the loaded measuring station or <code>null</code> if a load error occured
	 * @throws Exception if "rootDir" is not <code>null</code> and one of the following conditions is met:
	 * 		<ul>
	 * 			<li>no preferences entry, <code>&lt;rootDir&gt;/eve/defaults.xml</code> does not exist</li>
	 * 			<li>preferences entry is not a valid target</li>
	 * 		</ul>
	 */
	public static IMeasuringStation loadMeasuringStation(Logger logger) 
			throws Exception {
		IMeasuringStation measuringStation = null;
		File deviceDefinition = null;
		
		Parameters params = Startup.readStartupParameters();
		
		// if no eve.root is given, use internal default definition
		if (params.useDefaultDevices()) {
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
						new File(params.getRootDir() + "eve/default.xml");
				if(!pathToDefaultMeasuringStation.exists()) {
					String message = "Could not find 'default.xml' in 'eve.root'!";
					logger.fatal(message);
					throw new Exception(message);
				}
				deviceDefinition = new File(params.getRootDir() + 
						"eve/default.xml");
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
					new MeasuringStationLoader(Startup.loadSchemaFile(logger));
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
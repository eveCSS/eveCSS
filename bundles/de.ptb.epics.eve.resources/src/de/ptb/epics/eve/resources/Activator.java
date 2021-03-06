package de.ptb.epics.eve.resources;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import de.ptb.epics.eve.data.scandescription.defaults.DefaultsManager;
import de.ptb.epics.eve.resources.init.Startup;
import de.ptb.epics.eve.resources.jobs.defaults.SaveDefaults;
import de.ptb.epics.eve.util.data.Version;

/**
 * 
 * @author Marcus Michalsky
 * @since 1.2
 */
public class Activator implements BundleActivator {

	/** */
	public static final String PLUGIN_ID = "de.ptb.epics.eve.resources";

	private static final Logger LOGGER = Logger.getLogger(
			Activator.class.getName());

	private static BundleContext context;
	private static Activator plugin;

	private static Version schemaVersion;
	
	private DefaultsManager defaultsManager = null;
	private IWorkspace workspace;
	
	/**
	 * Constructor
	 */
	public Activator() {
		plugin = this;
	}

	/**
	 * Returns the activator.
	 * 
	 * @return the activator
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns the bundle context.
	 * 
	 * @return the bundle context
	 */
	public static BundleContext getContext() {
		return context;
	}

	/**
	 * {@inheritDoc}
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		this.workspace = ResourcesPlugin.getWorkspace();
	}

	/**
	 * {@inheritDoc}
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		// saving defaults
		File defaultsFile = this.workspace.getRoot()
				.getLocation().append("/defaults.xml").toFile();
		File schemaFile = de.ptb.epics.eve.resources.Activator
				.getDefaultsSchema();
		Job job = new SaveDefaults("Saving Defaults", this.getDefaultsManager(),
				null, defaultsFile, schemaFile);
		job.schedule();
		job.join();
		Activator.context = null;
	}
	
	/**
	 * 
	 * @return
	 */
	public DefaultsManager getDefaultsManager() {
		if (this.defaultsManager == null) {
			this.defaultsManager = new DefaultsManager();
			File defaultsFile = ResourcesPlugin.getWorkspace().getRoot()
					.getLocation().append("/defaults.xml").toFile();
			if (defaultsFile.exists()) {
				LOGGER.info("found defaults file for user "
						+ System.getProperty("user.name") + " (" + 
						ResourcesPlugin.getWorkspace().getRoot().getLocation().
						lastSegment() + ")");
			} else {
				LOGGER.info("no defaults file for user "
						+ System.getProperty("user.name") + " (" + 
						ResourcesPlugin.getWorkspace().getRoot().getLocation().
						lastSegment() + ")");
			}
			this.defaultsManager.init(defaultsFile,
					de.ptb.epics.eve.resources.Activator.getDefaultsSchema());
			if (this.defaultsManager.isInitialized() && 
					!this.defaultsManager.getWorkingDirectory().exists()) {
				this.defaultsManager.setWorkingDirectory(new File(Startup
						.readStartupParameters().getRootDir()));
			}
		}
		return this.defaultsManager;
	}

	/**
	 * Returns a file path that could be used as initial path for file dialogs 
	 * etc. 
	 * 
	 * One of the following path' will be returned (descending priority)
	 * <ul>
	 *  <li>the path in the defaults (if any)</li>
	 *  <li>the directory <code>&lt;eve.root&gt;/eve/scml/</code> (if exists)</li>
	 *  <li>the directory <code>&lt;eve.root&gt;/eve/</code></li>
	 * </ul>
	 * @return an initial file path for e.g. file dialogs
	 * @since 1.29
	 */
	public String getFilterPath() {
		// if defaults working directory is set return it
		File defaultsDirectory = Activator.getDefault().getDefaultsManager()
				.getWorkingDirectory();
		if (defaultsDirectory.isDirectory()) {
			LOGGER.debug(
					"filter path is: " + defaultsDirectory.getAbsolutePath());
			return defaultsDirectory.getAbsolutePath();
		}
		// if <eve.root>/eve/scml/ exists return it
		File eveDir = new File(Startup.readStartupParameters().getRootDir() 
				+ "eve/");
		File scmlDir = new File(eveDir.getAbsolutePath() + "/scml/");
		if (scmlDir.isDirectory()) {
			LOGGER.debug("filter path is: " + scmlDir.getAbsolutePath());
			return scmlDir.getAbsolutePath();
		}
		// neither defaults working directory set nor does <eve.root>/eve/scml
		// exist -> return <eve.root>/eve/ (which has to exist)
		LOGGER.debug("filter path is: " + eveDir.getAbsolutePath());
		return eveDir.getAbsolutePath();
	}
	
	public static File getDefaultDeviceDefinition() {
		try {
			URL url = new URL(
				"platform:/plugin/de.ptb.epics.eve.resources/cfg/default.xml");
			return new File(FileLocator.toFileURL(url).toURI());
		} catch (MalformedURLException e1) {
			LOGGER.error(e1.getMessage(), e1);
		} catch (URISyntaxException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * @since 1.31
	 */
	public String getLoadedDeviceDefinition() {
		return Startup.getLoadedDeviceDefinition();
	}
	
	/**
	 * Returns the XML schema definition.
	 * 
	 * @return the XML schema definition
	 */
	public static File getXMLSchema() {
		try {
			URL url = new URL(
				"platform:/plugin/de.ptb.epics.eve.resources/cfg/schema.xsd");
			return new File(FileLocator.toFileURL(url).toURI());
		} catch (MalformedURLException e1) {
			LOGGER.error(e1.getMessage(), e1);
		} catch (URISyntaxException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public static Version getSchemaVersion() {
		if (schemaVersion != null) {
			return schemaVersion;
		}
		try {
			URL url = new URL(
				"platform:/plugin/de.ptb.epics.eve.resources/cfg/schema.xsd");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new File(FileLocator.toFileURL(
					url).toURI()));
			Node node = document.getElementsByTagName("schema").item(0);
			String versionString = node.getAttributes().getNamedItem("version")
					.getNodeValue();
			schemaVersion = new Version(
					Integer.parseInt(versionString.split("\\.")[0]), 
					Integer.parseInt(versionString.split("\\.")[1]));
			return schemaVersion;
		} catch(ParserConfigurationException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (MalformedURLException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (SAXException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (URISyntaxException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * Returns the defaults schema definition.
	 * 
	 * @return the defaults schema definition
	 */
	public static File getDefaultsSchema() {
		try {
			URL url;
			url = new URL(
				"platform:/plugin/de.ptb.epics.eve.resources/cfg/defaults.xsd");
			return new File(FileLocator.toFileURL(url).toURI());
		} catch (MalformedURLException e1) {
			LOGGER.error(e1.getMessage(), e1);
		} catch (URISyntaxException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * Returns a scml file for testing.
	 * 
	 * @return a scml file for testing
	 * @author Marcus Michalsky
	 * @since 1.18
	 */
	public static File getSCMLTestFile() {
		try {
			URL url;
			url = new URL(
				"platform:/plugin/de.ptb.epics.eve.resources/cfg/test.scml");
			return new File(FileLocator.toFileURL(url).toURI());
		} catch (MalformedURLException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (URISyntaxException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * Returns the logging configuration either for debug or production.
	 * 
	 * @param debug <code>true</code> if debug mode, <code>false</code> otherwise
	 * @return the logging configuration or <code>null</code> if an error occurred
	 */
	public static File getLoggerConfiguration(boolean debug) {
		try {
			URL url;
			if (debug) {
				url = new URL(
					"platform:/plugin/de.ptb.epics.eve.resources/cfg/logger-debug.xml");
			} else {
				url = new URL(
					"platform:/plugin/de.ptb.epics.eve.resources/cfg/logger.xml");
			}
			return  new File(FileLocator.toFileURL(url).toURI());
		} catch (MalformedURLException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (URISyntaxException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		} 
		return null;
	}
}
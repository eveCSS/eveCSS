package de.ptb.epics.eve.editor;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.util.ILogger;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.MeasuringStation;
import de.ptb.epics.eve.resources.init.Parameters;
import de.ptb.epics.eve.resources.init.Startup;
import de.ptb.epics.eve.data.measuringstation.filter.ExcludeFilter;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.defaults.DefaultsManager;
import de.ptb.epics.eve.editor.jobs.defaults.LoadDefaults;
import de.ptb.epics.eve.editor.jobs.defaults.SaveDefaults;
import de.ptb.epics.eve.editor.logging.EclipseLogListener;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
	
	/** 
	 * The unique identifier of the plug in 
	 */
	public static final String PLUGIN_ID = "de.ptb.epics.eve.editor";
	
	// The shared instance
	private static Activator plugin;
	
	private static Logger logger = Logger.getLogger(Activator.class.getName());
	
	/*
	 * Listener for the Eclipse Logger. catches logs of eclipse and forwards 
	 * them to the log4j logger. (only logs with level 
	 * org.eclipse.core.runtime.IStatus.WARNING or
	 * org.eclipse.core.runtime.IStatus.ERROR are forwarded).
	 */
	private ILogListener logListener;
	
	private IMeasuringStation measuringStation;
	private ExcludeFilter excludeFilter;
	private DefaultsManager defaultsManager;
	private File schemaFile;
	
	private Parameters startupParams;

	// 
	private EveEditorPerspectiveListener eveEditorPerspectiveListener;
	
	// used to handle save on close
	private WorkbenchListener workbenchListener;
	
	private String defaultWindowTitle;
	
	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
		try {
			Version version = Platform.getProduct().getDefiningBundle()
					.getVersion();
			this.defaultWindowTitle = "eveCSS v" + version.getMajor() + "."
					+ version.getMinor() + "." + version.getMicro();
		} catch (NullPointerException e) {
			this.defaultWindowTitle = "eveCSS";
		}
		eveEditorPerspectiveListener = new EveEditorPerspectiveListener();
		workbenchListener = new WorkbenchListener();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		
		startupParams = Startup.readStartupParameters();
		if (startupParams.useDefaultDevices()) {
			Startup.configureLogging("/tmp/", startupParams.isDebug(), logger);
			logger.info("No 'eve.root' given, logging to '/tmp/eve/'");
		} else {
			Startup.checkRootDir(startupParams.getRootDir());
			Startup.configureLogging(startupParams.getRootDir(), 
					startupParams.isDebug(), logger);
		}
		this.schemaFile = Startup.loadSchemaFile(logger);
		this.measuringStation = Startup.loadMeasuringStation(logger);
		
		this.excludeFilter = new ExcludeFilter();
		this.excludeFilter.setSource(this.measuringStation);
		
		loadImagesColorsAndFonts();
		loadDefaults();
		startupReport();
		
		logListener = new EclipseLogListener();
		Platform.addLogListener(logListener);
		if(logger.isDebugEnabled()) {
			getWorkbench().getActiveWorkbenchWindow().getSelectionService().
				addSelectionListener(new SelectionTracker());
		}
		
		org.eclipse.core.databinding.util.Policy.setLog(new ILogger() {
			
			@Override
			public void log(IStatus status) {
				logger.debug(status.getMessage());
			}
		});
		
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().
				addPerspectiveListener(eveEditorPerspectiveListener);
		PlatformUI.getWorkbench().addWorkbenchListener(workbenchListener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		Platform.removeLogListener(logListener);
		logListener = null;
		plugin = null;
		
		super.stop(context);
	}
	
	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	/**
	 * 
	 * @return windowTitle
	 */
	public String getDefaultWindowTitle() {
		return this.defaultWindowTitle;
	}
	
	/**
	 * Returns the root directory the plug in is working on.
	 * 
	 * @return the root directory the plug in is working on
	 */
	public String getRootDirectory() {
		return this.startupParams.getRootDir();
	}
	
	/**
	 * 
	 * @return measuring station
	 */
	public IMeasuringStation getMeasuringStation() {
		// maybe rename this method, see also #1507
		return this.excludeFilter;
	}

	/**
	 * Returns the (unfiltered) device definition as loaded from file.
	 * @return the device definition as loaded from file
	 * @since 1.22
	 */
	public IMeasuringStation getDeviceDefinition() {
		return this.measuringStation;
	}
	
	/**
	 * 
	 * @return schema
	 */
	public File getSchemaFile() {
		return this.schemaFile;
	}
	
	/**
	 * 
	 * @return defaults
	 */
	public DefaultsManager getDefaults() {
		return this.defaultsManager;
	}
	
	/*
	 * 
	 */
	private void loadDefaults() {
		this.defaultsManager = new DefaultsManager();
		File defaultsFile = ResourcesPlugin.getWorkspace().getRoot()
				.getLocation().append("/defaults.xml").toFile();
		if (defaultsFile.exists()) {
			logger.info("found defaults file for user "
					+ System.getProperty("user.name") + " (" + 
					ResourcesPlugin.getWorkspace().getRoot().getLocation().
					lastSegment() + ")");
		} else {
			logger.info("no defaults file for user "
					+ System.getProperty("user.name") + " (" + 
					ResourcesPlugin.getWorkspace().getRoot().getLocation().
					lastSegment() + ")");
		}
		Job job = new LoadDefaults("Loading Defaults", this.defaultsManager,
				defaultsFile,
				de.ptb.epics.eve.resources.Activator.getDefaultsSchema());
		job.schedule();
	}
	
	/**
	 * 
	 * @param scanDescription
	 */
	public void saveDefaults(ScanDescription scanDescription) {
		File defaultsFile = ResourcesPlugin.getWorkspace().getRoot()
				.getLocation().append("/defaults.xml").toFile();
		File schemaFile = de.ptb.epics.eve.resources.Activator
				.getDefaultsSchema();
		Job job = new SaveDefaults("Saving Defaults", this.defaultsManager,
				scanDescription, defaultsFile, schemaFile);
		job.schedule();
	}
	
	/*
	 * 
	 */
	private void startupReport() {
		if(logger.isInfoEnabled()) {
			logger.info("debug mode: " + startupParams.isDebug());
			logger.info("eve.root set: " + !startupParams.useDefaultDevices());
			logger.info("root directory: " + startupParams.getRootDir());
			logger.info("measuring station: " + 
				((MeasuringStation)measuringStation).getName() + " (" +
				measuringStation.getVersion() + ")");
			logger.info("measuring station location: " + 
					measuringStation.getLoadedFileName());
			logger.info("schema file: " + 
					measuringStation.getSchemaFileName());
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			logger.info("workspace: " + workspace.getRoot().getLocation().
					toFile().getAbsolutePath());
		}
	}
	
	private void loadImagesColorsAndFonts() {
		ImageRegistry imagereg = getImageRegistry();
		imagereg.put("MOTOR", imageDescriptorFromPlugin(
				PLUGIN_ID, "icons/devices/motor.gif").createImage());
		imagereg.put("AXIS", imageDescriptorFromPlugin(
				PLUGIN_ID, "icons/devices/axis.gif").createImage());
		imagereg.put("MOTORSAXES", imageDescriptorFromPlugin(
				PLUGIN_ID, "icons/devices/motorsaxes.gif").createImage());
		imagereg.put("DETECTOR", imageDescriptorFromPlugin(
				PLUGIN_ID, "icons/devices/detector.gif").createImage());
		imagereg.put("CHANNEL", imageDescriptorFromPlugin(
				PLUGIN_ID, "icons/devices/channel.gif").createImage());
		imagereg.put("DETECTORSCHANNELS", imageDescriptorFromPlugin(
				PLUGIN_ID, "icons/devices/detectorschannels.gif"));
		imagereg.put("CLASS", imageDescriptorFromPlugin(
				PLUGIN_ID, "icons/devices/class.png").createImage());
		imagereg.put("DEVICE", imageDescriptorFromPlugin(
				PLUGIN_ID, "icons/devices/device.gif").createImage());
		imagereg.put("DEVICES", imageDescriptorFromPlugin(
				PLUGIN_ID, "icons/devices/devices.gif").createImage());
		imagereg.put("OPTION", imageDescriptorFromPlugin(
				PLUGIN_ID, "icons/devices/option.gif").createImage());
		imagereg.put("PLOT", imageDescriptorFromPlugin(
				PLUGIN_ID, "icons/devices/plot.gif").createImage());
		imagereg.put("RENAME", imageDescriptorFromPlugin(
				PLUGIN_ID, "icons/rename.gif").createImage());
		imagereg.put("CHECKED", imageDescriptorFromPlugin(
				PLUGIN_ID, "icons/checked.gif").createImage());
		imagereg.put("UNCHECKED", imageDescriptorFromPlugin(
				PLUGIN_ID, "icons/unchecked.gif").createImage());
		
		imagereg.put("ADD_SM", imageDescriptorFromPlugin(
				PLUGIN_ID, "icons/add_stat.gif").createImage());
		
		imagereg.put("MAXIMIZE", imageDescriptorFromPlugin(
				PLUGIN_ID, "icons/maximize.gif").createImage());
		imagereg.put("RESTORE", imageDescriptorFromPlugin(
				PLUGIN_ID, "icons/thin_restore_view.gif").createImage());
		imagereg.put("SAVE", imageDescriptorFromPlugin(
				PLUGIN_ID, "icons/save_edit.gif").createImage());
		
		imagereg.put("CHAIN", imageDescriptorFromPlugin(
				PLUGIN_ID, "icons/devices/chain.gif").createImage());
		imagereg.put("SCANMODULE", imageDescriptorFromPlugin(
				PLUGIN_ID, "icons/devices/scanmodule.gif"));
	}
}
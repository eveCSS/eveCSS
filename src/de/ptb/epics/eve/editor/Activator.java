package de.ptb.epics.eve.editor;

import java.io.File;

import org.apache.log4j.xml.DOMConfigurator;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.processors.MeasuringStationLoader;
import de.ptb.epics.eve.preferences.PreferenceConstants;
import de.ptb.epics.eve.data.measuringstation.filter.ExcludeFilter;
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
	
	private ILogListener logListener;
	
	private IMeasuringStation measuringStation;
	
	private ExcludeFilter excludeFilter;
		
	private File schemaFile;
		
	private EveEditorPerspectiveListener eveEditorPerspectiveListener;
	
	/**
	 * The constructor
	 */
	public Activator() {
		// activate logging 
		DOMConfigurator.configure(System.getProperty("user.home") + "/logger.xml");
		
		eveEditorPerspectiveListener = new EveEditorPerspectiveListener();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		logListener = new EclipseLogListener();
		Platform.addLogListener(logListener);
		
		final String measuringStationDescription = 
				de.ptb.epics.eve.preferences.Activator.getDefault().
					getPreferenceStore().getString(
					PreferenceConstants.P_DEFAULT_MEASURING_STATION_DESCRIPTION);
		
		if(!measuringStationDescription.equals("")) { 
			final int lastSeperatorIndex = 
				measuringStationDescription.lastIndexOf(File.separatorChar);
			final String schemaFileLocation = 
				measuringStationDescription.substring(0, lastSeperatorIndex + 1) 
											+ "scml.xsd";
			this.schemaFile = new File(schemaFileLocation);
		}
		
		if( !measuringStationDescription.equals( "" ) ) {
			final File measuringStationDescriptionFile = new File( measuringStationDescription );
			if( this.schemaFile.exists()) {
			if( measuringStationDescriptionFile.exists() ) {
				try {
					final MeasuringStationLoader measuringStationLoader = new MeasuringStationLoader( this.schemaFile );
					measuringStationLoader.load( measuringStationDescriptionFile );
					this.measuringStation = measuringStationLoader.getMeasuringStation();
				} catch( final Throwable th ) {
					this.measuringStation = null;
				}
			} else {
				this.measuringStation = null;
			}
			} else {
				this.schemaFile = null;
			}
		}
		this.excludeFilter = new ExcludeFilter();
		this.excludeFilter.setSource( this.measuringStation );
		
		
		
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().
				addPerspectiveListener(eveEditorPerspectiveListener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().
		removePerspectiveListener(eveEditorPerspectiveListener);
		eveEditorPerspectiveListener = null;
		
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
	 * @return measuring station
	 */
	public IMeasuringStation getMeasuringStation() {
		return this.excludeFilter;
	}

	/**
	 * 
	 * @return schema
	 */
	public File getSchemaFile() {
		return this.schemaFile;
	}	
}
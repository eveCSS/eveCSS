package de.ptb.epics.eve.util.ui;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.ptb.epics.eve.util.ui"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
		this.loadColorsAndFonts();
		plugin = this;
	}

	/**
	 * {@inheritDoc}
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/**
	 * {@inheritDoc} 
	 */
	public void stop(BundleContext context) throws Exception {
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

	/*
	 * 
	 */
	private void loadColorsAndFonts() {
		ImageRegistry imagereg = getImageRegistry();
		imagereg.put("SORT_ASCENDING", imageDescriptorFromPlugin(
		PLUGIN_ID, "icons/sort_ascending.gif").createImage());
		imagereg.put("SORT_DESCENDING", imageDescriptorFromPlugin(
				PLUGIN_ID, "icons/sort_descending.gif").createImage());
	}
}
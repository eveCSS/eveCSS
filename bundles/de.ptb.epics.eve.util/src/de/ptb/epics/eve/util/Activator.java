package de.ptb.epics.eve.util;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author Marcus Michalsky
 * @since 1.5
 */
public class Activator extends AbstractUIPlugin {

	/** the unique identifier of the plugin */
	public static final String PLUGIN_ID = "de.ptb.epics.eve.util";
	
	// The shared instance
	private static Activator plugin;

	/**
	 * 
	 */
	public Activator() {
		this.loadColorsAndFonts();
		plugin = this;
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
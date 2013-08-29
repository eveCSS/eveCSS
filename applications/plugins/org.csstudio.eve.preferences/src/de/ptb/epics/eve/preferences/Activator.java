package de.ptb.epics.eve.preferences;

import java.io.File;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	/** The plug-in ID */
	public static final String PLUGIN_ID = "de.ptb.epics.eve.preferences";

	// The shared instance
	private static Activator plugin;
	
	private String rootDir;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		this.rootDir = null;
		readStartupParameters();
		if(!checkRootDir()) {
			this.rootDir = null;
		}
		plugin = this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
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
	private void readStartupParameters() {
		String[] args = Platform.getCommandLineArgs();
		int i = 0;
		while (i < args.length) {
			if (args[i].equals("-eve.root")) {
				i++;
				rootDir = args[i];
			}
			i++;
		}
	}
	
	/**
	 * Returns the root directory the plug in is working on.
	 * 
	 * @return the root directory the plug in is working on
	 */
	public String getRootDirectory() {
		return rootDir;
	}
	
	/*
	 * Checks whether the directory (given by parameter -rootdir) contains a 
	 * folder named eve.
	 * 
	 * @return <code>true</code> if the root directory contains a folder named 
	 * 			eve, <code>false</code> otherwise
	 */
	private boolean checkRootDir() {
		if (rootDir == null) {
			return false;
		}
		if(!rootDir.endsWith("/")) {
			rootDir += "/";
		}
		String path = rootDir;
		File file = new File(path + "eve/");
		if(!file.exists()) {
			return false;
		}
		return true;
	}
}
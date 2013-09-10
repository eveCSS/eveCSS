package de.ptb.epics.eve.resources.init;

/**
 * Encapsulates startup parameters into a single object.
 * A parameter which is not set is <code>null</code> or false, 
 * except for the root directory, which then returns a default.
 * 
 * @author Marcus Michalsky
 * @since 1.14
 */
public class Parameters {
	private String rootDir;
	private boolean debug;
	
	public Parameters() {
		this.rootDir = null;
		this.debug = false;
	}

	/**
	 * Returns the root directory.
	 * 
	 * @return the rootDir
	 */
	public String getRootDir() {
		return this.useDefaultDevices() 
				? System.getProperty("user.home") 
				: this.rootDir;
	}

	/**
	 * @param rootDir the rootDir to set
	 */
	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}

	public boolean useDefaultDevices() {
		return this.rootDir == null;
	}
	
	/**
	 * @return the debug
	 */
	public boolean isDebug() {
		return this.debug;
	}

	/**
	 * @param debug the debug to set
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
}
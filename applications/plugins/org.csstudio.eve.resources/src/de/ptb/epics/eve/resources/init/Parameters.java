package de.ptb.epics.eve.resources.init;

/**
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
	 * @return the rootDir
	 */
	public String getRootDir() {
		return this.rootDir;
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
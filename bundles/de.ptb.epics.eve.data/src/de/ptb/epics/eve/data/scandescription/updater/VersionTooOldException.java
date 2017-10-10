package de.ptb.epics.eve.data.scandescription.updater;

import de.ptb.epics.eve.util.data.Version;

/**
 * @author Marcus Michalsky
 * @since 1.27.14
 */
public class VersionTooOldException extends Exception {
	private Version source;
	
	/**
	 * @param source the version of the source
	 */
	public VersionTooOldException(Version source) {
		this.source = source;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMessage() {
		return "Given version (" + this.source + 
				") is too old. Must be at least version (2.3)";
	}
}
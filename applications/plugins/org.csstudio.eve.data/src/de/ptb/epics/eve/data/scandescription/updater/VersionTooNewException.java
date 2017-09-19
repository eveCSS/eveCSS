package de.ptb.epics.eve.data.scandescription.updater;

import de.ptb.epics.eve.util.data.Version;

/**
 * @author Marcus Michalsky
 * @since 1.27.14
 */
public class VersionTooNewException extends Exception {
	private Version source;
	private Version target;
	
	/**
	 * @param source the version of the source
	 * @param target the version currently used by the application
	 */
	public VersionTooNewException(Version source, Version target) {
		this.source = source;
		this.target = target;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMessage() {
		return "Given version (" + this.source + 
				") is newer than the version used by the application " +
				"(" + this.target + ")";
	}
}
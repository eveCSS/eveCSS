package de.ptb.epics.eve.data.scandescription.defaults.updater;

import de.ptb.epics.eve.data.scandescription.updater.Updater;

/**
 * DefaultsUpdater updates defaults of older versions to the version currently 
 * used.
 * 
 * @author Marcus Michalsky
 * @since 1.22
 */
public class DefaultsUpdater extends Updater {

	/**
	 * Constructor.
	 * 
	 * Adds defaults update patches.
	 */
	public DefaultsUpdater() {
		super();
		this.addPatch(Patch1o0T1o1.getInstance());
		this.addPatch(Patch1o1T2o0.getInstance());
		this.addPatch(Patch2o0T3o0.getInstance());
	}
}
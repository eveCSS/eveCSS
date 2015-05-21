package de.ptb.epics.eve.data.scandescription.updater.patches;

import de.ptb.epics.eve.data.scandescription.updater.Updater;

/**
 * @author Marcus Michalsky
 * @since 1.22
 */
public class SCMLUpdater extends Updater {

	/**
	 * Constructor.
	 * 
	 * Adds SCML update patches.
	 */
	public SCMLUpdater() {
		super();
		this.addPatch(Patch2o3T3o0.getInstance());
		this.addPatch(Patch3o0T3o1.getInstance());
		this.addPatch(Patch3o1T3o2.getInstance());
		this.addPatch(Patch3o2T4o0.getInstance());
	}
}
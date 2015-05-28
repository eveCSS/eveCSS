package de.ptb.epics.eve.ecp1.client.interfaces;

/**
 * @author ?
 * @since 1.0
 */
public interface IEngineVersionListener {
	
	/**
	 * Gets called if the engine status has changed.
	 * 
	 * @param version engine version 
	 * @param revision engine revision
	 * @param patchlevel engine patchlevel
	 */
	void engineVersionChanged(final int version, final int revision, final int patchlevel);
}
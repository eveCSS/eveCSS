package de.ptb.epics.eve.ecp1.helper.statustracker;

/**
 * @author Marcus Michalsky
 * @since 1.28
 */
public interface EngineState {

	/**
	 * Returns whether the engine is connected
	 * @return <code>true</code> if the engine is connected, <code>false</code> otherwise
	 */
	boolean isConnected();
}
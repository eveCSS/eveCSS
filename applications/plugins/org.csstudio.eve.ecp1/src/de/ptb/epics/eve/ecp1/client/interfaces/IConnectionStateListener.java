package de.ptb.epics.eve.ecp1.client.interfaces;

/**
 * @author ?
 * @since 1.0
 */
public interface IConnectionStateListener {

	/**
	 * Gets called if the engine gets connected.
	 */
	public void stackConnected();
	
	/**
	 * Gets called if the engine gets disconnected.
	 */
	public void stackDisconnected();
}
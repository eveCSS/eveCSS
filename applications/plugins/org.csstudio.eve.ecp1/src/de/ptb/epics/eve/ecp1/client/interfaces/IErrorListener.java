package de.ptb.epics.eve.ecp1.client.interfaces;

/**
 * @author ?
 * @since 1.0
 */
public interface IErrorListener {

	/**
	 * Gets called if an error has occurred.
	 * 
	 * @param error the error that occurred.
	 */
	public void errorOccured(
			final de.ptb.epics.eve.ecp1.client.model.Error error);
}
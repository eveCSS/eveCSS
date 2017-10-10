package de.ptb.epics.eve.ecp1.client.interfaces;

import de.ptb.epics.eve.ecp1.client.model.Request;

/**
 * @author ?
 * @author Marcus Michalsky
 * @since 1.0
 */
public interface IRequestListener {
	
	/**
	 * Used by plugins to communicate with the users. Viewers may popup a 
	 * request-box and send an answer with the appropriate request-id via 
	 * the sendXYZAnswer methods offered by the given 
	 * {@link de.ptb.epics.eve.ecp1.client.model.Request}.
	 *
	 * @param request the request
	 */
	void request(final Request request);

	/**
	 * Gets called to cancel outstanding requests (another answer of a 
	 * request has been received by the engine).
	 * 
	 * @param request the request
	 */
	void cancelRequest(final Request request);
}
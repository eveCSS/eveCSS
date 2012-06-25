package de.ptb.epics.eve.ecp1.client.interfaces;

import de.ptb.epics.eve.ecp1.types.EngineStatus;

/**
 * @author ?
 * @since 1.0
 */
public interface IEngineStatusListener {
	
	/**
	 * Gets called if the engine status has changed.
	 * 
	 * @param engineStatus the engine status as in 
	 * 		{@link de.ptb.epics.eve.ecp1.types.EngineStatus}
	 * @param xmlName the name of the xml file
	 * @param repeatCount the repeat count
	 */
	public void engineStatusChanged(final EngineStatus engineStatus,
			final String xmlName, final int repeatCount);
}
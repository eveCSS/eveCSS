package de.ptb.epics.eve.ecp1.client.interfaces;

import de.ptb.epics.eve.ecp1.commands.ChainStatusCommand;

/**
 * @author ?
 * @since 1.0
 */
public interface IChainStatusListener {

	/**
	 * Gets called if the chain status has changed.
	 * 
	 * @param chainFullStatusCommand the chain status command
	 */
	void chainStatusChanged(final ChainStatusCommand chainStatusCommand);
}
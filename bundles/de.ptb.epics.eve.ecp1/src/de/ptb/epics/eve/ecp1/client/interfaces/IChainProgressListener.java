package de.ptb.epics.eve.ecp1.client.interfaces;

import de.ptb.epics.eve.ecp1.commands.ChainProgressCommand;

/**
 * @author ?
 * @since 1.0
 */
public interface IChainProgressListener {

	/**
	 * Gets called if the chain status has changed.
	 * 
	 * @param chainProgressCommand the chain progress command
	 */
	void chainProgressChanged(final ChainProgressCommand chainProgressCommand);
}
package de.ptb.epics.eve.ecp1.client.interfaces;

import de.ptb.epics.eve.ecp1.intern.ChainStatusCommand;

public interface IChainStatusListener {

	public void chainStatusChanged( final ChainStatusCommand chainStatusCommand );
}

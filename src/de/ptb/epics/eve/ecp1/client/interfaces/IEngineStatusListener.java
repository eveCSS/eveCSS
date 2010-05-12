package de.ptb.epics.eve.ecp1.client.interfaces;

import de.ptb.epics.eve.ecp1.intern.EngineStatus;

public interface IEngineStatusListener {
	public void engineStatusChanged( final EngineStatus engineStatus );
}

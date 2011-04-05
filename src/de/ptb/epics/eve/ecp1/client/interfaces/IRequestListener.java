package de.ptb.epics.eve.ecp1.client.interfaces;

import de.ptb.epics.eve.ecp1.client.model.Request;

public interface IRequestListener {
	public void request( final Request request );
	public void cancelRequest( final Request request );
}

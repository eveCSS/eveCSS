package de.ptb.epics.eve.viewer;

import de.ptb.epics.eve.ecp1.client.interfaces.IErrorListener;
import de.ptb.epics.eve.ecp1.client.model.Error;

public class EngineErrorReader implements IErrorListener {

	public void errorOccured( final Error error ) {
		
		Activator.getDefault().getMessagesContainer().addMessage( new ViewerMessage( error ) );

	}

}

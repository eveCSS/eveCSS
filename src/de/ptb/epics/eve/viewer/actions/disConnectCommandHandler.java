package de.ptb.epics.eve.viewer.actions;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.messages.MessageTypes;
import de.ptb.epics.eve.viewer.messages.ViewerMessage;

/**
 * @author eden
 */
public class disConnectCommandHandler extends AbstractHandler {

	/**
	 * 
	 */
	public disConnectCommandHandler() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		if( Activator.getDefault().getEcp1Client().isRunning() ) {
			try {
				Activator.getDefault().getEcp1Client().close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Activator.getDefault().getMessagesContainer().addMessage(
				new ViewerMessage(MessageTypes.INFO, "disconnected engine"));
		}
		return null;
	}
}
package de.ptb.epics.eve.viewer.handler.engine;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.views.messagesview.Levels;
import de.ptb.epics.eve.viewer.views.messagesview.ViewerMessage;

/**
 * <code>DisconnectCommandHandler</code> is the default command handler of the 
 * disconnect command.
 * 
 * @author Jens Eden
 * @author Marcus Michalsky
 */
public class Disconnect extends AbstractHandler {

	private static Logger logger = 
			Logger.getLogger(Disconnect.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		if(Activator.getDefault().getEcp1Client().isRunning()) {
			try {
				Activator.getDefault().getEcp1Client().disconnect();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
			Activator.getDefault().getMessageList().add(
				new ViewerMessage(Levels.INFO, "Disconnected from Engine"));
		}
		return null;
	}
}
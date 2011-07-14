package de.ptb.epics.eve.viewer.handler.engine;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import de.ptb.epics.eve.preferences.PreferenceConstants;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.messages.MessageTypes;
import de.ptb.epics.eve.viewer.messages.ViewerMessage;

/**
 * <code>ConnectCommandHandler</code> is the default command handler of the 
 * connect command.
 * <p>
 * It tries to connect to the engine at the ip address and port set in the 
 * preferences.
 * 
 * @author Jens Eden
 * @author Marcus Michalsky
 */
public class Connect extends AbstractHandler {

	private static Logger logger = 
			Logger.getLogger(Connect.class.getName());

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		if(!Activator.getDefault().getEcp1Client().isRunning()) {
			int port = 0;
			String EngineString = de.ptb.epics.eve.preferences.Activator.
				getDefault().getPreferenceStore().getString(
				PreferenceConstants.P_DEFAULT_ENGINE_ADDRESS);

			int index  = EngineString.lastIndexOf(":");
			if (index != -1) {
				port = Integer.parseInt(EngineString.substring(index + 1));
				EngineString = EngineString.substring(0, index);
			}
			
			if((port > 0) && (EngineString.length() > 1)) {
				Activator.getDefault().getMessagesContainer().addMessage(
					new ViewerMessage(MessageTypes.INFO,
						"Trying to connect to: " + EngineString + 
						" (Port: " + port + ")."));
				try {
					java.net.InetAddress localMachine = 
						java.net.InetAddress.getLocalHost();
					Activator.getDefault().getEcp1Client().connect(
							new InetSocketAddress(EngineString, port), 
							System.getProperty("user.name")+"@"+
								localMachine.getHostName());
					Activator.getDefault().getMessagesContainer().addMessage(
						new ViewerMessage(MessageTypes.INFO, 
							"Connection established to: " + EngineString + "."));
				} catch(final IOException e) {
					Activator.getDefault().getMessagesContainer().addMessage(
						new ViewerMessage(MessageTypes.ERROR, 
							"Cannot establish connection! " + e.getMessage() + "."));
					logger.error(e.getMessage(), e);
				}
			} else {
				Activator.getDefault().getMessagesContainer().addMessage(
						new ViewerMessage(MessageTypes.ERROR, 
						"Could not establish connection! Please provide a " +
						"valid address in CSS -> Preferences... -> " +
						"CSS Applications -> Eve"));
				logger.warn("Could not establish connection to engine due to " +
						"errors in the preferences.");
			}
		}
		return null;
	}
}
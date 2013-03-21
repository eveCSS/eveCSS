package de.ptb.epics.eve.viewer.handler.engine;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.messages.Levels;
import de.ptb.epics.eve.viewer.messages.ViewerMessage;
import de.ptb.epics.eve.viewer.preferences.PreferenceConstants;

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		if(!Activator.getDefault().getEcp1Client().isRunning()) {
			
			String EngineHost = Activator.getDefault().getPreferenceStore().
					getString(PreferenceConstants.P_DEFAULT_ENGINE_ADDRESS);

			Integer EnginePort = Activator.getDefault().getPreferenceStore()
					.getInt(PreferenceConstants.P_DEFAULT_ENGINE_PORT);
			
			try {
				java.net.InetAddress localMachine = 
						java.net.InetAddress.getLocalHost();
					Activator.getDefault().getEcp1Client().connect(
							new InetSocketAddress(EngineHost, EnginePort), 
							System.getProperty("user.name")+"@"+
								localMachine.getHostName());
					Activator.getDefault().getMessagesContainer().addMessage(
						new ViewerMessage(Levels.INFO, 
							"Connection established to: " + EngineHost + ":" + 
									Integer.toString(EnginePort) + "."));
			} catch(IOException e) {
				Activator.getDefault().getMessagesContainer().addMessage(
						new ViewerMessage(Levels.ERROR,
								"Cannot establish connection! "
										+ e.getMessage() + "."));
			}
		}
		return null;
	}
}
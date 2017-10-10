package de.ptb.epics.eve.viewer.handler.engine;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.preferences.PreferenceConstants;
import de.ptb.epics.eve.viewer.views.messagesview.Levels;
import de.ptb.epics.eve.viewer.views.messagesview.ViewerMessage;

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
			
			String engineHost = Activator.getDefault().getPreferenceStore().
					getString(PreferenceConstants.P_DEFAULT_ENGINE_ADDRESS);

			Integer enginePort = Activator.getDefault().getPreferenceStore()
					.getInt(PreferenceConstants.P_DEFAULT_ENGINE_PORT);
			
			try {
				java.net.InetAddress localMachine = 
						java.net.InetAddress.getLocalHost();
					Activator.getDefault().getEcp1Client().connect(
							new InetSocketAddress(engineHost, enginePort), 
							System.getProperty("user.name")+"@"+
								localMachine.getHostName());
					Activator.getDefault().getMessageList().add(
						new ViewerMessage(Levels.INFO, 
							"Connection established to: " + engineHost + ":" + 
									Integer.toString(enginePort) + "."));
			} catch(IOException e) {
				Activator.getDefault().getMessageList().add(
						new ViewerMessage(Levels.ERROR,
								"Cannot establish connection! "
										+ e.getMessage() + "."));
			}
		}
		return null;
	}
}
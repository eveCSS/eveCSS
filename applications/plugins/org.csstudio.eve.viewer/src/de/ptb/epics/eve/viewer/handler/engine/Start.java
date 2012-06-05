package de.ptb.epics.eve.viewer.handler.engine;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

import de.ptb.epics.eve.preferences.PreferenceConstants;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.messages.Levels;
import de.ptb.epics.eve.viewer.messages.ViewerMessage;

/**
 * <code>Start</code> is the default command handler of the engine start 
 * command.
 * <p>
 * It starts an engine process at the location defined in the preferences.
 * <br><br>
 * Intended to be used by the selection listener of the start button in 
 * the engine view.
 * 
 * @author Marcus Michalsky
 * @since 0.4.1
 */
public class Start extends AbstractHandler {

	private static Logger logger = Logger.getLogger(Start.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		String engineLocation = de.ptb.epics.eve.preferences.Activator.
				getDefault().getPreferenceStore().getString(
				PreferenceConstants.P_DEFAULT_ENGINE_LOCATION);
		
		int enginePort = 0;
		String engineHost = de.ptb.epics.eve.preferences.Activator.
			getDefault().getPreferenceStore().getString(
			PreferenceConstants.P_DEFAULT_ENGINE_ADDRESS);

		int index  = engineHost.lastIndexOf(":");
		if (index != -1) {
			enginePort = Integer.parseInt(engineHost.substring(index + 1));
			engineHost = engineHost.substring(0, index);
		} else {
			logger.warn("no valid port name");
			return null;
		}
		
		String[] parameters = {"-p", Integer.toString(enginePort)};
		
		Runtime runtime = Runtime.getRuntime();
		
		try {
			if(engineLocation.isEmpty()) {
				String message = 
					"Engine not started due to empty preferences entry!";
				logger.error(message);
				Activator.getDefault().getMessagesContainer().addMessage(
						new ViewerMessage(Levels.ERROR, message));
			} else {
				runtime.exec(engineLocation, parameters, null);
				String message = "started engine process at: " + engineLocation +
				"(Port: " + enginePort + ")";
				logger.info(message);
				Activator.getDefault().getMessagesContainer().addMessage(
						new ViewerMessage(Levels.INFO, message));
			}
			
			Thread.sleep(2000);
			
			IHandlerService handlerService = (IHandlerService) 
					PlatformUI.getWorkbench().getService(IHandlerService.class);
			handlerService.executeCommand(
					"de.ptb.epics.eve.viewer.connectCommand", null);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		} catch (NotDefinedException e) {
			logger.error(e.getMessage(), e);
		} catch (NotEnabledException e) {
			logger.error(e.getMessage(), e);
		} catch (NotHandledException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
}
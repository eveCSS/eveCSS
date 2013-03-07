package de.ptb.epics.eve.viewer.handler.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.messages.Levels;
import de.ptb.epics.eve.viewer.messages.ViewerMessage;
import de.ptb.epics.eve.viewer.preferences.EngineExecMacros;
import de.ptb.epics.eve.viewer.preferences.PreferenceConstants;

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
		
		String engineHost = Activator.getDefault().getPreferenceStore().
				getString(PreferenceConstants.P_DEFAULT_ENGINE_ADDRESS);
		
		Integer enginePort = Activator.getDefault().getPreferenceStore()
				.getInt(PreferenceConstants.P_DEFAULT_ENGINE_PORT);
		
		String engineLocation = Activator.getDefault().getPreferenceStore().
				getString(PreferenceConstants.P_DEFAULT_ENGINE_LOCATION);
		
		String engineParameters = Activator.getDefault().getPreferenceStore()
				.getString(PreferenceConstants.P_DEFAULT_ENGINE_PARAMETERS);

		logger.debug("EngineParameters (before substitution): " + engineParameters);
		for (EngineExecMacros macro : EngineExecMacros.values()) {
			engineParameters = engineParameters.replaceAll(
					java.util.regex.Pattern.quote(macro.toString()),
					EngineExecMacros.substituteMacro(macro));
		}
		logger.debug("EngineParameters (after substitution): " + engineParameters);
		
		List<String> parameters = new ArrayList<String>();
		parameters.add("-p");
		parameters.add(Integer.toString(enginePort));
		for (String s : engineParameters.split(" ")) {
			parameters.add(s.trim());
		}
		
		logger.debug("# parameters: " + parameters.size());
		
		Object[] parametersArray = parameters.toArray();
		String[] stringArray = Arrays.copyOf(parametersArray,
				parametersArray.length, String[].class); 
		
		Runtime runtime = Runtime.getRuntime();
		
		try {
			if(engineLocation.isEmpty()) {
				String message = 
					"Engine not started due to empty preferences entry!";
				logger.error(message);
				Activator.getDefault().getMessagesContainer().addMessage(
						new ViewerMessage(Levels.ERROR, message));
			} else {
				runtime.exec(engineLocation, stringArray, null);
				if (logger.isDebugEnabled()) {
					StringBuffer buff = new StringBuffer();
					buff.append(" ");
					for (String s : stringArray) {
						buff.append(s);
						buff.append(" ");
					}
					logger.debug("executed " + engineLocation + " "
							+ buff.toString());
				}
				String message = "started engine process at: " + engineHost +
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
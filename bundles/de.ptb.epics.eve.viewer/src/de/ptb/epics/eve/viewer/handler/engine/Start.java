package de.ptb.epics.eve.viewer.handler.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.preferences.EngineExecMacros;
import de.ptb.epics.eve.viewer.preferences.PreferenceConstants;
import de.ptb.epics.eve.viewer.views.messagesview.Levels;
import de.ptb.epics.eve.viewer.views.messagesview.ViewerMessage;

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
	private static final Logger LOGGER = Logger.getLogger(Start.class.getName());
	// private static final Logger ENGINE_LOGGER = Logger.getLogger()
	
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

		LOGGER.debug("EngineParameters (before substitution): " + engineParameters);
		for (EngineExecMacros macro : EngineExecMacros.values()) {
			engineParameters = engineParameters.replaceAll(
					java.util.regex.Pattern.quote(macro.toString()),
					EngineExecMacros.substituteMacro(macro));
		}
		LOGGER.debug("EngineParameters (after substitution): " + engineParameters);
		
		List<String> parameters = new ArrayList<>();
		parameters.add(engineLocation.trim());
		for (String s : engineParameters.split(" ")) {
			parameters.add(s.trim());
		}
		parameters.add("-p");
		parameters.add(Integer.toString(enginePort));

		try {
			if(engineLocation.isEmpty()) {
				String message = 
					"Engine not started due to empty preferences entry!";
				LOGGER.error(message);
				Activator.getDefault().getMessageList().add(
						new ViewerMessage(Levels.ERROR, message));
			} else {
				ProcessBuilder pb = new ProcessBuilder(parameters);
				pb.redirectErrorStream(true);
				/*File testLog = new File("/home/mmichals/eve/evEngine-" + Calendar.getInstance().getTime().getTime() + ".txt");
				pb.redirectOutput(testLog);*/ // does not work cause bash script and engine process redirects output
				/*Process p =  pb.start(); */
				/*BufferedReader br = new BufferedReader(
						new InputStreamReader(p.getInputStream()));
				String line;
				while ((line = br.readLine()) != null) {
					LOGGER.debug("[Engine]" + line);
				}
				br.close();
				br = null;*/ // does not work because it terminates immediately if no data in stream
				
				Process p = pb.start();
				
				if (LOGGER.isDebugEnabled()) {
					StringBuilder buff = new StringBuilder();
					buff.append(" ");
					for (String s : parameters) {
						buff.append(s);
						buff.append(" ");
					}
					LOGGER.debug("executed " + buff.toString());
				}
				
				Thread.sleep(2000);
				String message = "";
				
				try {
					switch (p.exitValue()) {
					case ExitStatus.RECEIVED_SIGTERM:
						message = "Engine terminated due to SIGTERM!";
						LOGGER.error(message);
						Activator.getDefault().getMessageList()
								.add(new ViewerMessage(Levels.ERROR, message));
						throw new ExecutionException(message);
					case ExitStatus.SUCCESS:
						message = "Engine terminated (without errors)!";
						LOGGER.error(message);
						Activator.getDefault().getMessageList()
								.add(new ViewerMessage(Levels.ERROR, message));
						throw new ExecutionException(message);
					case ExitStatus.TCP_CONNECTION_ERROR:
						message = "Engine could not be started due to a TCP connection error! Port wrong or already in use?";
						LOGGER.error(message);
						Activator.getDefault().getMessageList()
								.add(new ViewerMessage(Levels.ERROR, message));
						throw new ExecutionException(message);
					case ExitStatus.TCP_PORT_RANGE_VIOLATION:
						message = "Engine could not be started due to a TCP port range violation! (Port: " + enginePort + ")";
						LOGGER.error(message);
						Activator.getDefault().getMessageList()
								.add(new ViewerMessage(Levels.ERROR, message));
						throw new ExecutionException(message);
					default:
						break;
					}
				} catch (IllegalThreadStateException e) {
					LOGGER.debug("Thread has not (yet) been terminated.");
				}
				
				message = "started engine process at: " + engineHost +
				"(Port: " + enginePort + ")";
				LOGGER.info(message);
				Activator.getDefault().getMessageList().add(
						new ViewerMessage(Levels.INFO, message));
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (InterruptedException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
}
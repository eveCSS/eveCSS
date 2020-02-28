package de.ptb.epics.eve.editor.handler.axeschannelsview;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.editor.Activator;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class AddAbstractDevicesDefaultHandler extends AbstractHandler {
	private static final Logger LOGGER = Logger.getLogger(
			AddAbstractDevicesDefaultHandler.class.getName());
	
	public static final String COMMAND_ID = 
			"de.ptb.epics.eve.editor.command.scanmodule.addabstractdevices";
	
	public static final String PARAM_DEVICE_LIST = 
		"de.ptb.epics.eve.editor.command.scanmodule.addabstractdevices.devicelist";
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IMeasuringStation measuringStation = Activator.getDefault().
				getMeasuringStation();
		
		ICommandService commandService = (ICommandService) PlatformUI.
				getWorkbench().getService(ICommandService.class);
		
		Command addAxisCommand = commandService.getCommand(
				AddAxisDefaultHandler.COMMAND_ID);
		
		Command addChannelCommand = commandService.getCommand(
				AddChannelDefaultHandler.COMMAND_ID);
		
		String deviceList = event.getParameter(PARAM_DEVICE_LIST);
		for (String s : deviceList.split("!")) {
			AbstractDevice device = measuringStation.getAbstractDeviceById(s);
			if (device == null) {
				continue;
			}
			if (device instanceof MotorAxis) {
				Map<String, String> parameters = new HashMap<>();
				parameters.put(AddAxisDefaultHandler.PARAM_AXIS_ID, device.getID());
				try {
					ExecutionEvent executionEvent = new ExecutionEvent(
							addAxisCommand, parameters, event.getTrigger(), 
							event.getApplicationContext());
					addAxisCommand.executeWithChecks(executionEvent);
				} catch (NotDefinedException | NotEnabledException | NotHandledException e) {
					LOGGER.error(e.getMessage(), e);
				}
			} else if (device instanceof DetectorChannel) {
				Map<String, String> parameters = new HashMap<>();
				parameters.put(AddChannelDefaultHandler.PARAM_CHANNEL_ID, device.getID());
				try {
					ExecutionEvent executionEvent = new ExecutionEvent(
							addChannelCommand, parameters, event.getTrigger(), 
							event.getApplicationContext());
					addChannelCommand.executeWithChecks(executionEvent);
				} catch (NotDefinedException | NotEnabledException | NotHandledException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
		return null;
	}
}

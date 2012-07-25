package de.ptb.epics.eve.editor.handler;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.filter.ExcludeFilter;
import de.ptb.epics.eve.editor.Activator;

/**
 * @author Marcus Michalsky
 * @since 1.5
 */
public class AddAbstractDevices implements IHandler {

	private static Logger logger = Logger.getLogger(AddAbstractDevices.class
			.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IMeasuringStation measuringStation = Activator.getDefault()
				.getMeasuringStation();
		// workaround here. only the filter class supports item search by id.
		// until supported a filter has to be created
		ExcludeFilter excludeFilter = new ExcludeFilter();
		excludeFilter.setSource(measuringStation);
		
		ICommandService commandService = (ICommandService) PlatformUI
				.getWorkbench().getService(ICommandService.class);
		
		String deviceList = event.getParameter(
			"de.ptb.epics.eve.editor.command.addabstractdevices.devicelist");
		for(String s : deviceList.split("!")) {
			AbstractDevice device = excludeFilter.getAbstractDeviceById(s);
			if (device != null) {
				if (device instanceof MotorAxis) {
					Map<String, String> parameters= new HashMap<String, String>();
					parameters.put(
						"de.ptb.epics.eve.editor.command.addaxis.motoraxisid",
								device.getID());
					Command command = commandService.getCommand(
						"de.ptb.epics.eve.editor.command.addaxis");
					try {
						ExecutionEvent executionEvent = new ExecutionEvent(
								command, parameters, event.getTrigger(),
								event.getApplicationContext());
						command.executeWithChecks(executionEvent);
					} catch (NotDefinedException e) {
						logger.error(e.getMessage(), e);
					} catch (NotEnabledException e) {
						logger.error(e.getMessage(), e);
					} catch (NotHandledException e) {
						logger.error(e.getMessage(), e);
					}
				} else if (device instanceof DetectorChannel) {
					Map<String, String> parameters= new HashMap<String, String>();
					parameters.put(
						"de.ptb.epics.eve.editor.command.addchannel.detectorchannelid",
								device.getID());
					Command command = commandService.getCommand(
						"de.ptb.epics.eve.editor.command.addchannel");
					try {
						ExecutionEvent executionEvent = new ExecutionEvent(
								command, parameters, event.getTrigger(),
								event.getApplicationContext());
						command.executeWithChecks(executionEvent);
					} catch (NotDefinedException e) {
						logger.error(e.getMessage(), e);
					} catch (NotEnabledException e) {
						logger.error(e.getMessage(), e);
					} catch (NotHandledException e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isHandled() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
	}
}
package de.ptb.epics.eve.viewer.handler.plot;

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
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * @author Marcus Michalsky
 * @since 1.31
 */
public class AutoScaleOnOff extends AbstractHandler {
	private static final Logger LOGGER =
			Logger.getLogger(AutoScaleOnOff.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final String TOGGLE_STATE_ID = "org.eclipse.ui.commands.toggleState";
		final String ON_PARAMETER_ID = "de.ptb.epics.eve.viewer.views.plotview.autoscaleonoff.parameter.on";
		final String CMD_AUTO_SCALE_X = "de.ptb.epics.eve.viewer.views.plotview.autoscalex";
		final String CMD_AUTO_SCALE_Y1 = "de.ptb.epics.eve.viewer.views.plotview.autoscaley1";
		final String CMD_AUTO_SCALE_Y2 = "de.ptb.epics.eve.viewer.views.plotview.autoscaley2";
		
		ICommandService cmdService = (ICommandService) PlatformUI.
				getWorkbench().getService(ICommandService.class);
		IHandlerService handlerService = (IHandlerService)HandlerUtil.
				getActiveWorkbenchWindow(event).getWorkbench().
				getService(IHandlerService.class);
		
		String autoScaleOnParam = event.getParameter(ON_PARAMETER_ID);
		if (autoScaleOnParam == null) {
			String message = "On/Off Parameter is missing!";
			LOGGER.error(message);
			throw new ExecutionException("message");
		}
		boolean autoScaleOn = Boolean.parseBoolean(
				event.getParameter(ON_PARAMETER_ID));
		
		Command autoScaleX = cmdService.getCommand(CMD_AUTO_SCALE_X);
		Command autoScaleY1 = cmdService.getCommand(CMD_AUTO_SCALE_Y1);
		Command autoScaleY2 = cmdService.getCommand(CMD_AUTO_SCALE_Y2);
		
		boolean autoScaleXState = (Boolean) autoScaleX.getState(
				TOGGLE_STATE_ID).getValue();
		boolean autoScaleY1State = (Boolean) autoScaleY1.getState(
				TOGGLE_STATE_ID).getValue();
		boolean autoScaleY2State = (Boolean) autoScaleY2.getState(
				TOGGLE_STATE_ID).getValue();
		
		LOGGER.debug("Auto Scale X State (before): " + autoScaleXState);
		LOGGER.debug("Auto Scale Y1 State (before): " + autoScaleY1State);
		LOGGER.debug("Auto Scale Y2 State (before): " + autoScaleY2State);
		
		try {
			if (autoScaleOn) {
				if (!autoScaleXState) {
					handlerService.executeCommand(CMD_AUTO_SCALE_X, null);
				}
				if (!autoScaleY1State) {
					handlerService.executeCommand(CMD_AUTO_SCALE_Y1, null);
				}
				if (!autoScaleY2State) {
					handlerService.executeCommand(CMD_AUTO_SCALE_Y2, null);
				}
			} else {
				if (autoScaleXState) {
					handlerService.executeCommand(CMD_AUTO_SCALE_X, null);
				}
				if (autoScaleY1State) {
					handlerService.executeCommand(CMD_AUTO_SCALE_Y1, null);
				}
				if (autoScaleY2State) {
					handlerService.executeCommand(CMD_AUTO_SCALE_Y2, null);
				}
			}
		} catch (ExecutionException | NotDefinedException | NotHandledException | NotEnabledException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ExecutionException(e.getMessage());
		}
		
		LOGGER.debug("Auto Scale X State (after): " + autoScaleXState);
		LOGGER.debug("Auto Scale Y1 State (after): " + autoScaleY1State);
		LOGGER.debug("Auto Scale Y2 State (after): " + autoScaleY2State);
		
		return null;
	}
}

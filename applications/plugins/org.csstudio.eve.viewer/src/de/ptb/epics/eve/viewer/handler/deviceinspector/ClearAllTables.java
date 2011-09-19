package de.ptb.epics.eve.viewer.handler.deviceinspector;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * <code>ClearAllTables</code> is the default command handler of the 
 * <code>ClearAllTables</code> command. 
 * <p>
 * It removes all elements from the tables of the active 
 * {@link de.ptb.epics.eve.viewer.views.deviceinspectorview.DeviceInspectorView}.
 * 
 * @author Marcus Michalsky
 * @since 0.4.2
 */
public class ClearAllTables implements IHandler {

	private static Logger logger = 
		Logger.getLogger(ClearAllTables.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IHandlerService handlerService = (IHandlerService)HandlerUtil.
			getActiveWorkbenchWindow(event).getWorkbench().
			getService(IHandlerService.class);
		
		try {
			handlerService.executeCommand(
					"de.ptb.epics.eve.viewer.clearAxes", null);
			handlerService.executeCommand(
					"de.ptb.epics.eve.viewer.clearChannels", null);
			handlerService.executeCommand(
					"de.ptb.epics.eve.viewer.clearDevices", null);
		} catch (NotDefinedException e) {
			logger.error(e.getMessage(), e);
		} catch (NotEnabledException e) {
			logger.error(e.getMessage(), e);
		} catch (NotHandledException e) {
			logger.error(e.getMessage(), e);
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
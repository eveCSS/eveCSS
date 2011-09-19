package de.ptb.epics.eve.viewer.handler.deviceinspector;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.viewer.views.deviceinspectorview.DeviceInspectorView;

/**
 * <code>ClearDetectorChannelsTable</code> is the default command handler of the 
 * <code>clearChannels</code> command. 
 * <p>
 * It removes all elements from the detector channels table of the active 
 * {@link de.ptb.epics.eve.viewer.views.deviceinspectorview.DeviceInspectorView}.
 * 
 * @author Marcus Michalsky
 * @since 0.4.2
 */
public class ClearDetectorChannelsTable implements IHandler {

	private static Logger logger = 
		Logger.getLogger(ClearDetectorChannelsTable.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if(HandlerUtil.getActivePart(event) instanceof DeviceInspectorView) {
			((DeviceInspectorView)HandlerUtil.getActivePart(event)).
				clearDetectorChannelsTable();
		} else {
			logger.warn("Could not clear detector channels table. " +
						"Active part not found.");
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
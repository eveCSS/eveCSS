package de.ptb.epics.eve.viewer.handler.deviceinspector;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.viewer.views.deviceinspectorview.DeviceInspectorView;

/**
 * <code>ResetDeviceInspectorCommandHandler</code> is the default command 
 * handler of the <code>ResetDeviceInspectorCommand</code>.
 * <p>
 * Resets all changes of the layout the user has made (e.g. position of the 
 * sashes or maximization of a contained composite).
 * 
 * @author Marcus Michalsky
 * @since 0.4.1
 */
public class ResetDeviceInspectorCommandHandler implements IHandler {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		DeviceInspectorView deviceInspectorView = 
			(DeviceInspectorView)HandlerUtil.getActivePart(event);
		if(deviceInspectorView != null) {
			deviceInspectorView.resetLayout();
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
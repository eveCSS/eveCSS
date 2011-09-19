package de.ptb.epics.eve.viewer.handler.deviceinspector;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.viewer.views.deviceinspectorview.DeviceInspectorView;

/**
 * <code>RenameDeviceInspectorCommandHandler</code> is the default handler 
 * of the <code>RenameDeviceInspectorCommand</code>.
 * <p>
 * Opens an input dialog where the user can enter a name which is set as the 
 * title of the 
 * {@link de.ptb.epics.eve.viewer.views.deviceinspectorview.DeviceInspectorView}.
 * 
 * @author Marcus Michalsky
 * @since 0.4.1
 */
public class Rename implements IHandler {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		DeviceInspectorView deviceInspectorView = 
				(DeviceInspectorView)HandlerUtil.getActivePart(event);
		
		String partName = null;
		
		if(deviceInspectorView != null) {
			partName = deviceInspectorView.getPartName();
		}
		
		final InputDialog input = 
				new InputDialog(HandlerUtil.getActiveShell(event), 
				"Rename Device Inspector", 
				"Please enter the new name", 
				partName, 
				null);
		
		if(input.open() == InputDialog.OK) {
			deviceInspectorView.rename(input.getValue());
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
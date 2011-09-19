package de.ptb.epics.eve.viewer.handler.deviceinspector;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.viewer.views.deviceinspectorview.DeviceInspectorView;

/**
 * <code>NewDeviceInspectorCommandHandler</code> is the default command handler 
 * of the <code>NewDeviceInspectorCommand</code>. 
 * <p>
 * Opens an input dialog where the user can enter a name. A new 
 * {@link de.ptb.epics.eve.viewer.views.deviceinspectorview.DeviceInspectorView} 
 * is created and its title set to the user input.
 * 
 * @author Marcus Michalsky
 * @since 0.4.1
 */
public class New implements IHandler {

	private static Logger logger = Logger.getLogger(
			New.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IWorkbenchPage page = 
				HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();
		
		try {
			final InputDialog input = 
				new InputDialog(HandlerUtil.getActiveShell(event), 
				"Create Device Inspector", 
				"Please enter a name", 
				"Device Inspector", 
				null);
			
			String name = "";
			
			if(input.open() == InputDialog.OK) {
				name = input.getValue();
				
				page.showView("DeviceInspectorView", 
						String.valueOf(System.nanoTime()), 
						IWorkbenchPage.VIEW_ACTIVATE);
				
				DeviceInspectorView deviceInspectorView = 
					(DeviceInspectorView)HandlerUtil.getActivePart(event);
				deviceInspectorView.rename(name);
			}
		} catch (PartInitException e) {
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
package de.ptb.epics.eve.viewer.handler;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.viewer.views.deviceinspectorview.CommonTableElement;
import de.ptb.epics.eve.viewer.views.deviceoptionsview.DeviceOptionsView;

/**
 * <code>OpenDeviceInDeviceOptionsViewCommandHandler</code> is the default 
 * command handler of the <code>OpenDeviceInDeviceOptionsViewCommand</code>.
 * <p>
 * 
 * 
 * @author Marcus Michalsky
 * @since 0.4.1
 */
public class OpenDeviceInDeviceOptionsView implements IHandler {

	private static Logger logger = Logger.getLogger(
			OpenDeviceInDeviceOptionsView.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		logger.debug("execute");
		ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).
				getSelectionService().getSelection();
		if(logger.isDebugEnabled() && selection != null) {
			logger.debug(selection.toString());
		}
		if(selection instanceof IStructuredSelection) {
			for(Object o : ((IStructuredSelection) selection).toList()) {
				if(o instanceof CommonTableElement) {
					CommonTableElement cte = (CommonTableElement) o;
					logger.debug(cte.getAbstractDevice().getID());
					try {
						final IViewPart view = HandlerUtil.
							getActiveWorkbenchWindow(event).getActivePage().
							showView(
							"DeviceOptionsView", 
							cte.getAbstractDevice().getName(), 
							IWorkbenchPage.VIEW_CREATE);
						((DeviceOptionsView)view).setDevice(
								cte.getAbstractDevice());
					} catch (PartInitException e) {
						logger.error(e.getMessage(), e);
					}
				} else if(o instanceof AbstractDevice) {
					AbstractDevice dev = (AbstractDevice)o;
					logger.debug(dev.getID());
					try {
						final IViewPart view = HandlerUtil.
							getActiveWorkbenchWindow(event).getActivePage().
							showView(
							"DeviceOptionsView", 
							dev.getName(), 
							IWorkbenchPage.VIEW_CREATE);
						((DeviceOptionsView)view).setDevice(dev);
					} catch(PartInitException e) {
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
	public void dispose() {
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
	public void addHandlerListener(IHandlerListener handlerListener) {
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
	}
}
package de.ptb.epics.eve.viewer.handler.deviceinspector;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.views.deviceinspectorview.ui.DeviceInspectorView;

/**
 * <code>AddDevice</code> is the default command handler of the add device 
 * command.
 * <p>
 * It adds all devices provided by the selection service to the tables of the 
 * {@link de.ptb.epics.eve.viewer.views.deviceinspectorview.ui.DeviceInspectorView} 
 * with the secondary id given by the mandatory parameter of the command.
 * 
 * @author Marcus Michalsky
 * @since 0.4.2
 */
public class AddDevice implements IHandler {

	private static Logger logger = Logger.getLogger(AddDevice.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if(logger.isDebugEnabled()) {
			logger.debug("parameter: " + 
					event.getParameter("DeviceInspectorSecondaryId"));
		}
		
		ISelection selection = PlatformUI.getWorkbench().
			getActiveWorkbenchWindow().getSelectionService().getSelection();
		
		logger.debug(selection);
		
		// get the reference of the device inspector
		IViewReference[] ref = HandlerUtil.getActiveWorkbenchWindow(event).
				getActivePage().getViewReferences();
		DeviceInspectorView deviceInspectorView = null;
		for(IViewReference ivr : ref) {
			logger.debug("1st: " + ivr.getId() + " , 2nd: " + ivr.getSecondaryId());
			if (ivr.getId().equals(DeviceInspectorView.ID) ||
					ivr.getId().equals(DeviceInspectorView.GLOBAL_ID)) {
				if(ivr.getSecondaryId().equals(
						event.getParameter("DeviceInspectorSecondaryId"))) {
					deviceInspectorView = (DeviceInspectorView)ivr.getPart(false);
				}
			}
		}
		if(deviceInspectorView == null) {
			logger.warn("Item(s) could not be inserted. No such View found.");
			return null;
		}
		
		// look at what is selected
		if(!(selection instanceof IStructuredSelection)) {
			return null; // not compatible
		}
		
		if(((IStructuredSelection) selection).size() == 0) {
			return null; // nothing selected
		}
		
		// try to insert everything that is selected...
		for(Object sel : ((IStructuredSelection) selection).toList()) {
			if(sel instanceof AbstractDevice) {
				deviceInspectorView.addAbstractDevice((AbstractDevice)sel);
			} else if(sel instanceof List<?>) {
				for(Object o : (List<Object>)selection) {
					deviceInspectorView.addAbstractDevice((AbstractDevice)o);
				}
			} else if(sel instanceof String) {
				IMeasuringStation measuringstation = 
					Activator.getDefault().getMeasuringStation();
				List<AbstractDevice> devices = 
					measuringstation.getDeviceList((String)sel);
				for(AbstractDevice d : devices) {
					deviceInspectorView.addAbstractDevice(d);
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
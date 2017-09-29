package de.ptb.epics.eve.viewer.handler.deviceinspector;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.views.deviceinspectorview.ui.DeviceInspectorView;

/**
 * <code>AddAllPPDevices</code> is the default command handler of the 
 * addAllPPDevicesCommand.
 * It adds all devices available in the current measuring station to the table 
 * in the active 
 * {@link de.ptb.epics.eve.viewer.views.deviceinspectorview.ui.DeviceInspectorView}.
 * 
 * @author Marcus Michalsky
 * @since 0.4.2
 */
public class AddAllPPDevices implements IHandler {

	private static Logger logger = 
		Logger.getLogger(AddAllPPDevices.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if(HandlerUtil.getActivePart(event) instanceof DeviceInspectorView) {
			for(Device d : Activator.getDefault().getMeasuringStation().getDevices()) {
				((DeviceInspectorView)HandlerUtil.getActivePart(event)).
					addAbstractDevice(d);
			}
		} else {
			logger.warn("Could not add devices. Active part not found.");
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
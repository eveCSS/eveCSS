package de.ptb.epics.eve.viewer.handler.deviceinspector;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.views.deviceinspectorview.DeviceInspectorView;

/**
 * <code>AddAllChannels</code> is the default command handler of the 
 * AddAllChannels command.
 * It adds all channels available in the current measuring station to the table 
 * in the active 
 * {@link de.ptb.epics.eve.viewer.views.deviceinspectorview.DeviceInspectorView}.
 * 
 * @author Marcus Michalsky
 * @since 0.4.2
 */
public class AddAllChannels implements IHandler {

	private static Logger logger = 
			Logger.getLogger(AddAllChannels.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if(HandlerUtil.getActivePart(event) instanceof DeviceInspectorView) {
			for(Detector d : Activator.getDefault().getMeasuringStation().getDetectors()) {
				for(DetectorChannel ch : d.getChannels()) {
					((DeviceInspectorView)HandlerUtil.getActivePart(event)).
					addAbstractDevice(ch);
				}
			}
		} else {
			logger.warn("Could not add channels. Active part not found.");
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
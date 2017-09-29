package de.ptb.epics.eve.viewer.handler.deviceinspector;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.viewer.views.deviceinspectorview.CommonTableElement;
import de.ptb.epics.eve.viewer.views.deviceinspectorview.ui.DeviceInspectorView;

/**
 * @author Marcus Michalsky
 * @since 1.26
 */
public class Remove extends AbstractHandler {
	private static final Logger LOGGER = Logger.getLogger(Remove.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if (activePart.getSite().getId().equals(DeviceInspectorView.ID) ||
				activePart.getSite().getId().equals(DeviceInspectorView.GLOBAL_ID)) {
			ISelection selection = HandlerUtil.getCurrentSelection(event);
			if(selection instanceof IStructuredSelection) {
				for (Object o : ((IStructuredSelection)selection).toList()) {
					if(o instanceof CommonTableElement) {
						if (LOGGER.isDebugEnabled()) {
							AbstractDevice device = ((CommonTableElement)o)
									.getAbstractDevice();
							LOGGER.debug("Removing " + device.getName());
						}
						((DeviceInspectorView)activePart).
								removeElement((CommonTableElement)o);
					}
				}
			}
		} else {
			LOGGER.warn("Devices could not be removed!");
			throw new ExecutionException("DeviceInspectorView is not the active Part");
		}
		return null;
	}
}
package de.ptb.epics.eve.editor.handler.prepostposplotview;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.data.measuringstation.AbstractPrePostscanDevice;
import de.ptb.epics.eve.data.scandescription.Prescan;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.views.AbstractScanModuleView;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class AddPrescanDefaultHandler extends AbstractHandler {
	private static final Logger LOGGER = Logger.getLogger(
			AddPrescanDefaultHandler.class.getName());

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if (!(activePart instanceof AbstractScanModuleView)) {
			String message = "active part is not a scan module view";
			LOGGER.error(message);
			throw new ExecutionException(message);
		}
		ScanModule scanModule = ((AbstractScanModuleView)activePart).
				getScanModule();
		String prescanId = event.getParameter(
			"de.ptb.epics.eve.editor.command.scanmodule.addprescan.prescanid");
		LOGGER.debug("prescan to add is: " + prescanId);
		AbstractPrePostscanDevice device = Activator.getDefault().
				getMeasuringStation().getPrePostscanDeviceById(prescanId);
		if (device == null) {
			String message = "device name not found in device definition";
			LOGGER.error(message);
			throw new ExecutionException(message);
		}
		Prescan prescan = new Prescan(device, 
				device.getValue().getDefaultValue());
		scanModule.add(prescan);
		return null;
	}
}

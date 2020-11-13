package de.ptb.epics.eve.editor.handler.prepostposplotview;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.data.measuringstation.AbstractPrePostscanDevice;
import de.ptb.epics.eve.data.scandescription.Postscan;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.views.AbstractScanModuleView;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class AddPostscanDefaultHandler extends AbstractHandler {
	private static final Logger LOGGER = Logger.getLogger(
			AddPostscanDefaultHandler.class.getName());

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
		String postscanId = event.getParameter(
			"de.ptb.epics.eve.editor.command.scanmodule.addpostscan.postscanid");
		LOGGER.debug("postscan to add is: " + postscanId);
		AbstractPrePostscanDevice device = Activator.getDefault().
				getMeasuringStation().getPrePostscanDeviceById(postscanId);
		if (device == null) {
			String message = "device name not found in device definition";
			LOGGER.error(message);
			throw new ExecutionException(message);
		}
		Postscan postscan = new Postscan(device, 
				device.getValue().getDefaultValue());
		scanModule.add(postscan);
		return null;
	}
}

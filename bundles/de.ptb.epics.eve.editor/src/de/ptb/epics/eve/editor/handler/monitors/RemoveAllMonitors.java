package de.ptb.epics.eve.editor.handler.monitors;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.editor.views.scanview.ui.ScanView;

/**
 * @author Marcus Michalsky
 * @since 1.30
 */
public class RemoveAllMonitors extends AbstractHandler {
	private static final Logger LOGGER = Logger.getLogger(
			RemoveAllMonitors.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if (activePart.getSite().getId().equals(
				"de.ptb.epics.eve.editor.views.ScanView")) {
			ScanDescription scanDescription = ((ScanView)activePart).
					getCurrentScanDescription();
			scanDescription.removeAllMonitors();
		} else {
			LOGGER.error("Axes could not be removed!");
			throw new ExecutionException("ScanView is not the active part!");
		}
		return null;
	}
}
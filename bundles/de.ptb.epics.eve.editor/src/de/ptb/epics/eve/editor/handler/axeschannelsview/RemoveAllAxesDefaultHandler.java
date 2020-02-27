package de.ptb.epics.eve.editor.handler.axeschannelsview;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.editor.views.axeschannelsview.AbstractScanModuleView;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class RemoveAllAxesDefaultHandler extends AbstractHandler {
	private static final Logger LOGGER = Logger.getLogger(
			RemoveAllAxesDefaultHandler.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if (activePart instanceof AbstractScanModuleView) {
			((AbstractScanModuleView)activePart).getScanModule().removeAllAxes();;
		} else {
			LOGGER.error("Axes could not be removed.");
			throw new ExecutionException("Active part is not a Scan Module View.");
		}
		return null;
	}
}

package de.ptb.epics.eve.editor.handler.prepostposplotview;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.views.AbstractScanModuleView;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class RemoveAllPrePostscansDefaultHandler extends AbstractHandler {
	public static String ID = 
		"de.ptb.epics.eve.editor.command.scanmodule.removeallprepostscanentries";
	
	public static final Logger LOGGER = Logger.getLogger(
			RemoveAllPrePostscansDefaultHandler.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if (activePart instanceof AbstractScanModuleView) {
			ScanModule scanModule = ((AbstractScanModuleView)activePart).
					getScanModule();
			scanModule.removeAllPrescans();
			scanModule.removeAllPostscans();
		} else {
			String message = "Active part is not an AbstractScanModuleView";
			LOGGER.error(message);
			throw new ExecutionException(message);
		}
		return null;
	}
}

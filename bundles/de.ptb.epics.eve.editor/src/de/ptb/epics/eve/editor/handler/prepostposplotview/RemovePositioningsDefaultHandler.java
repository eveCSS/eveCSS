package de.ptb.epics.eve.editor.handler.prepostposplotview;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.data.scandescription.Positioning;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.views.AbstractScanModuleView;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class RemovePositioningsDefaultHandler extends AbstractHandler {
	public static String ID = 
			"de.ptb.epics.eve.editor.command.scanmodule.removepositionings";
	
	private static final Logger LOGGER = Logger.getLogger(
			RemovePositioningsDefaultHandler.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if (activePart instanceof AbstractScanModuleView) {
			ScanModule scanModule = ((AbstractScanModuleView)activePart).
					getScanModule();
			ISelection selection = HandlerUtil.getCurrentSelection(event);
			if (selection instanceof IStructuredSelection) {
				for (Object o : ((IStructuredSelection)selection).toList()) {
					if (o instanceof Positioning) {
						scanModule.remove((Positioning)o);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Positioning " + ((Positioning)o).
									getMotorAxis().getName() + " removed");
						}
					}
				}
			} else {
				String message = "Selection is not a structured selection";
				LOGGER.error(message);
				throw new ExecutionException(message);
			}
		} else {
			String message = "Active Part is not an AbstractScanModuleView";
			LOGGER.error(message);
			throw new ExecutionException(message);
		}
		return null;
	}
}

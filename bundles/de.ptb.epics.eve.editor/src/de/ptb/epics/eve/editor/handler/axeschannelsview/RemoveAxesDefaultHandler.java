package de.ptb.epics.eve.editor.handler.axeschannelsview;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.views.axeschannelsview.AbstractScanModuleView;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class RemoveAxesDefaultHandler extends AbstractHandler {
	public static final String ID = 
			"de.ptb.epics.eve.editor.command.scanmodule.removeaxes";
	
	private static final Logger LOGGER = Logger.getLogger(
			RemoveAxesDefaultHandler.class.getName());

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if (activePart instanceof AbstractScanModuleView) {
			ScanModule sm = ((AbstractScanModuleView)activePart).getScanModule();
			ISelection selection = HandlerUtil.getCurrentSelection(event);
			if (selection instanceof IStructuredSelection) {
				for (Object o : ((IStructuredSelection)selection).toList()) {
					if (o instanceof Axis) {
						sm.remove((Axis)o);
						if(LOGGER.isDebugEnabled()) {
							LOGGER.debug("Axis " + ((Axis)o).getMotorAxis().
									getName() + " removed");
						}
					}
				}
			}
		} else {
			LOGGER.error("Axis/Axes could not be removed.");
			throw new ExecutionException("Active Part is not a Scan Module View.");
		}
		return null;
	}
}

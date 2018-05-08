package de.ptb.epics.eve.editor.handler.monitors;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.editor.views.scanview.ui.ScanView;

/**
 * @author Marcus Michalsky
 * @since 1.30
 */
public class RemoveMonitor extends AbstractHandler {
	private static final Logger LOGGER = Logger.getLogger(
			RemoveMonitor.class.getName());
	
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
			ISelection selection = HandlerUtil.getCurrentSelection(event);
			if(selection instanceof IStructuredSelection) {
				Object o = ((IStructuredSelection)selection).getFirstElement();
				if (o instanceof Option) {
					scanDescription.removeMonitor((Option)o);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Option " + ((Option)o).getName() 
								+ " (" + ((Option)o).getParent().getName()
								+ ") removed");
					}
				}
			}
		} else {
			LOGGER.warn("Monitor could not be removed!");
			throw new ExecutionException("ScanView is not the active Part");
		}
		return null;
	}
}
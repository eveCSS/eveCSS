package de.ptb.epics.eve.editor.handler.pauseconditions;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.PauseCondition;
import de.ptb.epics.eve.editor.views.chainview.ChainView;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class RemovePauseConditionsDefaultHandler extends AbstractHandler {
	public static final String ID = 
			"de.ptb.epics.eve.editor.command.removepauseconditions";
	
	private static final Logger LOGGER = Logger.getLogger(
			RemovePauseConditionsDefaultHandler.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if (!(activePart instanceof ChainView)) {
			LOGGER.error("Pause Condition(s) could not be removed.");
			throw new ExecutionException("Active part is not ChainView.");
		}
		Chain chain = ((ChainView)activePart).getCurrentChain();
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			for (Object o : ((IStructuredSelection)selection).toList()) {
				if (o instanceof PauseCondition) {
					chain.removePauseCondition((PauseCondition)o);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Removed PauseCondition: " + o.toString());
					}
				}
			}
		}
		return null;
	}
}

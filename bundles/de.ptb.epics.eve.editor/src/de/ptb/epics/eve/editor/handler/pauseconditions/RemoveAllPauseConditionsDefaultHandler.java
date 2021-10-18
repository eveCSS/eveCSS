package de.ptb.epics.eve.editor.handler.pauseconditions;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.editor.views.chainview.ChainView;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class RemoveAllPauseConditionsDefaultHandler extends AbstractHandler {
	private static final Logger LOGGER = Logger.getLogger(
			RemoveAllPauseConditionsDefaultHandler.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if (!(activePart instanceof ChainView)) {
			LOGGER.error("Pause Conditions could not be removed.");
			throw new ExecutionException("Active part is not Chain View.");
		}
		((ChainView)activePart).getCurrentChain().removeAllPauseConditions();
		return null;
	}
}

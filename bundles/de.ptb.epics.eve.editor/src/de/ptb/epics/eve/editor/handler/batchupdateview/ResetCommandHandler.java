package de.ptb.epics.eve.editor.handler.batchupdateview;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.editor.views.batchupdateview.ui.BatchUpdateView;

/**
 * @author Marcus Michalsky
 * @since 1.30
 */
public class ResetCommandHandler extends AbstractHandler {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if (!activePart.getSite().getId().equals(
				"de.ptb.epics.eve.editor.views.batchupdateview")) {
			throw new ExecutionException(
					"BatchUpdateView is not the active Part");
		}
		((BatchUpdateView)activePart).reset();
		return null;
	}
}
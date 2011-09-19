package de.ptb.epics.eve.editor.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * <code>EveEditorStartCommandHandler</code> is the default command handler of 
 * the start command.
 * <p>
 * It tries to show the perspective "EveEditorPerspective" in the active 
 * workbench window.
 * 
 * @author Marcus Michalsky
 * @since 0.4.1
 */
public class EveEditorStartCommandHandler implements IHandler {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			HandlerUtil.getActiveWorkbenchWindow(event).getWorkbench().
				showPerspective("EveEditorPerpective", 
				HandlerUtil.getActiveWorkbenchWindow(event));
		} catch (WorkbenchException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isHandled() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
	}
}
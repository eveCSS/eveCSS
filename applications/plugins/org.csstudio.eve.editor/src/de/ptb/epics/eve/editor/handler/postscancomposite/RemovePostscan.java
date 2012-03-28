package de.ptb.epics.eve.editor.handler.postscancomposite;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.data.scandescription.Postscan;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;

/**
 * Default handler of the remove postscan command.
 * 
 * @author Marcus Michalsky
 * @since 1.2
 */
public class RemovePostscan implements IHandler {

	private static Logger logger = 
			Logger.getLogger(RemovePostscan.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if (activePart.getSite().getId().equals(
				"de.ptb.epics.eve.editor.views.ScanModulView")) {
			ScanModule sm = ((ScanModuleView)activePart).
					getCurrentScanModule();
			ISelection selection = HandlerUtil.getCurrentSelection(event);
			if(selection instanceof IStructuredSelection) {
				Object o = ((IStructuredSelection)selection).getFirstElement();
				if(o instanceof Postscan) {
					sm.remove(((Postscan)o));
					if(logger.isDebugEnabled()) {
						logger.debug("Postscan " + ((Postscan)o).
								getAbstractDevice().getName() + "removed");
					}
				}
			}
		} else {
			logger.warn("Postscan could not be removed!");
			throw new ExecutionException("ScanModulView is not the active part!");
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
	public void dispose() {
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
}
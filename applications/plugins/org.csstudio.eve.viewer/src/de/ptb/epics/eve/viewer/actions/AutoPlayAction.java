package de.ptb.epics.eve.viewer.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import de.ptb.epics.eve.viewer.Activator;

public class AutoPlayAction implements IWorkbenchWindowActionDelegate {

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
	public void init(IWorkbenchWindow window) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run(IAction action) {
		if (Activator.getDefault().getEcp1Client().getPlayListController().isAutoplay()) {
			Activator.getDefault().getEcp1Client().getPlayListController().setAutoplay(false);
		} else {
			Activator.getDefault().getEcp1Client().getPlayListController().setAutoplay(true);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}
}
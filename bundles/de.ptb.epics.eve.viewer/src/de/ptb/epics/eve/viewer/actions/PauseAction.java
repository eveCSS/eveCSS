package de.ptb.epics.eve.viewer.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import de.ptb.epics.eve.viewer.Activator;

/**
 * @see IWorkbenchWindowActionDelegate
 */
public class PauseAction implements IWorkbenchWindowActionDelegate {

	/**
	 * The constructor.
	 */
	public PauseAction() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run(IAction action) {
		Activator.getDefault().getEcp1Client().getPlayController().pause();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
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
	public void init(IWorkbenchWindow window) {
	}
}
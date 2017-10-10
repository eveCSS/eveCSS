package de.ptb.epics.eve.viewer.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import de.ptb.epics.eve.viewer.Activator;

/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class KillAction implements IWorkbenchWindowActionDelegate {
	
	/**
	 * The constructor.
	 */
	public KillAction() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run(IAction action) {
		Activator.getDefault().getEcp1Client().getPlayController().shutdownEngine();
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
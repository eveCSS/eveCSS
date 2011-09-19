package de.ptb.epics.eve.viewer.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import de.ptb.epics.eve.viewer.Activator;

public class AutoPlayAction implements IWorkbenchWindowActionDelegate {

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run(IAction action) {

		
		System.out.println("PlayListController: " + Activator.getDefault().getEcp1Client().getPlayListController().toString());
		
		
		if (Activator.getDefault().getEcp1Client().getPlayListController().isAutoplay()) {
			System.out.println("Autoplay wird ausgeschaltet");
			Activator.getDefault().getEcp1Client().getPlayListController().setAutoplay(false);
		}
		else {
			System.out.println("Autoplay wird eingeschaltet");
			Activator.getDefault().getEcp1Client().getPlayListController().setAutoplay(true);
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

}

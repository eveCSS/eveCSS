package de.ptb.epics.eve.editor.handler.editor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.progress.UIJob;

import de.ptb.epics.eve.editor.gef.editparts.ScanModuleEditPart;
import de.ptb.epics.eve.editor.jobs.filloptions.RemoveAllDevices;

/**
 * @author Marcus Michalsky
 * @since 1.6
 */
public class FillClear extends AbstractHandler {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		// look at what is selected
		if (!(selection instanceof IStructuredSelection)) {
			return null; // not compatible
		}
		if (((IStructuredSelection) selection).size() == 0) {
			return null; // nothing selected
		}
		Object element = ((IStructuredSelection)selection).getFirstElement();
		
		if (element instanceof ScanModuleEditPart) {
			UIJob removeAllDevices = new RemoveAllDevices(
					"Remove present Devices", 
					((ScanModuleEditPart)element).getModel());
			removeAllDevices.setUser(true);
			removeAllDevices.schedule();
		} else {
			throw new ExecutionException("no scan module selected.");
		}
		return null;
	}
}
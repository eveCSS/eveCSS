package de.ptb.epics.eve.editor.handler.editor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.editor.gef.editparts.ScanModuleEditPart;
import de.ptb.epics.eve.editor.jobs.filloptions.SaveAllChannelValues;

/**
 * @author Marcus Michalsky
 * @since 1.6
 */
public class FillChannelValues extends AbstractHandler {

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
			Job saveAllChannelValues = new SaveAllChannelValues(
					"Save all Channel Values", 
					((ScanModuleEditPart)element).getModel());
			saveAllChannelValues.setUser(true);
			HandlerUtil.getActiveSite(event).getSelectionProvider().
					setSelection(StructuredSelection.EMPTY);
			saveAllChannelValues.schedule();
		} else {
			throw new ExecutionException("no scan module selected.");
		}
		return null;
	}
}
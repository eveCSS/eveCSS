package de.ptb.epics.eve.editor.handler.changeto;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.progress.UIJob;

import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.gef.editparts.ScanModuleEditPart;
import de.ptb.epics.eve.editor.jobs.changesmtype.AxisSnapshotDynamic;

/**
 * @author Marcus Michalsky
 * @since 1.31
 */
public class ChangeToAxisSnapshotDynamic extends AbstractHandler {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (!(selection instanceof IStructuredSelection)) {
			// not compatible
			return null;
		}
		if (((IStructuredSelection) selection).size() == 0) {
			// nothing selected
			return null;
		}
		Object element = ((IStructuredSelection)selection).getFirstElement();
		final ISelectionProvider selectionProvider = HandlerUtil.
				getActiveSite(event).getSelectionProvider();
		
		if (element instanceof ScanModuleEditPart) {
			ScanModule scanModule = ((ScanModuleEditPart)element).getModel();
			UIJob axisSnapshotDynamicJob = new AxisSnapshotDynamic(scanModule);
			axisSnapshotDynamicJob.setUser(true);
			axisSnapshotDynamicJob.addJobChangeListener(
					new ReselectScanModuleJobChangeListener(selectionProvider, 
							((ScanModuleEditPart)element)));
			axisSnapshotDynamicJob.schedule();
		} else {
			throw new ExecutionException("No ScanModule selected.");
		}
		return null;
	}
}

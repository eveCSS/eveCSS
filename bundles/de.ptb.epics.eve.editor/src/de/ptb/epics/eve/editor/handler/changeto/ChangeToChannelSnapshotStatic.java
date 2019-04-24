package de.ptb.epics.eve.editor.handler.changeto;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.progress.UIJob;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.gef.editparts.ScanModuleEditPart;
import de.ptb.epics.eve.editor.jobs.changesmtype.ChannelSnapshotStatic;

/**
 * @author Marcus Michalsky
 * @since 1.31
 */
public class ChangeToChannelSnapshotStatic extends AbstractHandler {

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
			IMeasuringStation measuringStation = 
					Activator.getDefault().getDeviceDefinition();
			UIJob channelSnapshotStaticJob = 
					new ChannelSnapshotStatic(scanModule, measuringStation);
			channelSnapshotStaticJob.setUser(true);
			channelSnapshotStaticJob.addJobChangeListener(
					new ReselectScanModuleJobChangeListener(selectionProvider, 
							((ScanModuleEditPart)element)));
			channelSnapshotStaticJob.schedule();
		} else {
			throw new ExecutionException("no scan module selected.");
		}
		return null;
	}
}

package de.ptb.epics.eve.editor.handler.changeto;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;

import de.ptb.epics.eve.editor.gef.editparts.ScanModuleEditPart;

/**
 * @author Marcus Michalsky
 * @since 1.31
 */
public class ReselectScanModuleJobChangeListener implements IJobChangeListener {
	private final ISelectionProvider selectionProvider;
	private final ScanModuleEditPart scanModuleEditPart;
	
	public ReselectScanModuleJobChangeListener(ISelectionProvider selectionProvider,
			ScanModuleEditPart scanModuleEditPart) {
		super();
		this.selectionProvider = selectionProvider;
		this.scanModuleEditPart = scanModuleEditPart;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void aboutToRun(IJobChangeEvent event) {
		// nothing to do here so far
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void awake(IJobChangeEvent event) {
		// nothing to do here so far
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void done(IJobChangeEvent event) {
		this.selectionProvider.setSelection(
				new StructuredSelection(this.scanModuleEditPart));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void running(IJobChangeEvent event) {
		// nothing to do here so far
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void scheduled(IJobChangeEvent event) {
		this.selectionProvider.setSelection(StructuredSelection.EMPTY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sleeping(IJobChangeEvent event) {
		// nothing to do here so far
	}
}

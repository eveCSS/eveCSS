package de.ptb.epics.eve.editor.views.scanview.ui;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;

import de.ptb.epics.eve.editor.gef.editparts.ChainEditPart;
import de.ptb.epics.eve.editor.gef.editparts.ScanDescriptionEditPart;
import de.ptb.epics.eve.editor.gef.editparts.ScanModuleEditPart;
import de.ptb.epics.eve.editor.gef.editparts.StartEventEditPart;
import de.ptb.epics.eve.util.rcp.SingleSelectionProvider;

/**
 * @author Hartmut Scherr
 * @since 1.10
 */
public class ScanSelectionProvider extends SingleSelectionProvider {

	private static final Logger LOGGER = Logger
			.getLogger(ScanSelectionProvider.class.getName());

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		LOGGER.debug(part.getTitle() + " : " + selection.toString());
		if (!(part instanceof IEditorPart)) {
			return;
		}
		if (!((IEditorPart) part).getEditorSite().getId()
				.equals("de.ptb.epics.eve.editor.gef.GraphicalEditor")) {
			return;
		}
		if (!(selection instanceof IStructuredSelection)) {
			return;
		}
		IStructuredSelection sel = (IStructuredSelection) selection;
		if (sel.size() != 1) {
			return;
		}
		ISelection modelSelection = null;
		if ((sel.getFirstElement() instanceof ScanModuleEditPart)) {
			modelSelection = new StructuredSelection(
					((ScanModuleEditPart) sel.getFirstElement()).getModel()
							.getChain().getScanDescription());
		} else if ((sel.getFirstElement() instanceof ChainEditPart)) {
			modelSelection = new StructuredSelection(
					((ChainEditPart) sel.getFirstElement()).getModel()
							.getScanDescription());
		} else if ((sel.getFirstElement() instanceof ScanDescriptionEditPart)) {
			modelSelection = new StructuredSelection(
					((ScanDescriptionEditPart)sel.getFirstElement()).getModel());
		} else if ((sel.getFirstElement() instanceof StartEventEditPart)) {
			modelSelection = new StructuredSelection(
					((StartEventEditPart) sel.getFirstElement()).getModel()
							.getChain().getScanDescription());
		} else {
			return;
		}
		this.currentSelection = modelSelection;
		for (ISelectionChangedListener listener : this.listeners) {
			listener.selectionChanged(new SelectionChangedEvent(this,
					modelSelection));
		}
	}
}
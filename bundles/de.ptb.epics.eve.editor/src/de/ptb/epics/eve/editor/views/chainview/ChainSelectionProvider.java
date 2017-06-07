package de.ptb.epics.eve.editor.views.chainview;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;

import de.ptb.epics.eve.editor.gef.editparts.ScanModuleEditPart;
import de.ptb.epics.eve.util.rcp.SingleSelectionProvider;

/**
 * @author Hartmut Scherr
 * @since 1.10
 */
public class ChainSelectionProvider extends SingleSelectionProvider {

	private static final Logger LOGGER = Logger
			.getLogger(ChainSelectionProvider.class.getName());

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
		if (!(sel.getFirstElement() instanceof ScanModuleEditPart)) {
			return;
		}
		ISelection modelSelection = new StructuredSelection(
				((ScanModuleEditPart) sel.getFirstElement()).getModel()
						.getChain());
		this.currentSelection = modelSelection;
		for (ISelectionChangedListener listener : this.listeners) {
			listener.selectionChanged(new SelectionChangedEvent(this,
					modelSelection));
		}
	}
}
package de.ptb.epics.eve.editor.views.scanmoduleview;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;

import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;
import de.ptb.epics.eve.editor.gef.editparts.ScanModuleEditPart;
import de.ptb.epics.eve.util.ui.rcp.SingleSelectionProvider;

/**
 * @author Marcus Michalsky
 * @since 1.8
 */
public class ScanModuleSelectionProvider extends SingleSelectionProvider {
	private static final Logger LOGGER = Logger
			.getLogger(ScanModuleSelectionProvider.class.getName());
	private ScanModuleTypes scanModuleType;
	
	public ScanModuleSelectionProvider(ScanModuleTypes scanModuleType) {
		this.scanModuleType = scanModuleType;
	}
	
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
		IStructuredSelection sel = (IStructuredSelection)selection;
		if (sel.size() != 1) {
			return;
		}
		if (!(sel.getFirstElement() instanceof ScanModuleEditPart)) {
			return;
		}
		ScanModule scanModule = ((ScanModuleEditPart) sel.getFirstElement()).getModel();
		if (!scanModule.getType().equals(this.scanModuleType)) {
			return;
		}
		ISelection modelSelection = new StructuredSelection(scanModule);
		this.currentSelection = modelSelection;
		for (ISelectionChangedListener listener : this.listeners) {
			listener.selectionChanged(new SelectionChangedEvent(this,
					modelSelection));
		}
	}
}
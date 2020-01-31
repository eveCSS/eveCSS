package de.ptb.epics.eve.editor.views.axeschannelsview;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.gef.editparts.ChainEditPart;
import de.ptb.epics.eve.editor.gef.editparts.ScanDescriptionEditPart;
import de.ptb.epics.eve.editor.gef.editparts.ScanModuleEditPart;
import de.ptb.epics.eve.editor.gef.editparts.tree.ScanModuleTreeEditPart;
import de.ptb.epics.eve.editor.views.IEditorView;

/**
 * 
 * 
 * @author Marcus Michalsky
 * @since 1.34
 */
public abstract class AbstractScanModuleView extends ViewPart implements 
		IEditorView, ISelectionListener {

	/**
	 * If the first item of the (structured) selection is a scan module (edit part) 
	 * it is set via {@link #setScanModule(ScanModule)}. If a chain or scan description
	 * (edit part) is selected, it is reset.
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (!(selection instanceof IStructuredSelection)
				|| ((IStructuredSelection) selection).size() == 0) {
			return;
		}
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		Object o = structuredSelection.toList().get(
				structuredSelection.size() - 1);
		if (o instanceof ScanModuleEditPart || o instanceof ScanModuleTreeEditPart) {
			this.setScanModule((ScanModule) (((EditPart)o).getModel()));
		} else if (o instanceof ChainEditPart || o instanceof ScanDescriptionEditPart) {
			this.setScanModule(null);
		}
	}

	/**
	 * 
	 * @param scanModule
	 */
	protected abstract void setScanModule(ScanModule scanModule);
}

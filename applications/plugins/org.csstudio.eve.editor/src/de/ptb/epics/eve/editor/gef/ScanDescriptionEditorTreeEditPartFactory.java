package de.ptb.epics.eve.editor.gef;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.gef.editparts.tree.ChainTreeEditPart;
import de.ptb.epics.eve.editor.gef.editparts.tree.ScanDescriptionTreeEditPart;
import de.ptb.epics.eve.editor.gef.editparts.tree.ScanModuleTreeEditPart;

/**
 * @author Marcus Michalsky
 * @since 1.18
 */
public class ScanDescriptionEditorTreeEditPartFactory implements
		EditPartFactory {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EditPart createEditPart(EditPart context, Object model) {
		EditPart part = null;
		
		if (model instanceof ScanDescription) {
			part = new ScanDescriptionTreeEditPart();
		} else if (model instanceof Chain) {
			part = new ChainTreeEditPart();
		} else if (model instanceof ScanModule) {
			part = new ScanModuleTreeEditPart();
		}
		
		if(part != null) {
			part.setModel(model);
		}
		return part;
	}
}
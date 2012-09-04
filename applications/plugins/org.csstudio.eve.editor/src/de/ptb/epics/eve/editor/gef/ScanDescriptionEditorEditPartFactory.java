package de.ptb.epics.eve.editor.gef;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.Connector;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.StartEvent;
import de.ptb.epics.eve.editor.gef.editparts.ChainEditPart;
import de.ptb.epics.eve.editor.gef.editparts.ConnectionEditPart;
import de.ptb.epics.eve.editor.gef.editparts.StartEventEditPart;
import de.ptb.epics.eve.editor.gef.editparts.ScanDescriptionEditPart;
import de.ptb.epics.eve.editor.gef.editparts.ScanModuleEditPart;


/**
 * 
 * @author Marcus Michalsky
 * @since 1.6
 */
public class ScanDescriptionEditorEditPartFactory implements EditPartFactory {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EditPart createEditPart(EditPart context, Object model) {
		if (context instanceof ScanModuleEditPart
				|| context instanceof StartEventEditPart) {
			// connectors
			return new ConnectionEditPart((Connector)model);
		} else {
			// elements
			if (model instanceof ScanDescription) {
				return new ScanDescriptionEditPart((ScanDescription)model);
			} else if (model instanceof Chain) {
				return new ChainEditPart((Chain)model);
			} else if (model instanceof ScanModule) {
				return new ScanModuleEditPart((ScanModule)model);
			} else if (model instanceof StartEvent) {
				return new StartEventEditPart((StartEvent)model);
			}
		}
		return null;
	}
}
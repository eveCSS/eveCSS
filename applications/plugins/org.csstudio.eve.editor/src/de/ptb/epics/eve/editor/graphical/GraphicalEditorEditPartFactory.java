package de.ptb.epics.eve.editor.graphical;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.Connector;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.StartEvent;
import de.ptb.epics.eve.editor.graphical.editparts.ChainEditPart;
import de.ptb.epics.eve.editor.graphical.editparts.EventEditPart;
import de.ptb.epics.eve.editor.graphical.editparts.EventToScanModulConnectionEditPart;
import de.ptb.epics.eve.editor.graphical.editparts.ScanDescriptionEditPart;
import de.ptb.epics.eve.editor.graphical.editparts.ScanModuleEditPart;

/**
 * <code>GraphicalEditorEditPartFactory</code> creates the elements needed in 
 * the {@link GraphicalEditor}. It recognizes the contents model object and 
 * constructs this {@link EditPart}.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class GraphicalEditorEditPartFactory implements EditPartFactory {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EditPart createEditPart(final EditPart context, final Object model) {
		
		if(context instanceof ScanModuleEditPart || 
		   context instanceof EventEditPart) {
			return new EventToScanModulConnectionEditPart((Connector)model);
		} else {
			if (model instanceof ScanDescription) {
				return new ScanDescriptionEditPart((ScanDescription)model);
			} else if(model instanceof Chain) {
				return new ChainEditPart((Chain)model);
			} else if(model instanceof ScanModule) {
				return new ScanModuleEditPart((ScanModule)model);
			}  else if(model instanceof StartEvent) {
				return new EventEditPart((StartEvent)model);
			}
		}
		return null;
	}
}
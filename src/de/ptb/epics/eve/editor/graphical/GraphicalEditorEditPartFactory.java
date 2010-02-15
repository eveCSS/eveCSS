package de.ptb.epics.eve.editor.graphical;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.Connector;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.ScanModul;
import de.ptb.epics.eve.data.scandescription.StartEvent;
import de.ptb.epics.eve.editor.graphical.editparts.ChainEditPart;
import de.ptb.epics.eve.editor.graphical.editparts.EventEditPart;
import de.ptb.epics.eve.editor.graphical.editparts.EventToScanModulConnectionEditPart;
import de.ptb.epics.eve.editor.graphical.editparts.ScanDescriptionEditPart;
import de.ptb.epics.eve.editor.graphical.editparts.ScanModulEditPart;


public class GraphicalEditorEditPartFactory implements EditPartFactory {

	@Override
	public EditPart createEditPart( final EditPart context, final Object model ) {
		if( context instanceof ScanModulEditPart || context instanceof EventEditPart ) {
			return new EventToScanModulConnectionEditPart( (Connector)model );
		} else {
		
			if (model instanceof ScanDescription) {
				return new ScanDescriptionEditPart( (ScanDescription)model );
			} else if( model instanceof Chain ) {
				return new ChainEditPart( (Chain)model );
			} else if( model instanceof ScanModul ) {
				return new ScanModulEditPart( (ScanModul)model );
			}  else if( model instanceof StartEvent ) {
				return new EventEditPart( (StartEvent)model );
			}
		
		}
		
		return null;
		
		
	}

}

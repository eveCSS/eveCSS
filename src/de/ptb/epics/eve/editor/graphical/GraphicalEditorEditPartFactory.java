package de.ptb.epics.eve.editor.graphical;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.ScanModul;
import de.ptb.epics.eve.data.scandescription.StartEvent;


public class GraphicalEditorEditPartFactory implements EditPartFactory {

	@Override
	public EditPart createEditPart( final EditPart context, final Object model ) {
		if( context instanceof ScanModulEditPart || context instanceof EventEditPart ) {
			
			ScanModulEditorConnectionEditPart connectionEditPart = null;
			connectionEditPart = new ScanModulEditorConnectionEditPart();
			connectionEditPart.setModel( model );
			
			return connectionEditPart;
			
		} else {
		
			if (model instanceof ScanDescription) {
				return new ScanDescriptionEditPart( this.scanDescription );
			} else if( model instanceof Chain ) {
				return new ChainEditPart( (Chain)model );
			} else if( model instanceof ScanModul ) {
				ScanModulEditPart editPart = new ScanModulEditPart( (ScanModul)model );
				return editPart;
			}  else if( model instanceof StartEvent ) {
				return new EventEditPart( (StartEvent)model );
			}
		
		}
		
		return null;
		
		
	}

}

package de.ptb.epics.eve.editor.graphical.editparts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.editor.graphical.editparts.figures.ScanDescriptionFigure;

public class ScanDescriptionEditPart extends AbstractGraphicalEditPart {

	public ScanDescriptionEditPart( final ScanDescription scanDescription ) {
		this.setModel( scanDescription );
	}
	
	@Override
	protected IFigure createFigure() {
		return new ScanDescriptionFigure();
	}

	@Override
	protected void createEditPolicies() {

	}
	
	@Override
	protected List< Object > getModelChildren() {
		final ScanDescription scanDescription = (ScanDescription)this.getModel();
		final List< Object > children = new ArrayList< Object >();
		children.addAll( scanDescription.getChains() );
		
		final List< Object > startEvents = new ArrayList< Object >();
		final Iterator< Chain > it = scanDescription.getChains().iterator();
		
		while( it.hasNext() ) {
			startEvents.add( it.next().getStartEvent() );
		}
		
		children.addAll( startEvents );
		return children;
	}

}

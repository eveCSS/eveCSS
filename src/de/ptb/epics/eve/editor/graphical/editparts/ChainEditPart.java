package de.ptb.epics.eve.editor.graphical.editparts;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.editor.graphical.editparts.figures.ChainFigure;

public class ChainEditPart extends AbstractGraphicalEditPart {

	public ChainEditPart( final Chain chain ) {
		this.setModel( chain );
	}
	
	@Override
	protected IFigure createFigure() {
		return new ChainFigure( ((Chain)this.getModel()).getId() );
		
	}

	@Override
	protected void createEditPolicies() {
		// TODO Auto-generated method stub

	}
	
	@Override
	protected List< ? > getModelChildren() {
		final Chain chain = (Chain)this.getModel();
		return chain.getScanModuls();
	}

}

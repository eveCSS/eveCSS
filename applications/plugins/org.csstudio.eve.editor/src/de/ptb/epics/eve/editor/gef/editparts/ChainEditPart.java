package de.ptb.epics.eve.editor.gef.editparts;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.gef.figures.ChainFigure;

/**
 * @author Marcus Michalsky
 * @since 1.6
 */
public class ChainEditPart extends AbstractGraphicalEditPart {

	/**
	 * Constructor.
	 * 
	 * @param chain the model element
	 */
	public ChainEditPart(Chain chain) {
		this.setModel(chain);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		return new ChainFigure();
		// TODO Auto-generated method stub  old: return new ChainFigure(((Chain)this.getModel()).getId());	
	}

	/**
	 * Returns the model element.
	 */
	public Chain getModel() {
		return (Chain)super.getModel();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<ScanModule> getModelChildren() {
		return this.getModel().getScanModules();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createEditPolicies() {
	}
}
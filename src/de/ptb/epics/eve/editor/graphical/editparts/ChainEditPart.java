package de.ptb.epics.eve.editor.graphical.editparts;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.editor.graphical.editparts.figures.ChainFigure;

/**
 * <code>ChainEditPart</code>
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class ChainEditPart extends AbstractGraphicalEditPart {

	/**
	 * Constructs a <code>ChainEditPart</code>.
	 * 
	 * @param chain the chain
	 */
	public ChainEditPart(final Chain chain) {
		this.setModel(chain);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure createFigure() {
		return new ChainFigure(((Chain)this.getModel()).getId());	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createEditPolicies() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<?> getModelChildren() {
		final Chain chain = (Chain)this.getModel();
		return chain.getScanModuls();
	}
}